/* =========================================================
   RATION PORTAL - FRONTEND CONFIGURATION
   File: frontend/config.js

   Stage 12:
   - Backend API configuration
   - Mandatory Terms & Conditions enforcement
   - Registration protection
   - New-password protection
   - Password-reset protection
   ========================================================= */

(function () {
    "use strict";

    const PRODUCTION_API =
        "https://ration-portal-backend.onrender.com/api";

    const LOCAL_API =
        "http://localhost:8080/api";

    const hostname =
        window.location.hostname.toLowerCase();

    const isLocalDevelopment =
        hostname === "localhost" ||
        hostname === "127.0.0.1";

    const selectedApi =
        isLocalDevelopment
            ? LOCAL_API
            : PRODUCTION_API;

    window.RATION_API_BASE =
        window.RATION_API_BASE ||
        selectedApi;

    window.RATION_CONFIG = {
        apiBase:
            window.RATION_API_BASE,

        productionApi:
            PRODUCTION_API,

        localApi:
            LOCAL_API,

        isLocalDevelopment:
            isLocalDevelopment,

        environment:
            isLocalDevelopment
                ? "development"
                : "production"
    };

    /*
     * =========================================================
     * TERMS & CONDITIONS ENFORCEMENT
     * =========================================================
     *
     * These pages require explicit acceptance:
     *
     * 1. register.html
     * 2. register-password.html
     * 3. reset-password.html
     *
     * The checkbox is created automatically where the page
     * does not already provide one.
     */

    const TERMS_PAGES = [
        "register.html",
        "register-password.html",
        "reset-password.html"
    ];

    function getCurrentPage() {
        const file =
            window.location.pathname
                .split("/")
                .pop()
                .toLowerCase();

        return file || "index.html";
    }

    function isTermsPage() {
        return TERMS_PAGES.includes(
            getCurrentPage()
        );
    }

    function getForm() {
        return document.querySelector(
            "form"
        );
    }

    function getTermsCheckbox() {
        return document.getElementById(
            "termsAccepted"
        );
    }

    function createTermsCheckbox() {
        const page =
            getCurrentPage();

        const form =
            getForm();

        if (!form) {
            return null;
        }

        /*
         * Registration already contains the checkbox.
         */
        const existing =
            getTermsCheckbox();

        if (existing) {
            existing.required = true;
            return existing;
        }

        const wrapper =
            document.createElement(
                "div"
            );

        wrapper.id =
            "termsAcceptanceWrapper";

        wrapper.style.margin =
            "16px 0";

        wrapper.style.padding =
            "12px 14px";

        wrapper.style.border =
            "1px solid #dce5ee";

        wrapper.style.borderRadius =
            "10px";

        wrapper.style.background =
            "#f7fafc";

        wrapper.innerHTML =
            '<label for="termsAccepted" style="display:flex;align-items:flex-start;gap:9px;cursor:pointer;font-size:14px;line-height:1.5;color:#526274;">' +
            '<input id="termsAccepted" name="termsAccepted" type="checkbox" required style="margin-top:4px;width:16px;height:16px;flex:0 0 auto;">' +
            '<span>I agree to the <a href="terms.html" target="_blank" rel="noopener noreferrer">Terms &amp; Conditions</a>.</span>' +
            '</label>' +
            '<div id="termsAcceptanceError" role="alert" style="display:none;margin-top:7px;color:#b42318;font-size:13px;">Please accept the Terms &amp; Conditions before continuing.</div>';

        const submit =
            form.querySelector(
                'button[type="submit"], input[type="submit"]'
            );

        if (submit) {
            form.insertBefore(
                wrapper,
                submit.parentNode || submit
            );
        } else {
            form.appendChild(
                wrapper
            );
        }

        const checkbox =
            document.getElementById(
                "termsAccepted"
            );

        if (checkbox) {
            checkbox.required = true;
        }

        /*
         * New-password registration also has a separate
         * citizen declaration. Terms acceptance is independent
         * and mandatory.
         */
        if (
            page ===
            "register-password.html"
        ) {
            wrapper.setAttribute(
                "data-stage",
                "12"
            );
        }

        return checkbox;
    }

    function showTermsError() {
        const error =
            document.getElementById(
                "termsAcceptanceError"
            );

        if (error) {
            error.style.display =
                "block";
        }

        const checkbox =
            getTermsCheckbox();

        if (checkbox) {
            checkbox.focus();
        }
    }

    function hideTermsError() {
        const error =
            document.getElementById(
                "termsAcceptanceError"
            );

        if (error) {
            error.style.display =
                "none";
        }
    }

    function termsAccepted() {
        const checkbox =
            getTermsCheckbox();

        return Boolean(
            checkbox &&
            checkbox.checked
        );
    }

    function installTermsEnforcement() {
        if (!isTermsPage()) {
            return;
        }

        const form =
            getForm();

        if (!form) {
            return;
        }

        const checkbox =
            createTermsCheckbox();

        if (!checkbox) {
            return;
        }

        checkbox.required =
            true;

        checkbox.addEventListener(
            "change",
            function () {
                if (checkbox.checked) {
                    hideTermsError();
                }
            }
        );

        /*
         * Capture phase guarantees that this validation runs
         * before page-specific submit handlers.
         */
        if (
            form.dataset.stage12TermsBound !==
            "true"
        ) {
            form.dataset.stage12TermsBound =
                "true";

            form.addEventListener(
                "submit",
                function (event) {
                    if (
                        !termsAccepted()
                    ) {
                        event.preventDefault();
                        event.stopImmediatePropagation();
                        showTermsError();
                    }
                },
                true
            );
        }
    }

    /*
     * =========================================================
     * API REQUEST PROTECTION
     * =========================================================
     *
     * Registration and reset-password requests must carry:
     *
     *     "termsAccepted": "true"
     *
     * The backend independently verifies this value.
     */

    const originalFetch =
        window.fetch.bind(
            window
        );

    window.fetch =
        async function (
            input,
            init
        ) {
            const requestInit =
                init || {};

            let url = "";

            if (
                typeof input ===
                "string"
            ) {
                url = input;
            } else if (
                input &&
                input.url
            ) {
                url = input.url;
            }

            const normalizedUrl =
                url.toLowerCase();

            const protectedRegistration =
                normalizedUrl.includes(
                    "/auth/register"
                );

            const protectedReset =
                normalizedUrl.includes(
                    "/auth/reset-password"
                );

            if (
                protectedRegistration ||
                protectedReset
            ) {
                const checkbox =
                    getTermsCheckbox();

                if (
                    !checkbox ||
                    !checkbox.checked
                ) {
                    showTermsError();

                    return Promise.resolve(
                        new Response(
                            JSON.stringify({
                                message:
                                    "Terms & Conditions acceptance is required."
                            }),
                            {
                                status: 400,
                                headers: {
                                    "Content-Type":
                                        "application/json"
                                }
                            }
                        )
                    );
                }

                let body =
                    requestInit.body;

                if (
                    typeof body ===
                    "string"
                ) {
                    try {
                        const data =
                            JSON.parse(
                                body
                            );

                        data.termsAccepted =
                            "true";

                        body =
                            JSON.stringify(
                                data
                            );

                        requestInit.body =
                            body;
                    } catch (_) {
                        /*
                         * If the request is not JSON, do not
                         * silently modify it.
                         */
                    }
                }
            }

            return originalFetch(
                input,
                requestInit
            );
        };

    /*
     * =========================================================
     * INITIALIZATION
     * =========================================================
     */

    function initializeTermsProtection() {
        installTermsEnforcement();
    }

    if (
        document.readyState ===
        "loading"
    ) {
        document.addEventListener(
            "DOMContentLoaded",
            initializeTermsProtection,
            {
                once: true
            }
        );
    } else {
        initializeTermsProtection();
    }

    /*
     * Development-only information.
     */
    if (isLocalDevelopment) {
        console.info(
            "Ration Portal API:",
            window.RATION_API_BASE
        );

        console.info(
            "Ration Portal environment: development"
        );
    }

})();