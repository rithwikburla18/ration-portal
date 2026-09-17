/* =====================================================
   RATION PORTAL
   STAGE 2 - GOVERNMENT STYLE UI
   ===================================================== */


/* =====================================================
   WAIT FOR PAGE
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {


    console.log(
        "ðŸ‡®ðŸ‡³ Ration Portal Stage 2 loaded successfully."
    );


    /* =================================================
       ELEMENTS
       ================================================= */

    const decreaseFont =
        document.getElementById("decreaseFont");

    const normalFont =
        document.getElementById("normalFont");

    const increaseFont =
        document.getElementById("increaseFont");

    const languageSelect =
        document.getElementById("languageSelect");

    const mobileMenuButton =
        document.getElementById("mobileMenuButton");

    const mainNavigation =
        document.getElementById("mainNavigation");

    const rationSearchForm =
        document.getElementById("rationSearchForm");

    const rationCardNumber =
        document.getElementById("rationCardNumber");

    const searchMessage =
        document.getElementById("searchMessage");

    const citizenLoginButton =
        document.getElementById("citizenLoginButton");


    /* =================================================
       FONT SIZE - DECREASE
       ================================================= */

    decreaseFont.addEventListener(
        "click",
        function () {

            document.body.classList.remove(
                "font-large"
            );

            document.body.classList.add(
                "font-small"
            );

        }
    );


    /* =================================================
       FONT SIZE - NORMAL
       ================================================= */

    normalFont.addEventListener(
        "click",
        function () {

            document.body.classList.remove(
                "font-small"
            );

            document.body.classList.remove(
                "font-large"
            );

        }
    );


    /* =================================================
       FONT SIZE - INCREASE
       ================================================= */

    increaseFont.addEventListener(
        "click",
        function () {

            document.body.classList.remove(
                "font-small"
            );

            document.body.classList.add(
                "font-large"
            );

        }
    );


    /* =================================================
       LANGUAGE SELECTOR
       ================================================= */

    languageSelect.addEventListener(
        "change",
        function () {

            const selectedLanguage =
                languageSelect.value;


            if (selectedLanguage === "hi") {

                alert(
                    "Hindi language support will be implemented in a later stage."
                );

            }
            else if (selectedLanguage === "te") {

                alert(
                    "Telugu language support will be implemented in a later stage."
                );

            }
            else {

                console.log(
                    "English language selected."
                );

            }

        }
    );


    /* =================================================
       MOBILE MENU
       ================================================= */

    mobileMenuButton.addEventListener(
        "click",
        function () {

            const isOpen =
                mainNavigation.classList.toggle(
                    "mobile-open"
                );


            mobileMenuButton.setAttribute(
                "aria-expanded",
                isOpen
            );


            if (isOpen) {

                mobileMenuButton.textContent = "âœ•";

            }
            else {

                mobileMenuButton.textContent = "â˜°";

            }

        }
    );


    /* =================================================
       RATION SEARCH - DEMO
       ================================================= */

    rationSearchForm.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();


            const rationNumber =
                rationCardNumber.value.trim();


            if (rationNumber === "") {

                searchMessage.textContent =
                    "Please enter a Ration Card Number.";

                return;

            }


            searchMessage.textContent =
                "Search functionality will be connected to the backend in a later stage.";


            console.log(
                "Ration Card Number:",
                rationNumber
            );

        }
    );


    /* =================================================
       CITIZEN LOGIN
       ================================================= */

    citizenLoginButton.addEventListener(
        "click",
        function () {

            alert(
                "Citizen Login will be implemented in a later stage."
            );

        }
    );


    /* =================================================
       SERVICE BUTTONS
       ================================================= */

    const serviceButtons =
        document.querySelectorAll(
            ".service-button"
        );


    serviceButtons.forEach(
        function (button) {

            button.addEventListener(
                "click",
                function () {

                    alert(
                        "This citizen service will be implemented in a later stage."
                    );

                }
            );

        }
    );


    /* =================================================
       HELP BUTTON
       ================================================= */

    const helpButton =
        document.querySelector(".help-button");


    helpButton.addEventListener(
        "click",
        function () {

            alert(
                "Help and Support functionality will be implemented in a later stage."
            );

        }
    );


    /* =================================================
       NAVIGATION LINKS
       ================================================= */

    const navigationLinks =
        document.querySelectorAll(".nav-link");


    navigationLinks.forEach(
        function (link) {

            link.addEventListener(
                "click",
                function () {

                    navigationLinks.forEach(
                        function (item) {

                            item.classList.remove(
                                "active"
                            );

                        }
                    );


                    link.classList.add(
                        "active"
                    );


                    /* Close mobile navigation */

                    mainNavigation.classList.remove(
                        "mobile-open"
                    );


                    mobileMenuButton.textContent =
                        "â˜°";


                    mobileMenuButton.setAttribute(
                        "aria-expanded",
                        "false"
                    );

                }
            );

        }
    );


});