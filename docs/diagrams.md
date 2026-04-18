# Component Diagrams — Microservice Demo

## 1. System Context

```mermaid
graph TB
    Client["Client\n(Browser / API Consumer)"]

    subgraph System["Microservice Demo System"]
        US["User Service\n:8081"]
        OS["Order Service\n:8082"]
    end

    Client -->|"POST /api/orders\nGET  /api/orders"| OS
    Client -->|"GET /api/users/{id}"| US
    OS     -->|"GET /api/users/{id}\nHTTP REST"| US
```

---

## 2. Container Diagram

```mermaid
graph TB
    Client["Client"]

    subgraph Docker["Docker / AWS ECS  (ap-southeast-1)"]
        subgraph US["User Service  :8081"]
            UC["UserController\nGET /api/users/{id}\n——\nMap&lt;Long, UserResponse&gt; USERS\n(static in-memory, seeded: 1, 2, 3)"]
        end

        subgraph OS["Order Service  :8082"]
            OC["OrderController"]
            OSvc["OrderService"]
            UCli["UserClient\n(RestTemplate)"]
            DB[("H2 Database\njdbc:h2:mem:orderdb")]
        end
    end

    Client -->|HTTP| OC
    Client -->|HTTP| UC
    OC --> OSvc
    OSvc --> UCli
    UCli -->|"GET /api/users/{id}\nHTTP REST"| UC
    OSvc -->|JPA| DB
```

---

## 3. Order Service — Internal Components

```mermaid
graph TD
    subgraph OS["Order Service  (dev.hazoe.order_service)"]
        OC["OrderController\n——\nPOST /api/orders\nGET  /api/orders\nGET  /api/orders/{id}"]
        OSvc["OrderService\n——\ncreateOrder()\ngetAllOrders()\ngetOrder()"]
        UCli["UserClient\n——\ngetUserById(Long userId)\nvia RestTemplate"]
        ORepo["OrderRepository\n——\nJpaRepository&lt;Order, Long&gt;"]
        OEnt["Order  [Entity]\n——\nid, userId, userName\nproduct, amount, status"]
        Act["Actuator\n——\n/actuator/health"]
        H2[("H2 In-Memory DB\norderdb")]
    end

    ExtUS["User Service\n:8081"]

    OC    --> OSvc
    OSvc  --> UCli
    OSvc  --> ORepo
    ORepo --> OEnt
    OEnt  --> H2
    UCli  -->|"HTTP GET /api/users/{id}"| ExtUS
```

---

## 4. User Service — Internal Components

```mermaid
graph TD
    subgraph US["User Service  (dev.hazoe.user_service)"]
        UC["UserController\n——\nGET /api/users/{id}\n——\nMap&lt;Long, UserResponse&gt; USERS\n(static in-memory, seeded: 1, 2, 3)"]
        UResp["UserResponse  [DTO]\n——\nid, name, email"]
    end

    UC --> UResp
```

---

## 5. Sequence — Create Order Flow

```mermaid
sequenceDiagram
    participant C   as Client
    participant OC  as OrderController
    participant OS  as OrderService
    participant UCli as UserClient
    participant US  as User Service
    participant DB  as H2 Database
    participant SP  as Spring MVC

    C    ->> OC   : POST /api/orders {userId, product, amount}
    OC   ->> OS   : createOrder(request)
    OS   ->> UCli : getUserById(userId)
    UCli ->> US   : GET /api/users/{userId}

    alt User found (200 OK)
        US   -->> UCli : 200 {id, name, email}
        UCli -->> OS   : Optional<UserResponse>
        note over OS   : order.setUserId(...)<br/>order.setUserName(user.getName())<br/>order.setProduct(...)<br/>order.setAmount(...)
        OS   ->>  DB   : orderRepository.save(order)
        DB   -->> OS   : Order (with generated id)
        OS   -->> OC   : OrderResponse
        OC   -->> C    : 201 Created
    else User not found (404)
        US   -->> UCli : 404 Not Found
        UCli -->> OS   : Optional.empty()
        OS   ->>  SP   : throw ResponseStatusException(BAD_REQUEST)
        SP   -->> C    : 400 Bad Request
    end
```

---

## 6. Deployment — AWS

```mermaid
graph TB
    GH["GitHub Actions\nCI/CD"]

    subgraph AWS["AWS  (ap-southeast-1)"]
        subgraph ECR["ECR"]
            ECR_US["user-service"]
            ECR_OS["order-service"]
        end

        subgraph ECS["ECS Cluster: my-cluster"]
            ECS_US["ECS Service: user-service\nTask: user-service-task-definition\nPort: 8081"]
            ECS_OS["ECS Service: order-service\nTask: order-service-task-definition\nPort: 8082"]
        end
    end

    GH      -->|docker push| ECR_US
    GH      -->|docker push| ECR_OS
    ECR_US  -->|pull image| ECS_US
    ECR_OS  -->|pull image| ECS_OS
    ECS_OS  -->|"HTTP REST\nUSER_SERVICE_URL=http://user-service:8081"| ECS_US
```
