## Test API

- Order Product
```shell
curl -X POST http://localhost:8082/order \
-H "Content-Type: application/json" \
-d '{"productId": 1, "totalAmount": 15, "quantity": 2, "paymentMode": "CASH"}'  
```
