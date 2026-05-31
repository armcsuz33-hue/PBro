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

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as AvangardApp).database
    private val repository = ProductRepository(
        database.productDao(),
        database.stockTransactionDao()
    )

    val allProducts: StateFlow<List<Product>> = repository.getAllActiveProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _productTransactions = MutableStateFlow<List<StockTransaction>>(emptyList())
    val productTransactions: StateFlow<List<StockTransaction>> = _productTransactions.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllActiveProducts()
            } else {
                repository.searchProducts(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectProduct(productId: Long) {
        viewModelScope.launch {
            _selectedProduct.value = repository.getProductById(productId)
            repository.getTransactionsByProduct(productId).collect { transactions ->
                _productTransactions.value = transactions
            }
        }
    }

    // ===== ADMIN FUNKSIYALARI =====

    fun addProduct(
        name: String,
        description: String,
        purchasePrice: Double,
        sellingPrice: Double,
        stockQuantity: Int,
        category: String,
        imageResName: String = ""
    ) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                description = description,
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                stockQuantity = stockQuantity,
                category = category,
                imageResName = imageResName
            )
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deactivateProduct(productId: Long) {
        viewModelScope.launch {
            repository.deactivateProduct(productId)
        }
    }
}
