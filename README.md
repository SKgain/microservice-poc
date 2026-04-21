# 🧩 Microservices POC — Spring Boot

A proof-of-concept microservices project built with **Spring Boot 3.4.5** demonstrating service discovery, API routing, and inter-service communication using **Eureka Server**, **API Gateway**, and **OpenFeign**.

---

## 📐 Architecture

```
Client
  │
  ▼
API Gateway (port 8080)
  │
  ├──▶ Order Service (port 8081)
  │         │
  │         └──▶ Payment Service (port 8082)  ← via OpenFeign
  │
  └──▶ Payment Service (port 8082)
  
All services register with → Eureka Server (port 8761)
```

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 3.4.5 | Base framework |
| Spring Cloud | 2024.0.1 | Cloud ecosystem BOM |
| Spring Cloud Gateway | 2024.0.1 | API Gateway / routing |
| Netflix Eureka Server | 2024.0.1 | Service registry |
| Netflix Eureka Client | 2024.0.1 | Service discovery |
| OpenFeign | 2024.0.1 | Inter-service HTTP client |
| Lombok | latest | Boilerplate reduction |
| Java | 17 | Language version |
| Maven | 3.x | Build tool |

---

## 📁 Project Structure

```
microservice-poc/
├── eureka-server/          # Service registry
├── api-gateway/            # Entry point, routes requests
├── order-service/          # Handles orders, calls payment-service
└── payment-service/        # Handles payment processing
```

---

## ⚙️ Service Configuration

| Service | Port | Spring App Name |
|---|---|---|
| Eureka Server | 8761 | eureka-server |
| API Gateway | 8080 | api-gateway |
| Order Service | 8081 | order-service |
| Payment Service | 8082 | payment-service |

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.x

### Startup Order

> ⚠️ Always start in this order — each service depends on Eureka being up first.

**1. Start Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```
Verify at: http://localhost:8761

**2. Start Order Service**
```bash
cd order-service
mvn spring-boot:run
```

**3. Start Payment Service**
```bash
cd payment-service
mvn spring-boot:run
```

**4. Start API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

---

## 📡 API Endpoints

All requests go through the **API Gateway on port 8080**.

### Order Service

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/orders` | Get all orders |
| `GET` | `/api/orders/{id}` | Get order by ID |
| `POST` | `/api/orders` | Place a new order |

### Payment Service

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/payments/{orderId}` | Get payment by order ID |
| `POST` | `/api/payments/process` | Process a payment |

---

## 🧪 Testing

### Place an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "product": "Laptop",
    "quantity": 1,
    "amount": 1200.00
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "product": "Laptop",
  "quantity": 1,
  "amount": 1200.00,
  "paymentStatus": "SUCCESS"
}
```

### Get All Orders
```bash
curl http://localhost:8080/api/orders
```

### Get Order by ID
```bash
curl http://localhost:8080/api/orders/1
```

### Get Payment Status
```bash
curl http://localhost:8080/api/payments/1
```

---

## 🔄 Request Flow

```
POST /api/orders  (port 8080)
        │
        ▼
   API Gateway
   routes to → order-service (discovered via Eureka)
        │
        ▼
   OrderController.placeOrder()
        │
        ▼
   OpenFeign client
   calls → payment-service/payments/process (discovered via Eureka)
        │
        ▼
   PaymentController.processPayment()
   returns → { status: "SUCCESS", transactionId: "uuid" }
        │
        ▼
   Order returned with paymentStatus = "SUCCESS"
```

---

## 🔑 Key Concepts

### Eureka Server
All microservices register themselves with Eureka on startup. Eureka acts as a phone book — services ask it "where is payment-service?" instead of using hardcoded URLs.

### API Gateway
Single entry point for all client requests. Routes traffic based on path:
- `/api/orders/**` → `order-service`
- `/api/payments/**` → `payment-service`

Uses `lb://` (load balanced) URIs so it resolves service locations via Eureka automatically.

### OpenFeign
Declarative HTTP client in `order-service`. Instead of writing `RestTemplate` or `WebClient` boilerplate, you just define an interface:

```java
@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
}
```

Feign resolves `payment-service` to the actual host/port via Eureka — no hardcoded URLs needed.

---

## 📋 application.properties Reference

### eureka-server
```properties
server.port=8761
spring.application.name=eureka-server
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### api-gateway
```properties
server.port=8080
spring.application.name=api-gateway
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
spring.cloud.gateway.routes[0].id=order-service
spring.cloud.gateway.routes[0].uri=lb://order-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/orders/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1
spring.cloud.gateway.routes[1].id=payment-service
spring.cloud.gateway.routes[1].uri=lb://payment-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/payments/**
spring.cloud.gateway.routes[1].filters[0]=StripPrefix=1
```

### order-service
```properties
server.port=8081
spring.application.name=order-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.cloud.openfeign.client.config.default.connect-timeout=5000
spring.cloud.openfeign.client.config.default.read-timeout=5000
```

### payment-service
```properties
server.port=8082
spring.application.name=payment-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

---

## 🌐 Useful URLs

| URL | Description |
|---|---|
| http://localhost:8761 | Eureka Dashboard — see registered services |
| http://localhost:8080/api/orders | Orders via Gateway |
| http://localhost:8080/api/payments/1 | Payments via Gateway |

---

## 📌 Version Compatibility

| Spring Boot | Spring Cloud | Gateway Artifact |
|---|---|---|
| `3.4.x` | `2024.0.x` ✅ | `spring-cloud-starter-gateway` |
| `3.3.x` | `2023.0.x` ✅ | `spring-cloud-starter-gateway` |
