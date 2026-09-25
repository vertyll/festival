package com.vertyll.festival.catalog.product;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;

final class ProductVariants {

    static final int MAX_VARIANTS = 500;

    private ProductVariants() {
    }

    static List<ProductVariant> validate(List<ProductOption> options, List<ProductVariant> variants) {
        requireUniqueCodes(options);
        List<List<String>> expected = combinations(options);
        Map<List<String>, ProductVariant> provided =
                variants.stream().collect(Collectors.toMap(ProductVariant::valueCodes, Function.identity(), (_, _) -> {
                    throw new InvalidRequestException(MessageKeys.PRODUCT_VARIANT_DUPLICATED);
                }));
        if (!provided.keySet().equals(Set.copyOf(expected))) {
            throw new InvalidRequestException(MessageKeys.PRODUCT_VARIANTS_MISMATCH);
        }
        return expected.stream().map(combination -> Objects.requireNonNull(provided.get(combination))).toList();
    }

    static List<List<String>> combinations(List<ProductOption> options) {
        List<List<String>> result = List.of(List.of());
        for (ProductOption option : options) {
            result = result.stream()
                .flatMap(prefix -> option.values().stream().map(value -> append(prefix, value.code())))
                .toList();
            if (result.size() > MAX_VARIANTS) {
                throw new InvalidRequestException(MessageKeys.PRODUCT_TOO_MANY_VARIANTS, Map.of("max", MAX_VARIANTS));
            }
        }
        return result;
    }

    private static List<String> append(List<String> prefix, String valueCode) {
        return Stream.concat(prefix.stream(), Stream.of(valueCode)).toList();
    }

    private static void requireUniqueCodes(List<ProductOption> options) {
        Set<String> codes = new HashSet<>();
        for (ProductOption option : options) {
            if (!codes.add(option.code())) {
                throw new InvalidRequestException(MessageKeys.PRODUCT_OPTION_DUPLICATED);
            }
            OptionValue.requireUniqueCodes(option.values());
        }
    }
}
