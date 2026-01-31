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

class EditAccountViewModel(private val accountApi: AccountApi, private val bankApi: BankApi) : ViewModel() {

    // estados do formulario
    var description by mutableStateOf("")
    var balance by mutableStateOf("")
    var closingDay by mutableStateOf("")
    var dueDay by mutableStateOf("")
    var selectedType by mutableStateOf("CHECKING")

    var selectedBank by mutableStateOf<Bank?>(null)
    var bankList by mutableStateOf<List<Bank>>(emptyList())
        private set

    // estados de controle
    var isLoading by mutableStateOf(false)
        private set

    var isLoadingBanks by mutableStateOf(true)
        private set

    // eventos
    var toastMessage by mutableStateOf<String?>(null)
        private set

    var updateSuccess by mutableStateOf(false)
        private set

    // ID da conta sendo editada
    private var currentAccountId: Int = 0
    private var initialBankId: Int = 0

    // inicialização
    // chamado pela Screen ao abrir para preencher os dados
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
        description = initDesc
        initialBankId = initBankId
        selectedType = initType
        balance = initBalance.toString()
        closingDay = initClosing.toString()
        dueDay = initDue.toString()

        loadBanks() // carrega bancos e seleciona o correto
    }

    private fun loadBanks() {
        viewModelScope.launch {
            isLoadingBanks = true
            try {
                //val response = RetrofitClient.bankApi.getBanks()
                val response = bankApi.getBanks()
                if (response.isSuccessful) {
                    val banks = response.body()?.dataList ?: emptyList()
                    bankList = banks
                    // tenta encontrar o banco original na lista carregada
                    selectedBank = banks.find { it.id == initialBankId }
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

    // ação de atualizar
    fun updateAccount() {
        viewModelScope.launch {
            isLoading = true
            try {
                // monta o objeto de requisição
                val request = CreateAccountRequest(
                    description = description,
                    accountType = selectedType,
                    bank = selectedBank?.id ?: initialBankId,
                    balance = balance.toDoubleOrNull() ?: 0.0,
                    closingDay = closingDay.toIntOrNull() ?: 1,
                    paymentDueDay = dueDay.toIntOrNull() ?: 10
                )
                val response = accountApi.updateAccount(currentAccountId, request)
                //val response = RetrofitClient.accountApi.updateAccount(currentAccountId, request)

                if (response.isSuccessful) {
                    toastMessage = "Conta Atualizada!"
                    updateSuccess = true
                } else {
                    // tratamento de erro JSON (422)
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
                    toastMessage = finalMessage
                }
            } catch (e: Exception) {
                toastMessage = "Erro: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearToastMessage() {
        toastMessage = null
    }

    fun onNavigatedAway() {
        updateSuccess = false
    }
}