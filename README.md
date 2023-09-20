## Test API

- Order Product
```shell
curl -X POST http://localhost:8080/order/placeOrder \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_TOKEN" \
-d '{"productId": 1, "totalAmount": 15, "quantity": 5, "paymentMode": "CASH"}'  
```

- Get Order Details
```shell
curl -X GET "http://localhost:8080/order/5" \
-H "Authorization: Bearer YOUR_TOKEN"
```
