(function () {
    "use strict";

    const TOKEN_KEY = "ration_token";
    const USER_KEY = "ration_user";
    const ROLE_KEY = "ration_role";
    const BACKEND_URL = "https://ration-portal-backend.onrender.com";

    const PUBLIC_PAGES = [
        "index.html",
        "login.html",
        "register.html",
        "forgot-password.html",
        "reset-password.html",
        "about.html",
        "contact.html",
        "privacy-policy.html",
        "terms.html"
    ];

    const PROTECTED_SERVICE_FILES = [
        "ration-card.html",
        "e-ration-card.html",
        "family.html",
        "distribution.html",
        "onorc.html",
        "apply-ration-card.html",
        "applications.html"
    ];

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            return JSON.parse(localStorage.getItem(USER_KEY) || "null");
        } catch (_) {
            return null;
        }
    }

    function getRole() {
        return localStorage.getItem(ROLE_KEY) || getUser()?.role || "";
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem(ROLE_KEY);
        localStorage.removeItem("ration_authenticated");
    }

    function isTokenExpired(token) {
        if (!token) return true;
        try {
            const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
            return !payload.exp || payload.exp * 1000 <= Date.now();
        } catch (_) {
            return true;
        }
    }

    async function validateSession() {
        const token = getToken();

        if (!token || isTokenExpired(token)) {
            clearSession();
            return null;
        }

        try {
            const response = await fetch(BACKEND_URL + "/api/auth/me", {
                headers: { Authorization: "Bearer " + token }
            });

            if (!response.ok) {
                clearSession();
                return null;
            }

            const user = await response.json();
            localStorage.setItem(USER_KEY, JSON.stringify(user));
            if (user.role) localStorage.setItem(ROLE_KEY, user.role);
            localStorage.setItem("ration_authenticated", "true");
            return user;
        } catch (_) {
            return getUser();
        }
    }

    async function authenticatedFetch(url, options = {}) {
        const token = getToken();
        const headers = new Headers(options.headers || {});

        if (token) {
            headers.set("Authorization", "Bearer " + token);
        }

        if (options.body && !headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }

        return fetch(url, { ...options, headers });
    }

    function currentFile() {
        return (location.pathname.split("/").pop() || "index.html").toLowerCase();
    }

    function isPublicAuthPage() {
        return PUBLIC_PAGES.includes(currentFile());
    }

    function isProtectedServicePage() {
        return PROTECTED_SERVICE_FILES.includes(currentFile());
    }

    async function hasApprovedCard() {
        const token = getToken();
        if (!token) return false;

        if (getRole().toUpperCase() === "ADMIN") return true;

        try {
            const response = await authenticatedFetch(BACKEND_URL + "/api/ration-cards/my");
            return response.ok;
        } catch (_) {
            return false;
        }
    }

    function redirectToLogin() {
        const target = location.pathname + location.search + location.hash;
        location.href = "login.html?returnUrl=" + encodeURIComponent(target);
    }

    async function requireAuthentication() {
        const user = await validateSession();

        if (!user) {
            redirectToLogin();
            return false;
        }

        if (isProtectedServicePage() && !(await hasApprovedCard())) {
            alert("Your ration card must be approved before you can use this service.");
            location.href = "index.html";
            return false;
        }

        return true;
    }

    function installPasswordToggles() {
        document.querySelectorAll("[data-password-toggle]").forEach(button => {
            if (button.dataset.bound === "true") return;
            button.dataset.bound = "true";

            button.addEventListener("click", function () {
                const targetId = button.getAttribute("data-password-toggle");
                const input = document.getElementById(targetId);
                if (!input) return;

                const showing = input.type === "text";
                input.type = showing ? "password" : "text";
                button.setAttribute("aria-label", showing ? "Show password" : "Hide password");
                button.textContent = showing ? "👁" : "🙈";
            });
        });
    }

    function installTermsCheckbox() {
        const page = currentFile();

        if (!["login.html", "register.html", "register-password.html", "reset-password.html"].includes(page)) {
            return;
        }

        const form = document.querySelector("form");
        if (!form || form.querySelector("#termsAgreement")) return;

        const wrapper = document.createElement("label");
        wrapper.style.display = "flex";
        wrapper.style.alignItems = "flex-start";
        wrapper.style.gap = "8px";
        wrapper.style.margin = "12px 0";
        wrapper.style.fontSize = "14px";

        wrapper.innerHTML =
            '<input id="termsAgreement" type="checkbox" required style="margin-top:3px">' +
            '<span>I agree to the <a href="terms.html" target="_blank" rel="noopener">Terms & Conditions</a>.</span>';

        const submit = form.querySelector('button[type="submit"], input[type="submit"]');
        if (submit) {
            form.insertBefore(wrapper, submit);
        } else {
            form.appendChild(wrapper);
        }

        form.addEventListener("submit", function (event) {
            const checkbox = document.getElementById("termsAgreement");
            if (checkbox && !checkbox.checked) {
                event.preventDefault();
                alert("Please accept the Terms & Conditions before continuing.");
                checkbox.focus();
            }
        }, true);
    }

    function updateProtectedLinks() {
        const token = getToken();
        const user = getUser();

        document.querySelectorAll("a[href]").forEach(link => {
            const href = link.getAttribute("href") || "";
            const file = href.split("?")[0].split("#")[0].split("/").pop().toLowerCase();

            if (!PROTECTED_SERVICE_FILES.includes(file)) return;

            if (!token || !user) {
                link.style.display = "none";
                return;
            }

            if (getRole().toUpperCase() === "ADMIN") {
                link.style.display = "";
                return;
            }

            hasApprovedCard().then(approved => {
                link.style.display = approved ? "" : "none";
            });
        });
    }

    function logout() {
        clearSession();
        location.href = "login.html";
    }

    window.rationAuth = {
        getToken,
        getUser,
        getRole,
        clearSession,
        validateSession,
        authenticatedFetch,
        hasApprovedCard,
        requireAuthentication,
        installPasswordToggles,
        installTermsCheckbox,
        logout,
        backendUrl: BACKEND_URL
    };

    document.addEventListener("DOMContentLoaded", async function () {
        installPasswordToggles();
        installTermsCheckbox();

        if (isPublicAuthPage()) {
            updateProtectedLinks();
            return;
        }

        await requireAuthentication();
        updateProtectedLinks();
    });
})();