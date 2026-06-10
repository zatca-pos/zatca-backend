package com.zatca.pos.backend.controller

import com.zatca.pos.backend.entity.*
import com.zatca.pos.backend.repository.*
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = ["*"])
class PurchaseOrderController(
    private val poRepo: PurchaseOrderRepository,
    private val itemRepo: PurchaseOrderItemRepository,
    private val supplierRepo: SupplierRepository,
    private val branchRepo: BranchRepository
) {

    @GetMapping
    fun getAll(): List<PurchaseOrder> = poRepo.findAllByOrderByCreatedAtDesc()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): PurchaseOrder = poRepo.findById(id).orElseThrow()

    @GetMapping("/{id}/items")
    fun getItems(@PathVariable id: Long): List<PurchaseOrderItem> = itemRepo.findByPurchaseOrderId(id)

    data class CreatePORequest(
        val supplierId: Long,
        val branchId: Long,
        val notes: String = "",
        val items: List<POItemRequest>
    )
    data class POItemRequest(val productSku: String, val productName: String, val quantity: Long, val unitPriceInHalalas: Long)

    @PostMapping
    fun create(@RequestBody req: CreatePORequest): PurchaseOrder {
        val supplier = supplierRepo.findById(req.supplierId).orElseThrow()
        val branch = branchRepo.findById(req.branchId).orElseThrow()
        val total = req.items.sumOf { it.quantity * it.unitPriceInHalalas }
        val count = poRepo.count() + 1
        val po = poRepo.save(PurchaseOrder(
            supplier = supplier, branch = branch,
            orderNumber = "PO-${String.format("%04d", count)}",
            totalInHalalas = total, notes = req.notes, createdAt = Instant.now()
        ))
        req.items.forEach { item ->
            itemRepo.save(PurchaseOrderItem(
                purchaseOrder = po, productSku = item.productSku,
                productName = item.productName, quantity = item.quantity,
                unitPriceInHalalas = item.unitPriceInHalalas,
                totalInHalalas = item.quantity * item.unitPriceInHalalas
            ))
        }
        return po
    }

    @PutMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestParam status: String): PurchaseOrder {
        val po = poRepo.findById(id).orElseThrow()
        return poRepo.save(po.copy(status = PurchaseStatus.valueOf(status)))
    }
}