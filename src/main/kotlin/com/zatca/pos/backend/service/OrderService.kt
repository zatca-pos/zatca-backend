package com.zatca.pos.backend.service

import com.zatca.pos.backend.dto.*
import com.zatca.pos.backend.entity.*
import com.zatca.pos.backend.repository.*
import com.zatca.pos.crypto.ZatcaSigner
import com.zatca.pos.engine.VatCalculator
import com.zatca.pos.model.*
import com.zatca.pos.xml.InvoiceXmlBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val companyRepository: CompanyRepository
) {

    @Transactional
    fun checkout(request: CheckoutRequest): CheckoutResponse {
        val today = LocalDate.now()
        val todayStart = today.atStartOfDay(ZoneId.of("Asia/Riyadh")).toInstant()
        val countToday = orderRepository.countByBranchIdAndCreatedAtAfter(request.branchId, todayStart)
        val orderNumber = "${request.branchId}-${today}-${countToday + 1}"

        var totalNet = 0L
        var totalVat = 0L
        var totalGross = 0L
        val orderItems = mutableListOf<OrderItem>()
        val invoiceLines = mutableListOf<InvoiceLine>()
        val taxSubtotals = mutableListOf<TaxSubtotal>()

        request.items.forEachIndexed { index, cartItem ->
            val product = productRepository.findBySku(cartItem.productSku)
                ?: throw IllegalArgumentException("Product not found: ${cartItem.productSku}")

            val lineTotals = VatCalculator.calculateLineTotal(
                unitPriceIncludingVat = product.priceInHalalas,
                quantity = cartItem.quantity,
                vatRateBps = product.vatRateBps
            )

            totalNet += lineTotals.netAmount
            totalVat += lineTotals.vatAmount
            totalGross += lineTotals.grossAmount

            orderItems.add(OrderItem(
                productSku = product.sku,
                productName = product.nameEn,
                productNameAr = product.nameAr,
                quantity = cartItem.quantity,
                unitPriceInHalalas = product.priceInHalalas,
                vatRateBps = product.vatRateBps,
                netAmountInHalalas = lineTotals.netAmount,
                vatAmountInHalalas = lineTotals.vatAmount,
                totalAmountInHalalas = lineTotals.grossAmount
            ))

            invoiceLines.add(InvoiceLine(
                lineNumber = index + 1,
                quantity = cartItem.quantity,
                unitCode = product.unitCode,
                itemName = product.nameEn,
                netAmount = lineTotals.netAmount,
                vatRate = product.vatRateBps,
                vatAmount = lineTotals.vatAmount,
                totalAmount = lineTotals.grossAmount
            ))

            taxSubtotals.add(TaxSubtotal(
                taxableAmount = lineTotals.netAmount,
                vatAmount = lineTotals.vatAmount,
                vatRate = product.vatRateBps,
                categoryCode = "S"
            ))
        }

        val company = companyRepository.findTopByOrderByIdAsc() ?: Company()

        val invoice = ZatcaInvoice(
            id = orderNumber,
            uuid = UUID.randomUUID(),
            issueDate = Instant.now(),
            invoiceType = InvoiceType.SIMPLIFIED,
            seller = ZatcaParty(
                vatId = company.vatNumber,
                name = company.nameEn,
                streetName = company.addressEn,
                buildingNumber = company.buildingNumber,
                cityName = company.cityEn,
                postalCode = company.postalCode
            ),
            buyer = ZatcaParty.b2cConsumer(),
            lines = invoiceLines,
            taxTotal = TaxTotal(totalVatAmount = totalVat, subtotals = taxSubtotals),
            monetaryTotal = MonetaryTotal(netTotal = totalNet, vatTotal = totalVat, grossTotal = totalGross)
        )

        val unsignedXml = InvoiceXmlBuilder.build(invoice)
        val hash = ZatcaSigner.generateInvoiceHash(unsignedXml)
        val cert = ZatcaSigner.generateTestCertificate()
        val signedXml = ZatcaSigner.signXml(unsignedXml, cert.certificate, cert.privateKey, hash)

        val qrTlv = ZatcaSigner.generateQrCode(
            sellerName = company.nameEn,
            vatNumber = company.vatNumber,
            timestamp = invoice.issueDate,
            totalWithVat = totalGross,
            vatTotal = totalVat
        )

        val order = Order(
            orderNumber = orderNumber,
            branchId = request.branchId,
            totalInHalalas = totalGross,
            vatInHalalas = totalVat,
            netInHalalas = totalNet,
            zatcaXml = signedXml,
            qrCodeBase64 = qrTlv,
            zatcaHash = hash,
            zatcaStatus = "SIGNED"
        )
        val savedOrder = orderRepository.save(order)

        orderItems.forEach { item ->
            orderItemRepository.save(item.copy(order = savedOrder))
        }

        return CheckoutResponse(
            orderId = savedOrder.id,
            orderNumber = orderNumber,
            totalSar = VatCalculator.toSarString(totalGross),
            vatSar = VatCalculator.toSarString(totalVat),
            netSar = VatCalculator.toSarString(totalNet),
            qrCodeBase64 = qrTlv,
            items = orderItems.map { item ->
                CheckoutItemResponse(
                    productName = item.productName,
                    productNameAr = item.productNameAr,
                    quantity = item.quantity,
                    unitPriceSar = VatCalculator.toSarString(item.unitPriceInHalalas),
                    totalSar = VatCalculator.toSarString(item.totalAmountInHalalas),
                    vatSar = VatCalculator.toSarString(item.vatAmountInHalalas)
                )
            }
        )
    }

    fun getOrders(branchId: String): List<Order> =
        orderRepository.findByBranchIdOrderByCreatedAtDesc(branchId)
}