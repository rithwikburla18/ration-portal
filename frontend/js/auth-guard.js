/* =========================================================
   RATION PORTAL - AUTHENTICATION GUARD
   File: frontend/js/auth-guard.js

   Purpose:
   - Protect authenticated pages
   - Attach JWT token to API requests
   - Handle expired sessions
   - Redirect unauthenticated citizens to login
   ========================================================= */

(function () {
    "use strict";

    /* =====================================================
       CONFIGURATION
       ===================================================== */

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const REDIRECT_KEY = "rationPortalRedirect";

    const PUBLIC_PAGES = [
        "",
        "/",
        "/index.html",
        "/pages/login.html",
        "/pages/register.html"
    ];

    /* =====================================================
       TOKEN HELPERS
       ===================================================== */

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            const rawUser =
                localStorage.getItem(USER_KEY);

            return rawUser
                ? JSON.parse(rawUser)
                : null;
        } catch (error) {
            console.error(
                "Unable to read stored user:",
                error
            );

            return null;
        }
    }

    function saveUser(user) {
        if (!user) {
            localStorage.removeItem(USER_KEY);
            return;
        }

        localStorage.setItem(
            USER_KEY,
            JSON.stringify(user)
        );
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
    }

    /* =====================================================
       PAGE HELPERS
       ===================================================== */

    function getCurrentPage() {
        return window.location.pathname
            .replace(/\\/g, "/")
            .toLowerCase();
    }

    function isPublicPage() {
        const currentPage =
            getCurrentPage();

        return PUBLIC_PAGES.some(
            function (page) {
                return (
                    currentPage === page ||
                    currentPage.endsWith(page)
                );
            }
        );
    }

    function getLoginUrl() {
        const currentPage =
            getCurrentPage();

        if (
            currentPage.includes("/pages/")
        ) {
            return "login.html";
        }

        return "pages/login.html";
    }

    /* =====================================================
       REDIRECT
       ===================================================== */

    function redirectToLogin() {
        if (isPublicPage()) {
            return;
        }

        const currentUrl =
            window.location.href;

        sessionStorage.setItem(
            REDIRECT_KEY,
            currentUrl
        );

        window.location.href =
            getLoginUrl();
    }

    /* =====================================================
       AUTHENTICATION CHECK
       ===================================================== */

    function requireAuthentication() {
        const token = getToken();

        if (!token) {
            redirectToLogin();
            return false;
        }

        return true;
    }

    /* =====================================================
       JWT BASIC VALIDATION
       ===================================================== */

    function isJwtFormat(token) {
        if (!token) {
            return false;
        }

        const parts =
            token.split(".");

        return parts.length === 3;
    }

    function getJwtPayload(token) {
        if (!isJwtFormat(token)) {
            return null;
        }

        try {
            const payload =
                token.split(".")[1];

            const normalized =
                payload
                    .replace(/-/g, "+")
                    .replace(/_/g, "/");

            const decoded =
                decodeURIComponent(
                    atob(normalized)
                        .split("")
                        .map(function (character) {
                            return (
                                "%" +
                                (
                                    "00" +
                                    character
                                        .charCodeAt(0)
                                        .toString(16)
                                ).slice(-2)
                            );
                        })
                        .join("")
                );

            return JSON.parse(decoded);
        } catch (error) {
            console.warn(
                "Unable to decode JWT payload."
            );

            return null;
        }
    }

    function isTokenExpired(token) {
        const payload =
            getJwtPayload(token);

        /*
         * If this is not a standard JWT or the
         * payload cannot be decoded, allow the
         * backend to perform the final validation.
         */
        if (!payload) {
            return false;
        }

        if (!payload.exp) {
            return false;
        }

        const currentTime =
            Math.floor(
                Date.now() / 1000
            );

        return (
            Number(payload.exp) <=
            currentTime
        );
    }

    /* =====================================================
       SESSION VALIDATION
       ===================================================== */

    function validateLocalSession() {
        const token =
            getToken();

        if (!token) {
            return false;
        }

        if (isTokenExpired(token)) {
            clearSession();
            return false;
        }

        return true;
    }

    /* =====================================================
       AUTHENTICATED FETCH
       ===================================================== */

    async function authenticatedFetch(
        url,
        options
    ) {
        const requestOptions =
            options || {};

        const headers =
            new Headers(
                requestOptions.headers || {}
            );

        const token =
            getToken();

        if (token) {
            headers.set(
                "Authorization",
                "Bearer " + token
            );
        }

        if (
            requestOptions.body &&
            typeof requestOptions.body ===
                "object" &&
            !(requestOptions.body instanceof FormData) &&
            !headers.has("Content-Type")
        ) {
            headers.set(
                "Content-Type",
                "application/json"
            );
        }

        const response =
            await fetch(
                url,
                {
                    ...requestOptions,
                    headers: headers
                }
            );

        /*
         * A 401 means the backend rejected
         * the current authentication.
         */
        if (response.status === 401) {
            clearSession();

            if (!isPublicPage()) {
                sessionStorage.setItem(
                    REDIRECT_KEY,
                    window.location.href
                );

                window.location.href =
                    getLoginUrl();
            }
        }

        return response;
    }

    /* =====================================================
       LOGOUT
       ===================================================== */

    function logout() {
        clearSession();

        sessionStorage.removeItem(
            REDIRECT_KEY
        );

        const currentPage =
            getCurrentPage();

        if (
            currentPage.includes("/pages/")
        ) {
            window.location.href =
                "login.html";
        } else {
            window.location.href =
                "pages/login.html";
        }
    }

    /* =====================================================
       REDIRECT AFTER LOGIN
       ===================================================== */

    function getLoginRedirect() {
        const redirect =
            sessionStorage.getItem(
                REDIRECT_KEY
            );

        sessionStorage.removeItem(
            REDIRECT_KEY
        );

        if (!redirect) {
            return null;
        }

        /*
         * Only allow redirects back to the
         * same origin. This prevents an
         * open-redirect vulnerability.
         */
        try {
            const parsed =
                new URL(
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

    /* =====================================================
       PUBLIC API
       ===================================================== */

    window.rationAuth = {
        getToken: getToken,
        getUser: getUser,
        saveUser: saveUser,
        clearSession: clearSession,
        logout: logout,
        requireAuthentication:
            requireAuthentication,
        validateLocalSession:
            validateLocalSession,
        authenticatedFetch:
            authenticatedFetch,
        getLoginRedirect:
            getLoginRedirect,
        isAuthenticated:
            function () {
                return Boolean(
                    getToken()
                );
            }
    };

    /*
     * Compatibility with the existing
     * application code.
     */
    window.authGuard = window.rationAuth;

    /* =====================================================
       INITIAL PAGE GUARD
       ===================================================== */

    function initializeAuthGuard() {
        if (isPublicPage()) {
            return;
        }

        if (!validateLocalSession()) {
            redirectToLogin();
        }
    }

    if (
        document.readyState ===
        "loading"
    ) {
        document.addEventListener(
            "DOMContentLoaded",
            initializeAuthGuard
        );
    } else {
        initializeAuthGuard();
    }

})();