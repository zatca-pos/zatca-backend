package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Supplier
import com.zatca.pos.backend.repository.SupplierRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = ["*"])
class SupplierController(private val repo: SupplierRepository) {

    @GetMapping
    fun getAll(): List<Supplier> = repo.findByActiveTrue()

    @PostMapping
    fun create(@RequestBody s: Supplier): Supplier = repo.save(s)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody s: Supplier): Supplier {
        val e = repo.findById(id).orElseThrow()
        return repo.save(e.copy(nameEn=s.nameEn, nameAr=s.nameAr, vatNumber=s.vatNumber, phone=s.phone, email=s.email, address=s.address))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {
        val e = repo.findById(id).orElseThrow()
        repo.save(e.copy(active = false))
        return "OK"
    }
}