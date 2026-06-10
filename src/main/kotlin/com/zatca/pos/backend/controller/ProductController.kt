package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Product
import com.zatca.pos.backend.entity.ProductSize
import com.zatca.pos.backend.repository.CategoryRepository
import com.zatca.pos.backend.repository.ProductRepository
import com.zatca.pos.backend.repository.ProductSizeRepository
import com.zatca.pos.backend.repository.SubCategoryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = ["*"])
class ProductController(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val productSizeRepository: ProductSizeRepository
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) subCategoryId: Long?,
        @RequestParam(required = false) q: String?
    ): List<Product> {
        return when {
            q != null && q.isNotEmpty() -> productRepository.findByActiveTrueAndNameEnContainingIgnoreCase(q)
            subCategoryId != null -> productRepository.findBySubCategoryIdAndActiveTrue(subCategoryId)
            categoryId != null -> productRepository.findByCategoryIdAndActiveTrue(categoryId)
            else -> productRepository.findByActiveTrue()
        }
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<Product> {
        val product = productRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(product)
    }

    @PostMapping
    fun create(@RequestBody product: Product): Product {
        return productRepository.save(product)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody product: Product): Product {
        val existing = productRepository.findById(id).orElseThrow()
        return productRepository.save(existing.copy(
            sku = product.sku,
            nameEn = product.nameEn,
            nameAr = product.nameAr,
            category = product.category,
            subCategory = product.subCategory,
            priceInHalalas = product.priceInHalalas,
            vatRateBps = product.vatRateBps,
            unitCode = product.unitCode,
            hasSizes = product.hasSizes,
            active = product.active
        ))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {
        val product = productRepository.findById(id).orElseThrow()
        productRepository.save(product.copy(active = false))
        return "OK"
    }

    // Sizes
    @GetMapping("/{productId}/sizes")
    fun getSizes(@PathVariable productId: Long): List<ProductSize> =
        productSizeRepository.findByProductId(productId)

    @PostMapping("/{productId}/sizes")
    fun addSize(@PathVariable productId: Long, @RequestBody size: ProductSize): ProductSize =
        productSizeRepository.save(size.copy(productId = productId))

    @PutMapping("/sizes/{sizeId}")
    fun updateSize(@PathVariable sizeId: Long, @RequestBody size: ProductSize): ProductSize {
        val existing = productSizeRepository.findById(sizeId).orElseThrow()
        return productSizeRepository.save(existing.copy(
            sizeNameEn = size.sizeNameEn,
            sizeNameAr = size.sizeNameAr,
            priceInHalalas = size.priceInHalalas,
            sku = size.sku
        ))
    }

    @DeleteMapping("/sizes/{sizeId}")
    fun deleteSize(@PathVariable sizeId: Long): String {
        productSizeRepository.deleteById(sizeId)
        return "OK"
    }

    @GetMapping("/categories")
    fun getCategories() = categoryRepository.findByActiveTrue()

    @GetMapping("/subcategories")
    fun getSubCategories(@RequestParam categoryId: Long) =
        subCategoryRepository.findByCategoryIdAndActiveTrue(categoryId)
}