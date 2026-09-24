/* =========================================================
   RATION PORTAL
   Registration Page UI Enhancements

   File:
   frontend/js/register-ui.js

   Purpose:
   1. Replace text-based Show buttons with eye icons.
   2. Support password show/hide for all password fields.
   3. Add a required citizen declaration checkbox.
   4. Prevent registration until declaration is accepted.
   5. Keep the existing registration API logic untouched.
   ========================================================= */

(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {

        /* =====================================================
           SVG ICONS
           ===================================================== */

        const EYE_ICON = `
            <svg
                viewBox="0 0 24 24"
                width="22"
                height="22"
                aria-hidden="true"
                focusable="false"
            >
                <path
                    d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
                <circle
                    cx="12"
                    cy="12"
                    r="2.8"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                />
            </svg>
        `;

        const EYE_OFF_ICON = `
            <svg
                viewBox="0 0 24 24"
                width="22"
                height="22"
                aria-hidden="true"
                focusable="false"
            >
                <path
                    d="M3 3l18 18"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                />
                <path
                    d="M10.6 6.2A10.8 10.8 0 0 1 12 6c6 0 9.5 6 9.5 6a17 17 0 0 1-3.1 3.7"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
                <path
                    d="M6.1 6.9C3.8 8.4 2.5 12 2.5 12s3.5 6 9.5 6c1 0 1.9-.2 2.7-.5"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                />
                <path
                    d="M9.7 9.7a3.2 3.2 0 0 0 4.6 4.6"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.8"
                    stroke-linecap="round"
                />
            </svg>
        `;


        /* =====================================================
           ADD STYLES
           ===================================================== */

        const style = document.createElement("style");

        style.textContent = `
            /* -------------------------------------------------
               Password eye button
               ------------------------------------------------- */

            .rp-password-wrapper {
                position: relative;
                width: 100%;
            }

            .rp-password-wrapper input {
                width: 100%;
                box-sizing: border-box;
                padding-right: 54px !important;
            }

            .rp-password-eye {
                position: absolute;
                top: 50%;
                right: 12px;
                transform: translateY(-50%);

                width: 38px;
                height: 38px;

                display: inline-flex;
                align-items: center;
                justify-content: center;

                padding: 0;
                margin: 0;

                border: 0;
                border-radius: 8px;

                background: transparent;
                color: #60758a;

                cursor: pointer;

                transition:
                    background-color 0.15s ease,
                    color 0.15s ease;
            }

            .rp-password-eye:hover {
                background: #eef5fb;
                color: #075b96;
            }

            .rp-password-eye:focus-visible {
                outline: 3px solid rgba(8, 113, 201, 0.25);
                outline-offset: 2px;
            }

            .rp-password-eye svg {
                display: block;
            }


            /* -------------------------------------------------
               Declaration
               ------------------------------------------------- */

            .rp-declaration {
                display: flex;
                align-items: flex-start;
                gap: 12px;

                width: 100%;
                box-sizing: border-box;

                margin: 16px 0 22px;
                padding: 15px 16px;

                border: 1px solid #d7e4ee;
                border-radius: 12px;

                background: #f7fafc;

                color: #526b82;
                font-size: 13px;
                line-height: 1.55;

                cursor: pointer;
            }

            .rp-declaration:hover {
                border-color: #b8cedf;
                background: #f4f9fd;
            }

            .rp-declaration input[type="checkbox"] {
                flex: 0 0 auto;

                width: 19px;
                height: 19px;

                margin: 2px 0 0;

                accent-color: #0871c9;

                cursor: pointer;
            }

            .rp-declaration-text {
                flex: 1;
            }

            .rp-declaration-title {
                display: block;

                margin-bottom: 3px;

                color: #123e66;
                font-weight: 800;
            }

            .rp-declaration-error {
                display: none;

                margin-top: 7px;

                color: #b42318;
                font-size: 12px;
                font-weight: 700;
            }

            .rp-declaration.rp-invalid {
                border-color: #d92d20;
                background: #fff7f6;
            }

            .rp-declaration.rp-invalid .rp-declaration-error {
                display: block;
            }


            /* -------------------------------------------------
               Mobile
               ------------------------------------------------- */

            @media (max-width: 560px) {

                .rp-declaration {
                    padding: 13px 14px;
                    font-size: 12px;
                }

                .rp-password-eye {
                    right: 9px;
                }
            }
        `;

        document.head.appendChild(style);


        /* =====================================================
           PASSWORD SHOW / HIDE
           ===================================================== */

        const passwordInputs = Array.from(
            document.querySelectorAll(
                'input[type="password"], input[data-password-field]'
            )
        );

        passwordInputs.forEach(function (input, index) {

            if (!input || input.dataset.rpEyeReady === "true") {
                return;
            }

            input.dataset.rpEyeReady = "true";

            /*
             * Find an existing password wrapper.
             */
            let wrapper = input.parentElement;

            if (!wrapper) {
                return;
            }

            /*
             * Mark the wrapper.
             */
            wrapper.classList.add("rp-password-wrapper");

            /*
             * Remove old text-based Show/Hide buttons
             * associated with this password field.
             */
            const existingButtons =
                Array.from(wrapper.querySelectorAll("button"));

            existingButtons.forEach(function (button) {

                const buttonText =
                    (button.textContent || "")
                        .trim()
                        .toLowerCase();

                if (
                    buttonText === "show" ||
                    buttonText === "hide" ||
                    button.classList.contains("password-toggle")
                ) {
                    button.remove();
                }
            });


            /*
             * Create new eye button.
             */
            const eyeButton =
                document.createElement("button");

            eyeButton.type = "button";
            eyeButton.className = "rp-password-eye";

            eyeButton.setAttribute(
                "aria-label",
                "Show password"
            );

            eyeButton.setAttribute(
                "title",
                "Show password"
            );

            eyeButton.innerHTML = EYE_ICON;


            /*
             * Toggle password visibility.
             */
            eyeButton.addEventListener(
                "click",
                function () {

                    const currentlyHidden =
                        input.type === "password";

                    input.type =
                        currentlyHidden
                            ? "text"
                            : "password";

                    eyeButton.innerHTML =
                        currentlyHidden
                            ? EYE_OFF_ICON
                            : EYE_ICON;

                    eyeButton.setAttribute(
                        "aria-label",
                        currentlyHidden
                            ? "Hide password"
                            : "Show password"
                    );

                    eyeButton.setAttribute(
                        "title",
                        currentlyHidden
                            ? "Hide password"
                            : "Show password"
                    );
                }
            );


            wrapper.appendChild(eyeButton);
        });


        /* =====================================================
           FIND REGISTRATION FORM
           ===================================================== */

        const forms =
            Array.from(
                document.querySelectorAll("form")
            );

        let registrationForm = null;

        for (const form of forms) {

            const text =
                (form.textContent || "")
                    .toLowerCase();

            const hasPassword =
                form.querySelector(
                    'input[type="password"], input[data-password-field]'
                );

            const hasCreateAccount =
                text.includes("create account");

            if (
                hasPassword &&
                hasCreateAccount
            ) {
                registrationForm = form;
                break;
            }
        }

        /*
         * If the registration form cannot be found,
         * stop here without breaking the page.
         */
        if (!registrationForm) {
            return;
        }


        /* =====================================================
           ADD CITIZEN DECLARATION CHECKBOX
           ===================================================== */

        if (
            !document.getElementById(
                "registrationDeclaration"
            )
        ) {

            const declaration =
                document.createElement("label");

            declaration.className =
                "rp-declaration";

            declaration.setAttribute(
                "for",
                "registrationDeclaration"
            );

            declaration.innerHTML = `
                <input
                    type="checkbox"
                    id="registrationDeclaration"
                    name="registrationDeclaration"
                    required
                >

                <span class="rp-declaration-text">
                    <span class="rp-declaration-title">
                        Citizen declaration
                    </span>

                    I understand that the information provided
                    during registration will be used to create
                    my citizen account and provide access to
                    portal services.

                    <span
                        class="rp-declaration-error"
                        id="registrationDeclarationError"
                    >
                        Please accept the declaration before
                        creating your account.
                    </span>
                </span>
            `;


            /*
             * Find the Create Account button.
             */
            const submitButton =
                Array.from(
                    registrationForm.querySelectorAll(
                        'button[type="submit"], input[type="submit"], button'
                    )
                ).find(function (button) {

                    return (
                        (button.textContent || "")
                            .trim()
                            .toLowerCase()
                            .includes("create account")
                    );
                });


            if (submitButton) {

                /*
                 * Put declaration immediately before
                 * the Create Account button/container.
                 */
                const actionContainer =
                    submitButton.closest(
                        ".form-actions, .form-action, .actions, .button-group"
                    );

                if (actionContainer) {

                    actionContainer.parentNode.insertBefore(
                        declaration,
                        actionContainer
                    );

                } else {

                    submitButton.parentNode.insertBefore(
                        declaration,
                        submitButton
                    );
                }

            } else {

                /*
                 * Fallback: add before end of form.
                 */
                registrationForm.appendChild(
                    declaration
                );
            }
        }


        /* =====================================================
           DECLARATION VALIDATION
           ===================================================== */

        const declarationCheckbox =
            document.getElementById(
                "registrationDeclaration"
            );

        const declarationBox =
            declarationCheckbox
                ? declarationCheckbox.closest(
                    ".rp-declaration"
                )
                : null;


        if (
            declarationCheckbox &&
            declarationBox
        ) {

            declarationCheckbox.addEventListener(
                "change",
                function () {

                    if (declarationCheckbox.checked) {

                        declarationBox.classList.remove(
                            "rp-invalid"
                        );

                    }
                }
            );


            registrationForm.addEventListener(
                "submit",
                function (event) {

                    if (
                        !declarationCheckbox.checked
                    ) {

                        event.preventDefault();

                        declarationBox.classList.add(
                            "rp-invalid"
                        );

                        declarationCheckbox.focus();

                        declarationBox.scrollIntoView({
                            behavior: "smooth",
                            block: "center"
                        });
                    }
                },
                true
            );
        }

    });

})();