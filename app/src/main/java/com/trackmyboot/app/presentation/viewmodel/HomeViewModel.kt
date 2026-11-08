package com.trackmyboot.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmyboot.app.data.repository.ProductRepository
import com.trackmyboot.app.data.repository.RemoteRepository
import com.trackmyboot.app.data.repository.SettingsRepository
import com.trackmyboot.app.domain.model.Product
import com.trackmyboot.app.domain.model.ProductCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val remoteRepository: RemoteRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: Flow<ProductCategory?> = _selectedCategory.distinctUntilChanged()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.distinctUntilChanged()

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.distinctUntilChanged()

    // Products flows based on selected category
    val allProducts: Flow<List<Product>> = productRepository.getAllProducts()
    val productsOnSale: Flow<List<Product>> = productRepository.getProductsOnSale()
    val availableProducts: Flow<List<Product>> = productRepository.getAvailableProducts()

    val filteredProducts: Flow<List<Product>> = combine(
        allProducts,
        selectedCategory,
        settingsRepository.enabledCategories.map { it.split(",").filter { it.isNotBlank() } },
        settingsRepository.enabledStores.map { it.split(",").filter { it.isNotBlank() } }
    ) { products, category, enabledCategories, enabledStores ->
        products.filter { product ->
            val categoryMatch = category == null || product.category == category
            val categoryEnabled = enabledCategories.isEmpty() || enabledCategories.contains(product.category.name)
            val storeEnabled = enabledStores.isEmpty() || enabledStores.contains(product.store.name)
            categoryMatch && categoryEnabled && storeEnabled
        }.sortedByDescending { it.discountPercentage }
            .thenBy { it.currentPrice }
    }

    val categories = ProductCategory.values().toList()

    fun selectCategory(category: ProductCategory?) {
        _selectedCategory.value = category
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = remoteRepository.syncAllProducts()
            when (result) {
                is Result.Success -> {
                    _isLoading.value = false
                }
                is Result.Failure -> {
                    _isLoading.value = false
                    _error.value = result.exception.message ?: "Erro ao sincronizar dados"
                }
            }
        }
    }

    fun syncCategory(category: ProductCategory) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = remoteRepository.syncCategory(category)
            when (result) {
                is Result.Success -> {
                    _isLoading.value = false
                }
                is Result.Failure -> {
                    _isLoading.value = false
                    _error.value = result.exception.message ?: "Erro ao sincronizar categoria"
                }
            }
        }
    }
}