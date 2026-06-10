package com.zatca.pos.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "purchase_orders")
data class PurchaseOrder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: Supplier,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false)
    val branch: Branch,

    @Column(nullable = false)
    val orderNumber: String,

    @Column(nullable = false)
    val totalInHalalas: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PurchaseStatus = PurchaseStatus.PENDING,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column
    val notes: String = ""
)

enum class PurchaseStatus {
    PENDING, RECEIVED, CANCELLED
}