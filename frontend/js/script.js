/* =========================================================
   RATION PORTAL - GLOBAL UI SCRIPT
   File: frontend/js/script.js
   ========================================================= */

(function () {
    "use strict";

    /* =====================================================
       ACCESSIBILITY - FONT SIZE
       ===================================================== */

    const FONT_SIZE_KEY = "rationPortalFontSize";

    const fontSizes = {
        small: "14px",
        normal: "16px",
        large: "18px"
    };

    function applyFontSize(size) {
        const selectedSize =
            fontSizes[size] || fontSizes.normal;

        document.documentElement.style.fontSize =
            selectedSize;

        localStorage.setItem(
            FONT_SIZE_KEY,
            size
        );

        document
            .querySelectorAll("[data-font-size]")
            .forEach(function (button) {
                const isActive =
                    button.getAttribute(
                        "data-font-size"
                    ) === size;

                button.classList.toggle(
                    "active",
                    isActive
                );
            });
    }

    function setupFontControls() {
        const savedSize =
            localStorage.getItem(FONT_SIZE_KEY) ||
            "normal";

        applyFontSize(savedSize);

        document
            .querySelectorAll("[data-font-size]")
            .forEach(function (button) {
                button.addEventListener(
                    "click",
                    function () {
                        const size =
                            button.getAttribute(
                                "data-font-size"
                            );

                        applyFontSize(size);

                        showNotification(
                            "Text size updated.",
                            "success"
                        );
                    }
                );
            });
    }

    /* =====================================================
       LANGUAGE SELECTOR
       ===================================================== */

    function setupLanguageSelector() {
        const selectors =
            document.querySelectorAll(
                "[data-language-selector]"
            );

        selectors.forEach(function (selector) {
            selector.addEventListener(
                "change",
                function () {
                    const language =
                        selector.value;

                    localStorage.setItem(
                        "rationPortalLanguage",
                        language
                    );

                    /*
                     * This project currently provides the
                     * English interface. The selector is
                     * prepared for future multilingual
                     * content.
                     */

                    if (language === "en") {
                        showNotification(
                            "English language selected.",
                            "success"
                        );
                    } else if (language === "hi") {
                        showNotification(
                            "Hindi interface support can be connected here.",
                            "info"
                        );
                    } else {
                        showNotification(
                            "Selected language: " +
                                language,
                            "info"
                        );
                    }
                }
            );

            const savedLanguage =
                localStorage.getItem(
                    "rationPortalLanguage"
                );

            if (savedLanguage) {
                selector.value = savedLanguage;
            }
        });
    }

    /* =====================================================
       SMOOTH SCROLL
       ===================================================== */

    function setupSmoothScroll() {
        document
            .querySelectorAll(
                'a[href^="#"]'
            )
            .forEach(function (link) {
                link.addEventListener(
                    "click",
                    function (event) {
                        const targetId =
                            link.getAttribute(
                                "href"
                            );

                        if (
                            !targetId ||
                            targetId === "#"
                        ) {
                            return;
                        }

                        const target =
                            document.querySelector(
                                targetId
                            );

                        if (!target) {
                            return;
                        }

                        event.preventDefault();

                        target.scrollIntoView({
                            behavior: "smooth",
                            block: "start"
                        });
                    }
                );
            });
    }

    /* =====================================================
       SCROLL TO TOP BUTTON
       ===================================================== */

    function setupScrollTop() {
        const scrollButton =
            document.querySelector(
                "[data-scroll-top]"
            );

        if (!scrollButton) {
            return;
        }

        function updateButton() {
            if (window.scrollY > 400) {
                scrollButton.classList.add(
                    "visible"
                );
            } else {
                scrollButton.classList.remove(
                    "visible"
                );
            }
        }

        window.addEventListener(
            "scroll",
            updateButton,
            { passive: true }
        );

        scrollButton.addEventListener(
            "click",
            function () {
                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });
            }
        );

        updateButton();
    }

    /* =====================================================
       CURRENT YEAR
       ===================================================== */

    function setupCurrentYear() {
        const currentYear =
            new Date().getFullYear();

        document
            .querySelectorAll(
                "[data-current-year]"
            )
            .forEach(function (element) {
                element.textContent =
                    currentYear;
            });
    }

    /* =====================================================
       ACTIVE NAVIGATION
       ===================================================== */

    function setupActiveNavigation() {
        const currentPath =
            window.location.pathname
                .split("/")
                .pop()
                .toLowerCase();

        document
            .querySelectorAll(
                ".main-nav a"
            )
            .forEach(function (link) {
                const href =
                    link.getAttribute("href");

                if (!href) {
                    return;
                }

                const cleanHref =
                    href
                        .split("?")[0]
                        .split("#")[0]
                        .split("/")
                        .pop()
                        .toLowerCase();

                if (
                    cleanHref &&
                    cleanHref === currentPath
                ) {
                    link.classList.add(
                        "active"
                    );
                }
            });
    }

    /* =====================================================
       GLOBAL NOTIFICATION SYSTEM
       ===================================================== */

    function showNotification(
        message,
        type,
        duration
    ) {
        const notificationType =
            type || "info";

        const timeout =
            Number(duration) || 4000;

        let container =
            document.querySelector(
                ".notification-container"
            );

        if (!container) {
            container =
                document.createElement("div");

            container.className =
                "notification-container";

            container.setAttribute(
                "aria-live",
                "polite"
            );

            container.setAttribute(
                "aria-atomic",
                "true"
            );

            document.body.appendChild(
                container
            );
        }

        const notification =
            document.createElement("div");

        notification.className =
            "notification notification-" +
            notificationType;

        notification.setAttribute(
            "role",
            "status"
        );

        const content =
            document.createElement("span");

        content.className =
            "notification-message";

        content.textContent =
            message;

        const closeButton =
            document.createElement("button");

        closeButton.type = "button";
        closeButton.className =
            "notification-close";
        closeButton.setAttribute(
            "aria-label",
            "Close notification"
        );
        closeButton.textContent = "×";

        closeButton.addEventListener(
            "click",
            function () {
                removeNotification(
                    notification
                );
            }
        );

        notification.appendChild(
            content
        );

        notification.appendChild(
            closeButton
        );

        container.appendChild(
            notification
        );

        requestAnimationFrame(
            function () {
                notification.classList.add(
                    "show"
                );
            }
        );

        window.setTimeout(
            function () {
                removeNotification(
                    notification
                );
            },
            timeout
        );
    }

    function removeNotification(
        notification
    ) {
        if (!notification) {
            return;
        }

        notification.classList.remove(
            "show"
        );

        window.setTimeout(
            function () {
                if (
                    notification.parentNode
                ) {
                    notification.parentNode.removeChild(
                        notification
                    );
                }
            },
            250
        );
    }

    window.showNotification =
        showNotification;

    /* =====================================================
       BUTTON LOADING STATE
       ===================================================== */

    function setupButtonLoading() {
        document
            .querySelectorAll(
                "[data-loading-button]"
            )
            .forEach(function (button) {
                button.addEventListener(
                    "click",
                    function () {
                        if (
                            button.dataset.loadingActive ===
                            "true"
                        ) {
                            return;
                        }

                        button.dataset.loadingActive =
                            "true";

                        button.dataset.originalText =
                            button.innerHTML;

                        button.disabled = true;

                        button.innerHTML =
                            '<span class="loading-spinner"></span> Loading...';

                        window.setTimeout(
                            function () {
                                if (
                                    button.dataset
                                        .restoreAfterLoading !==
                                    "false"
                                ) {
                                    button.disabled =
                                        false;

                                    button.innerHTML =
                                        button.dataset
                                            .originalText;

                                    button.dataset
                                        .loadingActive =
                                        "false";
                                }
                            },
                            800
                        );
                    }
                );
            });
    }

    /* =====================================================
       EXTERNAL LINK SAFETY
       ===================================================== */

    function setupExternalLinks() {
        document
            .querySelectorAll(
                'a[target="_blank"]'
            )
            .forEach(function (link) {
                const currentRel =
                    link.getAttribute("rel") ||
                    "";

                const values =
                    currentRel
                        .split(/\s+/)
                        .filter(Boolean);

                if (
                    !values.includes(
                        "noopener"
                    )
                ) {
                    values.push("noopener");
                }

                if (
                    !values.includes(
                        "noreferrer"
                    )
                ) {
                    values.push("noreferrer");
                }

                link.setAttribute(
                    "rel",
                    values.join(" ")
                );
            });
    }

    /* =====================================================
       KEYBOARD ACCESSIBILITY
       ===================================================== */

    function setupKeyboardAccessibility() {
        document.addEventListener(
            "keydown",
            function (event) {
                if (
                    event.key === "Escape"
                ) {
                    document
                        .querySelectorAll(
                            ".profile-dropdown.open"
                        )
                        .forEach(function (
                            dropdown
                        ) {
                            dropdown.classList.remove(
                                "open"
                            );
                        });

                    document
                        .querySelectorAll(
                            ".main-nav.open"
                        )
                        .forEach(function (
                            navigation
                        ) {
                            navigation.classList.remove(
                                "open"
                            );
                        });
                }
            }
        );
    }

    /* =====================================================
       FOCUS VISIBILITY
       ===================================================== */

    function setupFocusVisibility() {
        document.addEventListener(
            "keydown",
            function (event) {
                if (
                    event.key === "Tab"
                ) {
                    document.body.classList.add(
                        "keyboard-navigation"
                    );
                }
            }
        );

        document.addEventListener(
            "mousedown",
            function () {
                document.body.classList.remove(
                    "keyboard-navigation"
                );
            }
        );
    }

    /* =====================================================
       DISABLE DOUBLE SUBMISSION
       ===================================================== */

    function setupFormProtection() {
        document
            .querySelectorAll("form")
            .forEach(function (form) {
                form.addEventListener(
                    "submit",
                    function () {
                        if (
                            form.dataset
                                .submitted ===
                            "true"
                        ) {
                            return;
                        }

                        form.dataset.submitted =
                            "true";

                        window.setTimeout(
                            function () {
                                form.dataset
                                    .submitted =
                                    "false";
                            },
                            1500
                        );
                    }
                );
            });
    }

    /* =====================================================
       REDUCE MOTION SUPPORT
       ===================================================== */

    function setupReducedMotion() {
        const mediaQuery =
            window.matchMedia(
                "(prefers-reduced-motion: reduce)"
            );

        function updateReducedMotion() {
            document.documentElement.classList.toggle(
                "reduced-motion",
                mediaQuery.matches
            );
        }

        updateReducedMotion();

        if (
            typeof mediaQuery.addEventListener ===
            "function"
        ) {
            mediaQuery.addEventListener(
                "change",
                updateReducedMotion
            );
        }
    }

    /* =====================================================
       PRINT HANDLING
       ===================================================== */

    function setupPrintSupport() {
        window.addEventListener(
            "beforeprint",
            function () {
                document.body.classList.add(
                    "printing"
                );
            }
        );

        window.addEventListener(
            "afterprint",
            function () {
                document.body.classList.remove(
                    "printing"
                );
            }
        );
    }

    /* =====================================================
       INITIALIZATION
       ===================================================== */

    function initializeGlobalScript() {
        setupFontControls();
        setupLanguageSelector();
        setupSmoothScroll();
        setupScrollTop();
        setupCurrentYear();
        setupActiveNavigation();
        setupButtonLoading();
        setupExternalLinks();
        setupKeyboardAccessibility();
        setupFocusVisibility();
        setupFormProtection();
        setupReducedMotion();
        setupPrintSupport();
    }

    if (
        document.readyState ===
        "loading"
    ) {
        document.addEventListener(
            "DOMContentLoaded",
            initializeGlobalScript
        );
    } else {
        initializeGlobalScript();
    }

})();