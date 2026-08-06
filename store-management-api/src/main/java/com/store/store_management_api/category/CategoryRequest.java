package com.store.store_management_api.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(@NotBlank(message = "Category name is required")
                              @Size(max = 255, message = "Category name must be at most 255 characters")
                              @Schema(example = "Electronics")
                              String name,

                              @Size(max = 255, message = "Description must be at most 255 characters")
                              @Schema(example = "Smartphones, laptops, and other digital accessories")
                              String description) {
}
