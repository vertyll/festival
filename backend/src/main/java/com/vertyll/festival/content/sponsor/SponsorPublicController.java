package com.vertyll.festival.content.sponsor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sponsors")
@RequiredArgsConstructor
class SponsorPublicController {

    private final SponsorService service;

    @GetMapping
    List<SponsorResponse> list() {
        return service.findAll();
    }
}
