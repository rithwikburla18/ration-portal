(function () {
    "use strict";

    /*
     * ============================================================
     * RATION PORTAL
     * Authentication + Navigation + Password Security Guard
     * ============================================================
     */

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const ROLE_KEY = "rationPortalRole";
    const AUTHENTICATED_KEY = "ration_authenticated";
    const REDIRECT_KEY = "rationPortalRedirect";

    const LEGACY_TOKEN_KEY = "ration_token";
    const LEGACY_USER_KEY = "ration_user";
    const LEGACY_ROLE_KEY = "ration_role";

    const BACKEND_URL =
        "https://ration-portal-backend.onrender.com";

    const PUBLIC_PAGES = [
        "index.html",
        "login.html",
        "register.html",
        "register-password.html",
        "forgot-password.html",
        "reset-password.html",
        "about.html",
        "contact.html",
        "privacy.html",
        "terms.html"
    ];

    const PROTECTED_SERVICE_FILES = ["citizen-dashboard.html","ration-card.html","e-ration-card.html","family.html","distribution.html","onorc.html","apply-ration-card.html","applications.html","application-status.html","grievance.html","grievance-dashboard.html","grievance-status.html","transparency.html"]; const APPROVAL_REQUIRED_SERVICE_FILES = ["ration-card.html","e-ration-card.html","family.html","distribution.html","onorc.html"];

    /*
     * ============================================================
     * SESSION HELPERS
     * ============================================================
     */

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            return JSON.parse(
                localStorage.getItem(USER_KEY) || "null"
            );
        } catch (_) {
            return null;
        }
    }

    function getRole() {
        return (
            localStorage.getItem(ROLE_KEY) ||
            getUser()?.role ||
            ""
        );
    }

    function saveSession(token, user) {
        if (!token) {
            clearSession();
            return;
        }

        localStorage.setItem(TOKEN_KEY, token);

        if (user) {
            localStorage.setItem(
                USER_KEY,
                JSON.stringify(user)
            );

            if (user.role) {
                localStorage.setItem(
                    ROLE_KEY,
                    user.role
                );
            }
        }

        localStorage.setItem(
            AUTHENTICATED_KEY,
            "true"
        );
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem(ROLE_KEY);
        localStorage.removeItem(AUTHENTICATED_KEY);
        localStorage.removeItem(REDIRECT_KEY);

        /*
         * Remove old session keys so an old implementation
         * can never silently restore a previous session.
         */
        localStorage.removeItem(LEGACY_TOKEN_KEY);
        localStorage.removeItem(LEGACY_USER_KEY);
        localStorage.removeItem(LEGACY_ROLE_KEY);
    }

    /*
     * ============================================================
     * JWT VALIDATION
     * ============================================================
     */

    function decodeJwtPayload(token) {
        if (!token) {
            return null;
        }

        try {
            const parts = token.split(".");

            if (parts.length !== 3) {
                return null;
            }

            const base64 = parts[1]
                .replace(/-/g, "+")
                .replace(/_/g, "/");

            const json = decodeURIComponent(
                atob(base64)
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

            return JSON.parse(json);
        } catch (_) {
            return null;
        }
    }

    function isTokenExpired(token) {
        const payload = decodeJwtPayload(token);

        if (!payload || !payload.exp) {
            return true;
        }

        return (
            Number(payload.exp) * 1000 <=
            Date.now()
        );
    }

    /*
     * ============================================================
     * SERVER SESSION VALIDATION
     * ============================================================
     */

    async function validateSession() {
        const token = getToken();

        if (!token) {
            return null;
        }

        if (isTokenExpired(token)) {
            clearSession();
            return null;
        }

        try {
            const response = await fetch(
                BACKEND_URL + "/api/auth/me",
                {
                    method: "GET",
                    headers: {
                        Authorization:
                            "Bearer " + token,
                        Accept: "application/json"
                    }
                }
            );

            if (!response.ok) {
                clearSession();
                return null;
            }

            const user = await response.json();

            if (!user || !user.email) {
                clearSession();
                return null;
            }

            localStorage.setItem(
                USER_KEY,
                JSON.stringify(user)
            );

            if (user.role) {
                localStorage.setItem(
                    ROLE_KEY,
                    user.role
                );
            }

            localStorage.setItem(
                AUTHENTICATED_KEY,
                "true"
            );

            return user;
        } catch (_) {
            /*
             * Do not silently authenticate from stale local
             * data when the server cannot validate the token.
             */
            clearSession();
            return null;
        }
    }

    /*
     * ============================================================
     * AUTHENTICATED API REQUEST
     * ============================================================
     */

    async function authenticatedFetch(
        url,
        options
    ) {
        const requestOptions = {
            ...(options || {})
        };

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

        headers.set(
            "Accept",
            "application/json"
        );

        if (
            requestOptions.body &&
            !headers.has("Content-Type")
        ) {
            headers.set(
                "Content-Type",
                "application/json"
            );
        }

        requestOptions.headers = headers;

        const response = await fetch(
            url,
            requestOptions
        );

        if (
            response.status === 401 ||
            response.status === 403
        ) {
            clearSession();
        }

        return response;
    }

    /*
     * ============================================================
     * PAGE HELPERS
     * ============================================================
     */

    function currentFile() {
        return (
            location.pathname
                .split("/")
                .pop() ||
            "index.html"
        ).toLowerCase();
    }

    function isPublicAuthPage() {
        return PUBLIC_PAGES.includes(
            currentFile()
        );
    }

    function isProtectedServicePage() { return PROTECTED_SERVICE_FILES.includes(currentFile()); } function isApprovalRequiredPage() { return APPROVAL_REQUIRED_SERVICE_FILES.includes(currentFile()); }

    if (
        document.readyState ===
        "loading"
    ) {
        document.addEventListener(
            "DOMContentLoaded",
            initializeAuthGuard,
            {
                once: true
            }
        );
    } else {
        initializeAuthGuard();
    }
})();
