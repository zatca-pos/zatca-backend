package com.zatca.pos.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val orderNumber: String,  // Sequential per branch per day

    @Column(nullable = false)
    val uuid: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val branchId: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus = OrderStatus.COMPLETED,

    @Column(nullable = false)
    val totalInHalalas: Long,

    @Column(nullable = false)
    val vatInHalalas: Long,

    @Column(nullable = false)
    val netInHalalas: Long,

    @Column(columnDefinition = "TEXT")
    val zatcaXml: String? = null,

    @Column(length = 1000)
    val qrCodeBase64: String? = null,

    @Column
    val zatcaHash: String? = null,

    @Column
    val zatcaStatus: String? = null  // REPORTED, CLEARED, REJECTED
)

enum class OrderStatus {
    DRAFT, COMPLETED, CANCELLED
}