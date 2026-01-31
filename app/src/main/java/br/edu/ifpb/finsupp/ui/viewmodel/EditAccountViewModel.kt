package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.BankApi
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.EditAccountUiState
import kotlinx.coroutines.launch
import org.json.JSONObject

class EditAccountViewModel(
    private val accountApi: AccountApi,
    private val bankApi: BankApi
) : ViewModel() {

    var uiState by mutableStateOf(EditAccountUiState())
        private set

    // ID da conta sendo editada e ID do banco inicial (UI não exibe)
    private var currentAccountId: Int = 0
    private var initialBankId: Int = 0

    // funções para a UI atualizar os inputs
    fun updateDescription(v: String) { uiState = uiState.copy(description = v) }
    fun updateBalance(v: String) { uiState = uiState.copy(balance = v) }
    fun updateClosingDay(v: String) { uiState = uiState.copy(closingDay = v) }
    fun updateDueDay(v: String) { uiState = uiState.copy(dueDay = v) }
    fun updateType(v: String) { uiState = uiState.copy(selectedType = v) }
    fun updateBank(bank: Bank) { uiState = uiState.copy(selectedBank = bank) }

    fun initialize(
        id: Int,
        initDesc: String,
        initBankId: Int,
        initType: String,
        initBalance: Double,
        initClosing: Int,
        initDue: Int
    ) {
        currentAccountId = id
        initialBankId = initBankId

        // Atualiza o estado inicial
        uiState = uiState.copy(
            description = initDesc,
            selectedType = initType,
            balance = initBalance.toString(),
            closingDay = initClosing.toString(),
            dueDay = initDue.toString()
        )

        loadBanks()
    }

    private fun loadBanks() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingBanks = true)
            try {
                val response = bankApi.getBanks()
                if (response.isSuccessful) {
                    val banks = response.body()?.dataList ?: emptyList()

                    uiState = uiState.copy(
                        bankList = banks,
                        // tenta encontrar o banco original na lista carregada
                        selectedBank = banks.find { it.id == initialBankId },
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

    // ação de atualizar
    fun updateAccount() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // monta o objeto de requisição
                val request = CreateAccountRequest(
                    description = uiState.description,
                    accountType = uiState.selectedType,
                    bank = uiState.selectedBank?.id ?: initialBankId,
                    balance = uiState.balance.toDoubleOrNull() ?: 0.0,
                    closingDay = uiState.closingDay.toIntOrNull() ?: 1,
                    paymentDueDay = uiState.dueDay.toIntOrNull() ?: 10
                )

                val response = accountApi.updateAccount(currentAccountId, request)

                if (response.isSuccessful) {
                    uiState = uiState.copy(
                        toastMessage = "Conta Atualizada!",
                        updateSuccess = true,
                        isLoading = false
                    )
                } else {
                    // tratamento de erro JSON (422) - LÓGICA MANTIDA INTACTA
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
                            if(jsonObject.has("message")) jsonObject.getString("message") else "Erro na atualização"
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

    fun clearToastMessage() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun onNavigatedAway() {
        uiState = uiState.copy(updateSuccess = false)
    }
}