package com.store.store_management_api;

import com.store.store_management_api.category.Category;
import com.store.store_management_api.category.CategoryMapper;
import com.store.store_management_api.category.CategoryRepository;
import com.store.store_management_api.category.CategoryResponse;
import com.store.store_management_api.exception.ResourceNotFoundException;
import com.store.store_management_api.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Set;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private Category category;
    private CategoryResponse categoryResponse;

    private final Long PRODUCT_ID = 1L;
    private final Long CATEGORY_ID = 2L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setCategories(new HashSet<>());

        category = new Category();
        category.setId(CATEGORY_ID);
        category.setName("Test Category");

        categoryResponse = new CategoryResponse(CATEGORY_ID, "Test Category", "Category Description");

        productRequest = new ProductRequest("Test Product", BigDecimal.valueOf(100.0), 50);
        productResponse = new ProductResponse(
                PRODUCT_ID,
                "Test Product",
                BigDecimal.valueOf(100.0),
                50,
                Set.of(categoryResponse)
        );
    }

    @Nested
    @DisplayName("addProduct() Tests")
    class AddProduct {
        @Test
        @DisplayName("Should successfully add a product")
        void addProduct_Success() {
            when(productMapper.toProduct(productRequest)).thenReturn(product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.addProduct(productRequest);

            assertNotNull(result);
            assertEquals(PRODUCT_ID, result.id());
            verify(productRepository, times(1)).save(product);
        }
    }

    @Nested
    @DisplayName("findProduct() Tests")
    class FindProduct {
        @Test
        @DisplayName("Should return product when ID exists")
        void findProduct_Success() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.findProduct(PRODUCT_ID);

            assertNotNull(result);
            assertEquals(PRODUCT_ID, result.id());
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product ID does not exist")
        void findProduct_NotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.findProduct(PRODUCT_ID));
            verify(productMapper, never()).toProductResponse(any());
        }
    }

    @Nested
    @DisplayName("getAllProducts() Tests")
    class GetAllProducts {
        @Test
        @DisplayName("Should return list of all products")
        void getAllProducts_Success() {
            when(productRepository.findAll()).thenReturn(List.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            List<ProductResponse> result = productService.getAllProducts();

            assertEquals(1, result.size());
            verify(productRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getProductsByCategoryId() Tests")
    class GetProductsByCategoryId {
        @Test
        @DisplayName("Should return products for valid category ID")
        void getProductsByCategoryId_Success() {
            when(categoryRepository.existsById(CATEGORY_ID)).thenReturn(true);
            when(productRepository.findByCategoriesId(CATEGORY_ID)).thenReturn(List.of(product));
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            List<ProductResponse> result = productService.getProductsByCategoryId(CATEGORY_ID);

            assertEquals(1, result.size());
            verify(productRepository, times(1)).findByCategoriesId(CATEGORY_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for invalid category ID")
        void getProductsByCategoryId_CategoryNotFound() {
            when(categoryRepository.existsById(CATEGORY_ID)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> productService.getProductsByCategoryId(CATEGORY_ID));
            verify(productRepository, never()).findByCategoriesId(any());
        }
    }

    @Nested
    @DisplayName("getProductCategories() Tests")
    class GetProductCategories {
        @Test
        @DisplayName("Should return categories for valid product ID")
        void getProductCategories_Success() {
            product.getCategories().add(category);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

            List<CategoryResponse> result = productService.getProductCategories(PRODUCT_ID);

            assertEquals(1, result.size());
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for invalid product ID")
        void getProductCategories_ProductNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.getProductCategories(PRODUCT_ID));
        }
    }

    @Nested
    @DisplayName("updateProduct() Tests")
    class UpdateProduct {
        @Test
        @DisplayName("Should successfully update product")
        void updateProduct_Success() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            doNothing().when(productMapper).updateProduct(productRequest, product);
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.updateProduct(PRODUCT_ID, productRequest);

            assertNotNull(result);
            verify(productMapper, times(1)).updateProduct(productRequest, product);
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when updating non-existent product")
        void updateProduct_NotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(PRODUCT_ID, productRequest));
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("changePrice() Tests")
    class ChangePrice {
        @Test
        @DisplayName("Should successfully change product price")
        void changePrice_Success() {
            BigDecimal newPrice = BigDecimal.valueOf(150.0);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse); // Note: Mocking generic response for test simplicity

            ProductResponse result = productService.changePrice(PRODUCT_ID, newPrice);

            assertNotNull(result);
            assertEquals(newPrice, product.getPrice()); // Verify the state change before saving
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when changing price of non-existent product")
        void changePrice_NotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.changePrice(PRODUCT_ID, BigDecimal.valueOf(150.0)));
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteProduct() Tests")
    class DeleteProduct {
        @Test
        @DisplayName("Should successfully delete product")
        void deleteProduct_Success() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            productService.deleteProduct(PRODUCT_ID);

            verify(productRepository, times(1)).delete(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
        void deleteProduct_NotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(PRODUCT_ID));
            verify(productRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("assignCategoryToProduct() Tests")
    class AssignCategoryToProduct {
        @Test
        @DisplayName("Should successfully assign category to product")
        void assignCategoryToProduct_Success() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.assignCategoryToProduct(PRODUCT_ID, CATEGORY_ID);

            assertNotNull(result);
            assertTrue(product.getCategories().contains(category));
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if product not found")
        void assignCategoryToProduct_ProductNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.assignCategoryToProduct(PRODUCT_ID, CATEGORY_ID));
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if category not found")
        void assignCategoryToProduct_CategoryNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.assignCategoryToProduct(PRODUCT_ID, CATEGORY_ID));
            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeCategoryFromProduct() Tests")
    class RemoveCategoryFromProduct {
        @Test
        @DisplayName("Should successfully remove category from product")
        void removeCategoryFromProduct_Success() {
            product.getCategories().add(category); // Pre-add category

            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            when(productRepository.save(product)).thenReturn(product);
            when(productMapper.toProductResponse(product)).thenReturn(productResponse);

            ProductResponse result = productService.removeCategoryFromProduct(PRODUCT_ID, CATEGORY_ID);

            assertNotNull(result);
            assertFalse(product.getCategories().contains(category)); // Ensure it was removed
            verify(productRepository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if product not found")
        void removeCategoryFromProduct_ProductNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.removeCategoryFromProduct(PRODUCT_ID, CATEGORY_ID));
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if category not found")
        void removeCategoryFromProduct_CategoryNotFound() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> productService.removeCategoryFromProduct(PRODUCT_ID, CATEGORY_ID));
            verify(productRepository, never()).save(any());
        }
    }
}
