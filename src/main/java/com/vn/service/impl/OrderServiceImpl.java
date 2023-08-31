package com.vn.service.impl;

import com.vn.entity.Order;
import com.vn.exception.CustomException;
import com.vn.external.client.PaymentService;
import com.vn.external.client.ProductService;
import com.vn.external.request.PaymentRequest;
import com.vn.external.response.PaymentResponse;
import com.vn.model.OrderRequest;
import com.vn.model.OrderResponse;
import com.vn.model.ProductResponse;
import com.vn.repository.OrderRepository;
import com.vn.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;


@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public long placeOrder(
        OrderRequest orderRequest
    ) {
        // Order Entity -> Save the data with Status Order Created
        // Product Service - Block Products (Reduce the Quantity)
        // Payment Service -> Payments -> Susscess -> COMPLATE, Else
        // CANCELLED
        log.info("Placing Order Request: {}",orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");

        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to complate the payment");

        PaymentRequest paymentRequest
                = PaymentRequest.builder()
                .orderId(order.getOrderId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);

            log.info("Payment done Successfully. Changing the Order status to PLACED");
            orderStatus = "PLACED";
        }catch (Exception e) {
            log.info("Error occured in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);

        orderRepository.save(order);

        log.info("Order Places susscessfully with Order Id: {}",order.getOrderId());

        return order.getOrderId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {

        log.info("Get order details for Order Id: {}",orderId);

        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(
                        "Order not found for the order Id: " + orderId,
                        "NOT_FOUND",
                        404
                ));

        log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());

        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://product-service/product/" + order.getProductId(),
                    ProductResponse.class
        );

        log.info("Getting payment information form the Payment Service");

        PaymentResponse paymentResponse
                = restTemplate.getForObject(
            "http://payment-service/payment/order/" + order.getOrderId(),
                PaymentResponse.class);

        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails
                .builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
