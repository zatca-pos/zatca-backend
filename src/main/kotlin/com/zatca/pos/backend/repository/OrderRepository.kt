package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByBranchIdAndCreatedAtBetween(branchId: String, start: Instant, end: Instant): List<Order>
    fun findByBranchIdOrderByCreatedAtDesc(branchId: String): List<Order>
    fun countByBranchIdAndCreatedAtAfter(branchId: String, after: Instant): Long
}