package com.store.store_management_api.product;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              BigDecimal price,
                              Integer stockQuantity) {
}
