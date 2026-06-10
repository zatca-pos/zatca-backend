package com.zatca.pos.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "company")
data class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nameEn: String = "My Shop",

    @Column(nullable = false)
    val nameAr: String = "متجري",

    @Column(nullable = false, unique = true)
    val vatNumber: String = "311111111111113",

    @Column(length = 500)
    val addressEn: String = "Olaya Street, Riyadh",

    @Column(length = 500)
    val addressAr: String = "شارع العليا، الرياض",

    @Column
    val buildingNumber: String = "1234",

    @Column
    val postalCode: String = "12221",

    @Column
    val cityEn: String = "Riyadh",

    @Column
    val cityAr: String = "الرياض",

    @Column
    val country: String = "SA",

    @Column
    val phone: String = "",

    @Column
    val email: String = "",

    @Column
    val logoUrl: String = ""
)