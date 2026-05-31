package com.avangard.stock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avangard.stock.AvangardApp
import com.avangard.stock.data.model.DashboardStats
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import com.avangard.stock.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as AvangardApp).database
    private val repository = ProductRepository(
        database.productDao(),
        database.stockTransactionDao()
    )

    val products: StateFlow<List<Product>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentTransactions: StateFlow<List<StockTransaction>> = repository.getRecentTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockProducts: StateFlow<List<Product>> = repository.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        loadDashboardStats()
    }

    private fun loadDashboardStats() {
        viewModelScope.launch {
            combine(
                repository.getActiveProductCount(),
                repository.getTotalStockQuantity(),
                repository.getTotalQuantityByType(TransactionType.INCOMING),
                repository.getTotalQuantityByType(TransactionType.SOLD),
                repository.getTotalQuantityByType(TransactionType.RETURNED),
                repository.getTotalRevenue(),
                repository.getTotalCost()
            ) { values ->
                DashboardStats(
                    totalProducts = values[0] as? Int ?: 0,
                    totalStockQuantity = (values[1] as? Int) ?: 0,
                    totalIncoming = (values[2] as? Int) ?: 0,
                    totalSold = (values[3] as? Int) ?: 0,
                    totalReturned = (values[4] as? Int) ?: 0,
                    totalRevenue = (values[5] as? Double) ?: 0.0,
                    totalCost = (values[6] as? Double) ?: 0.0,
                    totalProfit = ((values[5] as? Double) ?: 0.0) - ((values[6] as? Double) ?: 0.0)
                )
            }.collect { stats ->
                _dashboardStats.value = stats
            }
        }
    }
}
