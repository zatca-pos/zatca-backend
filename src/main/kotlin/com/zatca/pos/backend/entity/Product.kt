package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val sku: String,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    val category: Category? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_category_id")
    val subCategory: SubCategory? = null,

    @Column(nullable = false)
    val priceInHalalas: Long,

    @Column(nullable = false)
    val vatRateBps: Long = 1500,

    @Column(nullable = false)
    val unitCode: String = "PCE",

    @Column(nullable = false)
    val hasSizes: Boolean = false,

    @Column(nullable = false)
    val active: Boolean = true
)