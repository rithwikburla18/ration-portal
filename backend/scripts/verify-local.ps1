$ErrorActionPreference = "Stop"
$baseUrl = "http://localhost:8080"
$script:passed = 0
$script:failures = @()

function Write-Check {
    param([string]$Name, [bool]$Success, [string]$Details = "")
    if ($Success) {
        $script:passed++
        Write-Host "PASS  $Name" -ForegroundColor Green
    } else {
        $script:failures += $Name
        Write-Host "FAIL  $Name $Details" -ForegroundColor Red
    }
}

Add-Type -AssemblyName System.Net.Http

function Invoke-LocalApi {
    param(
        [Parameter(Mandatory=$true)][string]$Method,
        [Parameter(Mandatory=$true)][string]$Path,
        [AllowNull()][object]$Body = $null,
        [string]$Token = ""
    )

    $headers = @{ Accept = "application/json" }
    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $headers.Authorization = "Bearer $Token"
    }

    $params = @{
        Uri = "$script:baseUrl$Path"
        Method = $Method
        Headers = $headers
        UseBasicParsing = $true
        TimeoutSec = 20
    }
    if ($null -ne $Body) {
        $params.ContentType = "application/json"
        $params.Body = ConvertTo-Json -InputObject $Body -Depth 20 -Compress
    }

    $status = 0
    $responseText = ""
    try {
        $response = Invoke-WebRequest @params
        $status = [int]$response.StatusCode
        $responseText = [string]$response.Content
    } catch {
        $webResponse = $_.Exception.Response
        if ($null -eq $webResponse) {
            throw "Could not reach the local backend at $script:baseUrl. Make sure start-local.ps1 is still running."
        }
        $status = [int]$webResponse.StatusCode
        if ($webResponse -is [System.Net.Http.HttpResponseMessage]) {
            $responseText = $webResponse.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        } else {
            try {
                $stream = $webResponse.GetResponseStream()
                if ($null -ne $stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $responseText = $reader.ReadToEnd()
                    $reader.Dispose()
                }
            } catch {
                $responseText = ""
            }
        }
    }

    $data = $null
    if (-not [string]::IsNullOrWhiteSpace($responseText)) {
        try { $data = ConvertFrom-Json -InputObject $responseText -ErrorAction Stop } catch { $data = $null }
    }
    return [PSCustomObject]@{ StatusCode = $status; Data = $data; Text = $responseText }
}

Write-Host ""
Write-Host "Ration Portal local HTTP smoke test" -ForegroundColor Cyan
Write-Host "Target: $baseUrl"
Write-Host "This script uses localhost only. It does not call Neon or send real notifications."
Write-Host ""

$health = Invoke-LocalApi -Method "GET" -Path "/api/health"
Write-Check "Backend health endpoint" ($health.StatusCode -eq 200 -and $health.Data.status -eq "UP") "HTTP $($health.StatusCode)"

$shopSearch = Invoke-LocalApi -Method "GET" -Path "/api/fair-price-shops?district=Warangal"
Write-Check "Public FPS search endpoint" ($shopSearch.StatusCode -eq 200) "HTTP $($shopSearch.StatusCode)"

$publicVerify = Invoke-LocalApi -Method "GET" -Path "/api/ration-cards/verify/000000000000"
Write-Check "Public card verification handles unknown card" ($publicVerify.StatusCode -eq 404) "HTTP $($publicVerify.StatusCode)"

$unauthenticatedApps = Invoke-LocalApi -Method "GET" -Path "/api/applications"
Write-Check "Application data requires authentication" ($unauthenticatedApps.StatusCode -in @(401,403)) "HTTP $($unauthenticatedApps.StatusCode)"

Write-Host ""
Write-Host "Sign in with the temporary administrator credentials you entered in start-local.ps1." -ForegroundColor Cyan
$adminEmail = (Read-Host "Local administrator email").Trim().ToLowerInvariant()
$secureAdmin = Read-Host "Local administrator password (hidden input)" -AsSecureString
$adminPtr = [IntPtr]::Zero
$adminPassword = $null
try {
    $adminPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureAdmin)
    $adminPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($adminPtr)
    $adminLogin = Invoke-LocalApi -Method "POST" -Path "/api/auth/login" -Body @{
        email = $adminEmail
        password = $adminPassword
        portal = "ADMIN"
    }
} finally {
    if ($adminPtr -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($adminPtr) }
    $adminPassword = $null
    $secureAdmin.Dispose()
}
Write-Check "Local administrator login" ($adminLogin.StatusCode -eq 200 -and $adminLogin.Data.role -eq "ADMIN" -and -not [string]::IsNullOrWhiteSpace($adminLogin.Data.token)) "HTTP $($adminLogin.StatusCode)"
if ($adminLogin.StatusCode -ne 200 -or [string]::IsNullOrWhiteSpace($adminLogin.Data.token)) {
    Write-Host ""
    Write-Host "Administrator login failed; remaining authenticated checks were skipped." -ForegroundColor Yellow
    Write-Host "Summary: $script:passed passed, $($script:failures.Count) failed."
    exit 1
}
$adminToken = [string]$adminLogin.Data.token

