package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Promotion
import com.zatca.pos.backend.repository.PromotionRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = ["*"])
class PromotionController(private val repo: PromotionRepository) {

    @GetMapping
    fun getAll(): List<Promotion> = repo.findByActiveTrue()

    @PostMapping
    fun create(@RequestBody p: Promotion): Promotion = repo.save(p)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody p: Promotion): Promotion {
        val e = repo.findById(id).orElseThrow()
        return repo.save(e.copy(
            nameEn=p.nameEn, nameAr=p.nameAr, discountType=p.discountType,
            discountValue=p.discountValue, appliesTo=p.appliesTo,
            categoryId=p.categoryId, productSku=p.productSku, branchId=p.branchId,
            startDate=p.startDate, endDate=p.endDate, active=p.active
        ))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {
        val e = repo.findById(id).orElseThrow()
        repo.save(e.copy(active = false))
        return "OK"
    }
}