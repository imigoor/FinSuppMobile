package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.BankApi
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddAccountViewModel(private val accountApi: AccountApi, private val bankApi: BankApi) : ViewModel() {

    // estado do formulario
    var description by mutableStateOf("")
    var balance by mutableStateOf("")
    var closingDay by mutableStateOf("")
    var dueDay by mutableStateOf("")

    // dropdowns
    var selectedType by mutableStateOf("CHECKING")
    var selectedBank by mutableStateOf<Bank?>(null)

    // dados para popular a lista
    var bankList by mutableStateOf<List<Bank>>(emptyList())
        private set

    // estados de controle
    var isLoading by mutableStateOf(false)
        private set

    var isLoadingBanks by mutableStateOf(true)
        private set

    // eventos (Mensagens e Navegação)
    var toastMessage by mutableStateOf<String?>(null)
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    // carrega os bancos ao iniciar (chamado pela Screen)
    fun loadBanks() {
        viewModelScope.launch {
            isLoadingBanks = true
            try {
                //val response = RetrofitClient.bankApi.getBanks()
                val response = bankApi.getBanks()
                if (response.isSuccessful) {
                    bankList = response.body()?.dataList ?: emptyList()
                } else {
                    toastMessage = "Erro ao carregar bancos"
                }
            } catch (e: Exception) {
                toastMessage = "Sem conexão com a API"
            } finally {
                isLoadingBanks = false
            }
        }
    }

    fun createAccount() {
        if (selectedBank == null) {
            toastMessage = "Selecione um banco!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val request = CreateAccountRequest(
                    description = description,
                    accountType = selectedType,
                    bank = selectedBank!!.id,
                    balance = balance.toDoubleOrNull() ?: 0.0,
                    closingDay = closingDay.toIntOrNull() ?: 1,
                    paymentDueDay = dueDay.toIntOrNull() ?: 10
                )

                //val response = RetrofitClient.accountApi.createAccount(request)
                val response = accountApi.createAccount(request)

                if (response.isSuccessful) {
                    toastMessage = "Conta Criada com Sucesso!"
                    saveSuccess = true // Gatilho para navegar
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
                    toastMessage = finalMessage
                }
            } catch (e: Exception) {
                toastMessage = "Erro: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // limpezas de eventos
    fun clearToastMessage() {
        toastMessage = null
    }

    fun onNavigatedAway() {
        saveSuccess = false
    }
}