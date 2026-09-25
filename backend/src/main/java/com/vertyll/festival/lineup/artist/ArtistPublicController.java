package com.vertyll.festival.lineup.artist;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
class ArtistPublicController {

    private final ArtistService service;

    @GetMapping
    List<ArtistResponse> list(@RequestParam(required = false) @Nullable @Min(1) @Max(100) Integer limit) {
        return service.findNewest(limit);
    }

    @GetMapping("/{id}")
    ArtistDetailsResponse details(@PathVariable ObjectId id) {
        return service.details(id);
    }
}
