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
- Get all Product
```shell
curl -X GET "http://localhost:9090/product" \
-H "Authorization: Bearer YOUR_TOKEN"
```

- Get by ProductID
```shell
curl -X GET "http://localhost:9090/product/1" \
-H "Authorization: Bearer YOUR_TOKEN"
```

- Add product
```shell
curl -X POST  "http://localhost:9090/product" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer YOUR_TOKEN" \
-d '{"productName": "Ngoc Hung", "price": 100, "quantity": 50}'
```
- Reduce Quantity
```shell
curl -X PUT "http://localhost:9090/product/reduceQuantity/1?quantity=50"\
-H "Authorization: Bearer YOUR_TOKEN" 
```