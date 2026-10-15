/* Cache reliability: this static site must always revalidate HTML and load current assets.
   Retire the old cache-first worker, which could resurrect stale HTML and scripts after a normal reopen. */
(function () {
    "use strict";
    if (!("serviceWorker" in navigator)) return;
    const retireLegacyWorker = function () {
        navigator.serviceWorker.getRegistrations()
            .then(function (registrations) {
                return Promise.all(registrations.map(function (registration) {
                    return registration.unregister();
                }));
            })
            .then(function () {
                if (!("caches" in window)) return;
                return caches.keys().then(function (keys) {
                    return Promise.all(keys.filter(function (key) {
                        return /^ration-portal-pwa-/i.test(key) || /^ration-portal-pwa-v1$/i.test(key);
                    }).map(function (key) { return caches.delete(key); }));
                });
            })
            .catch(function (error) {
                console.warn("Legacy offline cache cleanup could not finish; continuing with network requests.", error);
            });
    };
    if (document.readyState === "complete") retireLegacyWorker();
    else window.addEventListener("load", retireLegacyWorker, { once: true });
})();
