package com.vertyll.festival.catalog.product;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
class ProductPublicController {

    private final ProductService service;

    @GetMapping
    List<ProductCardResponse> search(
        @RequestParam(required = false) @Nullable @Size(max = 100) String term,
        @RequestParam(required = false) @Nullable @Size(max = 50) List<ObjectId> ids,
        @RequestParam(defaultValue = "NEWEST") ProductSort sort,
        @RequestParam(required = false) @Nullable @Min(1) @Max(100) Integer limit
    ) {
        return service.searchCards(new ProductSearch(term, ids == null ? List.of() : ids, sort, limit));
    }

    @GetMapping("/{id}")
    ProductDetailsResponse details(@PathVariable ObjectId id) {
        return service.details(id);
    }
}
