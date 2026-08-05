package com.store.store_management_api.category;

import com.store.store_management_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category with name: {}", request.name());
        Category category = categoryMapper.toEntity(request);
        log.debug("Mapped CategoryRequest to Category entity: {}", category);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories.");
        List<CategoryResponse> categories = categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
        log.info("Found {} categories.", categories.size());
        return categories;
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });
        log.debug("Found category: {}", category.getName());
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with ID: {} with new name: {}", id, request.name());
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {} for update.", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });
        categoryMapper.updateCategory(request, category);
        log.debug("Updated category entity for ID: {}", id);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with ID: {} updated successfully.", updatedCategory.getId());
        return categoryMapper.toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Category not found with ID: {} for deletion.", id);
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category with ID: {} deleted successfully.", id);
    }
}