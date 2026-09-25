package com.vertyll.festival.shop.order;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
class OrderAdminController {

    private final OrderService service;

    @GetMapping
    List<OrderResponse> list() {
        return service.findAll();
    }
}
