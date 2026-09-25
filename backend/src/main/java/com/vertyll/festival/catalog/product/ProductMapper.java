package com.vertyll.festival.catalog.product;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface ProductMapper {

    ProductAdminResponse toAdminResponse(ProductDocument document);

    ProductCardResponse toCard(ProductDocument document);
}
