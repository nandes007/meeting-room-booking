# Deployment Plan — roombook.nandes.tech

Deploy frontend + backend to the 8labs server (podman), published at
`roombook.nandes.tech` via Cloudflare.

## Decision: no separate backend domain

The frontend is a SPA, so the browser calls the API directly — it must be
publicly reachable. Instead of a second subdomain, the API is served under
the same domain at a path: `roombook.nandes.tech/api`.

- One DNS record, one nginx server block, zero CORS config.
- Add `api.roombook.nandes.tech` later only if other clients (mobile app,
  third parties) need the API.

## Architecture

```
Browser ──► Cloudflare ──► host nginx :80 ──► frontend container :9100
                                                ├─ /      → static SPA files
                                                └─ /api/  → api container :8080 ──► db container :5432
```

The frontend container's nginx is the single entry point. The api and db
containers are only reachable inside the compose network.

## Steps

### 1. Repo changes

- [x] Rename `docker-compose.yml` → `docker-compose.local.yml` (local setup, includes db).
- [x] Create `docker-compose.prod.yml`:
  - `db` (postgres + volume, **no published port**), `api`, `frontend`.
  - Frontend built with `VITE_API_BASE_URL=/api`.
  - Only published port: `9100:80` on the frontend.
  - Secrets (db password, etc.) from a server-side `.env` file, not committed.
- [x] Add `/api/` location to `meeting-room-booking-fe/nginx.conf`:
  proxy to `http://api:8080/`.

### 2. Server setup (8labs)

- [ ] Clone/copy the project to the server.
- [ ] Create `.env` with production db credentials.
- [ ] `podman compose -f docker-compose.prod.yml up -d --build`
  (or `podman-compose` depending on what's installed).

### 3. Host nginx

- [ ] New server block (same pattern as chinese-to-bahasa):

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name roombook.nandes.tech;

    location / {
        proxy_pass http://127.0.0.1:9100;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 3600s;
    }
}
```

- [ ] `nginx -t && systemctl reload nginx`.

### 4. Cloudflare

- [ ] Add A record: `roombook` → server IP, proxied (orange cloud).
- [ ] HTTPS handled by Cloudflare (Flexible mode, same as existing project).

### 5. Verify

- [ ] Open `https://roombook.nandes.tech`, log in.
- [ ] Confirm API calls in devtools hit `/api/...` and succeed.
- [ ] Restart containers, confirm db data survives (volume works).

## Deferred

- Origin TLS (Cloudflare Full mode + origin cert) — add for end-to-end encryption.
- Separate `api.` subdomain — add when a non-browser client needs the API.
- CI/CD — deploy is manual (`git pull` + compose up) until that gets annoying.
