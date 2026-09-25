package com.vertyll.festival.shop.order;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.security.UserIdentity;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
class CustomerOrderController {

    private final OrderService service;

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse place(@Valid @RequestBody PlaceOrderRequest request, UserIdentity user) {
        return service.place(user.id(), request);
    }

    @GetMapping("/api/account/orders")
    List<OrderResponse> myOrders(UserIdentity user) {
        return service.findByCustomer(user.id());
    }
}
