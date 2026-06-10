package com.zatca.pos.backend.service

import com.zatca.pos.backend.entity.Company
import com.zatca.pos.backend.repository.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyService(private val companyRepository: CompanyRepository) {

    fun getCompany(): Company {
        return companyRepository.findTopByOrderByIdAsc() ?: companyRepository.save(Company())
    }

    fun updateCompany(company: Company): Company {
        val existing = getCompany()
        return companyRepository.save(existing.copy(
            nameEn = company.nameEn,
            nameAr = company.nameAr,
            vatNumber = company.vatNumber,
            addressEn = company.addressEn,
            addressAr = company.addressAr,
            buildingNumber = company.buildingNumber,
            postalCode = company.postalCode,
            cityEn = company.cityEn,
            cityAr = company.cityAr,
            country = company.country,
            phone = company.phone,
            email = company.email,
            logoUrl = company.logoUrl
        ))
    }
}