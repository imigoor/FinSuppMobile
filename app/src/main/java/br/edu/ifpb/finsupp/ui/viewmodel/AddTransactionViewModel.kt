package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.RetrofitClient
import br.edu.ifpb.finsupp.network.model.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionViewModel : ViewModel() {

    // Abas: 0 = Withdraw, 1 = Deposit, 2 = Transfer
    var selectedTab by mutableStateOf(0)

    // Campos do formulário
    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var date by mutableStateOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    )

    // Seleções
    var selectedAccount by mutableStateOf<AccountApiData?>(null) // Conta Origem
    var selectedToAccount by mutableStateOf<AccountApiData?>(null) // Conta Destino (Transfer)
    var selectedCategory by mutableStateOf<Category?>(null)

    // Listas para Dropdowns
    var accountsList by mutableStateOf<List<AccountApiData>>(emptyList())
    var categoriesList by mutableStateOf<List<Category>>(emptyList())

    var isLoading by mutableStateOf(false)
    var saveSuccess by mutableStateOf(false)
    var toastMessage by mutableStateOf<String?>(null)

    fun loadDependencies() {
        viewModelScope.launch {
            try {
                // Carrega Contas
                val accRes = RetrofitClient.accountApi.getAccounts()
                if (accRes.isSuccessful) accountsList = accRes.body()?.dataList ?: emptyList()

                // Carrega Categorias
                val catRes = RetrofitClient.categoryApi.getCategories()
                if (catRes.isSuccessful) categoriesList = catRes.body()?.dataList ?: emptyList()

            } catch (e: Exception) {
                toastMessage = "Erro ao carregar dados iniciais"
            }
        }
    }

    fun createTransaction() {
        if (selectedAccount == null || amount.isBlank() || description.isBlank()) {
            toastMessage = "Preencha os campos obrigatórios"
            return
        }

        // Define o tipo baseado na aba
        val typeStr = when(selectedTab) {
            0 -> "WITHDRAW"
            1 -> "DEPOSIT"
            2 -> "TRANSFER"
            else -> "WITHDRAW"
        }

        // Validação específica de Transferência
        if (typeStr == "TRANSFER" && selectedToAccount == null) {
            toastMessage = "Selecione a conta de destino"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val request = CreateTransactionRequest(
                    description = description,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    transactionDate = date,
                    type = typeStr,
                    accountId = selectedAccount!!.id,
                    recipientAccountId = if (typeStr == "TRANSFER") selectedToAccount!!.id else null,
                    category = selectedCategory?.id
                )

                val response = RetrofitClient.transactionApi.createTransaction(request)
                if (response.isSuccessful) {
                    toastMessage = "Transação criada!"
                    saveSuccess = true
                } else {
                    toastMessage = "Erro: ${response.code()}"
                }
            } catch (e: Exception) {
                toastMessage = "Erro: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearToastMessage() { toastMessage = null }
    fun onNavigatedAway() { saveSuccess = false }
}