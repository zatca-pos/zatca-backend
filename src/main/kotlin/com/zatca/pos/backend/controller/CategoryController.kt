package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Category
import com.zatca.pos.backend.entity.SubCategory
import com.zatca.pos.backend.repository.CategoryRepository
import com.zatca.pos.backend.repository.SubCategoryRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = ["*"])
class CategoryController(
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository
) {

    @GetMapping
    fun getAll(): List<Category> = categoryRepository.findByActiveTrue()

    @PostMapping
    fun create(@RequestBody category: Category): Category = categoryRepository.save(category)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody category: Category): Category {
        val existing = categoryRepository.findById(id).orElseThrow()
        return categoryRepository.save(existing.copy(nameEn = category.nameEn, nameAr = category.nameAr))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {
        val cat = categoryRepository.findById(id).orElseThrow()
        categoryRepository.save(cat.copy(active = false))
        return "OK"
    }

    @GetMapping("/{id}/subcategories")
    fun getSubCategories(@PathVariable id: Long): List<SubCategory> =
        subCategoryRepository.findByCategoryIdAndActiveTrue(id)

    @PostMapping("/{id}/subcategories")
    fun createSubCategory(@PathVariable id: Long, @RequestBody sub: SubCategory): SubCategory {
        val category = categoryRepository.findById(id).orElseThrow()
        return subCategoryRepository.save(sub.copy(category = category))
    }

    @PutMapping("/subcategories/{subId}")
    fun updateSubCategory(@PathVariable subId: Long, @RequestBody sub: SubCategory): SubCategory {
        val existing = subCategoryRepository.findById(subId).orElseThrow()
        return subCategoryRepository.save(existing.copy(nameEn = sub.nameEn, nameAr = sub.nameAr))
    }

    @DeleteMapping("/subcategories/{subId}")
    fun deleteSubCategory(@PathVariable subId: Long): String {
        val sub = subCategoryRepository.findById(subId).orElseThrow()
        subCategoryRepository.save(sub.copy(active = false))
        return "OK"
    }
}