package com.vertyll.festival.catalog.product;

import java.util.List;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface ProductMapper {

    ProductAdminResponse toAdminResponse(ProductDocument document);

    ProductCardResponse toCard(ProductDocument document);

    List<ProductOptionResponse> toOptionResponses(List<ProductOption> options);

    List<ProductOption> toOptions(List<ProductRequest.Option> options);

    List<ProductVariant> toVariants(List<ProductRequest.Variant> variants);
}
