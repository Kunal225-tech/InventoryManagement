package com.example.inventorymanagement.data

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val totalProductCount: Flow<Int> = productDao.getTotalProductCount()
    val totalInventoryValue: Flow<Double?> = productDao.getTotalInventoryValue()
    val lowStockCount: Flow<Int> = productDao.getLowStockCount()
    val outOfStockCount: Flow<Int> = productDao.getOutOfStockCount()
    val categories: Flow<List<String>> = productDao.getAllCategories()

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts("%$query%")

    fun getProductById(id: Int): Flow<Product?> = productDao.getProductById(id)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
}
