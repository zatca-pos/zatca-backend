package com.zatca.pos.backend.service

import com.zatca.pos.backend.entity.Product
import com.zatca.pos.backend.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getAllActiveProducts(): List<Product> = productRepository.findByActiveTrue()

    fun getProductBySku(sku: String): Product? = productRepository.findBySku(sku)

    fun searchProducts(query: String): List<Product> =
        productRepository.findByActiveTrueAndNameEnContainingIgnoreCase(query)

    fun createProduct(product: Product): Product = productRepository.save(product)

    fun updateProduct(id: Long, product: Product): Product {
        val existing = productRepository.findById(id).orElseThrow {
            IllegalArgumentException("Product not found: $id")
        }
        return productRepository.save(existing.copy(
            sku = product.sku,
            nameEn = product.nameEn,
            nameAr = product.nameAr,
            priceInHalalas = product.priceInHalalas,
            vatRateBps = product.vatRateBps,
            unitCode = product.unitCode,
            active = product.active
        ))
    }

    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id).orElseThrow {
            IllegalArgumentException("Product not found: $id")
        }
        productRepository.save(product.copy(active = false))
    }
}