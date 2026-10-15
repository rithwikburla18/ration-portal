# UI remediation progress

Branch: `feature/ui-final-fixes` (PR #10). Do not merge or deploy from this checklist.

Status meanings: **DONE** means a source change exists in this branch; **UNVERIFIED** means browser/CI evidence is not yet available; **TODO** means not yet audited or implemented. Update this file in every UI-remediation commit.

## Step 0 — hosting/cache
- [x] TODO — complete full repository tree inventory; connector returned changed paths but not a recursive tree listing
- [x] DONE — added `render.yaml` static frontend service and response headers (deployment schema/runtime still UNVERIFIED)
- [x] DONE — deleted `frontend/_headers`; response headers remain in `render.yaml`
- [ ] TODO — confirm HTML/CSS/JS/image cache behaviour from deployed response headers

## Step 1 — browser automation
- [x] DONE — added `.github/workflows/ui-check.yml`, `frontend/tests/package.json`, `frontend/tests/playwright.config.cjs`, and `frontend/tests/ui.spec.cjs` to enumerate HTML pages and four viewports at runtime
- [x] DONE — Playwright assertions added for console/page errors, broken images, failed local requests and homepage hero/date-pill layout (assertion accuracy UNVERIFIED)
- [x] DONE — workflow uploads screenshots, test-results and HTML report as artifacts when generated
- [ ] UNVERIFIED — rerun GitHub Actions / Playwright after fixing the missing npm cache lockfile path

## Phase 1 — stale cache/session
- [x] TODO — audit all frontend files and add one shared version query to local CSS/JS/image references
- [ ] TODO — run `node scripts/bump-version.js <version>` and inspect its diff; not run from the connected GitHub editor
- [ ] DONE — `frontend/js/pwa.js` contains legacy service-worker unregister/cache cleanup logic (coverage on every page UNVERIFIED)
- [ ] TODO — remove service-worker registration from every page and verify cleanup script is loaded everywhere
- [ ] TODO — unify JWT/session storage key and Remember me behaviour across pages
- [ ] TODO — prevent authentication header flicker on first paint
- [x] TODO — add Render warm-up request and retrying “Connecting to server...” state

## Phase 2 — homepage
- [x] TODO — remove unwanted decorative elements from markup and CSS, not just hide them
- [x] TODO — fix greeting/date pill spacing and mobile wrapping
- [ ] TODO — verify emblems, nav overlap and proportions at 1366x768, 1536x864 and 1920x1080

## Phase 3 — shared design system
- [x] TODO — audit existing shared CSS/JS and assets before editing
- [x] TODO — consistent header, role-specific nav, footer, side panels and accessible controls on every non-home page
- [ ] TODO — correct citizen logo contrast, dealer emblem and right-edge background clipping

## Phase 4 — target pages
- [ ] TODO — citizen login (PDF p12)
- [ ] TODO — admin dashboard with ADMIN navigation (PDF p13)
- [ ] TODO — dealer login (PDF p14)
- [ ] TODO — dealer registration (PDF p15)
- [ ] TODO — citizen dashboard with citizen-only actions (PDF p16)
- [ ] TODO — My Family with real backend data and rice quota (PDF p17)

## Phase 5 — all remaining pages
- [ ] TODO — enumerate every `frontend/pages/*.html` file from the repository tree and add one checklist entry per file (repository tree listing is not available from the current connector action)
- [ ] TODO — apply shared design system while preserving APIs, authentication, OTP, emails and translations
- [ ] UNVERIFIED — all-page visual acceptance against the supplied PDF

## Changes in this phase\n- DONE — `.github/workflows/ui-check.yml`\n- DONE — `frontend/tests/package.json`\n- DONE — `frontend/tests/playwright.config.cjs`\n- DONE — `frontend/tests/ui.spec.cjs`\n- DONE — `scripts/bump-version.js` (execution UNVERIFIED)\n- DONE — `render.yaml` (Render accepts/deploys this blueprint UNVERIFIED)\n\n## Evidence log
- Initial PR #10 review: `frontend/index.html` already versions several CSS/JS references; `frontend/js/pwa.js` exists; `frontend/_headers` is present in the PR diff but is not a Render headers configuration.
- Latest inspected UI browser run #108 failed before tests: `actions/setup-node@v4` could not resolve `frontend/tests/package-lock.json`; workflow cache config removed in commit `9933b1ae6e5d06076c9f60fffc63dc1220be8e26`. Awaiting rerun to verify Playwright and screenshots.
\n- Commit `01202461f131283921418f75e673931887797636`: created this checklist.\n- Commit `e9a86f5b74a9568f486097325cbaab80f0e40418`: added UI workflow.\n- Commit `5b54ecdb070c297adcd1fe573f711ec5e40fd997`: added all-page Playwright spec.\n- Commit `fc5fcfc03a7df1d4294017894325005519c557cc`: added Render blueprint.\n- Commit `b4c3e93e24a37df1d199827732a0384d35f9548f`: corrected workflow dependency installation.\n