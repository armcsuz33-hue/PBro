package com.avangard.stock.data.dao

import androidx.room.*
import com.avangard.stock.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE stockQuantity <= minStockAlert AND isActive = 1")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    fun getActiveProductCount(): Flow<Int>

    @Query("SELECT SUM(stockQuantity) FROM products WHERE isActive = 1")
    fun getTotalStockQuantity(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = :quantity, updatedAt = :updatedAt WHERE id = :productId")
    suspend fun updateStockQuantity(productId: Long, quantity: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE products SET isActive = 0 WHERE id = :productId")
    suspend fun deactivateProduct(productId: Long)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isActive = 1")
    fun searchProducts(query: String): Flow<List<Product>>
}
