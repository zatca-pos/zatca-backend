package com.zatca.pos.backend.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "promotions")
data class Promotion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val discountType: DiscountType = DiscountType.PERCENTAGE,

    @Column(nullable = false)
    val discountValue: Long, // percentage (e.g. 10 = 10%) or amount in halalas

    @Column(nullable = false)
    val appliesTo: String = "ALL", // ALL, CATEGORY, PRODUCT

    @Column
    val categoryId: Long? = null,

    @Column
    val productSku: String? = null,

    @Column
    val branchId: Long? = null, // null = all branches

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Column(nullable = false)
    val active: Boolean = true
)

enum class DiscountType {
    PERCENTAGE, FIXED_AMOUNT
}