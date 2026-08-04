package com.store.store_management_api.product;

import com.store.store_management_api.category.CategoryResponse;

import java.math.BigDecimal;
import java.util.Set;

public record ProductResponse(Long id,
                              String name,
                              BigDecimal price,
                              Integer stockQuantity,
                              Set<CategoryResponse> categories) {
}
