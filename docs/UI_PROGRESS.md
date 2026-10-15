# UI remediation progress

Branch: `feature/ui-final-fixes` (PR #10). Do not merge or deploy from this checklist.

Status meanings: **DONE** means a source change exists in this branch; **UNVERIFIED** means browser/CI evidence is not yet available; **TODO** means not yet audited or implemented. Update this file in every UI-remediation commit.

## Step 0 — hosting/cache
- [ ] TODO — inspect repository tree and Render service definition
- [ ] TODO — configure Render static-site response headers in `render.yaml` or document exact dashboard headers
- [ ] TODO — decide whether to retain `frontend/_headers` (Render does not consume it)
- [ ] TODO — confirm HTML/CSS/JS/image cache behaviour from deployed response headers

## Step 1 — browser automation
- [ ] TODO — add Playwright project and CI workflow for every HTML page and four viewport sizes
- [ ] TODO — capture console errors, broken images, failed local assets, homepage hero clipping/overlap and date-pill gap
- [ ] TODO — upload all screenshots as workflow artifacts
- [ ] UNVERIFIED — first GitHub Actions / Playwright run and screenshots

## Phase 1 — stale cache/session
- [ ] TODO — audit all frontend files and add one shared version query to local CSS/JS/image references
- [ ] TODO — add and run version bump script
- [ ] DONE — `frontend/js/pwa.js` contains legacy service-worker unregister/cache cleanup logic (coverage on every page UNVERIFIED)
- [ ] TODO — remove service-worker registration from every page and verify cleanup script is loaded everywhere
- [ ] TODO — unify JWT/session storage key and Remember me behaviour across pages
- [ ] TODO — prevent authentication header flicker on first paint
- [ ] TODO — add Render warm-up request and retrying “Connecting to server...” state

## Phase 2 — homepage
- [ ] TODO — remove unwanted decorative elements from markup and CSS, not just hide them
- [ ] TODO — fix greeting/date pill spacing and mobile wrapping
- [ ] TODO — verify emblems, nav overlap and proportions at 1366x768, 1536x864 and 1920x1080

## Phase 3 — shared design system
- [ ] TODO — audit existing shared CSS/JS and assets before editing
- [ ] TODO — consistent header, role-specific nav, footer, side panels and accessible controls on every non-home page
- [ ] TODO — correct citizen logo contrast, dealer emblem and right-edge background clipping

## Phase 4 — target pages
- [ ] TODO — citizen login (PDF p12)
- [ ] TODO — admin dashboard with ADMIN navigation (PDF p13)
- [ ] TODO — dealer login (PDF p14)
- [ ] TODO — dealer registration (PDF p15)
- [ ] TODO — citizen dashboard with citizen-only actions (PDF p16)
- [ ] TODO — My Family with real backend data and rice quota (PDF p17)

## Phase 5 — all remaining pages
- [ ] TODO — enumerate every `frontend/pages/*.html` file from the repository tree and add one checklist entry per file
- [ ] TODO — apply shared design system while preserving APIs, authentication, OTP, emails and translations
- [ ] UNVERIFIED — all-page visual acceptance against the supplied PDF

## Evidence log
- Initial PR #10 review: `frontend/index.html` already versions several CSS/JS references; `frontend/js/pwa.js` exists; `frontend/_headers` is present in the PR diff but is not a Render headers configuration.
- No Playwright run or screenshot artifact is claimed until a completed GitHub Actions run is inspected.
