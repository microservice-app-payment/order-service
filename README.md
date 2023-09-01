## Test API

- Order Product
```shell
curl -X POST http://localhost:9090/order/placeOrder \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_TOKEN" \
-d '{"productId": 1, "totalAmount": 15, "quantity": 2, "paymentMode": "CASH"}'  
```

- Get Order Details
```shell
curl -X GET "http://localhost:9090/order/5" \
-H "Authorization: Bearer YOUR_TOKEN"
```