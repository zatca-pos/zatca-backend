package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.PurchaseOrder
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseOrderRepository : JpaRepository<PurchaseOrder, Long> {
    fun findByBranchIdOrderByCreatedAtDesc(branchId: Long): List<PurchaseOrder>
    fun findAllByOrderByCreatedAtDesc(): List<PurchaseOrder>
}