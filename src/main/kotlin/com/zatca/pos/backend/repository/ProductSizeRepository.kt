package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.ProductSize
import org.springframework.data.jpa.repository.JpaRepository

interface ProductSizeRepository : JpaRepository<ProductSize, Long> {
    fun findByProductId(productId: Long): List<ProductSize>
}