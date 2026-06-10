package com.zatca.pos.backend.dto

data class CheckoutResponse(
    val orderId: Long,
    val orderNumber: String,
    val totalSar: String,
    val vatSar: String,
    val netSar: String,
    val qrCodeBase64: String,
    val items: List<CheckoutItemResponse>
)

data class CheckoutItemResponse(
    val productName: String,
    val productNameAr: String = "",
    val quantity: Long,
    val unitPriceSar: String,
    val totalSar: String,
    val vatSar: String
)