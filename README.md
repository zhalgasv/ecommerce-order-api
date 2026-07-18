# E-commerce Order API

REST API for an e-commerce order management system built with Spring Boot. The project is designed as a learning backend application focused on clean layering, JPA relationships, DTO mapping, validation, exception handling, and order/cart domain modeling.

## Current Scope

The project currently contains the foundation for:

- user, product, category, cart, and order domains
- order read endpoint: `GET /api/orders/{orderId}`
- order and order item entity relationships
- cart and cart item entity relationships
- DTO-based API responses
- centralized exception handling with a common `ApiError` response
- JWT/security dependencies prepared for authentication flows

Order creation from cart is the next planned step.

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

Note: `pom.xml` currently configures `java.version` as `17`. If the project should strictly use Java 21, update the Maven property before relying on Java 21 language/runtime features.

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

## Useful Endpoint

```http
GET /api/orders/{orderId}
```

Returns an `OrderResponse` with order status, total price, creation time, and order items.

## Next Development Steps

- complete `CartRepository`
- implement cart lookup by user
- create order from cart
- clear cart after successful order creation
- add stock validation
- add integration tests for order checkout
