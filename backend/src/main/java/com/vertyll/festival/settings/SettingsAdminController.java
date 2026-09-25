package com.vertyll.festival.settings;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
class SettingsAdminController {

    private final ShopSettingsService service;

    @GetMapping
    ShopSettingsResponse get() {
        return service.currentResponse();
    }

    @PutMapping
    ShopSettingsResponse update(@Valid @RequestBody ShopSettingsRequest request) {
        return service.update(request);
    }
}
