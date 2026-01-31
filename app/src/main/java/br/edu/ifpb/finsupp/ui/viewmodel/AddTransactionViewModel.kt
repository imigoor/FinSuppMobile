package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.CategoryApi
import br.edu.ifpb.finsupp.network.service.TransactionApi
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.AddTransactionUiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionViewModel(
    private val transactionApi: TransactionApi,
    private val accountApi: AccountApi,
    private val categoryApi: CategoryApi
) : ViewModel() {

    var uiState by mutableStateOf(AddTransactionUiState())
        private set

    // funcoes pra UI atualizar os campos
    fun updateTab(index: Int) { uiState = uiState.copy(selectedTab = index) }
    fun updateDescription(v: String) { uiState = uiState.copy(description = v) }
    fun updateAmount(v: String) { uiState = uiState.copy(amount = v) }
    fun updateDate(v: String) { uiState = uiState.copy(date = v) }

    fun updateSelectedAccount(acc: AccountApiData) { uiState = uiState.copy(selectedAccount = acc) }
    fun updateSelectedToAccount(acc: AccountApiData) { uiState = uiState.copy(selectedToAccount = acc) }
    fun updateSelectedCategory(cat: Category) { uiState = uiState.copy(selectedCategory = cat) }

    fun loadDependencies() {
        viewModelScope.launch {
            try {
                // Carrega Contas
                val accRes = accountApi.getAccounts()
                val accounts = if (accRes.isSuccessful) accRes.body()?.dataList ?: emptyList() else emptyList()

                // Carrega Categorias
                val catRes = categoryApi.getCategories()
                val categories = if (catRes.isSuccessful) catRes.body()?.dataList ?: emptyList() else emptyList()

                // Atualiza o estado de uma vez
                uiState = uiState.copy(
                    accountsList = accounts,
                    categoriesList = categories
                )

            } catch (e: Exception) {
                uiState = uiState.copy(toastMessage = "Erro ao carregar dados iniciais")
            }
        }
    }

    fun createTransaction() {
        if (uiState.selectedAccount == null || uiState.amount.isBlank() || uiState.description.isBlank()) {
            uiState = uiState.copy(toastMessage = "Preencha os campos obrigatórios")
            return
        }

        // Define o tipo baseado na aba
        val typeStr = when(uiState.selectedTab) {
            0 -> "WITHDRAW"
            1 -> "DEPOSIT"
            2 -> "TRANSFER"
            else -> "WITHDRAW"
        }

        // Validação específica de Transferência
        if (typeStr == "TRANSFER" && uiState.selectedToAccount == null) {
            uiState = uiState.copy(toastMessage = "Selecione a conta de destino")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val request = CreateTransactionRequest(
                    description = uiState.description,
                    amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                    transactionDate = uiState.date,
                    type = typeStr,
                    accountId = uiState.selectedAccount!!.id,
                    recipientAccountId = if (typeStr == "TRANSFER") uiState.selectedToAccount!!.id else null,
                    category = uiState.selectedCategory?.id
                )

                val response = transactionApi.createTransaction(request)
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        toastMessage = "Transação criada!",
                        saveSuccess = true,
                        isLoading = false
                    )
                } else {
                    uiState = uiState.copy(
                        toastMessage = "Erro: ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    toastMessage = "Erro: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearToastMessage() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun onNavigatedAway() {
        uiState = uiState.copy(saveSuccess = false)
    }
}