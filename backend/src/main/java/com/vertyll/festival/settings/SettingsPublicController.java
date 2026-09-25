package com.vertyll.festival.settings;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
class SettingsPublicController {

    private final ShopSettingsService service;

    @GetMapping
    ShopSettingsResponse get() {
        return service.currentResponse();
    }
}
