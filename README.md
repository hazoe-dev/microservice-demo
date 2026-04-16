**Terminal 1 — user-service:**
```
cd "user-service/user-service"  
./mvnw spring-boot:run
```

**Terminal 2 — order-service:**

```
cd "order-service"
./mvnw spring-boot:run
```

**Testing**
```
# Get a user
curl http://localhost:8081/api/users/1

# Create an order (user-service must be running)
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "product": "Laptop", "amount": 999.99}'

# Get the order
curl http://localhost:8082/api/orders/1

# Unknown user → 400 Bad Request
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": 99, "product": "Phone", "amount": 500}'

```