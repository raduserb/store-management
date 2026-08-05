package com.store.store_management_api.product;

import com.store.store_management_api.category.Category;
import com.store.store_management_api.category.CategoryMapper;
import com.store.store_management_api.category.CategoryRepository;
import com.store.store_management_api.category.CategoryResponse;
import com.store.store_management_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public ProductResponse addProduct(ProductRequest request) {
        log.info("Adding new product with name: {}", request.name());
        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully with ID: {}", savedProduct.getId());
        return productMapper.toProductResponse(savedProduct);
    }

    public ProductResponse findProduct(Long id) {
        log.info("Finding product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        log.debug("Found product: {}", product.getName());
        return productMapper.toProductResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products.");
        List<ProductResponse> products = productRepository.findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
        log.info("Found {} products.", products.size());
        return products;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        log.info("Fetching products for category ID: {}", categoryId);
        if (!categoryRepository.existsById(categoryId)) {
            log.warn("Category not found with ID: {} when fetching products.", categoryId);
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        List<ProductResponse> products = productRepository.findByCategoriesId(categoryId)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
        log.info("Found {} products for category ID: {}", products.size(), categoryId);
        return products;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getProductCategories(Long productId) {
        log.info("Fetching categories for product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} when fetching categories.", productId);
                    return new ResourceNotFoundException("Product not found with id: " + productId);
                });
        List<CategoryResponse> categories = product.getCategories()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
        log.info("Found {} categories for product ID: {}", categories.size(), productId);
        return categories;
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {} with new name: {}", id, request.name());
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} for update.", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        productMapper.updateProduct(request, product);
        Product updatedProduct = productRepository.save(product);
        log.info("Product with ID: {} updated successfully.", updatedProduct.getId());
        return productMapper.toProductResponse(updatedProduct);
    }

    public ProductResponse changePrice(Long id, BigDecimal newPrice) {
        log.info("Changing price for product ID: {} to {}", id, newPrice);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} for price change.", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        product.setPrice(newPrice);
        Product updatedProduct = productRepository.save(product);
        log.info("Price for product ID: {} changed successfully.", updatedProduct.getId());
        return productMapper.toProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Attempting to delete product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} for deletion.", id);
                    return new ResourceNotFoundException("Product not found with id: " + id);
                });
        productRepository.delete(product);
        log.info("Product with ID: {} deleted successfully.", id);
    }

    @Transactional
    public ProductResponse assignCategoryToProduct(Long productId, Long categoryId) {
        log.info("Assigning category ID: {} to product ID: {}", categoryId, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} for category assignment.", productId);
                    return new ResourceNotFoundException("Product not found with id: " + productId);
                });
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {} for product assignment.", categoryId);
                    return new ResourceNotFoundException("Category not found with id: " + categoryId);
                });
        product.getCategories().add(category);
        Product savedProduct = productRepository.save(product);
        log.info("Category ID: {} assigned to product ID: {} successfully.", categoryId, productId);
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse removeCategoryFromProduct(Long productId, Long categoryId) {
        log.info("Removing category ID: {} from product ID: {}", categoryId, productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {} for category removal.", productId);
                    return new ResourceNotFoundException("Product not found with id: " + productId);
                });
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {} for product category removal.", categoryId);
                    return new ResourceNotFoundException("Category not found with id: " + categoryId);
                });
        product.getCategories().remove(category);
        Product savedProduct = productRepository.save(product);
        log.info("Category ID: {} removed from product ID: {} successfully.", categoryId, productId);
        return productMapper.toProductResponse(savedProduct);
    }
}