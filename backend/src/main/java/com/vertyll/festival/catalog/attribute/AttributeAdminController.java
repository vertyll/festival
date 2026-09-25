package com.vertyll.festival.catalog.attribute;

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
@RequestMapping("/api/admin/attributes")
@RequiredArgsConstructor
class AttributeAdminController {

    private final AttributeService service;

    @GetMapping
    List<AttributeResponse> list() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AttributeResponse create(@Valid @RequestBody AttributeRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    AttributeResponse update(@PathVariable ObjectId id, @Valid @RequestBody AttributeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable ObjectId id) {
        service.delete(id);
    }
}
