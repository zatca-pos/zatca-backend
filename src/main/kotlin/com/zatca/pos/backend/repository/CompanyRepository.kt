package com.zatca.pos.backend.repository

import com.zatca.pos.backend.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findTopByOrderByIdAsc(): Company?
}