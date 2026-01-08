# 🎟️ FlashTicket - Event Booking API

FlashTicket is a robust and secure backend API for managing events and ticket sales. Built with **Spring Boot 3** and **Java 17**, it simulates a high-concurrency environment where data integrity and security are paramount.

## 🚀 Key Features

* **🛡️ Secure Authentication:** Stateless JWT (JSON Web Token) implementation for secure user access.
* **⚡ Concurrency Control:** Uses **Optimistic Locking** (`@Version`) to prevent double-booking issues during high-traffic ticket sales.
* **🏗️ Clean Architecture:** Layered architecture (Controller, Service, Repository) with **DTO Pattern** to ensure data hiding and API contract stability.
* **✅ Data Integrity:** Fully transactional operations (`@Transactional`) ensuring ACID compliance.
* **🔍 Validation:** Comprehensive input validation and centralized global exception handling.
* **📄 Documentation:** Integrated Swagger UI for API testing and documentation.

## 🛠️ Tech Stack

* **Java 17**
* **Spring Boot 3** (Web, Security, Data JPA, Validation)
* **PostgreSQL** (Production) / **H2 Database** (Dev/Test)
* **Docker** (Optional)
* **Maven**

## ⚙️ Installation & Setup

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/emrekarabacak1/ticket-booking.git](https://github.com/emrekarabacak1/ticket-booking.git)
    cd ticket-booking
    ```

2.  **Configure Database**
    Update `src/main/resources/application.properties` with your database credentials.

3.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```

4.  **Access Swagger UI**
    Navigate to `http://localhost:8080/swagger-ui/index.html` to test the API endpoints.

## 🔌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT |
| `POST` | `/api/events` | Create a new event (Admin only) |
| `GET` | `/api/events` | List all events |
| `POST` | `/api/tickets` | Buy a ticket (Requires Auth) |
| `GET` | `/api/tickets/my-tickets` | View purchased tickets |

---
*Developed by [Emre Karabacak](https://github.com/emrekarabacak1)*
