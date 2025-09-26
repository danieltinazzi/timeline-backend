# Timeline

Timeline is a retro-style, feature-rich social network built with Spring Boot and NextJS.
The goal of this project is to combine modern backend architectures with the UI and features of early social networks.

---

## 🚀 Key Technologies

- **Spring Boot** (Java 21)  
- **PostgreSQL** (relational database)  
- **Docker Compose V2** (simple orchestration)
- **GitHub Actions** (auto-deploy on GHCR)  

---

## 📦 Prerequisites

- [Docker](https://docs.docker.com/get-docker/) + [Docker Compose V2](https://docs.docker.com/compose/)  
- (Optional) JDK 21 and Gradle if you want to run without Docker

---

## ▶️ Quick Start

### Development Mode (local build)

Builds and runs the containers (backend, Postgres) from source:

```
docker compose -f compose.yml up
```

- Backend available at `http://localhost:8080`  
- Postgres as a docker container

### Production Mode (pre-built images)

Uses pre-built images automatically published to GitHub Container Registry via GitHub Actions:

```
docker compose -f compose.yml -f compose.prod.yml up -d
```

- No local build performed  
- Faster and more stable startup  
- Designed for remote server deployment

---

## ⚙️ Configuration

Sensitive environment variables are loaded from a `.env` file (not included in the repo).  
A sample file is available:

```
cp .env.example .env
```

---

## 📂 Project Structure

- `src/` → Spring Boot source code  
- `compose.yml` → base configuration for local build  
- `compose.prod.yml` → configuration for pre-built images (GHCR)  
- `Dockerfile` → backend image definition  

---

## 📌 Project Status and current features

- 🔨 Work in progress  
- Authentication with JWT tokens  
- Passwords hashed with bcrypt  
- CI/CD with GitHub Actions deploying to GHCR  
- Simple orchestration with Docker Compose  
- Goal: complete backend for a feature-rich social network

---

## 🤝 Contributing

The project is open and public:  

- Feel free to open **Issues** or submit **Pull Requests**  
- Recommended workflow: Fork → branch → PR  

---

## 📜 License

Released under the [MIT License](LICENSE)