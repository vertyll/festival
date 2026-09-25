package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

@Document("products")
record ProductDocument(
    @Id ObjectId id,
    LocalizedText name,
    @Nullable LocalizedText description,
    BigDecimal price,
    @Indexed @Nullable ObjectId categoryId,
    List<String> images,
    List<ProductOption> options,
    List<ProductVariant> variants,
    Instant createdAt,
    Instant updatedAt
) {

    ProductDocument {
        images = List.copyOf(images);
        options = List.copyOf(options);
        variants = List.copyOf(variants);
    }

    static ProductDocument create(ProductRequest request, Instant now) {
        return new ProductDocument(
            new ObjectId(),
            request.name(),
            request.description(),
            request.price(),
            request.categoryId(),
            request.images(),
            request.options(),
            ProductVariants.validate(request.options(), request.variants()),
            now,
            now
        );
    }

    ProductDocument update(ProductRequest request, Instant now) {
        return new ProductDocument(
            id,
            request.name(),
            request.description(),
            request.price(),
            request.categoryId(),
            request.images(),
            request.options(),
            ProductVariants.validate(request.options(), request.variants()),
            createdAt,
            now
        );
    }

    int totalStock() {
        return variants.stream().mapToInt(ProductVariant::stock).sum();
    }

    List<String> valueCodesFor(Map<String, String> selection) {
        if (selection.size() != options.size()) {
            throw new InvalidRequestException(MessageKeys.PRODUCT_OPTIONS_INCOMPLETE, Map.of("product", name));
        }
        List<String> valueCodes = new ArrayList<>(options.size());
        for (ProductOption option : options) {
            String valueCode = selection.get(option.code());
            if (valueCode == null || option.values().stream().noneMatch(value -> value.code().equals(valueCode))) {
                throw new InvalidRequestException(
                    MessageKeys.PRODUCT_OPTION_VALUE_INVALID,
                    Map.of("product", name, "option", option.name())
                );
            }
            valueCodes.add(valueCode);
        }
        return List.copyOf(valueCodes);
    }

    List<SelectedOption> describe(List<String> valueCodes) {
        List<SelectedOption> selection = new ArrayList<>(options.size());
        for (int i = 0; i < options.size(); i++) {
            ProductOption option = options.get(i);
            String valueCode = valueCodes.get(i);
            OptionValue value = option.values()
                .stream()
                .filter(candidate -> candidate.code().equals(valueCode))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "Product " + id + " does not have value " + valueCode + " for option " + option.code()
                    )
                );
            selection.add(new SelectedOption(option.name(), value.label()));
        }
        return List.copyOf(selection);
    }
}