$adminPending = Invoke-LocalApi -Method "GET" -Path "/api/ration-cards/admin/pending" -Token $adminToken
Write-Check "Admin can query pending ration cards" ($adminPending.StatusCode -eq 200) "HTTP $($adminPending.StatusCode)"

$adminApps = Invoke-LocalApi -Method "GET" -Path "/api/applications" -Token $adminToken
Write-Check "Admin can query ration-card applications" ($adminApps.StatusCode -eq 200) "HTTP $($adminApps.StatusCode)"

$adminMetrics = Invoke-LocalApi -Method "GET" -Path "/api/dashboard/metrics" -Token $adminToken
Write-Check "Admin dashboard aggregates" ($adminMetrics.StatusCode -eq 200 -and $adminMetrics.Data.role -eq "ADMIN") "HTTP $($adminMetrics.StatusCode)"

$transparency = Invoke-LocalApi -Method "GET" -Path "/api/transparency/summary" -Token $adminToken
Write-Check "Transparency summary" ($transparency.StatusCode -eq 200 -and $null -ne $transparency.Data.totalRationCards) "HTTP $($transparency.StatusCode)"

$grievanceAdminList = Invoke-LocalApi -Method "GET" -Path "/api/grievances" -Token $adminToken
Write-Check "Admin grievance queue" ($grievanceAdminList.StatusCode -eq 200) "HTTP $($grievanceAdminList.StatusCode)"

Write-Host ""
Write-Host "Creating an isolated temporary citizen account for workflow checks..." -ForegroundColor Cyan
$identity = [Guid]::NewGuid().ToString("N")
$citizenEmail = "citizen-smoke-" + $identity.Substring(0,12) + "@example.test"
$citizenPassword = "Citizen-" + $identity.Substring(0,20) + "Aa1!"
$registerCitizen = Invoke-LocalApi -Method "POST" -Path "/api/auth/register" -Body @{
    fullName = "Local Smoke Citizen"
    email = $citizenEmail
    password = $citizenPassword
    termsAccepted = "true"
}
Write-Check "Citizen registration validates and creates account" ($registerCitizen.StatusCode -eq 200) "HTTP $($registerCitizen.StatusCode)"
if ($registerCitizen.StatusCode -ne 200) {
    Write-Host "Citizen-dependent workflow checks cannot continue." -ForegroundColor Yellow
    Write-Host "Summary: $script:passed passed, $($script:failures.Count) failed."
    exit 1
}

$citizenLogin = Invoke-LocalApi -Method "POST" -Path "/api/auth/login" -Body @{
    email = $citizenEmail
    password = $citizenPassword
}
Write-Check "Citizen login and JWT issuance" ($citizenLogin.StatusCode -eq 200 -and $citizenLogin.Data.role -eq "CITIZEN" -and -not [string]::IsNullOrWhiteSpace($citizenLogin.Data.token)) "HTTP $($citizenLogin.StatusCode)"
if ($citizenLogin.StatusCode -ne 200 -or [string]::IsNullOrWhiteSpace($citizenLogin.Data.token)) {
    Write-Host "Citizen-dependent workflow checks cannot continue." -ForegroundColor Yellow
    Write-Host "Summary: $script:passed passed, $($script:failures.Count) failed."
    exit 1
}
$citizenToken = [string]$citizenLogin.Data.token

$citizenMetrics = Invoke-LocalApi -Method "GET" -Path "/api/dashboard/metrics" -Token $citizenToken
Write-Check "Pending citizen can view their dashboard status" ($citizenMetrics.StatusCode -eq 200 -and $citizenMetrics.Data.role -eq "CITIZEN") "HTTP $($citizenMetrics.StatusCode)"

