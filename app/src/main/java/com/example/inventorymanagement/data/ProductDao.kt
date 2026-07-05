package com.example.inventorymanagement.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE :searchQuery OR category LIKE :searchQuery ORDER BY name ASC")
    fun searchProducts(searchQuery: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): Flow<Product?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalProductCount(): Flow<Int>

    @Query("SELECT SUM(quantity * price) FROM products")
    fun getTotalInventoryValue(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM products WHERE quantity <= minQuantity AND quantity > 0")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE quantity = 0")
    fun getOutOfStockCount(): Flow<Int>

    @Query("SELECT DISTINCT category FROM products ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
}
