package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Company
import com.zatca.pos.backend.service.CompanyService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/company")
@CrossOrigin(origins = ["*"])
class CompanyController(private val companyService: CompanyService) {

    @GetMapping
    fun getCompany(): Company = companyService.getCompany()

    @PutMapping
    fun updateCompany(@RequestBody company: Company): Company =
        companyService.updateCompany(company)
}