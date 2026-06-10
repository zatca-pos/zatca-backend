package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findBySku(sku: String): Product?
    fun findByActiveTrue(): List<Product>
    fun findByActiveTrueAndNameEnContainingIgnoreCase(query: String): List<Product>
    fun findByCategoryIdAndActiveTrue(categoryId: Long): List<Product>
    fun findBySubCategoryIdAndActiveTrue(subCategoryId: Long): List<Product>
}