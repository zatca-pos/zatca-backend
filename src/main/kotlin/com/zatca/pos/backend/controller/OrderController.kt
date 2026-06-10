package com.zatca.pos.backend.controller

import com.zatca.pos.backend.dto.CheckoutRequest
import com.zatca.pos.backend.dto.CheckoutResponse
import com.zatca.pos.backend.entity.Order
import com.zatca.pos.backend.service.OrderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = ["*"])
class OrderController(private val orderService: OrderService) {

    @PostMapping("/checkout")
    fun checkout(@RequestBody request: CheckoutRequest): CheckoutResponse =
        orderService.checkout(request)

    @GetMapping
    fun getOrders(@RequestParam branchId: String): List<Order> =
        orderService.getOrders(branchId)
}
