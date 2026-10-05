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
# HEADER ACTIONS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Citizen Login' `
    '<span data-i18n="auth.login">Citizen Login</span>'

$content = Set-FirstOccurrence `
    $content `
    '>Register<' `
    '><span data-i18n="auth.register">Register</span><'

# =========================================================
# NAVIGATION
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '<span>Home</span>' `
    '<span data-i18n="nav.home">Home</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span>Services</span>' `
    '<span data-i18n="nav.services">Services</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span>Ration Card</span>' `
    '<span data-i18n="nav.rationCard">Ration Card</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span>FPS</span>' `
    '<span data-i18n="nav.fps">FPS</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span>Help</span>' `
    '<span data-i18n="nav.help">Help</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span>Transparency</span>' `
    '<span data-i18n="nav.transparency">Transparency</span>'

# =========================================================
# HERO KICKER
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Citizen Digital Service Portal' `
    '<span data-i18n="hero.kicker">Citizen Digital Service Portal</span>'

# =========================================================
# HERO TITLE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '<span class="rp-title-saffron">Right Food</span>' `
    '<span class="rp-title-saffron" data-i18n="hero.rightFood">Right Food</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span class="rp-title-blue">Right People</span>' `
    '<span class="rp-title-blue" data-i18n="hero.rightPeople">Right People</span>'

$content = Set-FirstOccurrence `
    $content `
    '<span class="rp-title-green">Right Time</span>' `
    '<span class="rp-title-green" data-i18n="hero.rightTime">Right Time</span>'

# =========================================================
# HERO SUBTITLE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '<div class="rp-hero-subtitle">' `
    '<div class="rp-hero-subtitle" data-i18n="hero.subtitle">'

# =========================================================
# HERO TAGLINE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '<p class="rp-hero-tagline">' `
    '<p class="rp-hero-tagline" data-i18n="hero.tagline">'

# =========================================================
# SEARCH LABEL
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '<label class="sr-only" for="rationSearchInput">' `
    '<label class="sr-only" for="rationSearchInput" data-i18n="search.label">'

# =========================================================
# SEARCH PLACEHOLDER
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'placeholder="Enter Ration Card Number / Ration ID / Member ID"' `
    'placeholder="Enter Ration Card Number / Ration ID / Member ID" data-i18n-placeholder="search.placeholder"'

# =========================================================
# SEARCH BUTTON
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '>Search</button>' `
    '><span data-i18n="search.button">Search</span></button>'

# =========================================================
# HERO CHIPS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    '>Food Security for All<' `
    '><span data-i18n="hero.chip.food">Food Security for All</span><'

$content = Set-FirstOccurrence `
    $content `
    '>Transparent System<' `
    '><span data-i18n="hero.chip.transparent">Transparent System</span><'

$content = Set-FirstOccurrence `
    $content `
    '>Digital India<' `
    '><span data-i18n="hero.chip.digital">Digital India</span><'

$content = Set-FirstOccurrence `
    $content `
    '>Easy Access<' `
    '><span data-i18n="hero.chip.access">Easy Access</span><'

# =========================================================
# HERO FAMILY MESSAGE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Citizen-first public distribution' `
    '<span data-i18n="hero.familySmall">Citizen-first public distribution</span>'

$content = Set-FirstOccurrence `
    $content `
    'Mera Ration' `
    '<span data-i18n="hero.familyMera">Mera Ration</span>'

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
Write-Host "STAGE 17.5A COMPLETE." -ForegroundColor Green
Write-Host ""
Write-Host "Header              : DONE"
Write-Host "Navigation           : DONE"
Write-Host "Hero                 : DONE"
Write-Host "Search               : DONE"
Write-Host "Hero chips           : DONE"
Write-Host "Hero family message  : DONE"
Write-Host ""
