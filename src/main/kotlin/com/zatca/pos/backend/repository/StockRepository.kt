package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface StockRepository : JpaRepository<Stock, Long> {
    fun findByBranchId(branchId: Long): List<Stock>
    fun findByProductSkuAndBranchId(productSku: String, branchId: Long): Stock?
}