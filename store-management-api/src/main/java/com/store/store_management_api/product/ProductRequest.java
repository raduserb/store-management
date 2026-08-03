package com.store.store_management_api.product;

import java.math.BigDecimal;

public record ProductRequest(String name,
                             BigDecimal price,
                             Integer stockQuantity) {
}
