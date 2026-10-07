/* =========================================================
   RATION PORTAL - AUTHENTICATION GUARD
   STAGE 11.1 - STRICT SESSION + NAVIGATION SECURITY
   ========================================================= */

(function () {
    "use strict";

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const ROLE_KEY = "rationPortalUserRole";
    const ADMIN_EMAIL_KEY = "rationPortalAdminEmail";
    const REDIRECT_KEY = "rationPortalRedirect";

    const BACKEND_URL =
        "https://ration-portal-backend.onrender.com";

    const PUBLIC_PAGES = [
        "/",
        "/index.html",
        "/pages/login.html",
        "/pages/register.html",
        "/pages/forgot-password.html",
        "/pages/reset-password.html",
        "/pages/register-password.html",
        "/pages/admin-login.html"
    ];

    function normalizePath(path) {
        let value = String(path || "")
            .replace(/\\/g, "/")
            .toLowerCase();

        if (value.length > 1 && value.endsWith("/")) {
            value = value.slice(0, -1);
        }

        return value;
    }

    function getCurrentPage() {
        return normalizePath(window.location.pathname);
    }

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

        const role = String(user.role || "")
            .trim()
            .toUpperCase();

        localStorage.setItem(ROLE_KEY, role);

        if (role === "ADMIN") {
            localStorage.setItem(
                ADMIN_EMAIL_KEY,
                user.email || ""
            );
        } else {
            localStorage.removeItem(ADMIN_EMAIL_KEY);
        }
    }

    function clearSession() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem(ROLE_KEY);
        localStorage.removeItem(ADMIN_EMAIL_KEY);
    }

    function isLoginPage() {
        return getCurrentPage().endsWith("/pages/login.html");
    }

    function isAdminLoginPage() {
        return getCurrentPage().endsWith("/pages/admin-login.html");
    }

    function isRegisterPage() {
        return getCurrentPage().endsWith("/pages/register.html");
    }

    function isRegisterPasswordPage() {
        return getCurrentPage()
            .endsWith("/pages/register-password.html");
    }

    function isResetPasswordPage() {
        return getCurrentPage()
            .endsWith("/pages/reset-password.html");
    }

    function isPublicPage() {
        const page = getCurrentPage();

        return PUBLIC_PAGES.some(function (item) {
            return page === normalizePath(item);
        });
    }

    function isAdminPage() {
        const page = getCurrentPage();

        return (
            page.endsWith("/pages/admin-portal.html") ||
            page.includes("/admin/")
        );
    }

    function getLoginUrl() {
        if (isAdminPage()) {
            return "admin-login.html";
        }

        if (getCurrentPage().includes("/pages/")) {
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

    function isJwtFormat(token) {
        return Boolean(
            token &&
            token.split(".").length === 3
        );
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
                "=".repeat(
                    (4 - normalized.length % 4) % 4
                );

            return JSON.parse(
                decodeURIComponent(
                    atob(padded)
                        .split("")
                        .map(function (character) {
                            return "%" +
                                (
                                    "00" +
                                    character
                                        .charCodeAt(0)
                                        .toString(16)
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
                String(user.status || "")
                    .toUpperCase() !== "ACTIVE"
            ) {
                clearSession();
                return null;
            }

            saveUser(user);

            return user;
        } catch (error) {
            console.error(
                "Backend authentication validation failed:",
                error
            );

            /*
             * IMPORTANT:
             * Do not destroy a valid local JWT simply because
             * the backend temporarily cannot be reached.
             *
             * The next authenticated API request will still
             * enforce backend authorization.
             */
            return getUser();
        }
    }

    function redirectForRole(user) {
        if (!user) {
            redirectToLogin();
            return false;
        }

        const role = String(user.role || "")
            .trim()
            .toUpperCase();

        if (isAdminPage() && role !== "ADMIN") {
            clearSession();
            window.location.href = "admin-login.html";
            return false;
        }

        return true;
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

        return redirectForRole(user);
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
        }

        if (response.status === 403) {
            console.warn(
                "Access denied by backend authorization."
            );

            if (isAdminPage()) {
                clearSession();
                window.location.href =
                    "admin-login.html";
            }
        }

        return response;
    }

    function logout() {
        clearSession();

        sessionStorage.removeItem(
            REDIRECT_KEY
        );

        if (isAdminPage()) {
            window.location.href =
                "admin-login.html";
            return;
        }

        if (getCurrentPage().includes("/pages/")) {
            window.location.href =
                "login.html";
            return;
        }

        window.location.href =
            "pages/login.html";
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

    /*
     * =====================================================
     * PASSWORD VISIBILITY
     * =====================================================
     */

    function findPasswordInput(button) {
        if (!button) {
            return null;
        }

        const targetId =
            button.getAttribute(
                "data-password-target"
            );

        if (targetId) {
            return document.getElementById(targetId);
        }

        const parent = button.parentElement;

        if (parent) {
            const input =
                parent.querySelector(
                    "input[type='password'], input[type='text'][data-password-input]"
                );

            if (input) {
                return input;
            }
        }

        return null;
    }

    function bindPasswordButton(button) {
        if (!button) {
            return;
        }

        const input = findPasswordInput(button);

        if (!input) {
            return;
        }

        if (
            button.dataset
                .rationPasswordBound === "true"
        ) {
            return;
        }

        button.dataset
            .rationPasswordBound = "true";

        button.type = "button";

        button.addEventListener(
            "click",
            function (event) {
                event.preventDefault();
                event.stopPropagation();

                const showing =
                    input.type === "text";

                input.type =
                    showing
                        ? "password"
                        : "text";

                button.setAttribute(
                    "aria-label",
                    showing
                        ? "Show password"
                        : "Hide password"
                );

                button.setAttribute(
                    "title",
                    showing
                        ? "Show password"
                        : "Hide password"
                );
            }
        );
    }

    function installPasswordControls() {
        const inputs =
            Array.from(
                document.querySelectorAll(
                    "input[type='password']"
                )
            );

        inputs.forEach(function (input, index) {
            input.dataset.passwordInput = "true";

            if (!input.id) {
                input.id =
                    "rationPassword_" + index;
            }

            const parent = input.parentElement;

            if (!parent) {
                return;
            }

            let button =
                parent.querySelector(
                    "[data-password-toggle='true']"
                );

            if (!button) {
                button =
                    Array.from(
                        parent.querySelectorAll(
                            "button"
                        )
                    ).find(function (candidate) {
                        const label =
                            String(
                                candidate.getAttribute(
                                    "aria-label"
                                ) || ""
                            ).toLowerCase();

                        const title =
                            String(
                                candidate.getAttribute(
                                    "title"
                                ) || ""
                            ).toLowerCase();

                        return (
                            label.includes("password") ||
                            title.includes("password") ||
                            candidate.id
                                .toLowerCase()
                                .includes("toggle")
                        );
                    });
            }

            if (button) {
                button.dataset.passwordTarget =
                    input.id;

                button.dataset.passwordToggle =
                    "true";

                bindPasswordButton(button);
            }
        });
    }

    /*
     * =====================================================
     * TERMS & CONDITIONS
     * =====================================================
     */

    function installTermsProtection() {
        const page = getCurrentPage();

        let form = null;
        let message =
            "Please accept the Terms & Conditions before continuing.";

        if (page.endsWith("/pages/login.html")) {
            form =
                document.querySelector(
                    "#loginForm"
                );

            message =
                "Please accept the Terms & Conditions before signing in.";
        }

        if (page.endsWith("/pages/register.html")) {
            form =
                document.querySelector(
                    "#registerForm"
                );
        }

        if (
            page.endsWith(
                "/pages/register-password.html"
            )
        ) {
            form =
                document.querySelector(
                    "#registerPasswordForm"
                );
        }

        if (
            page.endsWith(
                "/pages/reset-password.html"
            )
        ) {
            form =
                document.querySelector(
                    "#resetPasswordForm"
                );
        }

        if (!form) {
            return;
        }

        let checkbox =
            form.querySelector(
                "#termsAccepted"
            );

        if (!checkbox) {
            const wrapper =
                document.createElement("label");

            wrapper.style.cssText =
                "display:flex;align-items:center;gap:9px;margin:16px 0;font-size:13px;cursor:pointer;";

            checkbox =
                document.createElement("input");

            checkbox.type = "checkbox";
            checkbox.id = "termsAccepted";
            checkbox.name = "termsAccepted";
            checkbox.required = true;

            const text =
                document.createElement("span");

            text.textContent =
                "I agree to the Terms & Conditions.";

            wrapper.appendChild(checkbox);
            wrapper.appendChild(text);

            const submit =
                form.querySelector(
                    "button[type='submit'],input[type='submit']"
                );

            if (submit) {
                form.insertBefore(
                    wrapper,
                    submit.parentElement || submit
                );
            } else {
                form.appendChild(wrapper);
            }
        }

        if (
            form.dataset
                .rationTermsBound === "true"
        ) {
            return;
        }

        form.dataset
            .rationTermsBound = "true";

        form.addEventListener(
            "submit",
            function (event) {
                if (!checkbox.checked) {
                    event.preventDefault();
                    event.stopImmediatePropagation();

                    checkbox.focus();

                    alert(message);

                    return false;
                }
            },
            true
        );
    }

    /*
     * =====================================================
     * PUBLIC AUTH PAGE HANDLING
     * =====================================================
     */

    function preparePublicAuthPage() {
        /*
         * IMPORTANT:
         * DO NOT clear an existing session merely because
         * the login page was opened.
         *
         * This fixes the previous behavior where navigating
         * through the site could destroy the authenticated
         * session.
         */

        installTermsProtection();
        installPasswordControls();
    }

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
        validateBackendSession:
            validateBackendSession,
        authenticatedFetch:
            authenticatedFetch,
        getLoginRedirect:
            getLoginRedirect,

        isAuthenticated: function () {
            return validateLocalSession();
        }
    };

    window.authGuard =
        window.rationAuth;

    async function initializeAuthGuard() {
        if (isPublicPage()) {
            preparePublicAuthPage();
            return;
        }

        const authenticated =
            await requireAuthentication();

        if (!authenticated) {
            return;
        }

        /*
         * Password and terms controls are also installed
         * on protected pages where applicable.
         */
        installTermsProtection();
        installPasswordControls();
    }

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