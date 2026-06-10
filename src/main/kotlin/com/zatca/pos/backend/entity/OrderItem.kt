package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order? = null,

    @Column(nullable = false)
    val productSku: String,

    @Column(nullable = false)
    val productName: String,

    @Column(nullable = false)
    val productNameAr: String = "",

    @Column(nullable = false)
    val quantity: Long,

    @Column(nullable = false)
    val unitPriceInHalalas: Long,

    @Column(nullable = false)
    val vatRateBps: Long,

    @Column(nullable = false)
    val netAmountInHalalas: Long,

    @Column(nullable = false)
    val vatAmountInHalalas: Long,

    @Column(nullable = false)
    val totalAmountInHalalas: Long
)