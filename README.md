# File Tracking and Management System

The File Tracking System is a multi-platform application designed to help organizations manage, track, and audit files efficiently. It provides a backend built with Java (Spring Boot, Maven), a user-friendly web frontend built with React, and a desktop client built with JavaFX.

---

## Features

- **File Registration & Tracking**: Register new files with metadata (title, course code, exam session, creator, timestamp, status, etc.), update existing files, and search files by title or status.
- **User Management**: User roles and permissions managed via Spring Data JPA repositories.
- **Audit Logging**: Every file operation (creation, update, etc.) is logged with user and timestamp for traceability.
- **REST API**: Backend exposes RESTful endpoints for all major operations.
- **React Frontend**: Responsive web UI for interacting with the system.
- **Desktop Client**: JavaFX-based desktop application for file management and tracking.

---

## Project Structure

```
File-tracking-system/
│
├── backend/       # Java Spring Boot backend (API, DB, logic)
│   ├── src/
│   ├── pom.xml
│   └── README.md
│
├── web/           # React frontend (UI, static files)
│   ├── public/
│   ├── src/
│   └── README.md
│
├── desktop/       # JavaFX desktop client (UI for desktop usage)
│   ├── src/
│   └── README.md
│
└── openjfx-*/     # JavaFX SDK for UI
```

---

## Backend

- **Build & Run:**
  ```bash
  cd backend
  mvn compile
  mvn exec:java
  ```
- **Key Classes:**
  - `FileController`: REST API endpoints for file management (`/api/files`)
  - `AuditController`: REST API for retrieving audit logs (`/api/audit`)
  - `FileRepository`, `UserRepository`, `AuditLogRepository`: JPA repositories for data access
  - `File`, `AuditLog`: Main entity classes

---

## Web Frontend

- **Start the App:**
  ```bash
  cd web
  npm install
  npm start
  ```
- **Scripts:**
  - `npm start` — Start development server
  - `npm test` — Run unit tests
  - `npm run build` — Production build

---

## Desktop Client

- **Requirements:** Java 17+, JavaFX SDK (see `openjfx-*` directory)
- **Build & Run:**
  ```bash
  cd desktop
  # Example Maven command, customize as per desktop/README.md instructions
  mvn compile
  mvn exec:java
  ```
- **Features:**
  - GUI for file management and tracking using JavaFX
  - Connects to the backend API for full functionality

---

## API Overview

- `GET /api/files/{id}`: Get file details
- `POST /api/files/register`: Register a new file
- `PATCH /api/files/{id}`: Update file info
- `GET /api/files/search?query=...`: Search files
- `GET /api/audit/{fileId}`: Get audit logs for a file

---

## Technologies Used

- **Backend:** Java, Spring Boot, Spring Data JPA, Maven
- **Web Frontend:** React, Create React App, JavaScript
- **Desktop Client:** JavaFX, Java 17+, Maven
- **Database:** (Configure as needed in backend for JPA support)

---

## License

This project may include components under different open-source licenses (see `/openjfx-*/` for third-party legal notices).

---

## Author

- [Aayush01055](https://github.com/Aayush01055)

---

## Contribution

Pull requests and issues are welcome!
