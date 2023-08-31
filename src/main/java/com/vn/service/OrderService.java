package com.vn.service;

import com.vn.model.OrderRequest;

public interface OrderService {

    long placeOrder(OrderRequest orderRequest);
}
