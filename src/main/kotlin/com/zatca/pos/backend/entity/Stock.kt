package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "stock", uniqueConstraints = [UniqueConstraint(columnNames = ["product_sku", "branch_id"])])
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "product_sku", nullable = false)
    val productSku: String,

    @Column(name = "product_name", nullable = false)
    val productName: String,

    @Column(name = "branch_id", nullable = false)
    val branchId: Long,

    @Column(nullable = false)
    val quantity: Long = 0
)