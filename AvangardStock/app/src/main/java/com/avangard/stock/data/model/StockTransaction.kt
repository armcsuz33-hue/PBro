package com.avangard.stock.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOMING,   // Skladga kirdi
    SOLD,       // Sotuvga chiqdi
    RETURNED    // Qaytib keldi
}

@Entity(
    tableName = "stock_transactions",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("date"), Index("type")]
)
data class StockTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val type: TransactionType,
    val quantity: Int,
    val pricePerUnit: Double,
    val totalAmount: Double = quantity * pricePerUnit,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
