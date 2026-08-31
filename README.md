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
   - # Student Management System

A **Student Management System** is a Java-based application designed to manage and organize student information efficiently. The system allows administrators to add, view, update, and delete student records through a simple and user-friendly interface.

This project was developed to practice **Java programming, database connectivity, CRUD operations, and application development**.

## 🚀 Features

* 🔐 User Login System
* ➕ Add New Student
* 👀 View Student Details
* ✏️ Update Student Information
* 🗑️ Delete Student Records
* 🔍 Search Student
* 📋 Manage Student Records
* 📊 Manage Attendance
* 💾 Database Storage
* 🖥️ Simple and User-Friendly Interface

## 🛠️ Technologies Used

* **Programming Language:** Java
* **Backend:** Java / Spring Boot *(if applicable)*
* **Database:** MySQL
* **Database Connectivity:** JDBC / JPA
* **Frontend:** HTML, CSS, JavaScript *(if applicable)*
* **IDE:** IntelliJ IDEA / Eclipse / VS Code
* **Build Tool:** Maven *(if applicable)*

## 📂 Project Structure

```text
Student-Management-System/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/studentmanagement/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── StudentManagementApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│
├── pom.xml
└── README.md
```

## ⚙️ Main Operations

The application provides the following CRUD operations:

| Operation | Description                       |
| --------- | --------------------------------- |
| Create    | Add a new student                 |
| Read      | View student information          |
| Update    | Modify student information        |
| Delete    | Remove student records            |
| Search    | Find students using their details |

## 🗄️ Database

The system stores student information in a database.

Example student fields:

```text
Student ID
Registration Number
Name
Email
Phone Number
Department
Course
Semester
Address
Attendance
```

## ▶️ How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/student-management-system.git
```

### 2. Open the Project

Open the project in your preferred IDE such as:

* IntelliJ IDEA
* Eclipse
* VS Code

### 3. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE student_management;
```

Update your database configuration in:

```text
application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_management
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Build the Project

If using Maven:

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Then open the application in your browser.

## 🔑 Login

The system provides an authentication mechanism for authorized users.

Example:

```text
Username: admin
Password: ********
```

> Change the default credentials according to your implementation.

## 🎯 Project Objectives

* To develop a computerized student management system.
* To reduce manual work involved in maintaining student records.
* To perform CRUD operations efficiently.
* To learn Java backend development.
* To understand database integration.
* To implement a real-world software project.

## 🔮 Future Enhancements

* Student result management
* Online fee management
* Role-based authentication
* Student profile management
* Email notifications
* PDF report generation
* Admin dashboard
* REST API integration
* Cloud database integration

## 👨‍💻 Author

**Pruthiraj Behera**

B.Tech – Computer Science & Engineering

## 📄 License

This project is created for **educational and academic purposes**.
"""

## ⭐ Acknowledgement

This project was developed as part of my learning and practical experience in **Java application development**.
<img width="1898" height="908" alt="Screenshot 2026-08-13 234928" src="https://github.com/user-attachments/assets/2cb58756-6004-4356-b038-f059d1ce97f6" />
<img width="1897" height="903" alt="Screenshot 2026-08-13 234909" src="https://github.com/user-attachments/assets/1dfb1783-9f41-4533-871d-c171071ce303" />
<img width="1903" height="911" alt="Screenshot 2026-08-13 234132" src="https://github.com/user-attachments/assets/b483ef18-ce54-4e8a-aac0-5a9dfbee086c" />



