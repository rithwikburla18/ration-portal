/* =========================================================
   RATION PORTAL - AUTHENTICATION GUARD
   STAGE 20A - STRICT SESSION + LOGIN SECURITY
   ========================================================= */

(function () {
    "use strict";

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";
    const REDIRECT_KEY = "rationPortalRedirect";
    const ROLE_KEY = "rationPortalUserRole";
    const ADMIN_EMAIL_KEY = "rationPortalAdminEmail";

    const BACKEND_URL =
        "https://ration-portal-backend.onrender.com";

    /*
     * These pages are intentionally public.
     *
     * IMPORTANT:
     * Never put "" into this array because every pathname
     * endsWith("") and would make every page public.
     */
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

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            const raw =
                localStorage.getItem(USER_KEY);

            return raw
                ? JSON.parse(raw)
                : null;
        } catch (error) {
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
        localStorage.removeItem(ROLE_KEY);
        localStorage.removeItem(ADMIN_EMAIL_KEY);
    }

    function normalizePath(path) {
        let value =
            String(path || "")
                .replace(/\\/g, "/")
                .toLowerCase();

        if (value.length > 1 &&
            value.endsWith("/")) {
            value = value.slice(0, -1);
        }

        return value;
    }

    function getCurrentPage() {
        return normalizePath(
            window.location.pathname
        );
    }

    function isLoginPage() {
        return getCurrentPage()
            .endsWith("/pages/login.html");
    }

    function isAdminLoginPage() {
        return getCurrentPage()
            .endsWith("/pages/admin-login.html");
    }

    function isRegisterPage() {
        return getCurrentPage()
            .endsWith("/pages/register.html");
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
            const normalized =
                normalizePath(item);

            return page === normalized;
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

        window.location.href =
            getLoginUrl();
    }

    function redirectForRole(user) {
        if (!user) {
            redirectToLogin();
            return false;
        }

        const role =
            String(user.role || "")
                .trim()
                .toUpperCase();

        if (
            isAdminPage() &&
            role !== "ADMIN"
        ) {
            clearSession();
            window.location.href =
                "admin-login.html";

            return false;
        }

        return true;
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
            const part =
                token.split(".")[1];

            const normalized =
                part
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
        const payload =
            getJwtPayload(token);

        if (!payload || !payload.exp) {
            return false;
        }

        return Number(payload.exp) <=
            Math.floor(Date.now() / 1000);
    }

    function validateLocalSession() {
        const token =
            getToken();

        if (
            !token ||
            !isJwtFormat(token)
        ) {
            return false;
        }

        if (isTokenExpired(token)) {
            clearSession();
            return false;
        }

        return true;
    }

    async function validateBackendSession() {
        const token =
            getToken();

        if (!token) {
            return null;
        }

        try {
            const response =
                await fetch(
                    BACKEND_URL +
                    "/api/auth/me",
                    {
                        method: "GET",
                        headers: {
                            "Authorization":
                                "Bearer " + token,
                            "Accept":
                                "application/json"
                        }
                    }
                );

            if (!response.ok) {
                clearSession();
                return null;
            }

            const user =
                await response.json();

            if (
                !user ||
                String(
                    user.status || ""
                ).toUpperCase() !== "ACTIVE"
            ) {
                clearSession();
                return null;
            }

            saveUser(user);

            localStorage.setItem(
                ROLE_KEY,
                String(
                    user.role || ""
                ).toUpperCase()
            );

            if (
                String(
                    user.role || ""
                ).toUpperCase() === "ADMIN"
            ) {
                localStorage.setItem(
                    ADMIN_EMAIL_KEY,
                    user.email || ""
                );
            } else {
                localStorage.removeItem(
                    ADMIN_EMAIL_KEY
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

        const user =
            await validateBackendSession();

        if (!user) {
            redirectToLogin();
            return false;
        }

        return redirectForRole(user);
    }

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
            typeof requestOptions.body === "object" &&
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

        if (
            getCurrentPage()
                .includes("/pages/")
        ) {
            window.location.href =
                "login.html";
            return;
        }

        window.location.href =
            "pages/login.html";
    }

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

    /*
     * =====================================================
     * TERMS & CONDITIONS
     * =====================================================
     */

    function createTermsCheckbox(
        form,
        message
    ) {
        if (!form) {
            return null;
        }

        let checkbox =
            form.querySelector(
                "#termsAccepted"
            );

        if (checkbox) {
            return checkbox;
        }

        const actions =
            form.querySelector(
                "button[type='submit'], input[type='submit']"
            );

        const wrapper =
            document.createElement(
                "div"
            );

        wrapper.style.cssText =
            [
                "margin:18px 0 14px",
                "display:flex",
                "flex-direction:column",
                "gap:7px"
            ].join(";");

        const label =
            document.createElement(
                "label"
            );

        label.htmlFor =
            "termsAccepted";

        label.style.cssText =
            [
                "display:flex",
                "align-items:center",
                "gap:9px",
                "font-size:13px",
                "line-height:1.5",
                "color:#52677c",
                "cursor:pointer",
                "user-select:none"
            ].join(";");

        checkbox =
            document.createElement(
                "input"
            );

        checkbox.type =
            "checkbox";

        checkbox.id =
            "termsAccepted";

        checkbox.name =
            "termsAccepted";

        checkbox.required =
            true;

        checkbox.setAttribute(
            "aria-required",
            "true"
        );

        checkbox.style.cssText =
            [
                "appearance:auto",
                "-webkit-appearance:checkbox",
                "display:inline-block",
                "width:18px",
                "height:18px",
                "min-width:18px",
                "min-height:18px",
                "margin:0",
                "padding:0",
                "cursor:pointer",
                "accent-color:#0871c9",
                "flex-shrink:0"
            ].join(";");

        const text =
            document.createElement(
                "span"
            );

        text.textContent =
            "Terms & Conditions accepted";

        label.appendChild(
            checkbox
        );

        label.appendChild(
            text
        );

        const error =
            document.createElement(
                "div"
            );

        error.id =
            "termsError";

        error.style.cssText =
            [
                "display:none",
                "color:#b42318",
                "font-size:12px"
            ].join(";");

        error.setAttribute(
            "role",
            "alert"
        );

        error.textContent =
            message ||
            "Please accept the Terms & Conditions before continuing.";

        wrapper.appendChild(
            label
        );

        wrapper.appendChild(
            error
        );

        if (actions) {
            form.insertBefore(
                wrapper,
                actions.parentElement || actions
            );
        } else {
            form.appendChild(
                wrapper
            );
        }

        return checkbox;
    }

    function protectTermsSubmission(
        form,
        message
    ) {
        if (!form) {
            return;
        }

        const checkbox =
            createTermsCheckbox(
                form,
                message
            );

        if (!checkbox) {
            return;
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

                    const error =
                        document.getElementById(
                            "termsError"
                        );

                    if (error) {
                        error.textContent =
                            message ||
                            "Please accept the Terms & Conditions before continuing.";

                        error.style.display =
                            "block";
                    }

                    checkbox.focus();

                    return false;
                }

                const error =
                    document.getElementById(
                        "termsError"
                    );

                if (error) {
                    error.style.display =
                        "none";
                }

                return true;
            },
            true
        );
    }

    function installTermsProtection() {
        if (
            isLoginPage()
        ) {
            const form =
                document.querySelector(
                    "#loginForm, form"
                );

            protectTermsSubmission(
                form,
                "Please accept the Terms & Conditions before signing in."
            );
        }

        if (
            isRegisterPage()
        ) {
            const form =
                document.querySelector(
                    "#registerForm, form"
                );

            protectTermsSubmission(
                form,
                "Please accept the Terms & Conditions before continuing."
            );
        }

        if (
            isRegisterPasswordPage()
        ) {
            const form =
                document.querySelector(
                    "#registerPasswordForm, form"
                );

            protectTermsSubmission(
                form,
                "Please accept the Terms & Conditions before creating your account."
            );
        }
    }

    /*
     * =====================================================
     * PASSWORD VISIBILITY
     * =====================================================
     */

    function findPasswordInput(
        button
    ) {
        if (!button) {
            return null;
        }

        const targetId =
            button.getAttribute(
                "data-password-target"
            );

        if (
            targetId
        ) {
            return document.getElementById(
                targetId
            );
        }

        const parent =
            button.parentElement;

        if (parent) {
            const nearby =
                parent.querySelector(
                    "input[type='password'], input[data-password-input]"
                );

            if (nearby) {
                return nearby;
            }
        }

        const grandParent =
            parent &&
            parent.parentElement;

        if (grandParent) {
            const nearby =
                grandParent.querySelector(
                    "input[type='password'], input[data-password-input]"
                );

            if (nearby) {
                return nearby;
            }
        }

        return null;
    }

    function updatePasswordButton(
        button,
        visible
    ) {
        if (!button) {
            return;
        }

        button.textContent =
            visible
                ? "👁"
                : "👁";

        button.setAttribute(
            "aria-label",
            visible
                ? "Hide password"
                : "Show password"
        );

        button.setAttribute(
            "title",
            visible
                ? "Hide password"
                : "Show password"
        );

        button.style.cssText +=
            ";display:inline-flex;align-items:center;justify-content:center;cursor:pointer;min-width:42px;min-height:42px;border:0;background:transparent;font-size:18px;";
    }

    function bindPasswordButton(
        button
    ) {
        if (!button) {
            return;
        }

        if (
            button.dataset
                .rationPasswordBound === "true"
        ) {
            return;
        }

        const input =
            findPasswordInput(
                button
            );

        if (!input) {
            return;
        }

        /*
         * Clone first so an old Show/Hide listener
         * from existing page code cannot fire twice.
         */
        const replacement =
            button.cloneNode(true);

        button.replaceWith(
            replacement
        );

        button =
            replacement;

        button.dataset
            .rationPasswordBound = "true";

        button.type =
            "button";

        button.addEventListener(
            "click",
            function (event) {
                event.preventDefault();
                event.stopImmediatePropagation();

                const visible =
                    input.type === "password";

                input.type =
                    visible
                        ? "text"
                        : "password";

                updatePasswordButton(
                    button,
                    visible
                );
            },
            true
        );

        updatePasswordButton(
            button,
            input.type === "text"
        );
    }

    function createPasswordButton(
        input,
        index
    ) {
        if (!input || !input.parentElement) {
            return;
        }

        let existing =
            input.parentElement.querySelector(
                "[data-password-toggle='true']"
            );

        if (existing) {
            bindPasswordButton(
                existing
            );
            return;
        }

        const nearbyButton =
            Array.from(
                input.parentElement.querySelectorAll(
                    "button"
                )
            ).find(function (button) {
                const text =
                    String(
                        button.textContent ||
                        ""
                    ).trim().toLowerCase();

                const label =
                    String(
                        button.getAttribute(
                            "aria-label"
                        ) || ""
                    ).toLowerCase();

                const title =
                    String(
                        button.getAttribute(
                            "title"
                        ) || ""
                    ).toLowerCase();

                return (
                    text === "show" ||
                    text === "hide" ||
                    label.includes(
                        "password"
                    ) ||
                    title.includes(
                        "password"
                    ) ||
                    button.id
                        .toLowerCase()
                        .includes(
                            "toggle"
                        )
                );
            });

        if (nearbyButton) {
            bindPasswordButton(
                nearbyButton
            );
            return;
        }

        const button =
            document.createElement(
                "button"
            );

        button.type =
            "button";

        button.dataset
            .passwordTarget =
            input.id ||
            "";

        button.dataset
            .passwordToggle =
            "true";

        button.setAttribute(
            "aria-label",
            "Show password"
        );

        button.setAttribute(
            "title",
            "Show password"
        );

        button.textContent =
            "👁";

        button.style.cssText =
            [
                "position:absolute",
                "right:6px",
                "top:50%",
                "transform:translateY(-50%)",
                "min-width:42px",
                "min-height:42px",
                "display:inline-flex",
                "align-items:center",
                "justify-content:center",
                "border:0",
                "background:transparent",
                "cursor:pointer",
                "font-size:18px",
                "z-index:3"
            ].join(";");

        const parent =
            input.parentElement;

        const position =
            window.getComputedStyle(
                parent
            ).position;

        if (
            position === "static"
        ) {
            parent.style.position =
                "relative";
        }

        parent.appendChild(
            button
        );

        bindPasswordButton(
            button
        );
    }

    function installPasswordControls() {
        const inputs =
            Array.from(
                document.querySelectorAll(
                    "input[type='password']"
                )
            );

        inputs.forEach(
            function (
                input,
                index
            ) {
                input.dataset
                    .passwordInput =
                    "true";

                if (!input.id) {
                    input.id =
                        "rationPassword_" +
                        index;
                }

                createPasswordButton(
                    input,
                    index
                );
            }
        );
    }

    /*
     * =====================================================
     * PUBLIC AUTH PAGE RULE
     * =====================================================
     */

    function preparePublicAuthPage() {
        /*
         * Whenever the user explicitly opens Login or
         * Admin Login, do not silently reuse an old token.
         * This forces email/password entry again.
         *
         * The redirect-after-login value in sessionStorage
         * remains untouched.
         */
        if (
            isLoginPage() ||
            isAdminLoginPage()
        ) {
            clearSession();
        }

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
        isAuthenticated:
            function () {
                return Boolean(
                    getToken()
                );
            }
    };

    window.authGuard =
        window.rationAuth;

    async function initializeAuthGuard() {
        /*
         * Public pages do not need backend session
         * validation.
         */
        if (isPublicPage()) {
            preparePublicAuthPage();
            return;
        }

        const authenticated =
            await requireAuthentication();

        if (!authenticated) {
            return;
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