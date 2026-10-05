$ErrorActionPreference = "Stop"

$path = Join-Path $PSScriptRoot "index.html"

$content = [System.IO.File]::ReadAllText(
    $path,
    [System.Text.Encoding]::UTF8
)

function Set-FirstOccurrence {
    param(
        [string]$Text,
        [string]$Old,
        [string]$New
    )

    $index = $Text.IndexOf($Old)

    if ($index -lt 0) {
        return $Text
    }

    return $Text.Substring(0, $index) +
           $New +
           $Text.Substring($index + $Old.Length)
}

# =========================================================
# SERVICES SECTION
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Citizen services' `
    '<span data-i18n="services.kicker">Citizen services</span>'

$content = Set-FirstOccurrence `
    $content `
    'Essential Services at One Place' `
    '<span data-i18n="services.title">Essential Services at One Place</span>'

$content = Set-FirstOccurrence `
    $content `
    'Simple digital access to ration card, grievance, fair price shop and entitlement services.' `
    '<span data-i18n="services.description">Simple digital access to ration card, grievance, fair price shop and entitlement services.</span>'

$content = Set-FirstOccurrence `
    $content `
    'Five citizen services' `
    '<span data-i18n="services.applications">Five citizen services</span>'

# =========================================================
# SERVICE 1 — APPLY NEW RATION CARD
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Apply New Ration Card' `
    '<span data-i18n="service.apply.title">Apply New Ration Card</span>'

$content = Set-FirstOccurrence `
    $content `
    'Get your new ration card online through a guided citizen application journey.' `
    '<span data-i18n="service.apply.description">Get your new ration card online through a guided citizen application journey.</span>'

# =========================================================
# SERVICE 2 — DOWNLOAD E-RATION CARD
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Download e-Ration Card' `
    '<span data-i18n="service.download.title">Download e-Ration Card</span>'

$content = Set-FirstOccurrence `
    $content `
    'Download your digital ration card for easy access and verification.' `
    '<span data-i18n="service.download.description">Download your digital ration card for easy access and verification.</span>'

# =========================================================
# SERVICE 3 — LOCATE FPS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Locate FPS' `
    '<span data-i18n="service.fps.title">Locate FPS</span>'

$content = Set-FirstOccurrence `
    $content `
    'Find Fair Price Shops and distribution information near you.' `
    '<span data-i18n="service.fps.description">Find Fair Price Shops and distribution information near you.</span>'

# =========================================================
# SERVICE 4 — LODGE GRIEVANCE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Lodge Grievance' `
    '<span data-i18n="service.grievance.title">Lodge Grievance</span>'

$content = Set-FirstOccurrence `
    $content `
    'Submit a grievance and track its status through the citizen portal.' `
    '<span data-i18n="service.grievance.description">Submit a grievance and track its status through the citizen portal.</span>'

# =========================================================
# SERVICE 5 — ENTITLEMENT & BENEFITS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Entitlement & Benefits' `
    '<span data-i18n="service.entitlement.title">Entitlement & Benefits</span>'

$content = Set-FirstOccurrence `
    $content `
    'View your ration entitlement, benefits and monthly allocation details.' `
    '<span data-i18n="service.entitlement.description">View your ration entitlement, benefits and monthly allocation details.</span>'

# =========================================================
# TRANSPARENCY DASHBOARD
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Transparency Dashboard' `
    '<span data-i18n="dashboard.kicker">Transparency Dashboard</span>'

$content = Set-FirstOccurrence `
    $content `
    'Public Distribution System Overview' `
    '<span data-i18n="dashboard.title">Public Distribution System Overview</span>'

$content = Set-FirstOccurrence `
    $content `
    'Live public information on ration cards, rice distribution and active fair price shops.' `
    '<span data-i18n="dashboard.description">Live public information on ration cards, rice distribution and active fair price shops.</span>'

# =========================================================
# DASHBOARD STATISTICS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Total Ration Cards' `
    '<span data-i18n="dashboard.cards">Total Ration Cards</span>'

$content = Set-FirstOccurrence `
    $content `
    'Rice Distributed' `
    '<span data-i18n="dashboard.rice">Rice Distributed</span>'

$content = Set-FirstOccurrence `
    $content `
    'Active FPS' `
    '<span data-i18n="dashboard.fps">Active FPS</span>'

# =========================================================
# DASHBOARD GROWTH LABELS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Cards this month' `
    '<span data-i18n="dashboard.cardsGrowth">Cards this month</span>'

$content = Set-FirstOccurrence `
    $content `
    'Rice distributed this month' `
    '<span data-i18n="dashboard.riceGrowth">Rice distributed this month</span>'

$content = Set-FirstOccurrence `
    $content `
    'FPS currently active' `
    '<span data-i18n="dashboard.fpsGrowth">FPS currently active</span>'

# =========================================================
# DASHBOARD NOTE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Data shown here represents public ration services, family information and distribution workflows.' `
    '<span data-i18n="dashboard.note">Data shown here represents public ration services, family information and distribution workflows.</span>'

# =========================================================
# WRITE UTF-8 WITHOUT BOM
# =========================================================

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

[System.IO.File]::WriteAllText(
    $path,
    $content,
    $utf8NoBom
)

Write-Host ""
Write-Host "STAGE 17.5B COMPLETE." -ForegroundColor Green
Write-Host ""
Write-Host "Services section      : DONE"
Write-Host "Service cards         : DONE"
Write-Host "Transparency         : DONE"
Write-Host "Dashboard statistics  : DONE"
Write-Host "Growth labels         : DONE"
Write-Host "Dashboard note        : DONE"
Write-Host ""
