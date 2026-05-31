package com.avangard.stock.data.model

data class DashboardStats(
    val totalProducts: Int = 0,
    val totalStockQuantity: Int = 0,
    val totalIncoming: Int = 0,
    val totalSold: Int = 0,
    val totalReturned: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalProfit: Double = 0.0,
    val lowStockProducts: Int = 0
)

data class ProductWithStats(
    val product: Product,
    val totalIncoming: Int = 0,
    val totalSold: Int = 0,
    val totalReturned: Int = 0,
    val revenue: Double = 0.0
)

data class MonthlyReport(
    val month: String,
    val incoming: Int = 0,
    val sold: Int = 0,
    val returned: Int = 0,
    val revenue: Double = 0.0,
    val profit: Double = 0.0
)
