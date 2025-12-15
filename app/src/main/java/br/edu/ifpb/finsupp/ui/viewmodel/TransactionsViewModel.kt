package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.RetrofitClient
import br.edu.ifpb.finsupp.network.model.TransactionApiData
import kotlinx.coroutines.launch

class TransactionsViewModel : ViewModel() {
    var uiTransactions by mutableStateOf<List<TransactionApiData>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.transactionApi.getTransactions()
                if (response.isSuccessful) {
                    uiTransactions = response.body()?.dataList ?: emptyList()
                } else {
                    toastMessage = "Erro ao carregar transações"
                }
            } catch (e: Exception) {
                toastMessage = "Erro de conexão"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.transactionApi.deleteTransaction(id)
                if (response.isSuccessful) {
                    toastMessage = "Transação removida"
                    loadData() // Recarrega a lista
                } else {
                    toastMessage = "Erro ao deletar"
                }
            } catch (e: Exception) {
                toastMessage = "Erro de conexão"
            }
        }
    }

    fun clearToastMessage() { toastMessage = null }
}