package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.SubCategory
import org.springframework.data.jpa.repository.JpaRepository

interface SubCategoryRepository : JpaRepository<SubCategory, Long> {
    fun findByCategoryIdAndActiveTrue(categoryId: Long): List<SubCategory>
    fun findByActiveTrue(): List<SubCategory>
}