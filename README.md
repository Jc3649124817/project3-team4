# Reactive Banking (Project 3 – Team 4)

A full-stack banking application: a **Spring Boot REST API** secured with **JWT authentication**, backed by **PostgreSQL**, and a **React + TypeScript** single-page frontend. Users can register, log in, view their accounts, make deposits, withdrawals and transfers, browse filtered transaction history, and manage their profile.

## Features

- **Authentication** – register and log in; the API issues a JWT that the frontend sends with every protected request.
- **Accounts** – list your accounts and look up a single account by account number.
- **Transactions** – create deposits, withdrawals and transfers between accounts.
- **Transaction history** – filterable and sortable list of past transactions.
- **Profile management** – view and update profile details, and change your password.
- **Role-based access** – `/users/**` is restricted to the `admin` role; everything except `/auth/**` requires a valid token.
- **Centralized error handling** and **AOP request logging** on the backend.
- **Spring Boot Actuator** endpoints for health and metrics.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 4.1, Spring Web MVC, Spring Data JPA, Spring Security, JJWT 0.11.5, Spring Actuator, Maven |
| Database | PostgreSQL (schema `reactive_banking`) |
| Frontend | React 19, TypeScript, Vite, React Router, React Bootstrap / Bootstrap 5, Axios |
| Testing | JUnit 5, Spring Boot test starters (controller, service and integration tests) |

## Project Structure

```
project3-team4/
├── backend/BankAPI/             # Spring Boot application
│   └── src/main/java/com/WAR/BankAPI/
│       ├── controllers/         # REST controllers
│       ├── services/            # Business logic
│       ├── repos/               # Data access (Spring Data JPA)
│       ├── models/              # JPA entities and enums
│       ├── DTOs/                # Request/response objects
│       ├── jwt/                 # JwtUtil, JwtFilter, SecurityConfig
│       ├── specs/               # Transaction filtering specifications
│       ├── aspects/             # Logging aspect
│       └── exceptions/          # Global exception handler
└── frontend/                    # React + TypeScript app (Vite)
    └── src/
        ├── pages/               # Login, Register, Dashboard, Transactions, History, Profile
        ├── components/          # Reusable UI components
        ├── hooks/               # Auth context and data hooks
        ├── service/             # API calls
        └── docs/                # JSON contracts for the API
```

## API Overview

| Method | Endpoint | Description | Auth |
| --- | --- | --- | --- |
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth` | Log in and receive a JWT | Public |
| GET | `/me` | Get the current user's profile | JWT |
| PUT | `/me` | Update the current user's profile | JWT |
| PUT | `/me/password` | Change password | JWT |
| GET | `/accounts` | List the user's accounts | JWT |
| GET | `/accounts/{accountNumber}` | Get one account | JWT |
| GET | `/transactions` | List transactions (filter/sort) | JWT |
| POST | `/transactions` | Create a deposit, withdrawal or transfer | JWT |
| `*` | `/users/**` | User administration | JWT, `admin` role |
| GET | `/actuator/**` | Health and metrics | JWT |

Request and response shapes are documented in [`frontend/src/docs/refined_json_contracts.md`](frontend/src/docs/refined_json_contracts.md).

## Getting Started

### Prerequisites

- JDK 21
- Node.js (current LTS) and npm
- PostgreSQL running on `localhost:5432`

### 1. Database

The app expects a `reactive_banking` schema in the default `postgres` database. It is created on startup via `schema.sql`, and tables are created/updated by Hibernate (`ddl-auto=update`).

Database credentials are read from environment variables, with these defaults:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=password
```

### 2. Backend

```bash
cd backend/BankAPI
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on `http://localhost:5173`. It talks to `http://localhost:8080` by default; override with `VITE_API_BASE_URL` in a `.env` file. The backend's CORS config allows `http://localhost:5173` (and `4200`).

## Running Tests

```bash
cd backend/BankAPI
./mvnw test
```

Test configuration lives in `src/test/resources/application-test.properties`.

## Contributors

Thanks to the team members who built this project:

| Contributor | GitHub |
| --- | --- |
| John Chen | [@Jc3649124817](https://github.com/Jc3649124817) |
| Abishek Shankara | [@AbishekSh](https://github.com/AbishekSh) |
| Connor Townson | [@connorg2404](https://github.com/connorg2404) |
| Benedict Martinez | [@Rymarmar](https://github.com/Rymarmar) |
| Nigel Sequeira | [@nrsequeira](https://github.com/nrsequeira) |
