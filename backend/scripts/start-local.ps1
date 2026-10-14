$ErrorActionPreference = "Stop"
$backendRoot = Split-Path -Parent $PSScriptRoot
Set-Location $backendRoot

Write-Host ""
Write-Host "Ration Portal isolated local backend" -ForegroundColor Cyan
Write-Host "Database: temporary in-memory H2. No Neon connection and no Flyway migrations." -ForegroundColor Green
Write-Host "Outbound Brevo email/SMS are disabled. Local data is erased when the app stops." -ForegroundColor Yellow
Write-Host ""

$secureAdmin = $null
$adminPtr = [IntPtr]::Zero
$rng = $null
$secretBytes = New-Object byte[] 32
$adminPassword = $null
try {
    $adminEmail = (Read-Host "Enter the local administrator email").Trim().ToLowerInvariant()
    if ($adminEmail -notmatch '^[^\s@]+@[^\s@]+\.[^\s@]+$' -or $adminEmail.Length -gt 150) {
        throw "Enter a valid administrator email address."
    }

    $secureAdmin = Read-Host "Enter a temporary local administrator password (hidden input)" -AsSecureString
    $adminPtr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureAdmin)
    $adminPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($adminPtr)
    if ([string]::IsNullOrWhiteSpace($adminPassword) -or $adminPassword.Length -lt 8 -or [Text.Encoding]::UTF8.GetByteCount($adminPassword) -gt 72) {
        throw "The local administrator password must be at least 8 characters and no more than 72 UTF-8 bytes."
    }

    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($secretBytes)
    $env:SPRING_PROFILES_ACTIVE = "local"
    $env:JWT_SECRET = [Convert]::ToBase64String($secretBytes)
    $env:JWT_EXPIRATION_MS = "86400000"
    $env:ADMIN_EMAIL = $adminEmail
    $env:ADMIN_PASSWORD = $adminPassword
    $env:BREVO_API_KEY = ""
    $env:BREVO_FROM_EMAIL = ""
    $env:BREVO_SMS_SENDER = "RationPortalLocal"
    $env:MAIL_USERNAME = ""
    $env:MAIL_PASSWORD = ""
    $env:APP_PUBLIC_API_BASE_URL = "http://localhost:8080"
    $env:APP_CORS_ALLOWED_ORIGINS = "http://localhost:5500,http://127.0.0.1:5500"

    $confirm = Read-Host "Type START to launch the local-only backend"
    if ($confirm -cne "START") {
        Write-Host "Cancelled. No application started." -ForegroundColor Yellow
        return
    }

    Write-Host "Starting with local profile and a temporary admin account..." -ForegroundColor Green
    & .\gradlew.bat bootRun
    if ($LASTEXITCODE -ne 0) {
        throw "Backend exited with code $LASTEXITCODE. Review the error above; do not deploy."
    }
}
finally {
    if ($adminPtr -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($adminPtr) }
    if ($rng) { $rng.Dispose() }
    [Array]::Clear($secretBytes, 0, $secretBytes.Length)
    $adminPassword = $null
    if ($secureAdmin) { $secureAdmin.Dispose() }
    Remove-Item Env:SPRING_PROFILES_ACTIVE, Env:JWT_SECRET, Env:JWT_EXPIRATION_MS, Env:ADMIN_EMAIL, Env:ADMIN_PASSWORD, Env:BREVO_API_KEY, Env:BREVO_FROM_EMAIL, Env:BREVO_SMS_SENDER, Env:MAIL_USERNAME, Env:MAIL_PASSWORD, Env:APP_PUBLIC_API_BASE_URL, Env:APP_CORS_ALLOWED_ORIGINS -ErrorAction SilentlyContinue
}
