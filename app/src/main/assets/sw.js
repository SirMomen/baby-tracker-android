const CACHE = "baby-tracker-v3";
const ASSETS = ["./", "index.html", "manifest.webmanifest", "icon.svg", "icon-192.png", "icon-512.png", "apple-touch-icon.png"];

self.addEventListener("install", e => {
  e.waitUntil(caches.open(CACHE).then(c => c.addAll(ASSETS)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", e => {
  e.waitUntil(
    caches.keys()
      .then(keys => Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

// Network first, fall back to cache so the app also opens offline.
self.addEventListener("fetch", e => {
  if (e.request.method !== "GET") return;
  e.respondWith(
    fetch(e.request)
      .then(res => {
        const copy = res.clone();
        caches.open(CACHE).then(c => c.put(e.request, copy));
        return res;
      })
      .catch(() => caches.match(e.request))
  );
});

// Tapping a notification (or its Done / Snooze button) hands control to the app.
self.addEventListener("notificationclick", e => {
  const action = e.action || "";
  const rid = e.notification.data && e.notification.data.rid;
  e.notification.close();
  e.waitUntil(
    clients.matchAll({ type: "window", includeUncached: true }).then(list => {
      if (list.length) {
        if (action) { list[0].postMessage({ type: "notif-action", action, rid }); return; }
        return list[0].focus();
      }
      const q = action ? "?na=" + encodeURIComponent(action) + "&rid=" + encodeURIComponent(rid) : "";
      return clients.openWindow("./index.html" + q);
    })
  );
});