$citizenTransparency = Invoke-LocalApi -Method "GET" -Path "/api/transparency/summary" -Token $citizenToken
Write-Check "Citizen transparency access before card approval" ($citizenTransparency.StatusCode -eq 200) "HTTP $($citizenTransparency.StatusCode)"

$manualCard = Invoke-LocalApi -Method "POST" -Path "/api/ration-cards" -Token $citizenToken -Body @{
    rationCardNumber = "123456789099"
    cardType = "BPL"
    headOfFamily = "Local Smoke Citizen"
    address = "1 Local Test Road"
    district = "Warangal"
    state = "Telangana"
    pincode = "506001"
    ownerEmail = $citizenEmail
}
Write-Check "Citizen cannot bypass the application workflow with a manual card" ($manualCard.StatusCode -in @(403,423)) "HTTP $($manualCard.StatusCode)"

$application = Invoke-LocalApi -Method "POST" -Path "/api/applications/ration-card" -Token $citizenToken -Body @{
    headOfFamily = "Local Smoke Citizen"
    mobile = "9876543210"
    email = "ignored@example.test"
    cardType = "BPL"
    address = "1 Local Test Road"
    district = "Warangal"
    state = "Telangana"
    pincode = "506001"
    familyMembers = @(@{
        name = "Local Smoke Citizen"
        age = 30
        gender = "Other"
        relationship = "Self"
    })
}
$appNumber = [string]$application.Data.applicationNumber
Write-Check "Citizen can submit a validated ration-card application" ($application.StatusCode -eq 200 -and -not [string]::IsNullOrWhiteSpace($appNumber)) "HTTP $($application.StatusCode)"
if ($application.StatusCode -ne 200 -or [string]::IsNullOrWhiteSpace($appNumber)) {
    Write-Host "Application approval/PDF checks cannot continue." -ForegroundColor Yellow
    Write-Host "Summary so far: $script:passed passed, $($script:failures.Count) failed."
    exit 1
}

$citizenApps = Invoke-LocalApi -Method "GET" -Path "/api/applications" -Token $citizenToken
$ownApps = @($citizenApps.Data)
$ownsCreatedApp = @($ownApps | Where-Object { $_.applicationNumber -eq $appNumber }).Count -gt 0
Write-Check "Citizen sees their own application" ($citizenApps.StatusCode -eq 200 -and $ownsCreatedApp) "HTTP $($citizenApps.StatusCode)"

$citizenDecision = Invoke-LocalApi -Method "PUT" -Path ("/api/applications/" + [Uri]::EscapeDataString($appNumber) + "/status") -Token $citizenToken -Body @{ status = "APPROVED" }
Write-Check "Citizen cannot approve their own application" ($citizenDecision.StatusCode -eq 403) "HTTP $($citizenDecision.StatusCode)"

$adminDecision = Invoke-LocalApi -Method "PUT" -Path ("/api/applications/" + [Uri]::EscapeDataString($appNumber) + "/status") -Token $adminToken -Body @{ status = "APPROVED" }
Write-Check "Admin approval issues a ration card" ($adminDecision.StatusCode -eq 200 -and $adminDecision.Data.status -eq "APPROVED") "HTTP $($adminDecision.StatusCode)"

$applicationStatus = Invoke-LocalApi -Method "GET" -Path ("/api/applications/" + [Uri]::EscapeDataString($appNumber)) -Token $citizenToken
$cardNumber = [string]$applicationStatus.Data.issuedRationCardNumber
Write-Check "Approved application completes card issuance" ($applicationStatus.StatusCode -eq 200 -and $applicationStatus.Data.completionStatus -eq "COMPLETED" -and $cardNumber -match "^[0-9]{12}$") "HTTP $($applicationStatus.StatusCode)"
if ($applicationStatus.StatusCode -ne 200 -or $cardNumber -notmatch "^[0-9]{12}$") {
    Write-Host "Card/PDF and downstream workflow checks cannot continue." -ForegroundColor Yellow
    Write-Host "Summary so far: $script:passed passed, $($script:failures.Count) failed."
    exit 1
}

$card = Invoke-LocalApi -Method "GET" -Path ("/api/ration-cards/" + $cardNumber) -Token $citizenToken
Write-Check "Citizen can access their issued card" ($card.StatusCode -eq 200 -and $card.Data.rationCardNumber -eq $cardNumber) "HTTP $($card.StatusCode)"

$verifiedCard = Invoke-LocalApi -Method "GET" -Path ("/api/ration-cards/verify/" + $cardNumber)
Write-Check "Public QR/card verification" ($verifiedCard.StatusCode -eq 200 -and $verifiedCard.Data.verified -eq $true) "HTTP $($verifiedCard.StatusCode)"

