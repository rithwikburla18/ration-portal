# Ration Portal Backend API

Base path: `/api`. Except for health, sign-in/registration, approved-FPS lookup, and public card verification, endpoints require a JWT bearer token in the `Authorization: Bearer <token>` header. Role checks are enforced by Spring Security and/or the controller.

## Local configuration

The backend requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`. Never commit real values. For the guided PowerShell launcher, use `backend/scripts/start-local.ps1`; it deliberately asks for confirmation because Flyway migrations modify the selected database. Use a Neon development branch for local work.

Notification delivery uses `BREVO_API_KEY` and `BREVO_FROM_EMAIL`; SMS additionally uses `BREVO_SMS_SENDER`. Admin bootstrap requires `ADMIN_EMAIL` and `ADMIN_PASSWORD` to be set in the intended environment. Do not set bootstrap credentials while connecting local code to a production database.

## Authentication

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/auth/check-email` | Public | Check registration email availability |
| POST | `/auth/register` | Public | Register a citizen |
| POST | `/auth/login` | Public | Issue JWT after credential and role/status checks |
| GET | `/auth/me` | Authenticated | Read current account profile |
| POST | `/auth/forgot-password` | Public | Request a password reset |
| POST | `/auth/reset-password` | Public with reset token | Set a new password |

## Citizen and ration-card workflow

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/applications` | Citizen/Admin | Submit a ration-card application |
| GET | `/applications` | Citizen/Admin | Admin sees all; citizen sees own applications |
| GET | `/applications/{applicationNumber}` | Citizen/Admin | Read own application or admin view |
| PUT | `/applications/{applicationNumber}/status` | Admin only | Review application; approval triggers card issuance |
| GET | `/ration-cards` | Citizen/Admin | Admin list; citizen-owned approved cards |
| GET | `/ration-cards/my` | Authenticated | Read current-card approval state |
| GET | `/ration-cards/{number}` | Citizen/Admin | Read an authorized card |
| GET | `/ration-cards/{number}/pdf` | Card owner/Admin | Download digital ration-card PDF |
| GET | `/ration-cards/verify/{number}` | Public | Verify an active card by number |
| GET | `/ration-cards/admin/pending` | Admin | List pending cards |
| PUT | `/ration-cards/admin/{number}/approve` | Admin | Approve a card |
| PUT | `/ration-cards/admin/{number}/reject` | Admin | Reject a card |

## Fair-price shops and distribution

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/fps-dealer-applications/register` | Public | Submit dealer/shop registration |
| GET | `/fps-dealer-applications` | Admin | Review dealer applications |
| PUT | `/fps-dealer-applications/{id}/decision` | Admin | Approve or reject dealer |
| GET | `/fair-price-shops` | Public | Search approved shops; optional district/locality and coordinate query |
| POST | `/fps-distribution/otp` | Approved FPS dealer | Request citizen verification OTP |
| POST | `/fps-distribution/verify-otp` | Approved FPS dealer | Verify one-time OTP and receive short-lived verification token |
| POST | `/fps-distribution/submit` | Approved FPS dealer | Record monthly distribution using verified token |
| GET | `/fps-distribution/records` | Approved FPS dealer | Paginated shop-scoped records |
| GET | `/distribution-logs/ration-card/{rationCardId}` | Card owner/Admin | Read distribution history |
| GET | `/dashboard/metrics` | Citizen/Admin | Dashboard aggregates and citizen-scoped card details |
| GET | `/transparency/summary` | Authenticated | Aggregate transparency counters |

## Other citizen services

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| GET | `/family-members/ration-card/{rationCardId}` | Card owner/Admin | Read approved-card family members |
| POST | `/family-members` | Admin | Create a family member |
| POST | `/grievances` | Citizen/Admin | Submit a grievance |
| GET | `/grievances` | Admin or submitting citizen | List grievances by access scope |
| GET | `/grievances/{number}` | Admin or owner | Track an authorized grievance |
| PUT | `/grievances/{number}/status` | Admin only | Move grievance through an allowed status |
| POST | `/onorc/portability` | Card owner/Admin | Request portability record |
| GET | `/onorc/portability/{reference}` | Request owner/Admin | Read portability status |
| GET | `/onorc/my-requests` | Authenticated | List current user's portability requests |
| POST | `/import/ration-cards` | Admin | Import validated CSV records (multipart field: `file`) |
| GET | `/health` | Public | Application liveness check |

## Database migrations

Flyway migration files are under `src/main/resources/db/migration`. They run on application startup. Do not point local testing at a production database unless you intentionally accept schema migrations and existing-data backfill.
