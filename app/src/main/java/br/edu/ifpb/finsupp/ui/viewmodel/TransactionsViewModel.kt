package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.TransactionApiData
import br.edu.ifpb.finsupp.network.service.TransactionApi
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.TransactionsUiState
import kotlinx.coroutines.launch

class TransactionsViewModel(private val api: TransactionApi) : ViewModel() {

    var uiState by mutableStateOf(TransactionsUiState())
        private set

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                //val response = RetrofitClient.transactionApi.getTransactions()
                val response = api.getTransactions()
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        uiTransactions = response.body()?.dataList ?: emptyList()
                    )
                } else {
                    uiState = uiState.copy(toastMessage = "Sem transações para mostrar")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(toastMessage = "Erro de conexão")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            try {
                //val response = RetrofitClient.transactionApi.deleteTransaction(id)
                val response = api.deleteTransaction(id)
                if (response.isSuccessful) {
                    uiState = uiState.copy(toastMessage = "Transação removida")
                    loadData() // Recarrega a lista
                } else {
                    uiState = uiState.copy(toastMessage = "Erro ao deletar")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(toastMessage = "Erro de conexão")
            }
        }
    }

    fun clearToastMessage() {
        uiState = uiState.copy(toastMessage = null)
    }
}