$family = Invoke-LocalApi -Method "GET" -Path ("/api/family-members/ration-card/" + [string]$card.Data.id) -Token $citizenToken
Write-Check "Issued family members are owner-scoped" ($family.StatusCode -eq 200 -and @($family.Data).Count -eq 1) "HTTP $($family.StatusCode)"

$pdfPath = [System.IO.Path]::GetTempFileName()
$pdfOk = $false
try {
    Invoke-WebRequest -Uri "$baseUrl/api/ration-cards/$cardNumber/pdf" -Method GET -Headers @{ Authorization = "Bearer $citizenToken"; Accept = "application/pdf" } -OutFile $pdfPath -UseBasicParsing -TimeoutSec 30
    $pdfBytes = [System.IO.File]::ReadAllBytes($pdfPath)
    $pdfSignature = ""
    if ($pdfBytes.Length -ge 4) { $pdfSignature = [System.Text.Encoding]::ASCII.GetString($pdfBytes, 0, 4) }
    $pdfOk = ($pdfBytes.Length -gt 4 -and $pdfSignature -eq "%PDF")
    Write-Host ("PDF response diagnostic: download=completed; bytes={0}; signature=[{1}]" -f $pdfBytes.Length, $pdfSignature)
} catch {
    $pdfOk = $false
    $pdfStatus = "no HTTP response"
    $pdfError = $_.Exception.Message
    if ($_.Exception.Response) { try { $pdfStatus = [int]$_.Exception.Response.StatusCode } catch {} }
    Write-Host "PDF diagnostic: HTTP $pdfStatus; $pdfError"
} finally {
    Remove-Item $pdfPath -Force -ErrorAction SilentlyContinue
}
Write-Check "Citizen can download a generated PDF card" $pdfOk

$grievance = Invoke-LocalApi -Method "POST" -Path "/api/grievances" -Token $citizenToken -Body @{
    applicantName = "Local Smoke Citizen"
    rationCardNumber = $cardNumber
    category = "Distribution"
    contactNumber = "9876543210"
    description = "Local smoke-test grievance; no real citizen data."
}
$grievanceNumber = [string]$grievance.Data.grievanceNumber
Write-Check "Citizen can lodge an owner-verified grievance" ($grievance.StatusCode -eq 201 -and -not [string]::IsNullOrWhiteSpace($grievanceNumber)) "HTTP $($grievance.StatusCode)"

if (-not [string]::IsNullOrWhiteSpace($grievanceNumber)) {
    $grievanceUpdate = Invoke-LocalApi -Method "PUT" -Path ("/api/grievances/" + [Uri]::EscapeDataString($grievanceNumber) + "/status") -Token $adminToken -Body @{ status = "PROCESSING" }
    Write-Check "Admin can update grievance status" ($grievanceUpdate.StatusCode -eq 200 -and $grievanceUpdate.Data.status -eq "PROCESSING") "HTTP $($grievanceUpdate.StatusCode)"
}

$onorc = Invoke-LocalApi -Method "POST" -Path "/api/onorc/portability" -Token $citizenToken -Body @{
    rationCardNumber = $cardNumber
    destinationState = "Maharashtra"
    destinationDistrict = "Pune"
}
$onorcReference = [string]$onorc.Data.referenceNumber
Write-Check "Citizen can create an owner-verified ONORC request" ($onorc.StatusCode -eq 200 -and $onorcReference.StartsWith("ONORC-")) "HTTP $($onorc.StatusCode)"

if (-not [string]::IsNullOrWhiteSpace($onorcReference)) {
    $onorcUpdate = Invoke-LocalApi -Method "PUT" -Path ("/api/onorc/portability/" + [Uri]::EscapeDataString($onorcReference) + "/status") -Token $adminToken -Body @{ status = "PROCESSING" }
    Write-Check "Admin can update ONORC status" ($onorcUpdate.StatusCode -eq 200 -and $onorcUpdate.Data.status -eq "PROCESSING") "HTTP $($onorcUpdate.StatusCode)"
}

