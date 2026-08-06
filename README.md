# Store Management API

A containerized Spring Boot REST API for store management. It provides full CRUD operations for products and categories, secured by JWT authentication. The application includes two runtime profiles, allowing it to run with either an H2 in-memory database or MSSQL.

---

## Local Setup

The local development environment is containerized using Docker Compose profiles.

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.

### Running the Application (H2 Database)

**Start the environment:**

```bash
docker compose --profile h2 up --build -d
```

**Access Points:**
* **API URL:** http://localhost:8080/swagger-ui/index.html
* **Database Viewer:** http://localhost:8080/h2-console
    * **JDBC URL:** jdbc:h2:mem:storedb
    * **User:** sa
    * **Password:** *(Leave blank)*

**Stop the environment:**

```bash
docker compose --profile h2 down --remove-orphans
```
For mssql it is similar, just replace h2 with mssql in the docker compose commands.

---

## Authentication & Swagger

Built on Spring Security, the API implements secure, stateless authentication using JSON Web Tokens (JWT).

### 1. Registration
Start by creating an account via the `/api/auth/register` endpoint.

**Default request body:**

```json
{
  "username": "string",
  "password": "string",
  "role": "USER"
}
```

> **Note:** To delete products and categories, you must register the account using the `"role": "ADMIN"`.

### 2. Login
Send your credentials to the `/api/auth/login` endpoint. Upon success, you will receive a JWT.

### 3. Authenticating in Swagger UI
Endpoints for categories require authentication. To test these via Swagger:
1. Click the **Authorize** button at the top right of the page.
2. Enter your token in the value box
3. Click **Authorize** to apply the token to all subsequent requests.

---

## API Access Levels

| Feature / Action | Public | Authenticated (`USER`) | Administrator (`ADMIN`) |
| --- | --- | --- | --- |
| **Register & Login** | Yes | Yes | Yes |
| **Products (View, Create, Update)** | Yes | Yes | Yes |
| **Categories (View, Create, Update)** | No | Yes | Yes |
| **Delete Products & Categories Mapping** | No | No | Yes |
