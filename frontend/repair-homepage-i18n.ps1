$ErrorActionPreference = "Stop"

$path = Join-Path $PSScriptRoot "index.html"

$content = [System.IO.File]::ReadAllText(
    $path,
    [System.Text.Encoding]::UTF8
)

# =========================================================
# REMOVE WRONG / NESTED I18N HOOKS
# =========================================================

$content = $content.Replace(
    '<span data-i18n="announcement.title"><span data-i18n="announcement.title">Announcements</span></span>',
    '<span data-i18n="announcement.title">Announcements</span>'
)

$content = $content.Replace(
    '<span data-i18n="nav.transparency"><span data-i18n="footer.transparency">Transparency</span></span>',
    '<span data-i18n="nav.transparency">Transparency</span>'
)

# =========================================================
# FAMILY PROMO
# =========================================================

$content = [regex]::Replace(
    $content,
    '<div class="rp-section-kicker">\s*National\s+food\s+security\s*</div>',
    '<div class="rp-section-kicker"><span data-i18n="family.kicker">National food security</span></div>',
    1
)

$content = [regex]::Replace(
    $content,
    '<h3>\s*Food for\s*<span style="color:var\(--rp-green-dark\);">\s*Stronger India\s*</span>\s*</h3>',
    '<h3><span data-i18n="family.title">Food for</span> <span style="color:var(--rp-green-dark);" data-i18n="family.titleStrong">Stronger India</span></h3>',
    1
)

$content = [regex]::Replace(
    $content,
    '<p>\s*A\s+citizen-first\s+digital\s+experience\s+connecting\s*ration\s+services,\s*family\s+information\s+and\s*public\s+distribution\s+workflows\.\s*</p>',
    '<p><span data-i18n="family.description">A citizen-first digital experience connecting ration services, family information and public distribution workflows.</span></p>',
    1
)

$content = [regex]::Replace(
    $content,
    '<div class="rp-family-slogan">\s*Food for a\s*<span>Stronger India</span>\s*</div>',
    '<div class="rp-family-slogan"><span data-i18n="family.slogan">Food for a <span>Stronger India</span></span></div>',
    1
)

# =========================================================
# ANNOUNCEMENTS
# =========================================================

$content = [regex]::Replace(
    $content,
    '<span data-i18n="announcement.title">\s*<span data-i18n="announcement.title">Announcements</span>\s*</span>',
    '<span data-i18n="announcement.title">Announcements</span>',
    1
)

# =========================================================
# FEATURE 1 — TRANSPARENCY
# =========================================================

$content = [regex]::Replace(
    $content,
    '<h3>\s*Transparent\s*&\s*Accountable\s*</h3>',
    '<h3 data-i18n="feature.transparent.title">Transparent &amp; Accountable</h3>',
    1
)

$content = [regex]::Replace(
    $content,
    '<p>\s*Clear\s+service\s+journeys,\s*status\s+visibility\s+and\s*responsible\s+digital\s+workflows\.\s*</p>',
    '<p data-i18n="feature.transparent.description">Clear service journeys, status visibility and responsible digital workflows.</p>',
    1
)

# =========================================================
# FEATURE 2 — DIGITAL INDIA
# =========================================================

$content = [regex]::Replace(
    $content,
    '<h3>\s*Digital\s+India\s*</h3>',
    '<h3 data-i18n="feature.digital.title">Digital India</h3>',
    1
)

$content = [regex]::Replace(
    $content,
    '<p>\s*Modern\s+web\s+services\s+designed\s+for\s+simple\s+and\s*accessible\s+citizen\s+interactions\.\s*</p>',
    '<p data-i18n="feature.digital.description">Modern web services designed for simple and accessible citizen interactions.</p>',
    1
)

# =========================================================
# FEATURE 3 — ACCESS
# =========================================================

$content = [regex]::Replace(
    $content,
    '<h3>\s*Accessible\s+Services\s*</h3>',
    '<h3 data-i18n="feature.access.title">Accessible Services</h3>',
    1
)

# =========================================================
# FEATURE 4 — FOOD SECURITY / FAMILY
# =========================================================

$content = [regex]::Replace(
    $content,
    '<h3>\s*For\s+Every\s+Family\s*</h3>',
    '<h3 data-i18n="feature.garib.title">For Every Family</h3>',
    1
)

