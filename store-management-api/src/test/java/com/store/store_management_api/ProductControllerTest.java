package com.store.store_management_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.store_management_api.category.CategoryResponse;
import com.store.store_management_api.exception.ResourceNotFoundException;
import com.store.store_management_api.product.ProductController;
import com.store.store_management_api.product.ProductRequest;
import com.store.store_management_api.product.ProductResponse;
import com.store.store_management_api.product.ProductService;
import com.store.store_management_api.security.CustomUserDetailsService;
import com.store.store_management_api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@WithMockUser
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProductControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private ProductResponse productResponse;
    private ProductRequest validProductRequest;
    private ProductRequest invalidProductRequest;
    private CategoryResponse categoryResponse;

    private final Long PRODUCT_ID = 1L;
    private final Long CATEGORY_ID = 2L;

    public ProductControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse(CATEGORY_ID, "Test Category", "Description");

        productResponse = new ProductResponse(
                PRODUCT_ID,
                "Test Product",
                BigDecimal.valueOf(100.0),
                50,
                Set.of(categoryResponse)
        );

        validProductRequest = new ProductRequest("Test Product", BigDecimal.valueOf(100.0), 50);
        invalidProductRequest = new ProductRequest("", BigDecimal.valueOf(-10.0), -5);
    }

    @Nested
    @DisplayName("GET /api/products Operations")
    class GetOperations {

        @Test
        @DisplayName("Should return all products")
        void getAllProducts_Success() throws Exception {
            when(productService.getAllProducts()).thenReturn(List.of(productResponse));

            mockMvc.perform(get("/api/products/"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value(PRODUCT_ID))
                    .andExpect(jsonPath("$[0].name").value("Test Product"));
        }

        @Test
        @DisplayName("Should return product by ID")
        void findProductById_Success() throws Exception {
            when(productService.findProduct(PRODUCT_ID)).thenReturn(productResponse);

            mockMvc.perform(get("/api/products/{id}", PRODUCT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                    .andExpect(jsonPath("$.name").value("Test Product"));
        }

        @Test
        @DisplayName("Should return 404 when product ID not found")
        void findProductById_NotFound() throws Exception {
            when(productService.findProduct(PRODUCT_ID))
                    .thenThrow(new ResourceNotFoundException("Product not found"));

            mockMvc.perform(get("/api/products/{id}", PRODUCT_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return products by category ID")
        void getProductsByCategory_Success() throws Exception {
            when(productService.getProductsByCategoryId(CATEGORY_ID)).thenReturn(List.of(productResponse));

            mockMvc.perform(get("/api/products/category/{id}", CATEGORY_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1));
        }

        @Test
        @DisplayName("Should return categories for a product")
        void getProductCategories_Success() throws Exception {
            when(productService.getProductCategories(PRODUCT_ID)).thenReturn(List.of(categoryResponse));

            mockMvc.perform(get("/api/products/{productId}/categories", PRODUCT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size()").value(1))
                    .andExpect(jsonPath("$[0].id").value(CATEGORY_ID));
        }
    }

    @Nested
    @DisplayName("POST /api/products Operations")
    class PostOperations {

        @Test
        @DisplayName("Should create product and return 201 Created")
        void addProduct_Success() throws Exception {
            when(productService.addProduct(any(ProductRequest.class))).thenReturn(productResponse);

            mockMvc.perform(post("/api/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                    .andExpect(jsonPath("$.name").value("Test Product"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when validation fails")
        void addProduct_ValidationFailure() throws Exception {
            mockMvc.perform(post("/api/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidProductRequest)))
                    .andExpect(status().isBadRequest());

            Mockito.verifyNoInteractions(productService);
        }

        @Test
        @DisplayName("Should assign category to product")
        void assignCategoryToProduct_Success() throws Exception {
            when(productService.assignCategoryToProduct(PRODUCT_ID, CATEGORY_ID)).thenReturn(productResponse);

            mockMvc.perform(post("/api/products/{productId}/categories/{categoryId}", PRODUCT_ID, CATEGORY_ID)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PRODUCT_ID));
        }
    }

    @Nested
    @DisplayName("PUT & PATCH Operations")
    class UpdateOperations {

        @Test
        @DisplayName("Should update product successfully")
        void updateProduct_Success() throws Exception {
            when(productService.updateProduct(eq(PRODUCT_ID), any(ProductRequest.class))).thenReturn(productResponse);

            mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validProductRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PRODUCT_ID));
        }

        @Test
        @DisplayName("Should return 400 Bad Request on invalid update data")
        void updateProduct_ValidationFailure() throws Exception {
            mockMvc.perform(put("/api/products/{id}", PRODUCT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidProductRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should change price successfully")
        void changePrice_Success() throws Exception {
            BigDecimal newPrice = BigDecimal.valueOf(150.0);
            when(productService.changePrice(PRODUCT_ID, newPrice)).thenReturn(productResponse);

            mockMvc.perform(patch("/api/products/{id}", PRODUCT_ID)
                            .param("newPrice", newPrice.toString())
                            .with(csrf()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 400 Bad Request if new price is negative")
        void changePrice_ValidationFailure() throws Exception {
            mockMvc.perform(patch("/api/products/{id}", PRODUCT_ID)
                            .param("newPrice", "-50.0")
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE Operations (Admin Only)")
    class DeleteOperations {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete product successfully as ADMIN")
        void deleteProduct_Success() throws Exception {
            doNothing().when(productService).deleteProduct(PRODUCT_ID);

            mockMvc.perform(delete("/api/products/{id}", PRODUCT_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should remove category from product as ADMIN")
        void removeCategoryFromProduct_Success() throws Exception {
            when(productService.removeCategoryFromProduct(PRODUCT_ID, CATEGORY_ID)).thenReturn(productResponse);

            mockMvc.perform(delete("/api/products/{productId}/categories/{categoryId}", PRODUCT_ID, CATEGORY_ID)
                            .with(csrf()))
                    .andExpect(status().isOk());
        }
    }
}