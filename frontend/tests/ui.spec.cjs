const { test, expect } = require("@playwright/test");
const fs = require("node:fs");
const path = require("node:path");

const root = path.resolve(__dirname, "..");
const pagesDir = path.join(root, "pages");
const pages = ["index.html", ...fs.readdirSync(pagesDir, { withFileTypes: true })
  .filter((entry) => entry.isFile() && entry.name.endsWith(".html"))
  .map((entry) => "pages/" + entry.name).sort()];
const viewports = [
  { width: 1366, height: 768 },
  { width: 1920, height: 1080 },
  { width: 768, height: 1024 },
  { width: 390, height: 844 }
];
const screenshotDir = path.join(__dirname, "artifacts");
fs.mkdirSync(screenshotDir, { recursive: true });

for (const pagePath of pages) {
  for (const viewport of viewports) {
    test(`${pagePath} at ${viewport.width}x${viewport.height}`, async ({ page }, testInfo) => {
      await page.setViewportSize(viewport);
      const consoleErrors = [];
      const pageErrors = [];
      const localAssetFailures = [];
      page.on("console", (message) => {
        if (message.type() === "error") consoleErrors.push(message.text());
      });
      page.on("pageerror", (error) => pageErrors.push(error.message));
      page.on("requestfailed", (request) => {
        const url = request.url();
        if (url.startsWith(testInfo.project?.use?.baseURL || process.env.BASE_URL || "http://127.0.0.1:4173") && request.resourceType() !== "document" && request.failure()?.errorText !== "net::ERR_ABORTED") {
          localAssetFailures.push(url + " :: " + (request.failure()?.errorText || "request failed"));
        }
      });
      // APIs may be unavailable in CI; mock only remote API requests, never local assets.
      await page.route(/^https?:\/\/(?!127\.0\.0\.1:4173|localhost:4173).*(?:onrender\.com|api\/)/, async (route) => {
        const request = route.request();
        const url = request.url();
        if (/\.js(?:\?|$)|\.css(?:\?|$)|\.(?:png|jpe?g|svg|webp|ico)(?:\?|$)/i.test(url)) return route.continue();
        const body = /metrics|transparency|dashboard/i.test(url)
          ? { totalRationCards: 0, totalFamilyMembers: 0, totalDistributionRecords: 0, totalApplications: 0, totalGrievances: 0, familyMembers: [], recentDistribution: [] }
          : { message: "CI API mock", status: "OK", content: [] };
        await route.fulfill({ status: 200, contentType: "application/json", body: JSON.stringify(body) });
      });
      const url = "/" + pagePath.replace(/^\//, "");
      const response = await page.goto(url, { waitUntil: "networkidle", timeout: 30000 });
      expect(response, "navigation should return a response").not.toBeNull();
      expect(response.status(), "page should load successfully").toBeLessThan(400);
      await page.waitForTimeout(150);
      const registrations = await page.evaluate(async () => {
        if (!("serviceWorker" in navigator)) return [];
        return (await navigator.serviceWorker.getRegistrations()).map((registration) => registration.scope);
      });
      expect(registrations, "no service worker should remain registered").toEqual([]);
      const unversionedLocalRefs = await page.locator("link[href],script[src],img[src],source[src],video[poster]").evaluateAll((nodes) => nodes
        .map((node) => ({ tag: node.tagName, attribute: node.hasAttribute("href") ? "href" : node.hasAttribute("poster") ? "poster" : "src", value: node.getAttribute(node.hasAttribute("href") ? "href" : node.hasAttribute("poster") ? "poster" : "src") }))
        .filter((item) => item.value && !/^(?:[a-z]+:|\\/\\/|data:|#)/i.test(item.value) && /\\.(?:css|js|png|jpe?g|svg|webp|gif|ico|woff2?)(?:[?#]|$)/i.test(item.value) && !/[?&]v=[^&]+/.test(item.value))
        .map((item) => item.tag + "[" + item.attribute + "] " + item.value));
      expect(unversionedLocalRefs, "every local CSS/JS/image reference needs ?v=").toEqual([]);
      const images = await page.locator("img").evaluateAll((nodes) => nodes.map((img) => ({
        src: img.currentSrc || img.src, broken: !img.complete || img.naturalWidth === 0
      })).filter((img) => img.broken));
      expect(images, "broken images").toEqual([]);
      expect(localAssetFailures, "failed local requests").toEqual([]);
      if (pagePath === "index.html") {
        const hero = page.locator(".rp-hero").first();
        if (await hero.count()) {
          const boxes = await page.locator(".rp-hero *").evaluateAll((nodes) => nodes
            .filter((node) => /emblem|pillar|flag|corner/i.test((node.className?.baseVal || node.className || "") + " " + (node.id || "")))
            .map((node) => {
              const r = node.getBoundingClientRect();
              return { name: node.className?.baseVal || node.className || node.id, left: r.left, right: r.right, top: r.top, bottom: r.bottom, width: r.width, height: r.height };
            }));
          const nav = await page.locator(".rp-nav-wrap, nav").first().boundingBox().catch(() => null);
          for (const box of boxes) {
            expect(box.width, box.name + " width").toBeGreaterThan(0);
            expect(box.left, box.name + " left clipping").toBeGreaterThanOrEqual(-1);
            expect(box.right, box.name + " right clipping").toBeLessThanOrEqual(viewport.width + 1);
            if (nav && /corner|emblem/i.test(box.name)) expect(box.bottom, box.name + " should not be covered by nav").toBeLessThanOrEqual(nav.y + nav.height + 2);
          }
          const datePill = page.locator(".rp-nav-right, .rp-nav-date").first();
          if (await datePill.count()) {
            const parent = await datePill.evaluate((el) => {
              const r = el.parentElement.getBoundingClientRect();
              const c = el.getBoundingClientRect();
              return { parentRight: r.right, pillRight: c.right, gap: r.right - c.right };
            });
            expect(parent.gap, "empty gap to the right of date/time pill").toBeLessThan(48);
          }
        }
      }
      await page.screenshot({
        path: path.join(screenshotDir, pagePath.replaceAll("/", "__").replace(".html", "") + "-" + viewport.width + "x" + viewport.height + ".png"),
        fullPage: true
      });
      expect(consoleErrors, "browser console errors").toEqual([]);
      expect(pageErrors, "uncaught page errors").toEqual([]);
    });
  }
}
