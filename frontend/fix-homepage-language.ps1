$ErrorActionPreference = "Stop"

$path = Join-Path $PSScriptRoot "index.html"

$content = [System.IO.File]::ReadAllText(
    $path,
    [System.Text.Encoding]::UTF8
)

# =========================================================
# 1. FIX ASHOKA CHAKRA MOJIBAKE IN CSS
# =========================================================

$content = $content.Replace(
    'content: "â˜¸";',
    'content: "\2628";'
)

# =========================================================
# 2. FIX JAI HIND POSITIONING / OVERLAP
# =========================================================

$content = [System.Text.RegularExpressions.Regex]::Replace(
    $content,
    '(?s)\.rp-jaihind\s*\{.*?\n\s*\}',
@'
.rp-jaihind {
    position: relative;
    z-index: 2;
    margin-top: 12px;
    padding-right: 92px;
    color: var(--rp-green-dark);
    font-size: 13px;
    line-height: 1.4;
    font-weight: 900;
}
'@,
    1
)

# Give the banner enough natural vertical space.
$content = $content.Replace(
    'min-height: 145px;',
    'min-height: 175px;'
)

# =========================================================
# 3. REPLACE ANNOUNCEMENT SECTION
#    WITH I18N HOOKS
# =========================================================

$announcementSection = @'
<section
    class="rp-announcement"
    aria-label="Latest announcements"
>

    <div class="rp-announcement-shell">

        <div class="rp-announcement-title">
            <svg class="rp-icon-sm" aria-hidden="true">
                <use href="#icon-bell"></use>
            </svg>

            <span data-i18n="announcement.title">
                Announcements
            </span>
        </div>

        <div class="rp-announcement-viewport">

            <div
                class="rp-announcement-track"
                aria-live="off"
            >

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.verify"
                >
                    Citizens are encouraged to verify their family
                    details before submitting service applications.
                </div>

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.check"
                >
                    Check your ration entitlements and application
                    status through the citizen portal.
                </div>

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.verify"
                >
                    Citizens are encouraged to verify their family
                    details before submitting service applications.
                </div>

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.check"
                >
                    Check your ration entitlements and application
                    status through the citizen portal.
                </div>

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.verify"
                >
                    Citizens are encouraged to verify their family
                    details before submitting service applications.
                </div>

                <div
                    class="rp-announcement-item"
                    data-i18n="announcement.check"
                >
                    Check your ration entitlements and application
                    status through the citizen portal.
                </div>

            </div>

        </div>

    </div>

</section>
'@

$content = [System.Text.RegularExpressions.Regex]::Replace(
    $content,
    '(?s)<section\s+class="rp-announcement".*?</section>',
    $announcementSection,
    1
)

# =========================================================
# 4. REPLACE JAI HIND BANNER CONTENT
#    WITH I18N HOOKS
# =========================================================

$bannerBlock = @'
<div class="rp-feature-banner">

    <div
        class="rp-feature-flag"
        aria-hidden="true"
    ></div>

    <div
        class="rp-section-kicker"
        data-i18n="feature.bannerKicker"
    >
        Together for food security
    </div>

    <h3
        data-i18n="feature.jaiHind"
    >
        Jai Hind
    </h3>

    <p
        data-i18n="feature.bannerDescription"
    >
        A modern digital experience inspired by India's
        tricolour, rice cultivation and citizen-first public
        service values.
    </p>

    <div
        class="rp-jaihind"
        data-i18n="feature.bannerBottom"
    >
        Food • Family • Future
    </div>

</div>
'@

$content = [System.Text.RegularExpressions.Regex]::Replace(
    $content,
    '(?s)<div\s+class="rp-feature-banner">.*?</div>\s*</div>\s*</section>',
    $bannerBlock + "`r`n`r`n            </div>`r`n`r`n        </section>",
    1
)

# =========================================================
# 5. WRITE UTF-8 WITHOUT BOM
# =========================================================

$utf8NoBom = New-Object System.Text.UTF8Encoding($false)

[System.IO.File]::WriteAllText(
    $path,
    $content,
    $utf8NoBom
)

Write-Host ""
Write-Host "HOMEPAGE LANGUAGE FIX APPLIED SUCCESSFULLY." -ForegroundColor Green
Write-Host ""
Write-Host "Fixed:"
Write-Host "1. Announcement ticker language mixing"
Write-Host "2. Announcement mojibake"
Write-Host "3. Jai Hind banner translations"
Write-Host "4. Ashoka Chakra mojibake"
Write-Host "5. Jai Hind banner overlap"
Write-Host "6. UTF-8 encoding"
Write-Host ""