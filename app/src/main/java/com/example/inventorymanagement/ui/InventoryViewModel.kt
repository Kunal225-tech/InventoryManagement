package com.example.inventorymanagement.ui

import androidx.lifecycle.*
import com.example.inventorymanagement.data.Product
import com.example.inventorymanagement.data.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOrder { NAME, PRICE, QUANTITY }

class InventoryViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NAME)
    private val _categoryFilter = MutableStateFlow<String?>(null)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val allProducts: LiveData<List<Product>> = combine(
        _searchQuery,
        _sortOrder,
        _categoryFilter
    ) { query, sort, category ->
        Triple(query, sort, category)
    }.flatMapLatest { (query, sort, category) ->
        repository.allProducts.map { products ->
            products.filter { 
                (query.isEmpty() || it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)) &&
                (category == null || it.category == category)
            }.sortedWith(when (sort) {
                SortOrder.NAME -> compareBy { it.name.lowercase() }
                SortOrder.PRICE -> compareByDescending { it.price }
                SortOrder.QUANTITY -> compareByDescending { it.quantity }
            })
        }
    }.asLiveData()

    val totalProductCount = repository.totalProductCount.asLiveData()
    val totalInventoryValue = repository.totalInventoryValue.asLiveData()
    val lowStockCount = repository.lowStockCount.asLiveData()
    val outOfStockCount = repository.outOfStockCount.asLiveData()
    val categories = repository.categories.asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setCategoryFilter(category: String?) {
        _categoryFilter.value = category
    }

    fun insert(product: Product) = viewModelScope.launch {
        repository.insertProduct(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        repository.updateProduct(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        repository.deleteProduct(product)
    }

    fun getProductById(id: Int): LiveData<Product?> {
        return repository.getProductById(id).asLiveData()
    }
}

class InventoryViewModelFactory(private val repository: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
