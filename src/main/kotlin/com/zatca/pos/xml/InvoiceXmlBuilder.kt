package com.zatca.pos.xml

import com.zatca.pos.engine.VatCalculator
import com.zatca.pos.model.*
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object InvoiceXmlBuilder {

    private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun build(invoice: ZatcaInvoice): String {
        val xml = StringBuilder()
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        xml.append("<Invoice")
        xml.append(" xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"")
        xml.append(" xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"")
        xml.append(" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"")
        xml.append(" xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\"")
        xml.append(">\n")
        xml.append("  <cbc:ProfileID>urn:fatoora:sa:sar:reporting:1.0</cbc:ProfileID>\n")
        xml.append("  <cbc:ID>${invoice.id}</cbc:ID>\n")
        xml.append("  <cbc:UUID>${invoice.uuid}</cbc:UUID>\n")
        val utcDateTime = invoice.issueDate.atOffset(ZoneOffset.UTC)
        xml.append("  <cbc:IssueDate>${utcDateTime.format(DATE_FORMAT)}</cbc:IssueDate>\n")
        xml.append("  <cbc:IssueTime>${utcDateTime.format(TIME_FORMAT)}Z</cbc:IssueTime>\n")
        val typeCode = when (invoice.invoiceType) {
            InvoiceType.STANDARD -> "388"
            InvoiceType.SIMPLIFIED -> "381"
        }
        xml.append("  <cbc:InvoiceTypeCode name=\"$typeCode\">$typeCode</cbc:InvoiceTypeCode>\n")
        xml.append("  <cbc:DocumentCurrencyCode>SAR</cbc:DocumentCurrencyCode>\n")
        xml.append("  <cbc:TaxCurrencyCode>SAR</cbc:TaxCurrencyCode>\n")
        buildParty(xml, invoice.seller, "AccountingSupplierParty")
        buildParty(xml, invoice.buyer, "AccountingCustomerParty")
        xml.append("  <ext:UBLExtensions>\n")
        xml.append("    <ext:UBLExtension>\n")
        xml.append("      <ext:ExtensionURI>urn:oasis:names:specification:ubl:dsig:enveloped:xades</ext:ExtensionURI>\n")
        xml.append("      <ext:ExtensionContent>\n")
        xml.append("        <!-- SIGNATURE_PLACEHOLDER -->\n")
        xml.append("      </ext:ExtensionContent>\n")
        xml.append("    </ext:UBLExtension>\n")
        xml.append("  </ext:UBLExtensions>\n")
        buildTaxTotal(xml, invoice.taxTotal)
        buildMonetaryTotal(xml, invoice.monetaryTotal)
        invoice.lines.forEach { line -> buildInvoiceLine(xml, line) }
        xml.append("</Invoice>")
        return xml.toString()
    }

    private fun buildParty(xml: StringBuilder, party: ZatcaParty, tagName: String) {
        xml.append("  <cac:$tagName>\n")
        xml.append("    <cac:Party>\n")
        if (tagName == "AccountingSupplierParty") {
            xml.append("      <cac:PartyIdentification>\n")
            xml.append("        <cbc:ID schemeID=\"CRN\">${party.vatId}</cbc:ID>\n")
            xml.append("      </cac:PartyIdentification>\n")
        }
        xml.append("      <cac:PostalAddress>\n")
        xml.append("        <cbc:StreetName>${esc(party.streetName)}</cbc:StreetName>\n")
        xml.append("        <cbc:BuildingNumber>${party.buildingNumber}</cbc:BuildingNumber>\n")
        xml.append("        <cbc:CityName>${esc(party.cityName)}</cbc:CityName>\n")
        xml.append("        <cbc:PostalZone>${party.postalCode}</cbc:PostalZone>\n")
        xml.append("        <cac:Country>\n")
        xml.append("          <cbc:IdentificationCode>${party.countryCode}</cbc:IdentificationCode>\n")
        xml.append("        </cac:Country>\n")
        xml.append("      </cac:PostalAddress>\n")
        xml.append("      <cac:PartyTaxScheme>\n")
        xml.append("        <cbc:CompanyID>${party.vatId}</cbc:CompanyID>\n")
        xml.append("        <cac:TaxScheme>\n")
        xml.append("          <cbc:ID>VAT</cbc:ID>\n")
        xml.append("        </cac:TaxScheme>\n")
        xml.append("      </cac:PartyTaxScheme>\n")
        xml.append("      <cac:PartyLegalEntity>\n")
        xml.append("        <cbc:RegistrationName>${esc(party.name)}</cbc:RegistrationName>\n")
        xml.append("      </cac:PartyLegalEntity>\n")
        xml.append("    </cac:Party>\n")
        xml.append("  </cac:$tagName>\n")
    }

    private fun buildInvoiceLine(xml: StringBuilder, line: InvoiceLine) {
        xml.append("  <cac:InvoiceLine>\n")
        xml.append("    <cbc:ID>${line.lineNumber}</cbc:ID>\n")
        xml.append("    <cbc:InvoicedQuantity unitCode=\"${line.unitCode}\">${line.quantity}</cbc:InvoicedQuantity>\n")
        xml.append("    <cbc:LineExtensionAmount currencyID=\"SAR\">${VatCalculator.toSarString(line.netAmount)}</cbc:LineExtensionAmount>\n")
        xml.append("    <cac:TaxTotal>\n")
        xml.append("      <cbc:TaxAmount currencyID=\"SAR\">${VatCalculator.toSarString(line.vatAmount)}</cbc:TaxAmount>\n")
        xml.append("      <cac:TaxSubtotal>\n")
        xml.append("        <cbc:TaxableAmount currencyID=\"SAR\">${VatCalculator.toSarString(line.netAmount)}</cbc:TaxableAmount>\n")
        xml.append("        <cbc:TaxAmount currencyID=\"SAR\">${VatCalculator.toSarString(line.vatAmount)}</cbc:TaxAmount>\n")
        xml.append("        <cac:TaxCategory>\n")
        xml.append("          <cbc:ID>${catCode(line.vatRate)}</cbc:ID>\n")
        xml.append("          <cbc:Percent>${"%.2f".format(line.vatRate / 10000.0)}</cbc:Percent>\n")
        xml.append("          <cac:TaxScheme>\n")
        xml.append("            <cbc:ID>VAT</cbc:ID>\n")
        xml.append("          </cac:TaxScheme>\n")
        xml.append("        </cac:TaxCategory>\n")
        xml.append("      </cac:TaxSubtotal>\n")
        xml.append("    </cac:TaxTotal>\n")
        xml.append("    <cac:Item>\n")
        xml.append("      <cbc:Name>${esc(line.itemName)}</cbc:Name>\n")
        xml.append("    </cac:Item>\n")
        xml.append("    <cac:Price>\n")
        xml.append("      <cbc:PriceAmount currencyID=\"SAR\">${VatCalculator.toSarString(line.netAmount)}</cbc:PriceAmount>\n")
        xml.append("    </cac:Price>\n")
        xml.append("  </cac:InvoiceLine>\n")
    }

    private fun buildTaxTotal(xml: StringBuilder, taxTotal: TaxTotal) {
        xml.append("  <cac:TaxTotal>\n")
        xml.append("    <cbc:TaxAmount currencyID=\"SAR\">${VatCalculator.toSarString(taxTotal.totalVatAmount)}</cbc:TaxAmount>\n")
        taxTotal.subtotals.forEach { sub ->
            xml.append("    <cac:TaxSubtotal>\n")
            xml.append("      <cbc:TaxableAmount currencyID=\"SAR\">${VatCalculator.toSarString(sub.taxableAmount)}</cbc:TaxableAmount>\n")
            xml.append("      <cbc:TaxAmount currencyID=\"SAR\">${VatCalculator.toSarString(sub.vatAmount)}</cbc:TaxAmount>\n")
            xml.append("      <cac:TaxCategory>\n")
            xml.append("        <cbc:ID>${sub.categoryCode}</cbc:ID>\n")
            xml.append("        <cbc:Percent>${"%.2f".format(sub.vatRate / 10000.0)}</cbc:Percent>\n")
            xml.append("        <cac:TaxScheme>\n")
            xml.append("          <cbc:ID>VAT</cbc:ID>\n")
            xml.append("        </cac:TaxScheme>\n")
            xml.append("      </cac:TaxCategory>\n")
            xml.append("    </cac:TaxSubtotal>\n")
        }
        xml.append("  </cac:TaxTotal>\n")
    }

    private fun buildMonetaryTotal(xml: StringBuilder, total: MonetaryTotal) {
        xml.append("  <cac:LegalMonetaryTotal>\n")
        xml.append("    <cbc:LineExtensionAmount currencyID=\"SAR\">${VatCalculator.toSarString(total.netTotal)}</cbc:LineExtensionAmount>\n")
        xml.append("    <cbc:TaxExclusiveAmount currencyID=\"SAR\">${VatCalculator.toSarString(total.netTotal)}</cbc:TaxExclusiveAmount>\n")
        xml.append("    <cbc:TaxInclusiveAmount currencyID=\"SAR\">${VatCalculator.toSarString(total.grossTotal)}</cbc:TaxInclusiveAmount>\n")
        xml.append("    <cbc:PayableAmount currencyID=\"SAR\">${VatCalculator.toSarString(total.grossTotal)}</cbc:PayableAmount>\n")
        xml.append("  </cac:LegalMonetaryTotal>\n")
    }

    private fun catCode(vatRate: Long): String = when (vatRate) { 0L -> "Z"; else -> "S" }

    private fun esc(input: String): String = input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
}