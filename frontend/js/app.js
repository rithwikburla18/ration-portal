/* =========================================================
   RATION PORTAL - MAIN APPLICATION JAVASCRIPT
   File: frontend/js/app.js
   Purpose:
   - Authentication UI
   - Profile menu
   - Logout
   - Theme handling
   - Ration card search
   - Home-page search
   - Mobile navigation
   - Common API helpers
   ========================================================= */

(function () {
    "use strict";

    /* =====================================================
       CONFIGURATION
       ===================================================== */

    const API_BASE =
        window.RATION_API_BASE ||
        "https://ration-portal-backend.onrender.com/api";

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";

    /* =====================================================
       COMMON HELPERS
       ===================================================== */

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getStoredUser() {
        try {
            const rawUser = localStorage.getItem(USER_KEY);
            return rawUser ? JSON.parse(rawUser) : null;
        } catch (error) {
            console.error("Unable to read stored user:", error);
            return null;
        }
    }

    function setStoredUser(user) {
        if (!user) {
            localStorage.removeItem(USER_KEY);
            return;
        }

        localStorage.setItem(USER_KEY, JSON.stringify(user));
    }

    function clearAuthentication() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        sessionStorage.removeItem("rationPortalRedirect");
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) {
            return "";
        }

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function formatValue(value, fallback) {
        if (
            value === null ||
            value === undefined ||
            String(value).trim() === ""
        ) {
            return fallback || "Not available";
        }

        return escapeHtml(value);
    }

    function getPagePath(page) {
        const currentPage = window.location.pathname;

        if (
            currentPage.includes("/pages/") &&
            !page.startsWith("../")
        ) {
            return page;
        }

        return page;
    }

    function navigateTo(url) {
        window.location.href = url;
    }

    /* =====================================================
       API HELPER
       ===================================================== */

    async function apiFetch(endpoint, options) {
        const requestOptions = options || {};
        const headers = new Headers(requestOptions.headers || {});

        const token = getToken();

        if (token) {
            headers.set("Authorization", "Bearer " + token);
        }

        if (
            requestOptions.body &&
            typeof requestOptions.body === "object" &&
            !(requestOptions.body instanceof FormData) &&
            !headers.has("Content-Type")
        ) {
            headers.set("Content-Type", "application/json");
        }

        const response = await fetch(
            API_BASE + endpoint,
            {
                ...requestOptions,
                headers: headers
            }
        );

        if (response.status === 401) {
            clearAuthentication();

            const currentPath = window.location.pathname;

            if (
                !currentPath.endsWith("/login.html") &&
                !currentPath.endsWith("/")
            ) {
                sessionStorage.setItem(
                    "rationPortalRedirect",
                    window.location.href
                );

                navigateTo(
                    currentPath.includes("/pages/")
                        ? "login.html"
                        : "pages/login.html"
                );
            }
        }

        return response;
    }

    /* =====================================================
       PROFILE / AUTHENTICATION UI
       ===================================================== */

    function updateAuthenticationUI() {
        const token = getToken();
        const user = getStoredUser();

        const loginLinks =
            document.querySelectorAll("[data-auth-login]");

        const registerLinks =
            document.querySelectorAll("[data-auth-register]");

        const logoutButtons =
            document.querySelectorAll("[data-auth-logout]");

        const profileNames =
            document.querySelectorAll("[data-profile-name]");

        const profileEmails =
            document.querySelectorAll("[data-profile-email]");

        const profileMenus =
            document.querySelectorAll("[data-profile-menu]");

        if (token) {
            loginLinks.forEach(function (element) {
                element.style.display = "none";
            });

            registerLinks.forEach(function (element) {
                element.style.display = "none";
            });

            logoutButtons.forEach(function (element) {
                element.style.display = "";
            });

            profileMenus.forEach(function (element) {
                element.style.display = "";
            });

            profileNames.forEach(function (element) {
                element.textContent =
                    user && user.name
                        ? user.name
                        : user && user.email
                            ? user.email
                            : "Citizen";
            });

            profileEmails.forEach(function (element) {
                element.textContent =
                    user && user.email
                        ? user.email
                        : "";
            });
        } else {
            loginLinks.forEach(function (element) {
                element.style.display = "";
            });

            registerLinks.forEach(function (element) {
                element.style.display = "";
            });

            logoutButtons.forEach(function (element) {
                element.style.display = "none";
            });

            profileMenus.forEach(function (element) {
                element.style.display = "none";
            });
        }
    }

    function setupProfileMenu() {
        const profileButtons =
            document.querySelectorAll("[data-profile-button]");

        profileButtons.forEach(function (button) {
            button.addEventListener("click", function (event) {
                event.stopPropagation();

                const menu =
                    button.closest("[data-profile-menu]");

                if (!menu) {
                    return;
                }

                const dropdown =
                    menu.querySelector("[data-profile-dropdown]");

                if (!dropdown) {
                    return;
                }

                dropdown.classList.toggle("open");
            });
        });

        document.addEventListener("click", function () {
            document
                .querySelectorAll("[data-profile-dropdown]")
                .forEach(function (dropdown) {
                    dropdown.classList.remove("open");
                });
        });

        document
            .querySelectorAll("[data-profile-dropdown]")
            .forEach(function (dropdown) {
                dropdown.addEventListener(
                    "click",
                    function (event) {
                        event.stopPropagation();
                    }
                );
            });
    }

    function logout() {
        clearAuthentication();

        showNotification(
            "You have been logged out successfully.",
            "success"
        );

        setTimeout(function () {
            const currentPath = window.location.pathname;

            if (currentPath.includes("/pages/")) {
                navigateTo("login.html");
            } else {
                navigateTo("pages/login.html");
            }
        }, 500);
    }

    function setupLogoutButtons() {
        document
            .querySelectorAll("[data-auth-logout]")
            .forEach(function (button) {
                button.addEventListener("click", function (event) {
                    event.preventDefault();
                    logout();
                });
            });
    }

    /* =====================================================
       RATION CARD SEARCH
       ===================================================== */

    async function searchRationCard(rationNumber) {
        const cleanNumber =
            String(rationNumber || "").trim();

        if (!cleanNumber) {
            throw new Error("EMPTY_RATION_NUMBER");
        }

        const response = await apiFetch(
            "/ration-cards/" +
                encodeURIComponent(cleanNumber),
            {
                method: "GET"
            }
        );

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("RATION_CARD_NOT_FOUND");
            }

            throw new Error("RATION_CARD_REQUEST_FAILED");
        }

        return response.json();
    }

    function renderRationCardResult(card, resultElement) {
        if (!resultElement) {
            return;
        }

        const rationCardNumber =
            card.rationCardNumber ||
            card.rationNumber ||
            card.cardNumber;

        const cardType = card.cardType;
        const headOfFamily =
            card.headOfFamily ||
            card.headOfFamilyName;

        const address = card.address;
        const district = card.district;
        const state = card.state;
        const status = card.status;

        resultElement.innerHTML = `
            <div class="card card-padding">
                <div class="section-title">
                    <span>SEARCH RESULT</span>
                    <h2>Ration Card Details</h2>
                </div>

                <div class="form-grid">

                    <div class="form-group">
                        <label>Ration Card Number</label>
                        <div class="form-control">
                            ${formatValue(
                                rationCardNumber,
                                "Not available"
                            )}
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Card Type</label>
                        <div class="form-control">
                            ${formatValue(
                                cardType,
                                "Not available"
                            )}
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Head of Family</label>
                        <div class="form-control">
                            ${formatValue(
                                headOfFamily,
                                "Not available"
                            )}
                        </div>
                    </div>

                    <div class="form-group">
                        <label>District</label>
                        <div class="form-control">
                            ${formatValue(
                                district,
                                "Not available"
                            )}
                        </div>
                    </div>

                    <div class="form-group">
                        <label>State</label>
                        <div class="form-control">
                            ${formatValue(
                                state,
                                "Not available"
                            )}
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Status</label>
                        <div class="form-control">
                            ${formatValue(
                                status,
                                "Not available"
                            )}
                        </div>
                    </div>

                </div>
            </div>
        `;
    }

    async function trackCard() {
        const input =
            document.getElementById("rationNumber");

        const result =
            document.getElementById("trackResult");

        if (!input || !result) {
            return;
        }

        const rationNumber = input.value.trim();

        if (!rationNumber) {
            result.innerHTML = `
                <div class="alert alert-warning">
                    Please enter a ration card number.
                </div>
            `;
            input.focus();
            return;
        }

        result.innerHTML = `
            <div class="loading">
                <span class="loading-spinner"></span>
                <span>Searching ration card...</span>
            </div>
        `;

        try {
            const card =
                await searchRationCard(rationNumber);

            renderRationCardResult(card, result);

            result.scrollIntoView({
                behavior: "smooth",
                block: "nearest"
            });
        } catch (error) {
            console.error(
                "Ration card search failed:",
                error
            );

            if (
                error.message ===
                "RATION_CARD_NOT_FOUND"
            ) {
                result.innerHTML = `
                    <div class="alert alert-warning">
                        Ration card not found. Please check the
                        ration card number and try again.
                    </div>
                `;
            } else {
                result.innerHTML = `
                    <div class="alert alert-danger">
                        Unable to retrieve ration card details.
                        Please try again later.
                    </div>
                `;
            }
        }
    }

    window.trackCard = trackCard;

    function setupRationSearch() {
        const input =
            document.getElementById("rationNumber");

        const button =
            document.querySelector(
                "[data-ration-search]"
            );

        if (input) {
            input.addEventListener(
                "keydown",
                function (event) {
                    if (event.key === "Enter") {
                        event.preventDefault();
                        trackCard();
                    }
                }
            );
        }

        if (button) {
            button.addEventListener(
                "click",
                function (event) {
                    event.preventDefault();
                    trackCard();
                }
            );
        }
    }

    /* =====================================================
       THEME
       ===================================================== */

    function setupTheme() {
        const themeButtons =
            document.querySelectorAll(
                "[data-theme-toggle]"
            );

        const savedTheme =
            localStorage.getItem("rationPortalTheme");

        if (savedTheme === "dark") {
            document.body.classList.add("dark-theme");
        }

        themeButtons.forEach(function (button) {
            button.addEventListener(
                "click",
                function () {
                    document.body.classList.toggle(
                        "dark-theme"
                    );

                    const darkMode =
                        document.body.classList.contains(
                            "dark-theme"
                        );

                    localStorage.setItem(
                        "rationPortalTheme",
                        darkMode ? "dark" : "light"
                    );
                }
            );
        });
    }

    /* =====================================================
       MOBILE NAVIGATION
       ===================================================== */

    function setupMobileNavigation() {
        const menuButtons =
            document.querySelectorAll(
                "[data-mobile-menu-toggle]"
            );

        menuButtons.forEach(function (button) {
            button.addEventListener(
                "click",
                function () {
                    const targetSelector =
                        button.getAttribute(
                            "data-mobile-menu-toggle"
                        );

                    let menu = null;

                    if (targetSelector) {
                        menu =
                            document.querySelector(
                                targetSelector
                            );
                    }

                    if (!menu) {
                        menu =
                            document.querySelector(
                                ".main-nav"
                            );
                    }

                    if (!menu) {
                        return;
                    }

                    menu.classList.toggle("open");

                    const expanded =
                        menu.classList.contains(
                            "open"
                        );

                    button.setAttribute(
                        "aria-expanded",
                        String(expanded)
                    );
                }
            );
        });

        document
            .querySelectorAll(".main-nav a")
            .forEach(function (link) {
                link.addEventListener(
                    "click",
                    function () {
                        const nav =
                            link.closest(".main-nav");

                        if (nav) {
                            nav.classList.remove(
                                "open"
                            );
                        }
                    }
                );
            });
    }

    /* =====================================================
       QUICK SERVICE NAVIGATION
       ===================================================== */

    function setupServiceLinks() {
        document
            .querySelectorAll("[data-service-url]")
            .forEach(function (element) {
                element.addEventListener(
                    "click",
                    function () {
                        const url =
                            element.getAttribute(
                                "data-service-url"
                            );

                        if (url) {
                            navigateTo(url);
                        }
                    }
                );
            });
    }

    /* =====================================================
       PROFILE DATA REFRESH
       ===================================================== */

    function refreshProfileData() {
        const user = getStoredUser();

        if (!user) {
            return;
        }

        document
            .querySelectorAll("[data-profile-name]")
            .forEach(function (element) {
                element.textContent =
                    user.name ||
                    user.email ||
                    "Citizen";
            });

        document
            .querySelectorAll("[data-profile-email]")
            .forEach(function (element) {
                element.textContent =
                    user.email || "";
            });
    }

    /* =====================================================
       CURRENT USER HELPER
       ===================================================== */

    function isAuthenticated() {
        return Boolean(getToken());
    }

    window.rationPortalAuth = {
        getToken: getToken,
        getUser: getStoredUser,
        isAuthenticated: isAuthenticated,
        logout: logout,
        apiFetch: apiFetch
    };

    /* =====================================================
       INITIALIZATION
       ===================================================== */

    function initializeApp() {
        updateAuthenticationUI();
        refreshProfileData();

        setupProfileMenu();
        setupLogoutButtons();
        setupRationSearch();
        setupTheme();
        setupMobileNavigation();
        setupServiceLinks();
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            initializeApp
        );
    } else {
        initializeApp();
    }

})();