/* Legacy-worker retirement.
 * Previous cache-first handling could return stale CSS/JS on a normal reopen.
 * Existing registrations are also unregistered by js/pwa.js; this worker never caches responses.
 */
const CACHE_NAME = "ration-portal-pwa-retired-v2";
self.addEventListener("install", event => {
    event.waitUntil(self.skipWaiting());
});
self.addEventListener("activate", event => {
    event.waitUntil(
        caches.keys()
            .then(keys => Promise.all(keys.filter(key => /^ration-portal-pwa-/i.test(key)).map(key => caches.delete(key))))
            .then(() => self.registration.unregister())
            .then(() => self.clients.matchAll({ type: "window", includeUncontrolled: true }))
            .then(clients => Promise.all(clients.map(client => client.navigate(client.url))))
    );
});
self.addEventListener("fetch", event => {
    if (event.request.method !== "GET" || new URL(event.request.url).origin !== self.location.origin) return;
    event.respondWith(fetch(event.request));
});
