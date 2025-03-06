package org.example.project

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyViewModel(private val apiClient: ApiClient) : ViewModel() {
    val loading = mutableStateOf(LoadingState.IDLE)


    private val _models = MutableStateFlow(emptyList<Coin>())
    val models: StateFlow<List<Coin>> = _models

    private val _balances = MutableStateFlow(emptyList<BalanceItem>())
    val balances: StateFlow<List<BalanceItem>> = _balances

    private val _rates = MutableStateFlow(emptyList<Tier>())
    val rates: StateFlow<List<Tier>> = _rates

    val allUsdCash: StateFlow<String> = combine(balances, rates) { balances, rates ->
        calculateAllUsdCash(balances, rates)
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = "0.0"
    )

    private fun calculateAllUsdCash(balances: List<BalanceItem>, rates: List<Tier>): String {
        val usdCash = balances.sumOf { balance ->
            println("asdasd" + balance)
            balance.amount
//            rates.firstOrNull { it.from_currency == balance.currency }?.let {
//                balance.amount * (it.rates.firstOrNull()?.rate?.toDouble() ?: 0.0)
//            } ?: 0.0
        }
        return String.format("%.2f", usdCash)
    }


    fun fetchModelIntent() {
        loading.value = LoadingState.LOADING
        viewModelScope.launch {
            apiClient
                .fetchCoins()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.e("MainViewModel", "Failed to get list of models.")
                }
                .collect { response ->
                    when (response) {
                        is ApiError -> {
                            loading.value = LoadingState.ERROR
                            //response.message
                        }

                        is Response -> {
                            loading.value = LoadingState.IDLE
                            _models.value = response.currencies
                        }

                        else -> {
                        }
                    }
                }
        }
    }

    fun fetchWalletIntent() {
        loading.value = LoadingState.LOADING
        viewModelScope.launch {
            apiClient
                .fetchBalance()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    Log.e("MainViewModel", "Failed to get list of models." + e)
                }
                .collect { response ->
                    when (response) {
                        is ApiError -> {
                            loading.value = LoadingState.ERROR
                            //response.message
                            print("error!!!" + response)
                        }

                        is WalletResponse -> {
                            loading.value = LoadingState.IDLE

                            _balances.value = response.wallet
                            println(_balances.toString() + "gggggas")
                        }

                        else -> {}
                    }
                }
        }
    }

    fun fetchRatesIntent() {
        loading.value = LoadingState.LOADING
        viewModelScope.launch {
            apiClient
                .fetchRates()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.e("MainViewModel", "Failed to get list of models.")
                }
                .collect { response ->
                    when (response) {
                        is ApiError -> {
                            loading.value = LoadingState.ERROR
                            //response.message
                            print("error!!!" + response)
                        }

                        is LiveRates -> {
                            loading.value = LoadingState.IDLE
                            _rates.value = response.tiers

                            //every time change wash again
                        }

                        else -> {}
                    }
                }
        }
    }

    //union or split dont care
    fun washData() {

    }
}