$content = [regex]::Replace(
    $content,
    '<p>\s*The\s+design\s+emphasizes\s+food\s+security\s+and\s*citizen-centric\s+public\s+services\.\s*</p>',
    '<p data-i18n="feature.garib.description">The design emphasizes food security and citizen-centric public services.</p>',
    1
)

# =========================================================
# NATIONAL BANNER
# =========================================================

$content = [regex]::Replace(
    $content,
    '<div\s+class="rp-banner-kicker"\s+data-i18n="feature.bannerKicker"\s*>\s*Together for food security\s*</div>',
    '<div class="rp-banner-kicker" data-i18n="feature.bannerKicker">Together for food security</div>',
    1
)

$content = [regex]::Replace(
    $content,
    '<div\s+class="rp-banner-description"\s+data-i18n="feature.bannerDescription"\s*>\s*A modern digital experience inspired by India''s\s*tricolour, rice cultivation and citizen-first public\s*service values\.\s*</div>',
    '<div class="rp-banner-description" data-i18n="feature.bannerDescription">A modern digital experience inspired by India''s tricolour, rice cultivation and citizen-first public service values.</div>',
    1
)

$content = [regex]::Replace(
    $content,
    '<div\s+class="rp-banner-bottom"\s+data-i18n="feature.bannerBottom"\s*>\s*Food\s+â€¢\s+Family\s+â€¢\s+Future\s*</div>',
    '<div class="rp-banner-bottom" data-i18n="feature.bannerBottom">Food • Family • Future</div>',
    1
)

# =========================================================
# IMPORTANT NOTICE
# =========================================================

$content = [regex]::Replace(
    $content,
    '<p class="rp-notice-text">\s*This portal is an\s*educational software project\s*designed\s*to demonstrate a\s*modern Public Distribution System\s*experience using web technologies\.\s*</p>',
    '<p class="rp-notice-text" data-i18n="notice.text">This portal is an educational software project designed to demonstrate a modern Public Distribution System experience using web technologies.</p>',
    1
)

# =========================================================
# FOOTER
# =========================================================

$content = [regex]::Replace(
    $content,
    '<h3>\s*Ration Portal\s*</h3>',
    '<h3 data-i18n="footer.name">Ration Portal</h3>',
    1
)

$content = [regex]::Replace(
    $content,
    '<p>\s*Public\s+Distribution\s+System\s*</p>',
    '<p data-i18n="footer.pds">Public Distribution System</p>',
    1
)

$content = [regex]::Replace(
    $content,
    '<a href="#">\s*<span data-i18n="footer.about">About</span>\s*Us\s*</a>',
    '<a href="#"><span data-i18n="footer.about">About</span> Us</a>',
    1
)

$content = [regex]::Replace(
    $content,
    '<a href="#">\s*<span data-i18n="footer.privacy">Privacy Policy</span>\s*</a>',
    '<a href="#"><span data-i18n="footer.privacy">Privacy Policy</span></a>',
    1
)

$content = [regex]::Replace(
    $content,
    '<a href="#">\s*Terms & Conditions\s*</a>',
    '<a href="#" data-i18n="footer.terms">Terms &amp; Conditions</a>',
    1
)

$content = [regex]::Replace(
    $content,
    '<a\s+href="pages/grievance\.html">\s*<span data-i18n="footer.contact">Contact</span>\s*Us\s*</a>',
    '<a href="pages/grievance.html"><span data-i18n="footer.contact">Contact</span> Us</a>',
    1
)

# =========================================================
# COPYRIGHT
# =========================================================

$content = [regex]::Replace(
    $content,
    '<div>\s*Â©\s*<span id="currentYear">2026</span>\s*Ration Portal\s*</div>',
    '<div>© <span id="currentYear">2026</span> <span data-i18n="footer.name">Ration Portal</span></div>',
    1
)

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
Write-Host "STAGE 17.5E EXACT HTML REPAIR COMPLETE." -ForegroundColor Green
Write-Host ""
Write-Host "Family section             : DONE"
Write-Host "Announcement cleanup       : DONE"
Write-Host "Transparency feature       : DONE"
Write-Host "Digital India feature     : DONE"
Write-Host "Accessible feature         : DONE"
Write-Host "Family feature             : DONE"
Write-Host "National banner            : DONE"
Write-Host "Important notice           : DONE"
Write-Host "Footer                     : DONE"
Write-Host "UTF-8 without BOM          : DONE"
Write-Host ""
