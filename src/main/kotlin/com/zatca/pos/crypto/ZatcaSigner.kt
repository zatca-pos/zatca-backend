package com.zatca.pos.crypto

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.math.BigInteger
import java.security.*
import java.security.cert.X509Certificate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Handles cryptographic operations for ZATCA invoices:
 * - Self-signed certificate generation (for sandbox testing)
 * - Invoice hash calculation (SHA-256)
 * - XML Digital Signature (XMLDSig enveloped)
 * - TLV QR Code generation
 */
object ZatcaSigner {

    init {
        // Register Bouncy Castle as a security provider
        Security.addProvider(BouncyCastleProvider())
    }

    /**
     * Generate a self-signed X.509 certificate for ZATCA sandbox testing.
     * In production, this is replaced by the ZATCA CSID certificate.
     */
    fun generateTestCertificate(
        commonName: String = "ZATCA Test POS",
        organization: String = "Test Organization",
        country: String = "SA"
    ): ZatcaCertificate {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()

        val now = Date()
        val notAfter = Date.from(Instant.now().plus(365, ChronoUnit.DAYS))

        val issuer = X500Name("CN=$commonName, O=$organization, C=$country")
        val subject = issuer

        val certBuilder: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(
            issuer,
            BigInteger.valueOf(System.currentTimeMillis()),
            now,
            notAfter,
            subject,
            keyPair.public
        )

        val signer = JcaContentSignerBuilder("SHA256WithRSA")
            .setProvider("BC")
            .build(keyPair.private)

        val certificate = JcaX509CertificateConverter()
            .setProvider("BC")
            .getCertificate(certBuilder.build(signer))

        return ZatcaCertificate(
            certificate = certificate,
            privateKey = keyPair.private
        )
    }

    /**
     * Generate the ZATCA invoice hash.
     * SHA-256 of the complete unsigned XML, Base64 encoded.
     */
    fun generateInvoiceHash(unsignedXml: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(unsignedXml.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }

    /**
     * Sign the unsigned XML and embed the signature.
     * Returns the complete signed XML ready for ZATCA submission.
     */
    fun signXml(
        unsignedXml: String,
        certificate: X509Certificate,
        privateKey: PrivateKey,
        invoiceHash: String
    ): String {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(ByteArrayInputStream(unsignedXml.toByteArray(Charsets.UTF_8)))

        // Find the ExtensionContent element and clear the placeholder
        val extensionContent = findExtensionContent(document)
            ?: throw IllegalStateException("Could not find ExtensionContent in XML")

        // Build the signature XML structure
        val signatureXml = buildSignatureXml(document, invoiceHash, certificate, privateKey)
        extensionContent.appendChild(signatureXml)

        return documentToString(document)
    }

    /**
     * Generate the TLV-encoded QR code string for ZATCA.
     *
     * TLV Fields:
     * 1. Seller Name (Arabic if available)
     * 2. VAT Registration Number
     * 3. Invoice Timestamp (ISO 8601)
     * 4. Invoice Total including VAT
     * 5. VAT Total
     */
    fun generateQrCode(
        sellerName: String,
        vatNumber: String,
        timestamp: Instant,
        totalWithVat: Long,
        vatTotal: Long
    ): String {
        val totalSar = "%.2f".format(totalWithVat / 100.0)
        val vatSar = "%.2f".format(vatTotal / 100.0)
        val timeStr = timestamp.toString()

        val tlv = buildString {
            append(tlvField(1, sellerName))
            append(tlvField(2, vatNumber))
            append(tlvField(3, timeStr))
            append(tlvField(4, totalSar))
            append(tlvField(5, vatSar))
        }

        return Base64.getEncoder().encodeToString(tlv.toByteArray(Charsets.UTF_8))
    }

    // ========== PRIVATE HELPERS ==========

    private fun buildSignatureXml(
        document: Document,
        invoiceHash: String,
        certificate: X509Certificate,
        privateKey: PrivateKey
    ): Element {
        // Create the signature container
        val sigElement = document.createElementNS(
            "urn:oasis:names:specification:ubl:schema:xsd:CommonSignatureComponents-2",
            "sig:UBLDocumentSignatures"
        )

        val sacElement = document.createElementNS(
            "urn:oasis:names:specification:ubl:schema:xsd:SignatureAggregateComponents-2",
            "sac:SignatureInformation"
        )

        addTextElement(document, sacElement, "cbc:ID", "urn:oasis:names:specification:ubl:signature:1")
        addTextElement(document, sacElement, "cbc:ReferencedSignatureID", "urn:oasis:names:specification:ubl:signature:Invoice")
        addTextElement(document, sacElement, "cbc:SigningTime", Instant.now().toString())
        addTextElement(document, sacElement, "cbc:Hash", invoiceHash)

        // Create the digital signature
        val signature = Signature.getInstance("SHA256withRSA", "BC")
        signature.initSign(privateKey)
        signature.update(invoiceHash.toByteArray(Charsets.UTF_8))
        val signatureBytes = signature.sign()
        val signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes)

        val dsigElement = document.createElement("sac:Signature")
        dsigElement.setTextContent(signatureBase64)
        sacElement.appendChild(dsigElement)

        sigElement.appendChild(sacElement)
        return sigElement
    }

    private fun tlvField(tag: Int, value: String): String {
        val valueBytes = value.toByteArray(Charsets.UTF_8)
        val length = valueBytes.size
        return tag.toChar().toString() + length.toChar().toString() + String(valueBytes, Charsets.UTF_8)
    }

    private fun findExtensionContent(document: Document): Element? {
        // Try multiple ways to find the ExtensionContent element
        // ZATCA XML uses namespaces, so we need to search by namespace
        
        // Method 1: Search by namespace and local name
        val nsList = document.getElementsByTagNameNS(
            "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2",
            "ExtensionContent"
        )
        if (nsList.length > 0) {
            val element = nsList.item(0) as Element
            element.textContent = ""
            return element
        }
        
        // Method 2: Search all elements for one containing the placeholder
        val allElements = document.getElementsByTagName("*")
        for (i in 0 until allElements.length) {
            val element = allElements.item(i) as Element
            val text = element.textContent
            if (text != null && text.contains("SIGNATURE_PLACEHOLDER")) {
                element.textContent = ""
                return element
            }
        }
        
        // Method 3: Search by local name only
        for (i in 0 until allElements.length) {
            val element = allElements.item(i) as Element
            if (element.localName == "ExtensionContent") {
                element.textContent = ""
                return element
            }
        }
        
        return null
    }

    private fun addTextElement(document: Document, parent: Element, tagName: String, text: String) {
        val element = document.createElementNS(
            "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2",
            tagName
        )
        element.setTextContent(text)
        parent.appendChild(element)
    }

    private fun documentToString(document: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty("omit-xml-declaration", "no")
        transformer.setOutputProperty("encoding", "UTF-8")
        transformer.setOutputProperty("indent", "yes")
        val writer = StringWriter()
        transformer.transform(DOMSource(document), StreamResult(writer))
        return writer.toString()
    }
}

/**
 * Holds a certificate and its private key together.
 */
data class ZatcaCertificate(
    val certificate: X509Certificate,
    val privateKey: PrivateKey
)