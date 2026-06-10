package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Branch
import com.zatca.pos.backend.repository.BranchRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/branches")
@CrossOrigin(origins = ["*"])
class BranchController(private val branchRepository: BranchRepository) {

    @GetMapping
    fun getAll(): List<Branch> = branchRepository.findByActiveTrue()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Branch = branchRepository.findById(id).orElseThrow()

    @PostMapping
    fun create(@RequestBody branch: Branch): Branch = branchRepository.save(branch)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody branch: Branch): Branch {
        val existing = branchRepository.findById(id).orElseThrow()
        return branchRepository.save(existing.copy(
            code = branch.code,
            nameEn = branch.nameEn,
            nameAr = branch.nameAr,
            addressEn = branch.addressEn,
            addressAr = branch.addressAr,
            phone = branch.phone,
            active = branch.active
        ))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {
        val branch = branchRepository.findById(id).orElseThrow()
        branchRepository.save(branch.copy(active = false))
        return "OK"
    }
}