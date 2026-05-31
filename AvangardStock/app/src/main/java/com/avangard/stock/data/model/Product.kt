package com.avangard.stock.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val imageResName: String = "", // drawable resource nomi
    val purchasePrice: Double = 0.0, // sotib olish narxi
    val sellingPrice: Double = 0.0, // sotish narxi
    val stockQuantity: Int = 0, // ombordagi soni
    val minStockAlert: Int = 5, // minimal zaxira ogohlantirishi
    val category: String = "Muzlatgich",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
