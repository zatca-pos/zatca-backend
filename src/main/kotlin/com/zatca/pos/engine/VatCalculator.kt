package com.zatca.pos.engine

/**
 * Handles all VAT calculations for Saudi Arabia.
 * 
 * Key rules:
 * - All monetary values are in HALALAS (1 SAR = 100 Halalas)
 * - Shelf prices are always VAT-INCLUSIVE
 * - VAT rate is stored in BASIS POINTS (1500 = 15.00%)
 */
object VatCalculator {

    /**
     * Calculate the net (taxable) amount from a VAT-inclusive price.
     *
     * Example:
     *   priceIncludingVat = 11500 (115.00 SAR)
     *   vatRateBps = 1500 (15%)
     *   result = 10000 (100.00 SAR)
     */
    fun getNetAmount(priceIncludingVat: Long, vatRateBps: Long): Long {
        return priceIncludingVat * 10000L / (10000L + vatRateBps)
    }

    /**
     * Calculate the VAT amount from a VAT-inclusive price.
     *
     * Example:
     *   priceIncludingVat = 11500 (115.00 SAR)
     *   vatRateBps = 1500 (15%)
     *   result = 1500 (15.00 SAR)
     */
    fun getVatAmount(priceIncludingVat: Long, vatRateBps: Long): Long {
        val net = getNetAmount(priceIncludingVat, vatRateBps)
        return priceIncludingVat - net
    }

    /**
     * Calculate totals for a line item with quantity.
     *
     * Returns LineTotal containing net, vat, and gross amounts in halalas.
     */
    fun calculateLineTotal(
        unitPriceIncludingVat: Long,
        quantity: Long,
        vatRateBps: Long
    ): LineTotal {
        val grossTotal = unitPriceIncludingVat * quantity
        val netTotal = getNetAmount(grossTotal, vatRateBps)
        val vatTotal = grossTotal - netTotal

        return LineTotal(
            netAmount = netTotal,
            vatAmount = vatTotal,
            grossAmount = grossTotal
        )
    }

    /**
     * Convert halalas to a display string in SAR.
     */
    fun toSarString(halalas: Long): String {
        return "%.2f".format(halalas / 100.0)
    }

    data class LineTotal(
        val netAmount: Long,
        val vatAmount: Long,
        val grossAmount: Long
    )
}