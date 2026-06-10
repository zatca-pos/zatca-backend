package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.Stock
import com.zatca.pos.backend.repository.BranchRepository
import com.zatca.pos.backend.repository.ProductRepository
import com.zatca.pos.backend.repository.StockRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = ["*"])
class StockController(
    private val stockRepo: StockRepository,
    private val productRepo: ProductRepository,
    private val branchRepo: BranchRepository
) {

    @GetMapping
    fun getAll(@RequestParam(required = false) branchId: Long?): List<Stock> {
        return if (branchId != null) stockRepo.findByBranchId(branchId)
        else stockRepo.findAll()
    }

    data class StockUpdate(val productSku: String, val productName: String, val branchId: Long, val quantity: Long)

    @PostMapping
    fun addStock(@RequestBody req: StockUpdate): Stock {
        val existing = stockRepo.findByProductSkuAndBranchId(req.productSku, req.branchId)
        return if (existing != null) {
            stockRepo.save(existing.copy(quantity = existing.quantity + req.quantity))
        } else {
            stockRepo.save(Stock(productSku = req.productSku, productName = req.productName, branchId = req.branchId, quantity = req.quantity))
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody qty: Map<String, Long>): Stock {
        val s = stockRepo.findById(id).orElseThrow()
        return stockRepo.save(s.copy(quantity = qty["quantity"] ?: s.quantity))
    }
}