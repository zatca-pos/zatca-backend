package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "suppliers")
data class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @Column(unique = true)
    val vatNumber: String = "",

    @Column
    val phone: String = "",

    @Column
    val email: String = "",

    @Column
    val address: String = "",

    @Column(nullable = false)
    val active: Boolean = true
)