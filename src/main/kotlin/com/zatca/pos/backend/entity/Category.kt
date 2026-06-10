package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @Column(nullable = false)
    val active: Boolean = true
)