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
# FAMILY SECTION
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Family Information' `
    '<span data-i18n="family.kicker">Family Information</span>'

$content = Set-FirstOccurrence `
    $content `
    'Your Family, Your Ration Rights' `
    '<span data-i18n="family.title">Your Family, Your Ration Rights</span>'

$content = Set-FirstOccurrence `
    $content `
    'Manage family information, ration entitlement and public distribution services.' `
    '<span data-i18n="family.description">Manage family information, ration entitlement and public distribution services.</span>'

$content = Set-FirstOccurrence `
    $content `
    'One family, one secure digital service experience.' `
    '<span data-i18n="family.slogan">One family, one secure digital service experience.</span>'

# =========================================================
# ANNOUNCEMENTS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Announcements' `
    '<span data-i18n="announcement.title">Announcements</span>'

$content = Set-FirstOccurrence `
    $content `
    'Verify Ration Card' `
    '<span data-i18n="announcement.verify">Verify Ration Card</span>'

$content = Set-FirstOccurrence `
    $content `
    'Check Application Status' `
    '<span data-i18n="announcement.check">Check Application Status</span>'

# =========================================================
# FEATURE — TRANSPARENCY
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Transparent Distribution' `
    '<span data-i18n="feature.transparent.title">Transparent Distribution</span>'

$content = Set-FirstOccurrence `
    $content `
    'Public information and service data designed to improve transparency and citizen trust.' `
    '<span data-i18n="feature.transparent.description">Public information and service data designed to improve transparency and citizen trust.</span>'

# =========================================================
# FEATURE — DIGITAL
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Digital First' `
    '<span data-i18n="feature.digital.title">Digital First</span>'

$content = Set-FirstOccurrence `
    $content `
    'Access ration services online through a simple and modern digital platform.' `
    '<span data-i18n="feature.digital.description">Access ration services online through a simple and modern digital platform.</span>'

# =========================================================
# FEATURE — ACCESS
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Accessible Services' `
    '<span data-i18n="feature.access.title">Accessible Services</span>'

$content = Set-FirstOccurrence `
    $content `
    'Citizen-focused services designed for easy access across devices and users.' `
    '<span data-i18n="feature.access.description">Citizen-focused services designed for easy access across devices and users.</span>'

# =========================================================
# FEATURE — GARIB
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'For Every Family' `
    '<span data-i18n="feature.garib.title">For Every Family</span>'

$content = Set-FirstOccurrence `
    $content `
    'Supporting food security and access to essential public distribution services.' `
    '<span data-i18n="feature.garib.description">Supporting food security and access to essential public distribution services.</span>'

# =========================================================
# NATIONAL BANNER
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Jai Hind' `
    '<span data-i18n="feature.bannerKicker">Jai Hind</span>'

$content = Set-FirstOccurrence `
    $content `
    'Food • Family • Future' `
    '<span data-i18n="feature.bannerKicker">Food • Family • Future</span>'

$content = Set-FirstOccurrence `
    $content `
    'A citizen-first digital experience for a transparent and accessible Public Distribution System.' `
    '<span data-i18n="feature.bannerDescription">A citizen-first digital experience for a transparent and accessible Public Distribution System.</span>'

$content = Set-FirstOccurrence `
    $content `
    'Serving citizens with dignity, transparency and technology.' `
    '<span data-i18n="feature.bannerBottom">Serving citizens with dignity, transparency and technology.</span>'

# =========================================================
# NOTICE
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Important Notice' `
    '<span data-i18n="notice.title">Important Notice</span>'

$content = Set-FirstOccurrence `
    $content `
    'Please keep your ration card number and registered mobile number available when using citizen services.' `
    '<span data-i18n="notice.text">Please keep your ration card number and registered mobile number available when using citizen services.</span>'

$content = Set-FirstOccurrence `
    $content `
    'Never share your password, OTP or authentication credentials with anyone.' `
    '<span data-i18n="notice.warning">Never share your password, OTP or authentication credentials with anyone.</span>'

# =========================================================
# FOOTER
# =========================================================

$content = Set-FirstOccurrence `
    $content `
    'Ration Portal' `
    '<span data-i18n="footer.name">Ration Portal</span>'

$content = Set-FirstOccurrence `
    $content `
    'Public Distribution System' `
    '<span data-i18n="footer.pds">Public Distribution System</span>'

$content = Set-FirstOccurrence `
    $content `
    'About' `
    '<span data-i18n="footer.about">About</span>'

$content = Set-FirstOccurrence `
    $content `
    'Privacy Policy' `
    '<span data-i18n="footer.privacy">Privacy Policy</span>'

$content = Set-FirstOccurrence `
    $content `
    'Terms of Use' `
    '<span data-i18n="footer.terms">Terms of Use</span>'

$content = Set-FirstOccurrence `
    $content `
    'Contact' `
    '<span data-i18n="footer.contact">Contact</span>'

$content = Set-FirstOccurrence `
    $content `
    'Transparency' `
    '<span data-i18n="footer.transparency">Transparency</span>'

$content = Set-FirstOccurrence `
    $content `
    'Educational Project' `
    '<span data-i18n="footer.educational">Educational Project</span>'

$content = Set-FirstOccurrence `
    $content `
    'Designed for citizen services' `
    '<span data-i18n="footer.designed">Designed for citizen services</span>'

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
Write-Host "STAGE 17.5C COMPLETE." -ForegroundColor Green
Write-Host ""
Write-Host "Family section        : DONE"
Write-Host "Announcements         : DONE"
Write-Host "Feature sections      : DONE"
Write-Host "National banner       : DONE"
Write-Host "Notice                : DONE"
Write-Host "Footer                : DONE"
Write-Host ""
