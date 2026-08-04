package com.store.store_management_api.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank(message = "Product name is required")
                             @Size(max = 255, message = "Product name must be at most 255 characters")
                             String name,

                             @NotNull(message = "Price is required")
                             @PositiveOrZero(message = "Price cannot be negative")
                             BigDecimal price,

                             @NotNull(message = "Stock quantity is required")
                             @PositiveOrZero(message = "Stock quantity cannot be negative")
                             Integer stockQuantity) {
}