$dealerEmail = "dealer-smoke-" + $identity.Substring(0,12) + "@example.test"
$dealerPassword = "Dealer-" + $identity.Substring(0,20) + "Aa1!"
$dealerRegister = Invoke-LocalApi -Method "POST" -Path "/api/fps-dealer-applications/register" -Body @{
    dealerName = "Local Smoke Dealer"
    shopName = "Local Smoke FPS"
    licenseNumber = "SMOKE-" + $identity.Substring(0,16).ToUpperInvariant()
    mobile = "9876543211"
    email = $dealerEmail
    password = $dealerPassword
    district = "Warangal"
    mandalVillage = "Hanamkonda"
    shopAddress = "2 Local Test Road"
    latitude = 17.98
    longitude = 79.59
    termsAccepted = "true"
}
Write-Check "FPS dealer can submit an application" ($dealerRegister.StatusCode -eq 201) "HTTP $($dealerRegister.StatusCode)"

if ($dealerRegister.StatusCode -eq 201) {
    $dealerPendingLogin = Invoke-LocalApi -Method "POST" -Path "/api/auth/login" -Body @{
        email = $dealerEmail
        password = $dealerPassword
        portal = "FPS_DEALER"
    }
    Write-Check "Pending dealer cannot sign in to FPS services" ($dealerPendingLogin.StatusCode -eq 403)

    $dealerQueue = Invoke-LocalApi -Method "GET" -Path "/api/fps-dealer-applications" -Token $adminToken
    $dealerRow = @($dealerQueue.Data | Where-Object { $_.email -eq $dealerEmail } | Select-Object -First 1)
    if ($dealerQueue.StatusCode -eq 200 -and $dealerRow.Count -gt 0) {
        $dealerId = [string]$dealerRow[0].id
        $dealerDecision = Invoke-LocalApi -Method "PUT" -Path ("/api/fps-dealer-applications/" + $dealerId + "/decision") -Token $adminToken -Body @{ status = "APPROVED" }
        Write-Check "Admin can approve dealer and activate account" ($dealerDecision.StatusCode -eq 200 -and $dealerDecision.Data.status -eq "APPROVED") "HTTP $($dealerDecision.StatusCode)"

        $dealerLogin = Invoke-LocalApi -Method "POST" -Path "/api/auth/login" -Body @{
            email = $dealerEmail
            password = $dealerPassword
            portal = "FPS_DEALER"
        }
        Write-Check "Approved dealer can sign in" ($dealerLogin.StatusCode -eq 200 -and $dealerLogin.Data.role -eq "FPS_DEALER") "HTTP $($dealerLogin.StatusCode)"

        if ($dealerLogin.StatusCode -eq 200 -and -not [string]::IsNullOrWhiteSpace($dealerLogin.Data.token)) {
            $dealerToken = [string]$dealerLogin.Data.token
            $dealerRecords = Invoke-LocalApi -Method "GET" -Path "/api/fps-distribution/records" -Token $dealerToken
            Write-Check "Approved dealer can view their scoped records" ($dealerRecords.StatusCode -eq 200) "HTTP $($dealerRecords.StatusCode)"

            $shopSearchAfterApproval = Invoke-LocalApi -Method "GET" -Path "/api/fair-price-shops?district=Warangal"
            $shopNames = @($shopSearchAfterApproval.Data | Where-Object { $_.email -eq $dealerEmail -or $_.shopName -eq "Local Smoke FPS" })
            Write-Check "Approved FPS shop appears in public search" ($shopSearchAfterApproval.StatusCode -eq 200 -and $shopNames.Count -gt 0) "HTTP $($shopSearchAfterApproval.StatusCode)"

            $otpAttempt = Invoke-LocalApi -Method "POST" -Path "/api/fps-distribution/otp" -Token $dealerToken -Body @{ rationCardNumber = $cardNumber }
            Write-Check "OTP delivery fails closed without configured local email provider" ($otpAttempt.StatusCode -eq 503 -and -not ($otpAttempt.Data.PSObject.Properties.Name -contains "otp")) "HTTP $($otpAttempt.StatusCode)"
        }
    } else {
        Write-Check "Admin can retrieve the dealer application queue" $false "HTTP $($dealerQueue.StatusCode)"
    }
}

Write-Host ""
Write-Host "Smoke-test result: $script:passed passed; $($script:failures.Count) failed." -ForegroundColor Cyan
if ($script:failures.Count -gt 0) {
    Write-Host ("Failed checks: " + ($script:failures -join ", ")) -ForegroundColor Red
    Write-Host "No remote database was used. Local smoke-test records exist only in the in-memory H2 database."
    exit 1
}
Write-Host "All local HTTP smoke checks passed. Test data remains only in the temporary H2 database and disappears when the backend stops." -ForegroundColor Green
