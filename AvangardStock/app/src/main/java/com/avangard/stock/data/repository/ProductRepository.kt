package com.avangard.stock.data.repository

import com.avangard.stock.data.dao.ProductDao
import com.avangard.stock.data.dao.StockTransactionDao
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val transactionDao: StockTransactionDao
) {

    // ===== MAHSULOTLAR =====

    fun getAllActiveProducts(): Flow<List<Product>> = productDao.getAllActiveProducts()

    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()

    fun getActiveProductCount(): Flow<Int> = productDao.getActiveProductCount()

    fun getTotalStockQuantity(): Flow<Int?> = productDao.getTotalStockQuantity()

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    suspend fun addProduct(product: Product): Long = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deactivateProduct(productId: Long) = productDao.deactivateProduct(productId)

    // ===== TRANZAKSIYALAR =====

    fun getAllTransactions(): Flow<List<StockTransaction>> = transactionDao.getAllTransactions()

    fun getTransactionsByProduct(productId: Long): Flow<List<StockTransaction>> =
        transactionDao.getTransactionsByProduct(productId)

    fun getTransactionsByType(type: TransactionType): Flow<List<StockTransaction>> =
        transactionDao.getTransactionsByType(type)

    fun getRecentTransactions(limit: Int = 20): Flow<List<StockTransaction>> =
        transactionDao.getRecentTransactions(limit)

    fun getTotalRevenue(): Flow<Double?> = transactionDao.getTotalRevenue()

    fun getTotalCost(): Flow<Double?> = transactionDao.getTotalCost()

    fun getTotalQuantityByType(type: TransactionType): Flow<Int?> =
        transactionDao.getTotalQuantityByType(type)

    /**
     * Skladga mahsulot kirdi — yangi tranzaksiya qo'shish va stock yangilash
     */
    suspend fun addIncomingStock(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        val transaction = StockTransaction(
            productId = productId,
            type = TransactionType.INCOMING,
            quantity = quantity,
            pricePerUnit = pricePerUnit,
            totalAmount = quantity * pricePerUnit,
            note = note
        )
        transactionDao.insertTransaction(transaction)

        // Mahsulot stockini yangilash
        val product = productDao.getProductById(productId)
        product?.let {
            val newQuantity = it.stockQuantity + quantity
            productDao.updateStockQuantity(productId, newQuantity)
        }
    }

    /**
     * Mahsulot sotildi — stock kamaytirish
     */
    suspend fun addSoldStock(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        val transaction = StockTransaction(
            productId = productId,
            type = TransactionType.SOLD,
            quantity = quantity,
            pricePerUnit = pricePerUnit,
            totalAmount = quantity * pricePerUnit,
            note = note
        )
        transactionDao.insertTransaction(transaction)

        // Mahsulot stockini kamaytirish
        val product = productDao.getProductById(productId)
        product?.let {
            val newQuantity = (it.stockQuantity - quantity).coerceAtLeast(0)
            productDao.updateStockQuantity(productId, newQuantity)
        }
    }

    /**
     * Mahsulot qaytib keldi — stock oshirish
     */
    suspend fun addReturnedStock(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        val transaction = StockTransaction(
            productId = productId,
            type = TransactionType.RETURNED,
            quantity = quantity,
            pricePerUnit = pricePerUnit,
            totalAmount = quantity * pricePerUnit,
            note = note
        )
        transactionDao.insertTransaction(transaction)

        // Mahsulot stockini oshirish (qaytgan)
        val product = productDao.getProductById(productId)
        product?.let {
            val newQuantity = it.stockQuantity + quantity
            productDao.updateStockQuantity(productId, newQuantity)
        }
    }
}
