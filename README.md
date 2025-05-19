# ✅ TaskApp – Vaadin & Spring Boot

A modern and fully functional **task management web application** built with **Vaadin 24** and **Spring Boot**. Offers user registration, login/logout, personal task tracking, Kanban-style management, and a clean UI — all with secure authentication and role-based access.

![Vaadin](https://img.shields.io/badge/Vaadin-24-blue?logo=vaadin)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot)
![License](https://img.shields.io/badge/license-MIT-green)
![Java](https://img.shields.io/badge/Java-21+-orange)

---

## ✨ Features

- 📝 Create, edit, delete and manage tasks
- 🗂️ View as list or in Kanban board layout
- 🔐 Secure authentication (register, login, logout)
- 👤 User profiles & role support
- 🧱 Custom Vaadin layouts & views
- 🎨 Theme-based styling with CSS
- 🔧 Admin initializer for default user
- 🧪 Simple database checker at startup
- 🚀 Production-ready structure

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Node.js (for frontend build, optional)
- IDE: IntelliJ / Eclipse / VS Code

### 🔧 Build & Run

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:8080](http://localhost:8080)

---

### 🧪 Default User

The application includes an initializer:

```text
Username: admin
Password: admin
```

You can adjust this in `AdminInitializer.java` or secure it further via Spring Security.

---

## 📐 UI Overview

| View              | Description                        |
|------------------|------------------------------------|
| `HomeView`        | Landing page after login          |
| `TaskView`        | Traditional task list             |
| `KanbanView`      | Drag-and-drop Kanban interface    |
| `ProfileView`     | User details & profile management |
| `LoginView`       | Authentication form               |
| `RegisterView`    | Account creation                  |
| `LogoutView`      | Redirect-only logout mechanism    |

---

## 👨‍💻 Author

Made by **Nikola Hadzic**  
GitHub: [@hadzicni](https://github.com/hadzicni)

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
