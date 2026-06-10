package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "branches")
data class Branch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val code: String,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @Column
    val addressEn: String = "",

    @Column
    val addressAr: String = "",

    @Column
    val phone: String = "",

    @Column(nullable = false)
    val active: Boolean = true
)