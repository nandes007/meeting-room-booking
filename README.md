# Meeting Room Booking System

A full-stack application for managing meeting room reservations, featuring an administrative dashboard and an employee portal. This project is built with a modern tech stack ensuring scalability, security, and a premium user experience.

## 🚀 Features

- **Authentication & Authorization**: Secure login system for both **Admins** and **Employees** using PASETO tokens.
- **Admin Management**:
  - Create and manage employee accounts.
  - Create and manage meeting rooms.
  - View all bookings across the organization.
  - Approve or reject booking requests.
- **Booking Management**:
  - All users can create booking requests.
  - Real-time availability tracking (implemented via backend logic).
- **Interactive API**: Fully documented REST API with Swagger UI.

---

## 🏗️ Architecture

The project follows a **Client-Server architecture** with a clear separation between the frontend and backend:

- **Frontend (`meeting-room-booking-fe`)**: A modern Single Page Application (SPA) built with Vue 3, TypeScript, and Vite. It communicates with the backend via RESTful APIs.
- **Backend (`meeting-room-booking-api`)**: A robust REST API built with Spring Boot 4, following a layered architecture (Controller, Service, Repository).
- **Database**: PostgreSQL is used as the relational database to store users, rooms, and booking records.

---

## 🛠️ Tech Stack

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Security**: Spring Security + PASETO (Platform-Agnostic Security Tokens)
- **Database**: PostgreSQL
- **Migration**: Liquibase
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Build Tool**: Maven

### Frontend
- **Framework**: Vue 3.5
- **Build Tool**: Vite 6
- **Language**: TypeScript
- **State Management**: Pinia
- **Styling**: TailwindCSS 4
- **HTTP Client**: Axios

## 🐳 Docker Quick Start (Recommended)

The easiest and fastest way to get the entire application up and running is using Docker. This will spin up the PostgreSQL database, compile and package the backend API, automatically run the database migrations, and serve the frontend Vue application via Nginx.

### 📋 Prerequisites
- **Docker** and **Docker Compose** installed on your system.

### 🚀 Running the Application
1. **Build and start all services**:
   Run the following command from the root of the project:
   ```bash
   docker compose up --build
   ```

2. **Access the Services**:
   Once the containers are built and started:
   - **Frontend App**: Open [http://localhost:5173](http://localhost:5173) in your browser.
   - **Backend API**: The REST API is exposed at [http://localhost:8080](http://localhost:8080).
   - **API Documentation (Swagger)**: View the Swagger UI at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to interact with and test the endpoints.
   - **Database**: PostgreSQL is running inside the container and exposed on host port `5432` with:
     - **Host**: `localhost`
     - **Port**: `5432`
     - **Database Name**: `meeting_room_booking_db`
     - **Username**: `root`
     - **Password**: `password`

### 🛑 Stopping the Application
To stop the services and retain your database data:
```bash
docker compose down
```

To stop the services and completely reset the database (delete persistent volumes):
```bash
docker compose down -v
```

---

## 📋 Prerequisites (Manual Setup)

Before you begin, ensure you have the following installed:
- **Java JDK 21**
- **Node.js** (Version >= 18.0.0)
- **PostgreSQL** (Running on port 5432)
- **Maven** (Optional, `mvnw` wrapper is provided)
- **Make** (For running database migration commands easily)

---

## ⚙️ Backend Setup (`meeting-room-booking-api`)

1. **Configure Database**:
   - Create a PostgreSQL database named `meeting_room_booking_db`.
   - Update `src/main/resources/application.properties` if your database credentials differ:
     ```properties
     spring.datasource.username=root
     spring.datasource.password=password
     spring.datasource.url=jdbc:postgresql://localhost:5432/meeting_room_booking_db
     ```

2. **Run Migrations**:
   Before starting the app, you must run the database migrations using Liquibase (see the [Database Migrations](#database-migrations-liquibase) section).

3. **Start the Application**:
   ```bash
   cd meeting-room-booking-api
   ./mvnw spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

---

## 💻 Frontend Setup (`meeting-room-booking-fe`)

1. **Install Dependencies**:
   ```bash
   cd meeting-room-booking-fe
   npm install
   ```

2. **Start Development Server**:
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173`.

---

## 🗄️ Database Migrations (Liquibase)

This project uses **Liquibase** to manage database schema changes. The master changelog is located at `src/main/resources/db/changelog/db.changelog-master.yaml`.

A `Makefile` is provided in the `meeting-room-booking-api` folder for convenience:

| Command | Description |
|---------|-------------|
| `make migrate` | Run all pending migrations. |
| `make status` | Check which migrations have not been applied yet. |
| `make rollback COUNT=X` | Rollback the last X migrations. |
| `make rollback-all` | Drop all tables and reset the database. |

To run migrations without `make`:
```bash
./mvnw liquibase:update
```

---

## 📖 API Documentation (Swagger)

The project includes interactive API documentation. Once the backend is running, you can access it at:

**Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

This allows you to:
- View all available endpoints.
- Understand request/response structures.
- Test API calls directly from the browser.

---

## 🤝 Contributing

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.
