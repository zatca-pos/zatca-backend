package com.zatca.pos.model

import java.time.Instant
import java.util.UUID

/**
 * Represents a complete ZATCA-compliant invoice.
 */
data class ZatcaInvoice(
    val id: String,                     // Sequential invoice number (1, 2, 3...)
    val uuid: UUID,                     // Unique identifier for this invoice
    val issueDate: Instant,             // When the invoice was created
    val invoiceType: InvoiceType,       // STANDARD or SIMPLIFIED
    val seller: ZatcaParty,             // Your business
    val buyer: ZatcaParty,              // Customer (B2C placeholder for Simplified)
    val lines: List<InvoiceLine>,       // What they bought
    val taxTotal: TaxTotal,             // VAT summary
    val monetaryTotal: MonetaryTotal    // Final amounts
)

enum class InvoiceType {
    STANDARD,    // B2B - requires buyer VAT number
    SIMPLIFIED   // B2C - uses "000000000000000" for buyer
}

/**
 * A party involved in the invoice (seller or buyer).
 */
data class ZatcaParty(
    val vatId: String,          // VAT registration number or "000000000000000"
    val name: String,           // Company or person name
    val streetName: String,
    val buildingNumber: String,
    val cityName: String,
    val postalCode: String,
    val countryCode: String = "SA"
) {
    companion object {
        const val B2C_VAT_ID = "000000000000000"

        /** Pre-built B2C customer (individual consumer) */
        fun b2cConsumer() = ZatcaParty(
            vatId = B2C_VAT_ID,
            name = "مستهلك نهائي",
            streetName = "غير متوفر",
            buildingNumber = "0000",
            cityName = "الرياض",
            postalCode = "00000"
        )
    }
}

/**
 * A single line item on the invoice.
 * All amounts in HALALAS.
 */
data class InvoiceLine(
    val lineNumber: Int,
    val quantity: Long,
    val unitCode: String,       // "PCE" = pieces, "KGM" = kilograms
    val itemName: String,       // Product name
    val netAmount: Long,        // Taxable amount in halalas
    val vatRate: Long,          // VAT rate in basis points (1500 = 15%)
    val vatAmount: Long,        // VAT amount in halalas
    val totalAmount: Long       // netAmount + vatAmount
)

/**
 * VAT breakdown for the entire invoice.
 */
data class TaxTotal(
    val totalVatAmount: Long,
    val subtotals: List<TaxSubtotal>
)

data class TaxSubtotal(
    val taxableAmount: Long,    // Sum of all net amounts for this rate
    val vatAmount: Long,        // Sum of all VAT for this rate
    val vatRate: Long,          // VAT rate in basis points
    val categoryCode: String    // "S" = Standard, "Z" = Zero
)

/**
 * Final monetary totals for the invoice.
 */
data class MonetaryTotal(
    val netTotal: Long,         // Sum of all net amounts
    val vatTotal: Long,         // Sum of all VAT amounts
    val grossTotal: Long        // Total including VAT (what customer pays)
)