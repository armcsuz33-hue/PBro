package com.avangard.stock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avangard.stock.AvangardApp
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import com.avangard.stock.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TransactionUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as AvangardApp).database
    private val repository = ProductRepository(
        database.productDao(),
        database.stockTransactionDao()
    )

    val allTransactions: StateFlow<List<StockTransaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _filterType = MutableStateFlow<TransactionType?>(null)
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    val filteredTransactions: StateFlow<List<StockTransaction>> = _filterType
        .flatMapLatest { type ->
            if (type == null) {
                repository.getAllTransactions()
            } else {
                repository.getTransactionsByType(type)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(type: TransactionType?) {
        _filterType.value = type
    }

    /**
     * Skladga mahsulot qo'shish (KIRDI)
     */
    fun addIncomingTransaction(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        viewModelScope.launch {
            try {
                _uiState.value = TransactionUiState(isLoading = true)
                repository.addIncomingStock(productId, quantity, pricePerUnit, note)
                _uiState.value = TransactionUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = TransactionUiState(errorMessage = e.message ?: "Xatolik yuz berdi")
            }
        }
    }

    /**
     * Mahsulot sotildi (CHIQDI)
     */
    fun addSoldTransaction(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        viewModelScope.launch {
            try {
                _uiState.value = TransactionUiState(isLoading = true)
                repository.addSoldStock(productId, quantity, pricePerUnit, note)
                _uiState.value = TransactionUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = TransactionUiState(errorMessage = e.message ?: "Xatolik yuz berdi")
            }
        }
    }

    /**
     * Mahsulot qaytib keldi (QAYTDI)
     */
    fun addReturnedTransaction(productId: Long, quantity: Int, pricePerUnit: Double, note: String = "") {
        viewModelScope.launch {
            try {
                _uiState.value = TransactionUiState(isLoading = true)
                repository.addReturnedStock(productId, quantity, pricePerUnit, note)
                _uiState.value = TransactionUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = TransactionUiState(errorMessage = e.message ?: "Xatolik yuz berdi")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = TransactionUiState()
    }
}
