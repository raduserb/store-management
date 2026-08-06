package com.store.store_management_api.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(@NotBlank(message = "Product name is required")
                             @Size(max = 255, message = "Product name must be at most 255 characters")
                             @Schema(example = "Wireless Mouse")
                             String name,

                             @NotNull(message = "Price is required")
                             @PositiveOrZero(message = "Price cannot be negative")
                             @Schema(example = "99.99")
                             BigDecimal price,

                             @NotNull(message = "Stock quantity is required")
                             @PositiveOrZero(message = "Stock quantity cannot be negative")
                             @Schema(example = "50")
                             Integer stockQuantity) {
}
