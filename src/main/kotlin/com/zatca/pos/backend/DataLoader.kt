package com.zatca.pos.backend

import com.zatca.pos.backend.entity.*
import com.zatca.pos.backend.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DataLoader(
    private val productRepository: ProductRepository,
    private val companyRepository: CompanyRepository,
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val branchRepository: BranchRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (branchRepository.count() == 0L) {
            branchRepository.save(Branch(code = "BR001", nameEn = "Main Branch", nameAr = "الفرع الرئيسي", addressEn = "Olaya Street", addressAr = "شارع العليا", phone = "+966 11 234 5678"))
            branchRepository.save(Branch(code = "BR002", nameEn = "Mall Branch", nameAr = "فرع المول", addressEn = "Riyadh Mall", addressAr = "مول الرياض", phone = "+966 11 876 5432"))
        }

        if (companyRepository.count() == 0L) {
            companyRepository.save(Company(
                nameEn = "My Coffee Shop", nameAr = "مقهى القهوة العربية",
                vatNumber = "311111111111113",
                addressEn = "Olaya Street, Building 1234", addressAr = "شارع العليا، مبنى ١٢٣٤",
                buildingNumber = "1234", postalCode = "12221",
                cityEn = "Riyadh", cityAr = "الرياض", country = "SA",
                phone = "+966 11 234 5678", email = "info@mycoffeeshop.sa"
            ))
        }

        if (categoryRepository.count() == 0L) {
            val drinks = categoryRepository.save(Category(nameEn = "Beverages", nameAr = "مشروبات"))
            val food = categoryRepository.save(Category(nameEn = "Food", nameAr = "طعام"))
            val hotDrinks = subCategoryRepository.save(SubCategory(category = drinks, nameEn = "Hot Drinks", nameAr = "مشروبات ساخنة"))
            val coldDrinks = subCategoryRepository.save(SubCategory(category = drinks, nameEn = "Cold Drinks", nameAr = "مشروبات باردة"))
            val sweets = subCategoryRepository.save(SubCategory(category = food, nameEn = "Sweets", nameAr = "حلويات"))

            if (productRepository.count() == 0L) {
                productRepository.saveAll(listOf(
                    Product(sku = "COF-001", nameEn = "Arabic Coffee", nameAr = "قهوة عربية", category = drinks, subCategory = hotDrinks, priceInHalalas = 11500),
                    Product(sku = "TEA-001", nameEn = "Red Tea", nameAr = "شاي أحمر", category = drinks, subCategory = hotDrinks, priceInHalalas = 575),
                    Product(sku = "WAT-001", nameEn = "Water Bottle", nameAr = "زجاجة ماء", category = drinks, subCategory = coldDrinks, priceInHalalas = 115),
                    Product(sku = "DAT-001", nameEn = "Dates 1kg", nameAr = "تمر 1 كجم", category = food, subCategory = sweets, priceInHalalas = 3450),
                    Product(sku = "BKR-001", nameEn = "Baklava Box", nameAr = "علبة بقلاوة", category = food, subCategory = sweets, priceInHalalas = 5750),
                ))
            }
        }
    }
}