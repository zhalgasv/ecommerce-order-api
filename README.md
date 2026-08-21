# E-commerce Order API

REST API for an e-commerce order management system built with Spring Boot. The project is designed as a learning backend application focused on clean layering, JPA relationships, DTO mapping, validation, exception handling, and cart/order domain modeling.

## Current Scope

The project currently contains the foundation for:

- user, product, category, cart, and order domains
- order read endpoint: `GET /api/orders/{orderId}`
- checkout from cart
- order cancellation and completion
- order and order item entity relationships
- cart and cart item entity relationships
- DTO-based API responses
- centralized exception handling with a common `ApiError` response
- Liquibase database migrations
- Docker-based PostgreSQL setup
- unit tests for `OrderService`
- JWT/security dependencies prepared for authentication flows

## Tech Stack

- Java 21 target stack
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Liquibase
- Maven
- Lombok
- JUnit, Mockito, Testcontainers
- JWT
- OpenAPI/Swagger UI

## Architecture

The project follows a layered backend structure:

```text
controller -> service -> repository
                  |
               mapper
                  |
                 dto
```

Main package areas:

```text
auth
cart
category
config
exception
order
product
security
user
```

## Order Flow

Current implemented read flow:

```text
OrderController
 -> OrderService
 -> OrderRepository
 -> OrderMapper
 -> OrderResponse
```

`OrderItem` stores `unitPrice` separately from `Product.price` so completed orders can preserve historical pricing.

## Cart Flow

The intended checkout flow is:

```text
User adds products to Cart
Cart contains CartItem rows
User checks out
Order is created from Cart
CartItem becomes OrderItem
Cart is cleared
```

This keeps order creation controlled by backend state instead of trusting a repeated item list from the client.

## API Error Format

Errors are returned with a shared structure:

```json
{
  "status": 404,
  "message": "Order Not Found with id: 1",
  "path": "/api/orders/1",
  "timestamp": "2026-07-11T12:00:00"
}
```

Handled cases currently include:

- `ResourceNotFoundException` -> `404 NOT_FOUND`
- `BadRequestException` -> `400 BAD_REQUEST`
- validation errors -> `400 BAD_REQUEST`

## Run Locally

Start PostgreSQL with Docker:

```bash
docker compose up -d
```

Compile the project:

```bash
./mvnw -DskipTests compile
```

Run tests:

```bash
./mvnw test
```

Run compile checks without executing tests:

```bash
./mvnw -q -DskipTests test-compile
```

Start the application:

```bash
./mvnw spring-boot:run
```

The API runs on:

```text
http://localhost:8081
```

PostgreSQL runs on:

```text
localhost:5433
```

Liquibase applies database migrations automatically on application startup.

## Tests

The project currently has focused unit and web-layer tests:

- `OrderServiceTest` covers order business rules, checkout from cart, stock changes, ownership checks, and exception scenarios.
- `OrderControllerTest` covers HTTP endpoints, current-user lookup, service calls, successful responses, and common error responses.

`OrderControllerTest` uses:

```java
@AutoConfigureMockMvc(addFilters = false)
```

This disables Spring Security filters because these tests focus on controller behavior, not authentication. Security and JWT behavior should be tested separately when the real authentication flow is implemented.

## Useful Endpoints

```http
GET /api/orders/my
GET /api/orders/{orderId}
POST /api/orders/checkout
PATCH /api/orders/{orderId}/cancel
PATCH /api/orders/{orderId}/complete
```

Returns an `OrderResponse` with order status, total price, creation time, and order items.

## Security Note

Current-user endpoints are prepared for JWT-based authentication.
At the moment, `CurrentUserService` temporarily returns user id `1L`.
This should be replaced with `SecurityContext`-based user lookup when JWT authentication is completed.

## Next Development Steps

- replace path `userId` with authenticated user data from JWT
- improve Spring Security configuration
- add integration tests for checkout and Liquibase migrations
- add API documentation examples
