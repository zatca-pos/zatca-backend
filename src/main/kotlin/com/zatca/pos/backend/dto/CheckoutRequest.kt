package com.zatca.pos.backend.dto

data class CheckoutRequest(
    val branchId: String,
    val items: List<CartItem>
)

data class CartItem(
    val productSku: String,
    val quantity: Long
)