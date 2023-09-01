package com.vn.service;

import com.vn.entity.Order;
import com.vn.exception.CustomException;
import com.vn.external.client.PaymentService;
import com.vn.external.client.ProductService;
import com.vn.external.request.PaymentRequest;
import com.vn.external.response.PaymentResponse;
import com.vn.model.OrderRequest;
import com.vn.model.OrderResponse;
import com.vn.model.PaymentMode;
import com.vn.model.ProductResponse;
import com.vn.repository.OrderRepository;
import com.vn.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success(){
        // Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://payment-service/payment/order/" + order.getOrderId(),
                PaymentResponse.class)).thenReturn(getMockPaymentResponse());

        when(restTemplate.getForObject(
                "http://product-service/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        // Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1L);

        // Verification
        verify(orderRepository,times(1)).findById(anyLong());

        verify(restTemplate,times(1)).getForObject(
                "http://payment-service/payment/order/" + order.getOrderId(),
                PaymentResponse.class);

        verify(restTemplate,times(1)).getForObject(
                "http://product-service/product/" + order.getProductId(),
                ProductResponse.class);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(order.getOrderId(),orderResponse.getOrderId());
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found(){

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(1L));
        assertEquals("NOT_FOUND",exception.getErrorCode());
        assertEquals(404,exception.getStatus());

        verify(orderRepository,times(1))
                .findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        Long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2))
                .save(any(Order.class));
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(),orderId);
    }

    @DisplayName("Payment Order - Payment Failed Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed(){
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        Long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository,times(2))
                .save(any(Order.class));
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getOrderId(),orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(3L)
                .quantity(10L)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(100L)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1L)
                .amount(200L)
                .orderId(1L)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(3L)
                .productName("Ngoc Hung")
                .price(200L)
                .quantity(200L)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderId(1L)
                .orderDate(Instant.now())
                .orderStatus("PLACED")
                .amount(100L)
                .quantity(200L)
                .productId(3L)
                .build();
    }
}