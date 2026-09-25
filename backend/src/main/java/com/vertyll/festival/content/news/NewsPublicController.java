package com.vertyll.festival.content.news;

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
@RequestMapping("/api/news")
@RequiredArgsConstructor
class NewsPublicController {

    private final NewsService service;

    @GetMapping
    List<NewsResponse> list(@RequestParam(required = false) @Nullable @Min(1) @Max(100) Integer limit) {
        return service.findNewest(limit);
    }

    @GetMapping("/{id}")
    NewsResponse details(@PathVariable ObjectId id) {
        return service.findById(id);
    }
}
