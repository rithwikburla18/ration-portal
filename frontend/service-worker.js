const CACHE_NAME = "ration-portal-pwa-v1";

const APP_SHELL = [
    "./",
    "./index.html",
    "./manifest.json",
    "./css/style.css",
    "./css/redesign.css",
    "./js/app.js",
    "./js/script.js",
    "./js/pwa.js",
    "./assets/icons/icon-192.png",
    "./assets/icons/icon-512.png"
];

self.addEventListener("install", event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(APP_SHELL))
            .then(() => self.skipWaiting())
    );
});

self.addEventListener("activate", event => {
    event.waitUntil(
        caches.keys()
            .then(keys =>
                Promise.all(
                    keys
                        .filter(key => key !== CACHE_NAME)
                        .map(key => caches.delete(key))
                )
            )
            .then(() => self.clients.claim())
    );
});

self.addEventListener("fetch", event => {
    if (event.request.method !== "GET") {
        return;
    }

    const requestUrl = new URL(event.request.url);

    if (requestUrl.origin !== self.location.origin) {
        return;
    }

    if (event.request.mode === "navigate") {
        event.respondWith(
            fetch(event.request)
                .then(response => {
                    const responseClone = response.clone();

                    caches.open(CACHE_NAME)
                        .then(cache => cache.put("./index.html", responseClone));

                    return response;
                })
                .catch(() => caches.match("./index.html"))
        );

        return;
    }

    event.respondWith(
        caches.match(event.request)
            .then(cachedResponse => {
                if (cachedResponse) {
                    return cachedResponse;
                }

                return fetch(event.request)
                    .then(response => {
                        if (!response || response.status !== 200) {
                            return response;
                        }

                        const responseClone = response.clone();

                        caches.open(CACHE_NAME)
                            .then(cache => cache.put(event.request, responseClone));

                        return response;
                    });
            })
    );
});
