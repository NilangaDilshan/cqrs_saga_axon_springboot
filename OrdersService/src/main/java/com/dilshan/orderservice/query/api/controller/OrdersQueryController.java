package com.dilshan.orderservice.query.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
public class OrdersQueryController {


    @GetMapping
    public String getOrders() {
        return "Get Orders";
    }
}
