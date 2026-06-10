package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.PurchaseOrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseOrderItemRepository : JpaRepository<PurchaseOrderItem, Long> {
    fun findByPurchaseOrderId(poId: Long): List<PurchaseOrderItem>
}