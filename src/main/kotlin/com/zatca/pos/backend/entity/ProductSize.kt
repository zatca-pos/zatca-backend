package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "product_sizes")
data class ProductSize(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val productId: Long,

    @Column(nullable = false)
    val sizeNameEn: String,

    @Column(nullable = false)
    val sizeNameAr: String,

    @Column(nullable = false)
    val priceInHalalas: Long,

    @Column(nullable = false, unique = true)
    val sku: String
)