/* =========================================================
   RATION PORTAL — AUTHENTICATION GUARD
   ========================================================= */

(function () {
    "use strict";


    /* ---------------------------------------------------------
       CONFIGURATION
       --------------------------------------------------------- */

    const TOKEN_KEY =
        "rationPortalToken";

    const USER_KEY =
        "rationPortalUser";

    const API =
        window.RATION_API_BASE ||
        "https://ration-portal-backend.onrender.com/api";


    /* ---------------------------------------------------------
       CURRENT PAGE
       --------------------------------------------------------- */

    const path =
        window.location.pathname;


    const isHomePage =
        path.endsWith("/") ||
        path.endsWith("/index.html");


    /* ---------------------------------------------------------
       TOKEN
       --------------------------------------------------------- */

    const token =
        sessionStorage.getItem(
            TOKEN_KEY
        );


    /* ---------------------------------------------------------
       PROTECT PRIVATE PAGES
       --------------------------------------------------------- */

    if (
        !token &&
        !isHomePage
    ) {

        const loginPath =
            path.includes("/pages/")
                ? "login.html"
                : "pages/login.html";


        const redirect =
            encodeURIComponent(
                path +
                window.location.search
            );


        window.location.replace(
            loginPath +
            "?redirect=" +
            redirect
        );


        return;

    }


    /* ---------------------------------------------------------
       AUTHENTICATED API REQUESTS
       --------------------------------------------------------- */

    const originalFetch =
        window.fetch.bind(
            window
        );


    window.fetch =
        async function (
            input,
            init
        ) {

            const url =
                typeof input === "string"
                    ? input
                    : (
                        input &&
                        input.url
                    ) || "";


            const isApiRequest =
                url.startsWith(API) ||
                url.startsWith("/api/");


            const currentToken =
                sessionStorage.getItem(
                    TOKEN_KEY
                );


            if (
                isApiRequest &&
                currentToken
            ) {

                const options =
                    init
                        ? {
                            ...init
                        }
                        : {};


                const headers =
                    new Headers(
                        options.headers ||
                        {}
                    );


                headers.set(
                    "Authorization",
                    "Bearer " +
                    currentToken
                );


                options.headers =
                    headers;


                init =
                    options;

            }


            const response =
                await originalFetch(
                    input,
                    init
                );


            /* ---------------------------------------------
               SESSION EXPIRED / UNAUTHORIZED
               --------------------------------------------- */

            if (
                isApiRequest &&
                response.status === 401
            ) {

                sessionStorage.removeItem(
                    TOKEN_KEY
                );


                sessionStorage.removeItem(
                    USER_KEY
                );


                const loginPath =
                    path.includes("/pages/")
                        ? "login.html"
                        : "pages/login.html";


                const redirect =
                    encodeURIComponent(
                        path +
                        window.location.search
                    );


                window.location.replace(
                    loginPath +
                    "?redirect=" +
                    redirect
                );

            }


            return response;

        };


})();