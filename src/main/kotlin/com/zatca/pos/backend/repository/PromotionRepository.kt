package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Promotion
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface PromotionRepository : JpaRepository<Promotion, Long> {
    fun findByActiveTrue(): List<Promotion>
    fun findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        start: LocalDate, end: LocalDate
    ): List<Promotion>
    fun findByBranchIdOrBranchIdIsNull(branchId: Long): List<Promotion>
}