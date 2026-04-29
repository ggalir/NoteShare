# NoteShare

A fullstack Markdown note-taking app built with a microservices architecture.
Users register with email verification, write and preview Markdown notes in a live split-view editor, and generate public shareable links to individual notes.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     Browser                             │
│              React + Bootstrap (port 3000)              │
└──────┬──────────────┬──────────────┬────────────────────┘
       │ /api/auth    │ /api/notes   │ /api/share
       ▼              ▼              ▼
┌────────────┐  ┌────────────┐  ┌─────────────┐
│   auth     │  │   notes    │  │   sharing   │
│  :8081     │  │  :8082     │  │   :8083     │
└─────┬──────┘  └─────┬──────┘  └──────┬──────┘
      │ RabbitMQ      │ PostgreSQL      │ PostgreSQL
      │               │                │ (calls notes
      ▼               ▼                │  /internal)
┌────────────┐  ┌────────────┐         │
│   mailer   │  │ postgres-  │◄────────┘
│  :8084     │  │   notes    │
└────────────┘  └────────────┘
```

## Tech stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Backend     | Java 21, Spring Boot 4, Spring Security 7       |
| Auth        | JWT (stateless, validated per-service)          |
| Messaging   | RabbitMQ (email verification queue)             |
| Database    | PostgreSQL (separate DB per service)            |
| Frontend    | React 19, TypeScript, Vite, Bootstrap 5         |
| Markdown    | react-markdown (live split-view preview)        |
| Containers  | Docker, Docker Compose                          |
| Build       | Gradle (Spring Boot), npm (frontend)            |

## Services

| Service            | Port | Responsibility                                        |
|--------------------|------|-------------------------------------------------------|
| noteshare-auth     | 8081 | Register, login, email verification, JWT issuance    |
| noteshare-notes    | 8082 | CRUD notes, JWT validation, internal note endpoint   |
| noteshare-sharing  | 8083 | Generate UUID share tokens, resolve public links     |
| noteshare-mailer   | 8084 | Consumes RabbitMQ queue, sends verification emails   |
| noteshare-frontend | 3000 | React SPA served by nginx, proxies API requests      |

## Features

- **Email verification** — registration publishes to a RabbitMQ queue; the mailer service consumes it and sends a Mailtrap email with a verification link
- **JWT authentication** — stateless; notes and sharing services validate tokens locally using the shared secret, never calling auth
- **Live Markdown editor** — split-view: raw Markdown on the left, rendered preview on the right
- **Public share links** — generate a UUID token for any note; the public `/share/:token` page requires no login
- **Single Docker Compose** — one command starts all infrastructure and application services

## Running with Docker

### 1. Clone and configure

```bash
git clone https://github.com/ggalir/NoteShare.git
cd NoteShare
cp .env.example .env
```

Edit `.env` and fill in:
- `JWT_SECRET` — any base64 string (`openssl rand -base64 48`)
- `MAILTRAP_USERNAME` / `MAILTRAP_PASSWORD` — from [mailtrap.io](https://mailtrap.io) (free)

### 2. Start everything

```bash
docker compose up --build
```

The first build takes a few minutes (Gradle downloads dependencies). Subsequent builds are cached.

### 3. Open the app

```
http://localhost:3000
```

## Running locally (for development)

Start infrastructure only:

```bash
docker compose up postgres-auth postgres-notes postgres-sharing rabbitmq -d
```

Then run each Spring Boot service from IntelliJ with its `.env` file linked in the run configuration, and the frontend with:

```bash
cd noteshare-frontend
npm install
npm run dev   # http://localhost:5173
```

## Project structure

```
NoteShare/
├── Dockerfile                  # Shared multi-stage builder for all Spring Boot services
├── docker-compose.yml
├── .env.example
├── noteshare-auth/             # Spring Boot — auth, JWT, RabbitMQ publisher
├── noteshare-notes/            # Spring Boot — CRUD notes, JWT validation
├── noteshare-sharing/          # Spring Boot — share tokens, calls notes /internal
├── noteshare-mailer/           # Spring Boot — RabbitMQ consumer, email sender
└── noteshare-frontend/         # React + TypeScript + Bootstrap
    ├── Dockerfile              # node build → nginx
    └── nginx.conf              # static files + API proxy rules
```