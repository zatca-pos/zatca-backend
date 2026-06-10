package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Supplier
import org.springframework.data.jpa.repository.JpaRepository

interface SupplierRepository : JpaRepository<Supplier, Long> {
    fun findByActiveTrue(): List<Supplier>
}