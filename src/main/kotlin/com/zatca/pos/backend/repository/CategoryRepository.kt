package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByActiveTrue(): List<Category>
}