# 🚀 Spring Boot WebFlux + Vue.js Fullstack Application

> **Modern reactive fullstack web application** with JWT authentication, built with Spring Boot WebFlux, Vue 3, and MariaDB SkySQL.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![WebFlux](https://img.shields.io/badge/WebFlux-reactive-blueviolet.svg)](https://docs.spring.io/spring-boot/reference/web/reactive.html)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5.17-4FC08D.svg)](https://vuejs.org/)
[![MariaDB](https://img.shields.io/badge/MariaDB-SkySQL-003545.svg)](https://mariadb.com/products/skysql/)

---

## 📋 Table of Contents

- [🎯 Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [⚡ Quick Start](#-quick-start)
- [🗂️ Project Structure](#️-project-structure)
- [🔧 Configuration](#-configuration)
- [🔑 API Endpoints](#-api-endpoints)
- [💻 Development](#-development)
- [🧪 Testing](#-testing)
- [🚀 Deployment](#-deployment)
- [🔍 Troubleshooting](#-troubleshooting)

---

## 🎯 Features

### **Backend (Spring Boot WebFlux)**

- ✅ **Reactive Architecture** - Full WebFlux stack with R2DBC
- ✅ **JWT Authentication** - Stateless authentication with role-based access
- ✅ **Cloud Database** - MariaDB SkySQL integration
- ✅ **Security** - Spring Security with CORS configuration
- ✅ **Database Migration** - Flyway for schema management
- ✅ **Hot Reload** - Spring DevTools for development

### **Frontend (Vue.js)**

- ✅ **Modern SPA** - Vue 3 with Composition API
- ✅ **State Management** - Pinia for reactive state
- ✅ **Routing** - Vue Router with authentication guards
- ✅ **HTTP Client** - Axios with interceptors
- ✅ **Build Tool** - Vite for fast development
- ✅ **Authentication Flow** - Login, register, logout, and protected routes

### **DevOps**

- ✅ **Containerization** - Docker & Docker Compose
- ✅ **Development Environment** - Hot reload for both frontend and backend
- ✅ **Build Automation** - Makefile for common tasks
- ✅ **Environment Configuration** - Separate dev/prod configs

---

## 🏗️ Architecture

```
┌─────────────────┐    HTTP/REST    ┌────────────────────────────┐    R2DBC    ┌─────────────────┐
│   Vue.js SPA    │ ──────────────► │   Spring WebFlux           │ ──────────► │  MariaDB SkySQL │
│  (Frontend)     │                 │    (Backend)               │             │   (Database)    │
|                 |                 |                            |             |                 |
│                 │                 │ - WebFlux (API)            │             │ - Cloud DB      │
│ - Vue 3         │                 │ - JWT Auth                 │             │ - R2DBC         │
│ - Vite          │                 │ - Security                 │             │                 │
│ - Pinia         │                 │ - R2DBC (data access)      │             │                 │
│ - Axios         │                 │ - Flyway (JDBC migrations) │             │                 │
└─────────────────┘                 └────────────────────────────┘             └─────────────────┘
```

---

## 🛠️ Tech Stack

### **Backend**

- **Java 21** - Latest LTS version
- **Spring Boot 3.5.3** - Framework
- **Spring WebFlux** - Reactive web framework
- **Spring Security** - Authentication & authorization
- **Spring Data R2DBC** - Reactive database access
- **JWT (jjwt)** - Token-based authentication
- **MariaDB R2DBC** - Database driver
- **Flyway** - Database migration
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

### **Frontend**

- **Vue 3.5.17** - Progressive framework
- **Vite 7.0.0** - Build tool
- **Pinia 2.1.7** - State management
- **Vue Router 4.2.5** - Routing
- **Axios 1.6.0** - HTTP client
- **Node.js 20** - Runtime

### **Database**

- **MariaDB SkySQL** - Cloud database
- **R2DBC** - Reactive database connectivity

### **DevOps**

- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Makefile** - Task automation

---

## ⚡ Quick Start

### **Prerequisites**

- **Docker** and **Docker Compose** installed
- **Make** (optional, for easier commands)

### **1. Clone the repository**

```bash
git clone https://github.com/your-username/spring-vue-jwt-starter.git
cd spring-vue-jwt-starter
```

### **2. Configure environment variables**

```bash
# Copy environment template
cp backend/.env.example backend/.env

# Edit with your actual values
nano backend/.env
```

### **3. Start the application**

```bash
# Using Makefile (recommended)
make dev

# Or using Docker Compose directly
docker compose -f docker-compose.dev.yml up --build
```

### **4. Access the application**

- **Frontend**: [http://localhost:5173](http://localhost:5173)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Health Check**: [http://localhost:8080/api/auth/health](http://localhost:8080/api/auth/health)

### **5. Test the application**

- Navigate to [http://localhost:5173](http://localhost:5173)
- Register a new account or login with existing credentials
- Access the protected dashboard

---

## 🗂️ Project Structure

```
.
├── backend/                          # Spring Boot WebFlux API
│   ├── src/main/java/com/fab1/backend/
│   │   ├── config/                   # Security, CORS, Database config
│   │   │   ├── DataInitializer.java
│   │   │   ├── FlywayDataSourceConfig.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/               # REST Controllers
│   │   │   ├── AuthController.java
│   │   │   ├── TestController.java
│   │   │   └── UserController.java
│   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── LoginRequest.java
│   │   │   └── LoginResponse.java
│   │   ├── model/                    # Entity classes
│   │   │   ├── Role.java
│   │   │   └── User.java
│   │   ├── repository/               # R2DBC Repositories
│   │   │   ├── RoleRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/                  # Business logic
│   │       ├── AuthService.java
│   │       ├── CustomUserDetailsService.java
│   │       └── JwtService.java
│   ├── src/main/resources/
│   │   ├── db/migration/             # Flyway migrations
│   │   │   └── V1__init.sql
│   │   └── application.properties
│   ├── src/test/java/                # Unit tests
│   ├── Dockerfile                    # Production Docker image
│   └── pom.xml                       # Maven dependencies
├── frontend/                         # Vue.js SPA
│   ├── src/
│   │   ├── components/               # Vue components
│   │   │   └── HelloWorld.vue
│   │   ├── views/                    # Page components
│   │   │   ├── LoginView.vue
│   │   │   ├── RegisterView.vue
│   │   │   ├── DashboardView.vue
│   │   │   ├── ErrorCredentialsView.vue
│   │   │   ├── ErrorServerView.vue
│   │   │   └── ForgotPasswordView.vue
│   │   ├── router/                   # Vue Router config
│   │   │   └── index.js
│   │   ├── stores/                   # Pinia stores
│   │   │   └── auth.js
│   │   ├── App.vue                   # Main component
│   │   └── main.js                   # Entry point
│   ├── public/                       # Static assets
│   ├── Dockerfile                    # Production Docker image
│   ├── package.json                  # npm dependencies
│   ├── vite.config.js                # Vite configuration
│   └── yarn.lock                     # Yarn lock file
├── docker-compose.dev.yml            # Development environment
├── docker-compose.yml                # Production environment
├── Makefile                          # Task automation
└── README.md                         # This file
```

---

## 🔧 Configuration

### **Environment Variables**

Create `backend/.env` with your database and JWT configuration:

```env
# Database Configuration (MariaDB SkySQL)
DB_HOST=your-mariadb-host.mdb0001.db.skysql.net
DB_PORT=5001
DB_NAME=your_database_name
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here
JWT_EXPIRATION=86400000
```

### **Database Setup**

The application uses **MariaDB SkySQL** (cloud database). Make sure to:

1. **Create a SkySQL account** at [mariadb.com/products/skysql](https://mariadb.com/products/skysql/)
2. **Create a database instance**
3. **Configure the connection details** in your `.env` file
4. **Enable SSL** if required by your SkySQL configuration

### **CORS Configuration**

The backend is configured to allow requests from:

- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (alternative frontend port)

Modify `SecurityConfig.java` to add additional origins if needed.

---

## 🔑 API Endpoints

### **Public Endpoints**

| Method | Endpoint             | Description       |
| ------ | -------------------- | ----------------- |
| `POST` | `/api/auth/login`    | User login        |
| `POST` | `/api/auth/register` | User registration |
| `GET`  | `/api/auth/health`   | Health check      |
| `GET`  | `/api/auth/test`     | Test endpoint     |

### **Protected Endpoints**

| Method | Endpoint            | Description         | Required Role    |
| ------ | ------------------- | ------------------- | ---------------- |
| `POST` | `/api/auth/logout`  | User logout         | Any              |
| `GET`  | `/api/users/me`     | Get current user    | Any              |
| `GET`  | `/api/admin/**`     | Admin endpoints     | ADMIN            |
| `GET`  | `/api/moderator/**` | Moderator endpoints | ADMIN, MODERATOR |

### **Request/Response Examples**

**Login Request:**

```json
POST /api/auth/login
{
  "username": "user123",
  "password": "password123"
}
```

**Login Response:**

```json
{
  "success": true,
  "message": "Connexion réussie",
  "username": "user123",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response:**

```json
{
  "success": false,
  "message": "Identifiants incorrects",
  "username": null,
  "token": null
}
```

---

## 💻 Development

### **Available Make Commands**

```bash
# Start development environment
make dev

# Stop all containers
make down

# View logs
make logs

# Rebuild containers
make build

# Clean everything (containers, volumes, images)
make clean
```

### **Manual Docker Commands**

```bash
# Start development environment
docker compose -f docker-compose.dev.yml up --build

# Stop containers
docker compose -f docker-compose.dev.yml down

# View logs
docker compose -f docker-compose.dev.yml logs -f

# Access backend container
docker compose -f docker-compose.dev.yml exec backend-dev bash

# Access frontend container
docker compose -f docker-compose.dev.yml exec frontend-dev sh
```

### **Hot Reload**

- **Backend**: Spring DevTools enables automatic restart on code changes
- **Frontend**: Vite provides instant hot module replacement (HMR)

### **Database Management**

- **Migrations**: Located in `backend/src/main/resources/db/migration/`
- **Auto-migration**: Flyway runs automatically on startup
- **Add new migration**: Create `V2__description.sql` in the migration folder

---

## 🧪 Testing

### **Backend Tests**

```bash
# Access through Docker for tests
docker compose -f docker-compose.dev.yml exec backend-dev bash

# Run unit tests
mvn test

# Run specific test class
mvn test -Dtest=AuthControllerTest

# Run tests with coverage
mvn test jacoco:report
```

### **Frontend Tests**

```bash
# Install dependencies
cd frontend
yarn install

# Run tests (when implemented)
yarn test

# Run linting
yarn lint
```

### **Integration Testing**

The application includes integration tests that use the real MariaDB SkySQL database:

- `AuthControllerTest.java` - Tests authentication endpoints
- `BackendApplicationTests.java` - Basic application context test

---

## 🚀 Deployment

### **Production Build**

```bash
# Build production images
docker compose -f docker-compose.yml build

# Start production environment
docker compose -f docker-compose.yml up -d
```

### **Production Configuration**

1. **Update environment variables** for production
2. **Configure reverse proxy** (Nginx, Apache)
3. **Set up SSL certificates**
4. **Configure database connection pooling**
5. **Set up monitoring and logging**

### **Debug Commands**

```bash
# Check container status
docker compose -f docker-compose.dev.yml ps

# View detailed logs
docker compose -f docker-compose.dev.yml logs -f --tail=100

# Check network connectivity
docker compose -f docker-compose.dev.yml exec frontend-dev ping backend-dev
```

---

## 👤 Author

**Fabien Pineau**  
Junior Java Engineer  
📧 [official.fabien@gmail.com](mailto:official.fabien@gmail.com)  
🔗 [GitHub](https://github.com/NekodaMushi)

## 📢 For Recruiters

This project demonstrates:

- **Modern Java Development** (Spring Boot 3, WebFlux, Reactive Programming)
- **Frontend Skills** (Vue.js 3, Modern JavaScript, SPA Architecture)
- **Full-stack Integration** (REST APIs, JWT Authentication, CORS)
- **Database Management** (R2DBC, Cloud Database, Migrations)
- **DevOps Practices** (Docker, Docker Compose, Environment Configuration)
- **Security Best Practices** (JWT, CORS, Input Validation)

**Contact me** for any questions or to discuss opportunities!

---

_Last updated: July 2025_
