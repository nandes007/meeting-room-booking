# Deployment Plan — roombooking.nandes.tech

Deploy frontend + backend to the 8labs server (podman), published at
`roombooking.nandes.tech` via Cloudflare.

## Decision: no separate backend domain

The frontend is a SPA, so the browser calls the API directly — it must be
publicly reachable. Instead of a second subdomain, the API is served under
the same domain at a path: `roombooking.nandes.tech/api`.

- One DNS record, one nginx server block, zero CORS config.
- Add `api.roombooking.nandes.tech` later only if other clients (mobile app,
  third parties) need the API.

## Architecture

```
Browser ──► Cloudflare ──► host nginx :80 ──► frontend container :9100
                                                ├─ /      → static SPA files
                                                └─ /api/  → api container :8080 ──► Supabase (postgres)
```

The frontend container's nginx is the single entry point. The api container
is only reachable inside the compose network. The database is Supabase — no
db container in production.

## Steps

### 1. Repo changes

- [x] Rename `docker-compose.yml` → `docker-compose.local.yml` (local setup, includes db).
- [x] Create `docker-compose.prod.yml`:
  - `api` + `frontend` only — database is Supabase, no db container.
  - Frontend built with `VITE_API_BASE_URL=/api`.
  - Only published port: `9100:80` on the frontend.
  - Supabase connection (`DB_URL`, `DB_USER`, `DB_PASSWORD`) from a
    server-side `.env` file, not committed.
- [x] Add `/api/` location to `meeting-room-booking-fe/nginx.conf`:
  proxy to `http://api:8080/`.

### 2. Server setup (8labs)

- [ ] Clone/copy the project to the server.
- [ ] Create `.env` with the Supabase connection:

```env
DB_URL=jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres
DB_USER=postgres.<project-ref>
DB_PASSWORD=<supabase-db-password>
```

  (Values from Supabase dashboard → Connect → JDBC; use the **session pooler**
  on port 5432, not the transaction pooler on 6543 — Liquibase and Hibernate
  need session mode.)
- [ ] `podman compose -f docker-compose.prod.yml up -d --build`
  (or `podman-compose` depending on what's installed).
- [ ] Migrations run automatically on api startup (`SPRING_LIQUIBASE_ENABLED=true`);
  the Makefile targets are local-dev only (need Maven, absent in the image).
- [ ] Seed the superadmin (idempotent, one-off container):

```bash
podman-compose -f docker-compose.prod.yml run --rm api --spring.profiles.active=seed
```

### 3. Host nginx

- [ ] New server block (same pattern as chinese-to-bahasa):

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name roombooking.nandes.tech;

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

- [ ] Add A record: `roombooking` → server IP, proxied (orange cloud).
- [ ] HTTPS handled by Cloudflare (Flexible mode, same as existing project).

### 5. Verify

- [ ] Open `https://roombooking.nandes.tech`, log in.
- [ ] Confirm API calls in devtools hit `/api/...` and succeed.
- [ ] Confirm Liquibase migrations ran against Supabase (tables visible in dashboard).

## Deferred

- Origin TLS (Cloudflare Full mode + origin cert) — add for end-to-end encryption.
- Separate `api.` subdomain — add when a non-browser client needs the API.
- CI/CD — deploy is manual (`git pull` + compose up) until that gets annoying.
