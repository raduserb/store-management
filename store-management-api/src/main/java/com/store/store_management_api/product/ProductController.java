package com.store.store_management_api.product;

import com.store.store_management_api.category.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products")
    @GetMapping("/")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @Operation(summary = "Get products by category ID")
    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(id));
    }

    @Operation(summary = "Find a product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.findProduct(id);
        return ResponseEntity.ok(productResponse);
    }

    @Operation(summary = "Add a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.addProduct(productRequest);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @Operation(summary = "Change the price of a product")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> changePrice(
            @PathVariable Long id,
            @RequestParam @PositiveOrZero(message = "Price cannot be negative") BigDecimal newPrice) {
        ProductResponse productResponse = productService.changePrice(id, newPrice);
        return ResponseEntity.ok(productResponse);
    }

    @Operation(summary = "Delete a product by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get categories for a product")
    @GetMapping("/{productId}/categories")
    public ResponseEntity<List<CategoryResponse>> getProductCategories(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductCategories(productId));
    }

    @Operation(summary = "Assign a category to a product")
    @PostMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<ProductResponse> assignCategoryToProduct(
            @PathVariable Long productId,
            @PathVariable Long categoryId) {
        ProductResponse productResponse = productService.assignCategoryToProduct(productId, categoryId);
        return ResponseEntity.ok(productResponse);
    }

    @Operation(summary = "Remove a category from a product")
    @PreAuthorize("hasRole('ADMIN')") // Restricts access to ADMIN role only
    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ResponseEntity<ProductResponse> removeCategoryFromProduct(
            @PathVariable Long productId,
            @PathVariable Long categoryId) {

        ProductResponse productResponse = productService.removeCategoryFromProduct(productId, categoryId);
        return ResponseEntity.ok(productResponse);
    }
}