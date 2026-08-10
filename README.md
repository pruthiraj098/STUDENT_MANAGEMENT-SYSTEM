# Student Management System

An Enterprise Student Management System featuring modular separation of **Backend** (Spring Boot) and **Frontend** (HTML5, Bootstrap, CSS, JavaScript, Thymeleaf templates).

---

## 📁 Repository Structure

```
STUDENT_MANAGEMENT SYSTEM/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sms/      # Controllers, Entities, DTOs, Services, Security
│   │   │   └── resources/          # application.properties (H2, MySQL, Neon)
│   │   └── test/                  # Unit and integration tests
│   ├── pom.xml                    # Maven configuration
│   ├── mvnw.cmd                   # Maven wrapper
│   └── Dockerfile                 # Docker configuration
│
└── frontend/
    ├── static/                    # CSS, JS, and image assets
    │   ├── css/styles.css
    │   └── js/dashboard.js
    └── templates/                 # UI HTML templates
        ├── courses/
        ├── departments/
        ├── enrollments/
        ├── students/
        ├── fragments/
        └── *.html (index, login, dashboard, register, etc.)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or later installed
- Maven (or use `mvnw.cmd` included in `backend/`)

---

### Running the Application

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```

2. Compile and run:
   ```bash
   mvnw.cmd spring-boot:run
   ```
   *(Or `mvn spring-boot:run` if Maven is installed globally)*

3. Access the application in your browser:
   - **URL**: `http://localhost:8080`
   - **H2 Console**: `http://localhost:8080/h2-console`
