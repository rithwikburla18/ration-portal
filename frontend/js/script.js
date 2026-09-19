/* =========================================================
   RATION PORTAL — GLOBAL FRONTEND SCRIPT
   ========================================================= */

(function () {
    "use strict";


    /* ---------------------------------------------------------
       ACCESSIBILITY
       --------------------------------------------------------- */

    function initializeAccessibility() {

        const decreaseButton =
            document.getElementById("fontDecrease");

        const resetButton =
            document.getElementById("fontReset");

        const increaseButton =
            document.getElementById("fontIncrease");


        let fontScale =
            Number(
                localStorage.getItem("rationPortalFontScale")
            ) || 1;


        function applyFontScale() {

            document.documentElement.style.setProperty(
                "--font-scale",
                fontScale
            );

            document.documentElement.style.fontSize =
                `${fontScale}em`;

            localStorage.setItem(
                "rationPortalFontScale",
                String(fontScale)
            );

        }


        if (decreaseButton) {

            decreaseButton.addEventListener(
                "click",
                function () {

                    fontScale =
                        Math.max(
                            0.9,
                            Number(
                                (
                                    fontScale - 0.05
                                ).toFixed(2)
                            )
                        );

                    applyFontScale();

                }
            );

        }


        if (resetButton) {

            resetButton.addEventListener(
                "click",
                function () {

                    fontScale = 1;

                    applyFontScale();

                }
            );

        }


        if (increaseButton) {

            increaseButton.addEventListener(
                "click",
                function () {

                    fontScale =
                        Math.min(
                            1.15,
                            Number(
                                (
                                    fontScale + 0.05
                                ).toFixed(2)
                            )
                        );

                    applyFontScale();

                }
            );

        }


        applyFontScale();

    }


    /* ---------------------------------------------------------
       LANGUAGE SELECTOR
       --------------------------------------------------------- */

    function initializeLanguageSelector() {

        const selector =
            document.getElementById("languageSelector");

        if (!selector) {
            return;
        }


        const savedLanguage =
            localStorage.getItem(
                "rationPortalLanguage"
            );


        if (savedLanguage) {

            selector.value =
                savedLanguage;

        }


        selector.addEventListener(
            "change",
            function () {

                localStorage.setItem(
                    "rationPortalLanguage",
                    selector.value
                );


                if (
                    selector.value !== "English"
                ) {

                    showNotification(
                        "Language support will be expanded in a future version.",
                        "info"
                    );

                }

            }
        );

    }


    /* ---------------------------------------------------------
       MOBILE MENU
       --------------------------------------------------------- */

    function initializeMobileMenu() {

        const button =
            document.getElementById(
                "mobileMenuButton"
            );

        const navigation =
            document.getElementById(
                "mainNavigation"
            );


        if (!button || !navigation) {
            return;
        }


        button.addEventListener(
            "click",
            function () {

                const open =
                    navigation.classList.toggle(
                        "mobile-nav-open"
                    );

                button.setAttribute(
                    "aria-expanded",
                    String(open)
                );

            }
        );


        navigation
            .querySelectorAll("a")
            .forEach(function (link) {

                link.addEventListener(
                    "click",
                    function () {

                        navigation.classList.remove(
                            "mobile-nav-open"
                        );

                        button.setAttribute(
                            "aria-expanded",
                            "false"
                        );

                    }
                );

            });

    }


    /* ---------------------------------------------------------
       SMOOTH SCROLL
       --------------------------------------------------------- */

    function initializeSmoothScroll() {

        document
            .querySelectorAll(
                'a[href^="#"]'
            )
            .forEach(function (link) {

                link.addEventListener(
                    "click",
                    function (event) {

                        const targetId =
                            link.getAttribute("href");


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


    /* ---------------------------------------------------------
       SCROLL TO TOP
       --------------------------------------------------------- */

    function initializeScrollTop() {

        const button =
            document.getElementById(
                "scrollTopButton"
            );


        if (!button) {
            return;
        }


        function updateButton() {

            if (
                window.scrollY > 500
            ) {

                button.classList.add(
                    "visible"
                );

            } else {

                button.classList.remove(
                    "visible"
                );

            }

        }


        window.addEventListener(
            "scroll",
            updateButton,
            {
                passive: true
            }
        );


        button.addEventListener(
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


    /* ---------------------------------------------------------
       CURRENT YEAR
       --------------------------------------------------------- */

    function initializeCurrentYear() {

        const yearElements =
            document.querySelectorAll(
                "[data-current-year]"
            );


        const currentYear =
            new Date().getFullYear();


        yearElements.forEach(
            function (element) {

                element.textContent =
                    currentYear;

            }
        );

    }


    /* ---------------------------------------------------------
       EXTERNAL LINK SAFETY
       --------------------------------------------------------- */

    function initializeExternalLinks() {

        document
            .querySelectorAll(
                'a[target="_blank"]'
            )
            .forEach(function (link) {

                const rel =
                    link.getAttribute("rel") || "";


                if (
                    !rel.includes("noopener")
                ) {

                    link.setAttribute(
                        "rel",
                        `${rel} noopener noreferrer`.trim()
                    );

                }

            });

    }


    /* ---------------------------------------------------------
       BUTTON LOADING STATE
       --------------------------------------------------------- */

    function initializeButtonLoading() {

        document
            .querySelectorAll(
                "form"
            )
            .forEach(function (form) {

                form.addEventListener(
                    "submit",
                    function () {

                        const submitButton =
                            form.querySelector(
                                'button[type="submit"], input[type="submit"]'
                            );


                        if (!submitButton) {
                            return;
                        }


                        if (
                            submitButton.dataset.noLoading ===
                            "true"
                        ) {
                            return;
                        }


                        submitButton.classList.add(
                            "is-loading"
                        );


                        submitButton.dataset.originalText =
                            submitButton.textContent;


                        if (
                            submitButton.tagName ===
                            "BUTTON"
                        ) {

                            submitButton.textContent =
                                "Processing...";

                        }

                    }
                );

            });

    }


    /* ---------------------------------------------------------
       NOTIFICATION SYSTEM
       --------------------------------------------------------- */

    function showNotification(
        message,
        type
    ) {

        type =
            type || "info";


        let container =
            document.getElementById(
                "notificationContainer"
            );


        if (!container) {

            container =
                document.createElement(
                    "div"
                );

            container.id =
                "notificationContainer";

            container.className =
                "notification-container";

            document.body.appendChild(
                container
            );

        }


        const notification =
            document.createElement(
                "div"
            );


        notification.className =
            `notification notification-${type}`;


        notification.innerHTML = `
            <span class="notification-icon">
                ${getNotificationIcon(type)}
            </span>

            <span class="notification-text">
                ${escapeHtml(message)}
            </span>

            <button
                type="button"
                class="notification-close"
                aria-label="Close notification"
            >
                ×
            </button>
        `;


        container.appendChild(
            notification
        );


        requestAnimationFrame(
            function () {

                notification.classList.add(
                    "notification-visible"
                );

            }
        );


        const closeButton =
            notification.querySelector(
                ".notification-close"
            );


        function removeNotification() {

            notification.classList.remove(
                "notification-visible"
            );


            setTimeout(
                function () {

                    notification.remove();

                },
                250
            );

        }


        if (closeButton) {

            closeButton.addEventListener(
                "click",
                removeNotification
            );

        }


        setTimeout(
            removeNotification,
            5000
        );

    }


    function getNotificationIcon(type) {

        switch (type) {

            case "success":
                return "✓";

            case "error":
                return "!";

            case "warning":
                return "⚠";

            default:
                return "i";

        }

    }


    /* ---------------------------------------------------------
       HTML ESCAPE
       --------------------------------------------------------- */

    function escapeHtml(value) {

        return String(value)
            .replace(
                /&/g,
                "&amp;"
            )
            .replace(
                /</g,
                "&lt;"
            )
            .replace(
                />/g,
                "&gt;"
            )
            .replace(
                /"/g,
                "&quot;"
            )
            .replace(
                /'/g,
                "&#039;"
            );

    }


    /* ---------------------------------------------------------
       ACTIVE NAVIGATION
       --------------------------------------------------------- */

    function initializeActiveNavigation() {

        const currentPath =
            window.location.pathname
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


                if (
                    href.startsWith("#") ||
                    href.startsWith("http")
                ) {
                    return;
                }


                const linkPath =
                    href
                        .split("?")[0]
                        .split("#")[0]
                        .toLowerCase();


                const normalizedCurrent =
                    currentPath.endsWith("/")
                        ? currentPath + "index.html"
                        : currentPath;


                const normalizedLink =
                    linkPath.startsWith("/")
                        ? linkPath
                        : "/" + linkPath;


                if (
                    normalizedCurrent.endsWith(
                        normalizedLink
                    )
                ) {

                    link.classList.add(
                        "active"
                    );

                }

            });

    }


    /* ---------------------------------------------------------
       ACCESSIBLE FOCUS
       --------------------------------------------------------- */

    function initializeKeyboardFocus() {

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


    /* ---------------------------------------------------------
       INITIALIZATION
       --------------------------------------------------------- */

    document.addEventListener(
        "DOMContentLoaded",
        function () {

            initializeAccessibility();

            initializeLanguageSelector();

            initializeMobileMenu();

            initializeSmoothScroll();

            initializeScrollTop();

            initializeCurrentYear();

            initializeExternalLinks();

            initializeButtonLoading();

            initializeActiveNavigation();

            initializeKeyboardFocus();

        }
    );


    /* ---------------------------------------------------------
       GLOBAL API
       --------------------------------------------------------- */

    window.showNotification =
        showNotification;


})();