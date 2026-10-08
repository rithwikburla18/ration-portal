(function () {
    "use strict";

    if ("serviceWorker" in navigator) {
        window.addEventListener("load", function () {
            navigator.serviceWorker
                .register("./service-worker.js", { scope: "./" })
                .then(function () {
                    console.log("Ration Portal PWA service worker registered.");
                })
                .catch(function (error) {
                    console.error("Ration Portal PWA service worker registration failed:", error);
                });
        });
    }
})();
