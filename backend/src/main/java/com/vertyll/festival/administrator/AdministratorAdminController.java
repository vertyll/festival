package com.vertyll.festival.administrator;

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

import com.vertyll.festival.security.UserIdentity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/administrators")
@RequiredArgsConstructor
class AdministratorAdminController {

    private final AdministratorService service;

    @GetMapping
    List<AdministratorResponse> list() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    AdministratorResponse create(@Valid @RequestBody AdministratorRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    AdministratorResponse update(
        @PathVariable ObjectId id,
        @Valid @RequestBody AdministratorRequest request,
        UserIdentity user
    ) {
        return service.update(id, request, user.email());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable ObjectId id, UserIdentity user) {
        service.delete(id, user.email());
    }
}
