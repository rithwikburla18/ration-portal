/* =========================================================
   RATION PORTAL - FRONTEND CONFIGURATION
   File: frontend/config.js

   Purpose:
   - Store the backend API base URL
   - Keep API configuration in one place
   - Allow local development and production deployment
   ========================================================= */

(function () {
    "use strict";

    /*
     * Production Spring Boot backend deployed on Render.
     *
     * All frontend API calls use:
     *
     * https://ration-portal-backend.onrender.com/api
     *
     * Example:
     * /auth/login
     *
     * becomes:
     * https://ration-portal-backend.onrender.com/api/auth/login
     */

    const PRODUCTION_API =
        "https://ration-portal-backend.onrender.com/api";

    /*
     * Local Spring Boot backend.
     *
     * Use this when running the backend locally
     * with the default Spring Boot port 8080.
     */
    const LOCAL_API =
        "https://ration-portal-backend.onrender.com/api";

    /*
     * Detect whether the frontend is being opened
     * from a local development environment.
     */
    const hostname =
        window.location.hostname.toLowerCase();

    const isLocalDevelopment =
        hostname === "localhost" ||
        hostname === "127.0.0.1";

    /*
     * Select the API automatically.
     *
     * Local frontend:
     *     localhost -> localhost:8080/api
     *
     * Production frontend:
     *     Render -> production backend
     */
    const selectedApi =
        isLocalDevelopment
            ? LOCAL_API
            : PRODUCTION_API;

    /*
     * Allow an already-defined global configuration
     * to override the automatic selection.
     *
     * This is useful for testing without modifying
     * the source code.
     */
    window.RATION_API_BASE =
        window.RATION_API_BASE ||
        selectedApi;

    /*
     * Expose useful configuration information.
     */
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
     * Development-only console information.
     *
     * This does not expose passwords, JWT tokens,
     * database credentials, or other secrets.
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