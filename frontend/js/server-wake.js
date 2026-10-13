/* Non-blocking Render wake-up helper. Safe to include on public and protected pages. */
(function () {
  "use strict";
  var backend = (window.RATION_API_BASE_URL || "https://ration-portal-backend.onrender.com").replace(/\/$/, "");
  var shown = false;
  var banner;
  var started = Date.now();
  function showBanner() {
    if (banner || !document.body) return;
    banner = document.createElement("aside");
    banner.id = "rationServerWakeBanner";
    banner.setAttribute("role", "status");
    banner.setAttribute("aria-live", "polite");
    banner.style.cssText = "position:fixed;z-index:2147483000;left:12px;right:12px;bottom:12px;max-width:720px;margin:auto;padding:13px 16px;border:1px solid #d7e3f2;border-left:5px solid #ff9933;border-radius:12px;background:#fff;color:#17365f;box-shadow:0 8px 28px #082c5c22;font:14px/1.5 Arial,sans-serif";
    var text = document.createElement("span");
    text.textContent = "The server is starting up. This can take a minute on the first visit. The page remains available while it wakes.";
    banner.appendChild(text);
    var retry = document.createElement("button");
    retry.type = "button";
    retry.textContent = "Retry";
    retry.style.cssText = "margin-left:12px;padding:6px 12px;border:0;border-radius:7px;background:#082c5c;color:white;font-weight:700;cursor:pointer";
    retry.addEventListener("click", wake);
    banner.appendChild(retry);
    document.body.appendChild(banner);
    shown = true;
  }
  function hideBanner() {
    if (banner) banner.remove();
    banner = null;
    shown = false;
  }
  async function wake() {
    try {
      var controller = typeof AbortController === "function" ? new AbortController() : null;
      var timeout = controller ? setTimeout(function () { controller.abort(); }, 90000) : null;
      var options = { method: "GET", mode: "cors", cache: "no-store", keepalive: true, credentials: "omit", headers: { "Accept": "application/json" } };
      if (controller) options.signal = controller.signal;
      var response = await fetch(backend + "/api/health", options);
      if (timeout) clearTimeout(timeout);
      if (!response.ok) throw new Error("Health endpoint returned " + response.status);
      hideBanner();
    } catch (_) {
      if (Date.now() - started > 1800 || shown) showBanner();
    }
  }
  function begin() {
    if (Date.now() - started > 1800) showBanner();
    wake();
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", begin, { once: true });
  } else begin();
})();
