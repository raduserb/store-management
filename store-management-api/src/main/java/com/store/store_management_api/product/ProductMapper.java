package com.store.store_management_api.product;

import com.store.store_management_api.category.CategoryMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = CategoryMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

    @Mapping(target = "categories", ignore = true)
    Product toProduct(ProductRequest productRequest);

    ProductResponse toProductResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true)
    void updateProduct(ProductRequest productRequest, @MappingTarget Product product);
}
