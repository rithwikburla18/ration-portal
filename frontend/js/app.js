/* =========================================================
   RATION PORTAL — MAIN APPLICATION JAVASCRIPT
   ========================================================= */

(function () {
    "use strict";

    /* ---------------------------------------------------------
       API CONFIGURATION
       --------------------------------------------------------- */

    const API =
        window.RATION_API_BASE ||
        "https://ration-portal-backend.onrender.com/api";

    const TOKEN_KEY = "rationPortalToken";
    const USER_KEY = "rationPortalUser";


    /* ---------------------------------------------------------
       AUTHENTICATION UI
       --------------------------------------------------------- */

    function initializeAuthUI() {

        const authArea = document.getElementById("authArea");

        if (!authArea) {
            return;
        }

        const token = sessionStorage.getItem(TOKEN_KEY);
        const storedUser = sessionStorage.getItem(USER_KEY);

        let user = null;

        if (storedUser) {
            try {
                user = JSON.parse(storedUser);
            } catch (error) {
                console.warn("Unable to read stored user information.");
            }
        }

        /* ---------------------------------------------
           NOT LOGGED IN
           --------------------------------------------- */

        if (!token) {

            authArea.innerHTML = `
                <a href="pages/login.html" class="nav-login-btn">
                    <span class="nav-login-icon">↪</span>
                    <span>Citizen Login</span>
                </a>
            `;

            return;
        }


        /* ---------------------------------------------
           LOGGED IN
           --------------------------------------------- */

        const name =
            user?.name ||
            user?.fullName ||
            user?.username ||
            user?.email ||
            "Citizen";

        const role =
            user?.role ||
            "CITIZEN";

        const initials = getInitials(name);

        authArea.innerHTML = `
            <div class="profile-menu-wrapper">

                <button
                    type="button"
                    class="profile-trigger"
                    id="profileTrigger"
                    aria-expanded="false"
                    aria-haspopup="true"
                >

                    <span class="profile-avatar">
                        ${escapeHtml(initials)}
                    </span>

                    <span class="profile-trigger-text">
                        <strong>${escapeHtml(name)}</strong>
                        <small>${escapeHtml(role)}</small>
                    </span>

                    <span class="profile-arrow">⌄</span>

                </button>


                <div
                    class="profile-dropdown"
                    id="profileDropdown"
                    hidden
                >

                    <div class="profile-dropdown-header">

                        <span class="profile-avatar profile-avatar-large">
                            ${escapeHtml(initials)}
                        </span>

                        <div>
                            <strong>${escapeHtml(name)}</strong>
                            <small>${escapeHtml(role)}</small>
                        </div>

                    </div>


                    <div class="profile-dropdown-divider"></div>


                    <a href="#" class="profile-menu-item" data-profile-action="account">
                        <span class="profile-menu-icon">◉</span>
                        <span>Account</span>
                    </a>


                    <button
                        type="button"
                        class="profile-menu-item"
                        data-profile-action="theme"
                    >
                        <span class="profile-menu-icon">◐</span>
                        <span>Theme</span>
                    </button>


                    <div class="profile-dropdown-divider"></div>


                    <button
                        type="button"
                        class="profile-menu-item profile-logout"
                        data-profile-action="logout"
                    >
                        <span class="profile-menu-icon">↪</span>
                        <span>Log out</span>
                    </button>

                </div>

            </div>
        `;


        setupProfileMenu();
    }


    /* ---------------------------------------------------------
       PROFILE MENU
       --------------------------------------------------------- */

    function setupProfileMenu() {

        const trigger = document.getElementById("profileTrigger");
        const dropdown = document.getElementById("profileDropdown");

        if (!trigger || !dropdown) {
            return;
        }


        trigger.addEventListener("click", function (event) {

            event.stopPropagation();

            const isOpen =
                trigger.getAttribute("aria-expanded") === "true";

            trigger.setAttribute(
                "aria-expanded",
                String(!isOpen)
            );

            dropdown.hidden = isOpen;

        });


        document.addEventListener("click", function (event) {

            if (
                !dropdown.contains(event.target) &&
                !trigger.contains(event.target)
            ) {
                closeProfileMenu();
            }

        });


        document.addEventListener("keydown", function (event) {

            if (event.key === "Escape") {
                closeProfileMenu();
            }

        });


        dropdown.addEventListener("click", function (event) {

            const actionElement =
                event.target.closest("[data-profile-action]");

            if (!actionElement) {
                return;
            }

            const action =
                actionElement.getAttribute("data-profile-action");

            if (action === "logout") {

                event.preventDefault();

                logout();

            } else if (action === "theme") {

                event.preventDefault();

                toggleTheme();

            } else if (action === "account") {

                event.preventDefault();

                closeProfileMenu();

                showAccountMessage();

            }

        });

    }


    function closeProfileMenu() {

        const trigger = document.getElementById("profileTrigger");
        const dropdown = document.getElementById("profileDropdown");

        if (!trigger || !dropdown) {
            return;
        }

        trigger.setAttribute("aria-expanded", "false");
        dropdown.hidden = true;

    }


    /* ---------------------------------------------------------
       LOGOUT
       --------------------------------------------------------- */

    function logout() {

        sessionStorage.removeItem(TOKEN_KEY);
        sessionStorage.removeItem(USER_KEY);

        window.location.href = "index.html";

    }


    /* ---------------------------------------------------------
       THEME
       --------------------------------------------------------- */

    function toggleTheme() {

        const currentTheme =
            document.documentElement.getAttribute("data-theme");

        const newTheme =
            currentTheme === "dark" ? "light" : "dark";

        document.documentElement.setAttribute(
            "data-theme",
            newTheme
        );

        localStorage.setItem(
            "rationPortalTheme",
            newTheme
        );

        closeProfileMenu();

    }


    function loadSavedTheme() {

        const savedTheme =
            localStorage.getItem("rationPortalTheme");

        if (savedTheme === "dark" || savedTheme === "light") {

            document.documentElement.setAttribute(
                "data-theme",
                savedTheme
            );

        }

    }


    /* ---------------------------------------------------------
       ACCOUNT MESSAGE
       --------------------------------------------------------- */

    function showAccountMessage() {

        const user =
            getStoredUser();

        const name =
            user?.name ||
            user?.fullName ||
            user?.username ||
            "Citizen";

        const role =
            user?.role ||
            "CITIZEN";

        alert(
            "Citizen Account\n\n" +
            "Name: " + name + "\n" +
            "Role: " + role
        );

    }


    /* ---------------------------------------------------------
       RATION CARD SEARCH
       --------------------------------------------------------- */

    async function trackCard() {

        const input =
            document.getElementById("rationNumber");

        const result =
            document.getElementById("result");

        if (!input || !result) {
            return;
        }


        const rationNumber =
            input.value.trim();


        /* ---------------------------------------------
           EMPTY INPUT
           --------------------------------------------- */

        if (!rationNumber) {

            result.innerHTML = `
                <div class="search-message search-warning">
                    <strong>Ration Card Number Required</strong>
                    <p>
                        Please enter a valid ration card number
                        to continue.
                    </p>
                </div>
            `;

            return;
        }


        /* ---------------------------------------------
           LOADING
           --------------------------------------------- */

        result.innerHTML = `
            <div class="search-message search-loading">
                <div class="loading-spinner"></div>
                <strong>Searching ration card...</strong>
                <p>Please wait while we retrieve the details.</p>
            </div>
        `;


        try {

            const response = await fetch(
                `${API}/ration-cards/${encodeURIComponent(rationNumber)}`
            );


            /* -----------------------------------------
               UNAUTHORIZED
               ----------------------------------------- */

            if (response.status === 401) {

                sessionStorage.removeItem(TOKEN_KEY);
                sessionStorage.removeItem(USER_KEY);

                window.location.href =
                    "pages/login.html?redirect=" +
                    encodeURIComponent(
                        window.location.pathname +
                        window.location.search
                    );

                return;
            }


            /* -----------------------------------------
               NOT FOUND
               ----------------------------------------- */

            if (response.status === 404) {

                result.innerHTML = `
                    <div class="search-message search-error">
                        <strong>Ration Card Not Found</strong>
                        <p>
                            No ration card was found for
                            <strong>${escapeHtml(rationNumber)}</strong>.
                        </p>
                    </div>
                `;

                return;
            }


            /* -----------------------------------------
               OTHER SERVER ERROR
               ----------------------------------------- */

            if (!response.ok) {

                throw new Error(
                    "Server returned status " +
                    response.status
                );

            }


            const data =
                await response.json();


            /* -----------------------------------------
               DISPLAY RESULT
               ----------------------------------------- */

            displayRationCard(data);

        } catch (error) {

            console.error(
                "Ration card search error:",
                error
            );


            result.innerHTML = `
                <div class="search-message search-error">
                    <strong>Unable to Retrieve Details</strong>
                    <p>
                        The ration service could not be reached.
                        Please try again after some time.
                    </p>
                </div>
            `;

        }

    }


    /* ---------------------------------------------------------
       DISPLAY RATION CARD
       --------------------------------------------------------- */

    function displayRationCard(data) {

        const result =
            document.getElementById("result");

        if (!result) {
            return;
        }


        const rationCardNumber =
            data.rationCardNumber ??
            data.Ration_id ??
            data.ration_id ??
            "—";


        const cardType =
            data.cardType ??
            data.type ??
            "—";


        const headOfFamily =
            data.headOfFamily ??
            data.Name ??
            data.name ??
            "—";


        const district =
            data.district ??
            "—";


        const state =
            data.state ??
            "Telangana";


        const status =
            data.status ??
            "ACTIVE";


        const statusClass =
            String(status).toUpperCase() === "ACTIVE"
                ? "status-active"
                : "status-default";


        result.innerHTML = `

            <div class="ration-result-card">

                <div class="ration-result-header">

                    <div>
                        <span class="result-eyebrow">
                            RATION CARD DETAILS
                        </span>

                        <h3>
                            ${escapeHtml(String(rationCardNumber))}
                        </h3>
                    </div>

                    <span class="ration-status ${statusClass}">
                        ${escapeHtml(String(status))}
                    </span>

                </div>


                <div class="ration-result-grid">

                    <div class="result-field">

                        <span class="result-field-label">
                            Ration Card Number
                        </span>

                        <strong>
                            ${escapeHtml(String(rationCardNumber))}
                        </strong>

                    </div>


                    <div class="result-field">

                        <span class="result-field-label">
                            Card Type
                        </span>

                        <strong>
                            ${escapeHtml(String(cardType))}
                        </strong>

                    </div>


                    <div class="result-field">

                        <span class="result-field-label">
                            Head of Family
                        </span>

                        <strong>
                            ${escapeHtml(String(headOfFamily))}
                        </strong>

                    </div>


                    <div class="result-field">

                        <span class="result-field-label">
                            District
                        </span>

                        <strong>
                            ${escapeHtml(String(district))}
                        </strong>

                    </div>


                    <div class="result-field">

                        <span class="result-field-label">
                            State
                        </span>

                        <strong>
                            ${escapeHtml(String(state))}
                        </strong>

                    </div>


                    <div class="result-field">

                        <span class="result-field-label">
                            Status
                        </span>

                        <strong class="${statusClass}">
                            ${escapeHtml(String(status))}
                        </strong>

                    </div>

                </div>

            </div>
        `;

    }


    /* ---------------------------------------------------------
       SEARCH FORM EVENT
       --------------------------------------------------------- */

    function initializeSearchForm() {

        const form =
            document.getElementById("rationSearchForm");

        if (form) {

            form.addEventListener(
                "submit",
                function (event) {

                    event.preventDefault();

                    trackCard();

                }
            );

        }


        const searchButton =
            document.getElementById("searchButton");

        if (
            searchButton &&
            !form
        ) {

            searchButton.addEventListener(
                "click",
                function (event) {

                    event.preventDefault();

                    trackCard();

                }
            );

        }


        const input =
            document.getElementById("rationNumber");

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

    }


    /* ---------------------------------------------------------
       MOBILE NAVIGATION
       --------------------------------------------------------- */

    function initializeMobileNavigation() {

        const menuButton =
            document.getElementById("mobileMenuButton");

        const navigation =
            document.getElementById("mainNavigation");

        if (!menuButton || !navigation) {
            return;
        }


        menuButton.addEventListener(
            "click",
            function () {

                const expanded =
                    menuButton.getAttribute("aria-expanded") === "true";

                menuButton.setAttribute(
                    "aria-expanded",
                    String(!expanded)
                );

                navigation.classList.toggle(
                    "mobile-nav-open"
                );

            }
        );

    }


    /* ---------------------------------------------------------
       STORED USER
       --------------------------------------------------------- */

    function getStoredUser() {

        const stored =
            sessionStorage.getItem(USER_KEY);

        if (!stored) {
            return null;
        }

        try {

            return JSON.parse(stored);

        } catch (error) {

            return null;

        }

    }


    /* ---------------------------------------------------------
       INITIALS
       --------------------------------------------------------- */

    function getInitials(name) {

        if (!name) {
            return "C";
        }

        const words =
            String(name)
                .trim()
                .split(/\s+/)
                .filter(Boolean);


        if (words.length === 1) {

            return words[0]
                .substring(0, 2)
                .toUpperCase();

        }


        return (
            words[0].charAt(0) +
            words[words.length - 1].charAt(0)
        ).toUpperCase();

    }


    /* ---------------------------------------------------------
       HTML ESCAPING
       --------------------------------------------------------- */

    function escapeHtml(value) {

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    }


    /* ---------------------------------------------------------
       GLOBAL FUNCTIONS
       --------------------------------------------------------- */

    window.trackCard = trackCard;
    window.logout = logout;


    /* ---------------------------------------------------------
       PAGE INITIALIZATION
       --------------------------------------------------------- */

    document.addEventListener(
        "DOMContentLoaded",
        function () {

            loadSavedTheme();

            initializeAuthUI();

            initializeSearchForm();

            initializeMobileNavigation();

        }
    );

})();