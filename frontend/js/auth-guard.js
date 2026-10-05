/* =========================================================
   RATION PORTAL - AUTHENTICATION GUARD
   Stage 19 - Production Security Hardening
   ========================================================= */

(function () {
    "use strict";

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const REDIRECT_KEY = "rationPortalRedirect";
    const BACKEND_URL = "https://ration-portal-backend.onrender.com";

    const PUBLIC_PAGES = [
        "",
        "/",
        "/index.html",
        "/pages/login.html",
        "/pages/register.html",
        "/pages/forgot-password.html",
        "/pages/reset-password.html",
        "/pages/register-password.html",
        "/pages/admin-login.html"
    ];

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            const raw = localStorage.getItem(USER_KEY);
            return raw ? JSON.parse(raw) : null;
        } catch (error) {
            return null;
        }
    }

    function saveUser(user) {
        if (!user) {
            localStorage.removeItem(USER_KEY);
            return;
        }
        localStorage.setItem(USER_KEY, JSON.stringify(user));
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem("rationPortalUserRole");
        localStorage.removeItem("rationPortalAdminEmail");
    }

    function getCurrentPage() {
        return window.location.pathname.replace(/\\/g, "/").toLowerCase();
    }

    function isPublicPage() {
        const page = getCurrentPage();
        return PUBLIC_PAGES.some(function (item) {
            return page === item || page.endsWith(item);
        });
    }

    function isAdminPage() {
        const page = getCurrentPage();
        return page.includes("/admin-portal.html") || page.includes("/admin/");
    }

    function getLoginUrl() {
        const page = getCurrentPage();

        if (isAdminPage()) {
            return "admin-login.html";
        }

        if (page.includes("/pages/")) {
            return "login.html";
        }

        return "pages/login.html";
    }

    function redirectToLogin() {
        if (isPublicPage()) {
            return;
        }

        sessionStorage.setItem(
            REDIRECT_KEY,
            window.location.href
        );

        window.location.href = getLoginUrl();
    }

    function redirectForRole(user) {
        if (!user) {
            redirectToLogin();
            return;
        }

        const role = String(user.role || "").toUpperCase();

        if (isAdminPage() && role !== "ADMIN") {
            clearSession();
            window.location.href = "login.html";
            return;
        }
    }

    function isJwtFormat(token) {
        return Boolean(token && token.split(".").length === 3);
    }

    function getJwtPayload(token) {
        if (!isJwtFormat(token)) {
            return null;
        }

        try {
            const part = token.split(".")[1];
            const normalized = part
                .replace(/-/g, "+")
                .replace(/_/g, "/");

            const padded =
                normalized +
                "=".repeat((4 - normalized.length % 4) % 4);

            return JSON.parse(
                decodeURIComponent(
                    atob(padded)
                        .split("")
                        .map(function (character) {
                            return "%" +
                                ("00" +
                                    character.charCodeAt(0).toString(16)
                                ).slice(-2);
                        })
                        .join("")
                )
            );
        } catch (error) {
            return null;
        }
    }

    function isTokenExpired(token) {
        const payload = getJwtPayload(token);

        if (!payload || !payload.exp) {
            return false;
        }

        return Number(payload.exp) <=
            Math.floor(Date.now() / 1000);
    }

    function validateLocalSession() {
        const token = getToken();

        if (!token || !isJwtFormat(token)) {
            return false;
        }

        if (isTokenExpired(token)) {
            clearSession();
            return false;
        }

        return true;
    }

    async function validateBackendSession() {
        const token = getToken();

        if (!token) {
            return null;
        }

        try {
            const response = await fetch(
                BACKEND_URL + "/api/auth/me",
                {
                    method: "GET",
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Accept": "application/json"
                    }
                }
            );

            if (!response.ok) {
                clearSession();
                return null;
            }

            const user = await response.json();

            if (
                !user ||
                String(user.status || "").toUpperCase() !== "ACTIVE"
            ) {
                clearSession();
                return null;
            }

            saveUser(user);
            localStorage.setItem(
                "rationPortalUserRole",
                String(user.role || "").toUpperCase()
            );

            if (user.role &&
                String(user.role).toUpperCase() === "ADMIN") {
                localStorage.setItem(
                    "rationPortalAdminEmail",
                    user.email || ""
                );
            }

            return user;
        } catch (error) {
            console.error(
                "Backend authentication validation failed:",
                error
            );
            return null;
        }
    }

    async function requireAuthentication() {
        if (!validateLocalSession()) {
            clearSession();
            redirectToLogin();
            return false;
        }

        const user = await validateBackendSession();

        if (!user) {
            redirectToLogin();
            return false;
        }

        redirectForRole(user);

        if (isAdminPage() &&
            String(user.role || "").toUpperCase() !== "ADMIN") {
            return false;
        }

        return true;
    }

    async function authenticatedFetch(url, options) {
        const requestOptions = options || {};
        const headers = new Headers(
            requestOptions.headers || {}
        );

        const token = getToken();

        if (token) {
            headers.set(
                "Authorization",
                "Bearer " + token
            );
        }

        if (
            requestOptions.body &&
            typeof requestOptions.body === "object" &&
            !(requestOptions.body instanceof FormData) &&
            !headers.has("Content-Type")
        ) {
            headers.set(
                "Content-Type",
                "application/json"
            );
        }

        const response = await fetch(
            url,
            {
                ...requestOptions,
                headers: headers
            }
        );

        if (response.status === 401) {
            clearSession();

            if (!isPublicPage()) {
                redirectToLogin();
            }
        } else if (response.status === 403) {
            console.warn(
                "Access denied by backend authorization."
            );

            if (isAdminPage()) {
                window.location.href = "login.html";
            }
        }

        return response;
    }

    function logout() {
        clearSession();
        sessionStorage.removeItem(REDIRECT_KEY);

        if (isAdminPage()) {
            window.location.href = "admin-login.html";
        } else if (getCurrentPage().includes("/pages/")) {
            window.location.href = "login.html";
        } else {
            window.location.href = "pages/login.html";
        }
    }

    function getLoginRedirect() {
        const redirect =
            sessionStorage.getItem(REDIRECT_KEY);

        sessionStorage.removeItem(REDIRECT_KEY);

        if (!redirect) {
            return null;
        }

        try {
            const parsed = new URL(
                redirect,
                window.location.origin
            );

            if (
                parsed.origin !==
                window.location.origin
            ) {
                return null;
            }

            return (
                parsed.pathname +
                parsed.search +
                parsed.hash
            );
        } catch (error) {
            return null;
        }
    }

    window.rationAuth = {
        getToken: getToken,
        getUser: getUser,
        saveUser: saveUser,
        clearSession: clearSession,
        logout: logout,
        requireAuthentication: requireAuthentication,
        validateLocalSession: validateLocalSession,
        validateBackendSession: validateBackendSession,
        authenticatedFetch: authenticatedFetch,
        getLoginRedirect: getLoginRedirect,
        isAuthenticated: function () {
            return Boolean(getToken());
        }
    };

    window.authGuard = window.rationAuth;

    async function initializeAuthGuard() {
        if (isPublicPage()) {
            return;
        }

        const authenticated =
            await requireAuthentication();

        if (!authenticated) {
            return;
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            initializeAuthGuard
        );
    } else {
        initializeAuthGuard();
    }

})();