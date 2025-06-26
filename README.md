# 🎶 Musement Backend

A backend service for a concert discovery app that provides personalized recommendations, social features, and ticket management. 🚀

🎵 **Musement** is a Spring Boot REST API that:

* Integrates with Spotify to import and analyze user playlists
* Retrieves upcoming concerts through external APIs
* Generates personalized recommendations based on artist frequency in user playlists
* Provides registration and authentication (JWT + Spring Security + Google OAuth2), and session management
* Supports social features such as follows and discussion threads under concert events
* Enables storing and managing tickets and uploading media via Cloudinary
* Utilizes PostgreSQL for primary data storage and Elasticsearch for full-text search

---

## ⚙️ Prerequisites

Before running the application locally, make sure you have:

* Java 21+
* Gradle 7+
* PostgreSQL 14+ instance
* Elasticsearch 8+ cluster
* A Cloudinary account (set `CLOUDINARY_URL`)
* Spotify API credentials (`SPOTIFY_CLIENT_ID` and `SPOTIFY_SECRET`)
* Google OAuth2 credentials (`GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`)

---

## 🏗️ Architecture & Project Structure

The core modules are organized under `src/main/java/com/musement/backend`:

```
com/musement/backend/
├── config/           # Security and API configuration
├── controllers/      # REST controllers
├── documents/        # Elasticsearch document mappings
├── dto/              # Data transfer objects and mappers
├── exceptions/       # Custom exception definitions and handlers
├── models/           # JPA entity models
├── repositories/     # Spring Data JPA repositories
├── services/         # Business logic and external API integrations
└── MusementBackendApplication.java  # Application entry point

resources/
├── application.properties  # Core application settings
```

**Layers:**

* **Controllers** receive HTTP requests and delegate to services.
* **Services** implement business logic, integrate with Spotify, Cloudinary, Elasticsearch, and ticket APIs.
* **Repositories** handle persistence via Spring Data JPA.
* **Config** sets up security filters (JWT) and API configs.

---
