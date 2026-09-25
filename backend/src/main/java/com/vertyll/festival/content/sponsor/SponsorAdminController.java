package com.vertyll.festival.content.sponsor;

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
@RequestMapping("/api/admin/sponsors")
@RequiredArgsConstructor
class SponsorAdminController {

    private final SponsorService service;

    @GetMapping
    List<SponsorResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    SponsorResponse get(@PathVariable ObjectId id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SponsorResponse create(@Valid @RequestBody SponsorRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    SponsorResponse update(@PathVariable ObjectId id, @Valid @RequestBody SponsorRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable ObjectId id) {
        service.delete(id);
    }
}
