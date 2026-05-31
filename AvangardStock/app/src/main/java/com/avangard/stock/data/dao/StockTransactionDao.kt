package com.avangard.stock.data.dao

import androidx.room.*
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {

    @Query("SELECT * FROM stock_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE productId = :productId ORDER BY date DESC")
    fun getTransactionsByProduct(productId: Long): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<StockTransaction>>

    @Query("SELECT SUM(quantity) FROM stock_transactions WHERE type = :type")
    fun getTotalQuantityByType(type: TransactionType): Flow<Int?>

    @Query("SELECT SUM(totalAmount) FROM stock_transactions WHERE type = 'SOLD'")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM stock_transactions WHERE type = 'INCOMING'")
    fun getTotalCost(): Flow<Double?>

    @Query("""
        SELECT SUM(quantity) FROM stock_transactions 
        WHERE productId = :productId AND type = :type
    """)
    suspend fun getProductQuantityByType(productId: Long, type: TransactionType): Int?

    @Query("""
        SELECT SUM(totalAmount) FROM stock_transactions 
        WHERE type = 'SOLD' AND date BETWEEN :startDate AND :endDate
    """)
    fun getRevenueByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT * FROM stock_transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 20): Flow<List<StockTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: StockTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: StockTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: StockTransaction)
}
