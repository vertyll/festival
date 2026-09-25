package com.vertyll.festival.catalog.product;

import java.util.List;

import jakarta.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
class ProductAdminController {

    private final ProductService service;

    @GetMapping
    List<ProductAdminResponse> list() {
        return service.findAllForAdmin();
    }

    @GetMapping("/{id}")
    ProductAdminResponse get(@PathVariable ObjectId id) {
        return service.findForAdmin(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProductAdminResponse create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    ProductAdminResponse update(@PathVariable ObjectId id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable ObjectId id) {
        service.delete(id);
    }
}
