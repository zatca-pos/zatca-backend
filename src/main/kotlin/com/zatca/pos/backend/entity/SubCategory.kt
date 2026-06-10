package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "sub_categories")
data class SubCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,

    @Column(nullable = false)
    val nameEn: String,

    @Column(nullable = false)
    val nameAr: String,

    @Column(nullable = false)
    val active: Boolean = true
)