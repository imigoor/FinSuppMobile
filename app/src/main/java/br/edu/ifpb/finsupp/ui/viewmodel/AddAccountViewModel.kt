package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.BankApi
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.AddAccountUiState
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddAccountViewModel(private val accountApi: AccountApi, private val bankApi: BankApi) : ViewModel() {

    var uiState by mutableStateOf(AddAccountUiState())
        private set

    // funcoes pra UI atualizar os campos, input
    fun updateDescription(value: String) { uiState = uiState.copy(description = value) }
    fun updateBalance(value: String) { uiState = uiState.copy(balance = value) }
    fun updateClosingDay(value: String) { uiState = uiState.copy(closingDay = value) }
    fun updateDueDay(value: String) { uiState = uiState.copy(dueDay = value) }
    fun updateType(value: String) { uiState = uiState.copy(selectedType = value) }
    fun updateBank(bank: Bank) { uiState = uiState.copy(selectedBank = bank) }

    // carrega os bancos ao iniciar (chamado pela Screen)
    fun loadBanks() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingBanks = true)
            try {
                //val response = RetrofitClient.bankApi.getBanks()
                val response = bankApi.getBanks()
                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        bankList = response.body()?.dataList ?: emptyList(),
                        isLoadingBanks = false
                    )
                } else {
                    uiState = uiState.copy(
                        toastMessage = "Erro ao carregar bancos",
                        isLoadingBanks = false
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    toastMessage = "Sem conexão com a API",
                    isLoadingBanks = false
                )
            }
        }
    }

    fun createAccount() {
        if (uiState.selectedBank == null) {
            uiState = uiState.copy(toastMessage = "Selecione um banco!")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val request = CreateAccountRequest(
                    description = uiState.description,
                    accountType = uiState.selectedType,
                    bank = uiState.selectedBank!!.id,
                    balance = uiState.balance.toDoubleOrNull() ?: 0.0,
                    closingDay = uiState.closingDay.toIntOrNull() ?: 1,
                    paymentDueDay = uiState.dueDay.toIntOrNull() ?: 10
                )

                //val response = RetrofitClient.accountApi.createAccount(request)
                val response = accountApi.createAccount(request)

                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        toastMessage = "Conta Criada com Sucesso!",
                        saveSuccess = true,
                        isLoading = false
                    )
                } else {
                    // lógica de tratamento do erro 422 (JSON)
                    val rawError = response.errorBody()?.string()
                    val finalMessage = try {
                        val jsonObject = JSONObject(rawError ?: "")
                        val sb = StringBuilder()

                        if (jsonObject.has("dataList") && !jsonObject.isNull("dataList")) {
                            val dataArray = jsonObject.getJSONArray("dataList")
                            for (i in 0 until dataArray.length()) {
                                val erroItem = dataArray.getJSONObject(i)
                                if (erroItem.has("description")) {
                                    val desc = erroItem.getString("description")
                                    sb.append("$desc\n")
                                }
                            }
                        }

                        if (sb.isNotEmpty()) {
                            sb.toString().trim()
                        } else {
                            if(jsonObject.has("message")) jsonObject.getString("message") else "Erro de validação"
                        }
                    } catch (e: Exception) {
                        rawError ?: "Erro desconhecido"
                    }
                    uiState = uiState.copy(
                        toastMessage = finalMessage,
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

    // limpezas de eventos
    fun clearToastMessage() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun onNavigatedAway() {
        uiState = uiState.copy(saveSuccess = false)
    }
}