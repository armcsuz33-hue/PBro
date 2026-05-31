package com.avangard.stock.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.avangard.stock.data.dao.ProductDao
import com.avangard.stock.data.dao.StockTransactionDao
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Product::class, StockTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun stockTransactionDao(): StockTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "avangard_stock.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.productDao())
                }
            }
        }

        suspend fun populateDatabase(productDao: ProductDao) {
            // Avangard mahsulotlarini oldindan qo'shish
            val defaultProducts = listOf(
                Product(
                    name = "Avangard BCD 442 I",
                    description = "Ikki eshikli muzlatgich, 442 litr",
                    imageResName = "product_bcd_442i",
                    purchasePrice = 3_500_000.0,
                    sellingPrice = 4_200_000.0,
                    stockQuantity = 15,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard 800",
                    description = "Katta hajmli muzlatgich, 800 litr",
                    imageResName = "product_800",
                    purchasePrice = 5_800_000.0,
                    sellingPrice = 6_900_000.0,
                    stockQuantity = 8,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard BCD 410 S",
                    description = "O'rta hajmli muzlatgich, 410 litr",
                    imageResName = "product_bcd_410s",
                    purchasePrice = 3_200_000.0,
                    sellingPrice = 3_800_000.0,
                    stockQuantity = 20,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard LSC 395",
                    description = "Side-by-side muzlatgich, 395 litr",
                    imageResName = "product_lsc_395",
                    purchasePrice = 4_100_000.0,
                    sellingPrice = 4_900_000.0,
                    stockQuantity = 12,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard BCD 276 S",
                    description = "Kompakt muzlatgich, 276 litr",
                    imageResName = "product_bcd_276s",
                    purchasePrice = 2_400_000.0,
                    sellingPrice = 2_900_000.0,
                    stockQuantity = 25,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard BCD 276 BP",
                    description = "Kompakt muzlatgich premium, 276 litr",
                    imageResName = "product_bcd_276bp",
                    purchasePrice = 2_600_000.0,
                    sellingPrice = 3_100_000.0,
                    stockQuantity = 18,
                    category = "Muzlatgich"
                ),
                Product(
                    name = "Avangard SD/SC 1800",
                    description = "Tijorat muzlatkichi, 1800 litr",
                    imageResName = "product_sdsc_1800",
                    purchasePrice = 8_500_000.0,
                    sellingPrice = 9_800_000.0,
                    stockQuantity = 5,
                    category = "Tijorat muzlatkichi"
                )
            )

            defaultProducts.forEach { product ->
                productDao.insertProduct(product)
            }
        }
    }
}
