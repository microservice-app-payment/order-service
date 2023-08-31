package com.vn.external.client;

import com.vn.exception.CustomException;
import com.vn.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "enternal",fallbackMethod = "fallback")
@FeignClient(name = "payment-service",path = "/payment")
public interface PaymentService {
    @PostMapping
    ResponseEntity<Long> doPayment(
        @RequestBody PaymentRequest paymentRequest
    );

    default void fallback(Exception e){
        throw new CustomException(
                "Payment Service is not available",
                "UNAVAILABLE",
                500
        );
    }
}
