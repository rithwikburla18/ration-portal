$ErrorActionPreference = "Stop"
$backendRoot = Split-Path -Parent $PSScriptRoot
Set-Location $backendRoot

Write-Host ""
Write-Host "Ration Portal local backend launcher" -ForegroundColor Cyan
Write-Host "IMPORTANT: Flyway migrations run during startup. Use a Neon DEVELOPMENT branch, not your live production database." -ForegroundColor Yellow
Write-Host ""

$secureConnection = Read-Host "Paste the PostgreSQL URI from Neon (hidden input)" -AsSecureString
$connPtr = [IntPtr]::Zero
$connectionUri = $null
$rng = $null
$secretBytes = New-Object byte[] 32
$secureBrevo = $null
$brevoPtr = [IntPtr]::Zero
try {
    $connPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureConnection)
    $connectionUri = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($connPtr)
    $uri = [Uri]$connectionUri
    if ($uri.Scheme -notin @("postgresql", "postgres") -or -not $uri.UserInfo.Contains(":") -or [string]::IsNullOrWhiteSpace($uri.AbsolutePath) -or $uri.AbsolutePath -eq "/") {
        throw "Connection URI is invalid. Copy the full PostgreSQL URI from Neon Connect."
    }

    $userPassword = $uri.UserInfo.Split(":", 2)
    $env:DB_USERNAME = [Uri]::UnescapeDataString($userPassword[0])
    $env:DB_PASSWORD = [Uri]::UnescapeDataString($userPassword[1])
    $query = $uri.Query
    if ($query -notmatch "(?i)(^|[?&])sslmode=") {
        $query = if ([string]::IsNullOrEmpty($query)) { "?sslmode=require" } else { "$query&sslmode=require" }
    }
    $env:DB_URL = "jdbc:postgresql://$($uri.Authority)$($uri.AbsolutePath)$query"

    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($secretBytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($secretBytes)
    $env:JWT_EXPIRATION_MS = "86400000"
    $env:APP_CORS_ALLOWED_ORIGINS = "http://localhost:5500,http://127.0.0.1:5500"
    $env:APP_PUBLIC_API_BASE_URL = "http://localhost:8080"

    $sender = Read-Host "Brevo verified sender email (press Enter to skip outgoing notifications)"
    if (-not [string]::IsNullOrWhiteSpace($sender)) {
        $env:BREVO_FROM_EMAIL = $sender.Trim()
        $secureBrevo = Read-Host "Brevo API key (hidden input; press Enter to skip)" -AsSecureString
        $brevoPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureBrevo)
        $env:BREVO_API_KEY = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($brevoPtr)
    }

    Write-Host ""
    Write-Host "Database host: $($uri.Host)" -ForegroundColor Green
    Write-Host "Database name: $($uri.AbsolutePath.TrimStart('/'))" -ForegroundColor Green
    Write-Host "Credentials are loaded only into this PowerShell process and are not written to a file." -ForegroundColor Green
    $confirm = Read-Host "Type START to run the backend against this database"
    if ($confirm -cne "START") {
        Write-Host "Cancelled. No application started." -ForegroundColor Yellow
        return
    }
    & .\gradlew.bat bootRun
    if ($LASTEXITCODE -ne 0) {
        throw "Backend exited with code $LASTEXITCODE. Review the error above; do not deploy."
    }
}
finally {
    if ($connPtr -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($connPtr) }
    if ($brevoPtr -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($brevoPtr) }
    if ($rng) { $rng.Dispose() }
    [Array]::Clear($secretBytes, 0, $secretBytes.Length)
    $connectionUri = $null
    $secureConnection.Dispose()
    if ($secureBrevo) { $secureBrevo.Dispose() }
    Remove-Item Env:DB_URL, Env:DB_USERNAME, Env:DB_PASSWORD, Env:JWT_SECRET, Env:JWT_EXPIRATION_MS, Env:APP_CORS_ALLOWED_ORIGINS, Env:APP_PUBLIC_API_BASE_URL, Env:BREVO_API_KEY, Env:BREVO_FROM_EMAIL -ErrorAction SilentlyContinue
}
