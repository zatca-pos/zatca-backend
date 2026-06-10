package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "purchase_order_items")
data class PurchaseOrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    val purchaseOrder: PurchaseOrder? = null,

    @Column(nullable = false)
    val productSku: String,

    @Column(nullable = false)
    val productName: String,

    @Column(nullable = false)
    val quantity: Long,

    @Column(nullable = false)
    val unitPriceInHalalas: Long,

    @Column(nullable = false)
    val totalInHalalas: Long
)