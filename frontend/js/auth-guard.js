(function () {
    "use strict";
    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const ROLE_KEY = "rationPortalRole";
    const AUTHENTICATED_KEY = "ration_authenticated";
    const LEGACY_KEYS = ["ration_token", "ration_user", "ration_role"];
    const BACKEND_URL = "https://ration-portal-backend.onrender.com";
    const PROTECTED_SERVICE_FILES = [
        "citizen-dashboard.html", "ration-card.html", "e-ration-card.html",
        "family.html", "distribution.html", "onorc.html", "apply-ration-card.html",
        "applications.html", "application-status.html", "grievance.html",
        "grievance-dashboard.html", "grievance-status.html", "transparency.html",
        "fair-price-shop.html"
    ];
    const APPROVAL_REQUIRED_SERVICE_FILES = [
        "ration-card.html", "e-ration-card.html", "family.html", "distribution.html",
        "onorc.html", "grievance.html", "grievance-dashboard.html", "grievance-status.html",
        "transparency.html", "fair-price-shop.html"
    ];

    function currentFile() {
        return (window.location.pathname.split("/").pop() || "index.html").toLowerCase();
    }
    function getToken() { return localStorage.getItem(TOKEN_KEY) || ""; }
    function clearSession() {
        [TOKEN_KEY, USER_KEY, ROLE_KEY, AUTHENTICATED_KEY, ...LEGACY_KEYS, "rationPortalRedirect"]
            .forEach(function (key) { localStorage.removeItem(key); });
    }
    async function validateSession(token) {
        const response = await fetch(BACKEND_URL + "/api/auth/me", {
            method: "GET",
            headers: { "Authorization": "Bearer " + token, "Accept": "application/json" },
            cache: "no-store"
        });
        if (!response.ok) return null;
        const user = await response.json();
        if (!user || !user.email) return null;
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        if (user.role) localStorage.setItem(ROLE_KEY, user.role);
        localStorage.setItem(AUTHENTICATED_KEY, "true");
        return user;
    }
    async function hasApprovedCard(token) {
        try {
            const response = await fetch(BACKEND_URL + "/api/ration-cards/my", {
                method: "GET",
                headers: { "Authorization": "Bearer " + token, "Accept": "application/json" },
                cache: "no-store"
            });
            if (!response.ok) return false;
            const result = await response.json();
            return !!result && (result.approved === true ||
                ["APPROVED", "ACTIVE"].includes(String(result.status || "").toUpperCase()));
        } catch (_) {
            return false;
        }
    }
    function showApprovalRequired() {
        if (document.getElementById("rationApprovalGate")) return;
        const style = document.createElement("style");
        style.id = "rationApprovalGateStyles";
        style.textContent = "#rationApprovalGate{position:fixed;inset:0;z-index:2147483000;display:flex;align-items:center;justify-content:center;padding:22px;background:rgba(5,22,48,.76);backdrop-filter:blur(7px);font-family:Arial,Helvetica,sans-serif}"+
            "#rationApprovalGate .gate-card{width:min(560px,100%);background:#fff;border-radius:20px;overflow:hidden;box-shadow:0 25px 90px rgba(0,0,0,.32);text-align:center;border:1px solid #dce6f2}"+
            "#rationApprovalGate .gate-tricolor{height:7px;background:linear-gradient(90deg,#ff9933 0 33.33%,#fff 33.33% 66.66%,#138808 66.66% 100%)}"+
            "#rationApprovalGate .gate-content{padding:34px 30px 30px}"+
            "#rationApprovalGate .gate-icon{width:72px;height:72px;margin:0 auto 18px;border-radius:50%;display:grid;place-items:center;background:#fff4e8;color:#a95400;font-size:34px;border:1px solid #ffd8b0}"+
            "#rationApprovalGate h1{margin:0 0 12px;color:#082f65;font-size:clamp(22px,4vw,29px);line-height:1.2}"+
            "#rationApprovalGate p{margin:0 auto 24px;color:#4d5d72;font-size:16px;line-height:1.65;max-width:440px}"+
            "#rationApprovalGate .gate-note{display:block;margin-top:9px;color:#64748b;font-size:13px}"+
            "#rationApprovalGate .gate-close{border:0;border-radius:10px;background:linear-gradient(135deg,#0c63c7,#043b82);color:#fff;padding:13px 22px;font-weight:700;font-size:15px;cursor:pointer;box-shadow:0 7px 18px rgba(5,73,151,.22)}"+
            "#rationApprovalGate .gate-close:focus-visible{outline:3px solid #ff9933;outline-offset:3px}";
        document.head.appendChild(style);
        const overlay = document.createElement("div");
        overlay.id = "rationApprovalGate";
        overlay.setAttribute("role", "dialog");
        overlay.setAttribute("aria-modal", "true");
        overlay.setAttribute("aria-labelledby", "rationApprovalGateTitle");
        overlay.innerHTML = '<section class="gate-card"><div class="gate-tricolor"></div><div class="gate-content"><div class="gate-icon" aria-hidden="true">🔒</div><h1 id="rationApprovalGateTitle">Administrator Approval Required</h1><p>Your ration card has not yet been approved by the administrator. You cannot use this service until your ration card is approved.<span class="gate-note">Please check your application status or contact the ration portal administrator.</span></p><button type="button" class="gate-close" id="rationApprovalGateClose">Close &amp; Return to Citizen Dashboard</button></div></section>';
        document.body.appendChild(overlay);
        document.body.style.overflow = "hidden";
        document.getElementById("rationApprovalGateClose").addEventListener("click", function () {
            window.location.replace("./citizen-dashboard.html");
        });
    }
    async function initializeAuthGuard() {
        const file = currentFile();
        if (!PROTECTED_SERVICE_FILES.includes(file)) return;
        const token = getToken();
        if (!token) {
            clearSession();
            window.location.replace("./login.html");
            return;
        }
        let user;
        try { user = await validateSession(token); } catch (_) { user = null; }
        if (!user) {
            clearSession();
            window.location.replace("./login.html");
            return;
        }
        const role = String(user.role || localStorage.getItem(ROLE_KEY) || "").toUpperCase();
        if (role === "ADMIN") return;
        if (APPROVAL_REQUIRED_SERVICE_FILES.includes(file) && !(await hasApprovedCard(token))) {
            showApprovalRequired();
        }
    }
    async function authenticatedFetch(url, options) {
        const requestOptions = { ...(options || {}) };
        const headers = new Headers(requestOptions.headers || {});
        const token = getToken();
        if (token) headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/json");
        if (requestOptions.body && !headers.has("Content-Type")) headers.set("Content-Type", "application/json");
        requestOptions.headers = headers;
        const response = await fetch(url, requestOptions);
        if (response.status === 401) clearSession();
        return response;
    }
    window.rationAuth = Object.freeze({
        authenticatedFetch: authenticatedFetch,
        logout: async function () { clearSession(); }
    });
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initializeAuthGuard, { once: true });
    } else {
        initializeAuthGuard();
    }
})();