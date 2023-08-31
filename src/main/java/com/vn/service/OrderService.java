package com.vn.service;

import com.vn.model.OrderRequest;
import com.vn.model.OrderResponse;

public interface OrderService {

    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
