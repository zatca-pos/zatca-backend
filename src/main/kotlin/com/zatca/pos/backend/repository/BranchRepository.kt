package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Branch
import org.springframework.data.jpa.repository.JpaRepository

interface BranchRepository : JpaRepository<Branch, Long> {
    fun findByActiveTrue(): List<Branch>
    fun findByCode(code: String): Branch?
}