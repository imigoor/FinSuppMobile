package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.RetrofitClient
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    private var _allAccounts = listOf<AccountApiData>()

    // estados visíveis para a UI
    var uiAccounts by mutableStateOf<List<AccountApiData>>(emptyList())
        private set

    var banksMap by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var toastMessage by mutableStateOf<String?>(null)
        private set

    // Carrega dados iniciais
    fun loadData() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Carrega lista de bancos para mapear ID -> Nome
                val banksResponse = RetrofitClient.bankApi.getBanks()
                if (banksResponse.isSuccessful) {
                    banksMap = banksResponse.body()?.dataList?.associate { it.id to it.name } ?: emptyMap()
                }

                // 2. Carrega as contas do usuário
                val accountsResponse = RetrofitClient.accountApi.getAccounts()
                if (accountsResponse.isSuccessful) {
                    _allAccounts = accountsResponse.body()?.dataList ?: emptyList()
                    applySearchFilter() // Atualiza a lista visual
                } else {
                    toastMessage = "Erro ao carregar contas: ${accountsResponse.code()}"
                }
            } catch (e: Exception) {
                toastMessage = "Erro de conexão"
            } finally {
                isLoading = false
            }
        }
    }

    // Atualiza a busca e filtra a lista
    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        applySearchFilter()
    }

    private fun applySearchFilter() {
        uiAccounts = if (searchQuery.isBlank()) {
            _allAccounts
        } else {
            _allAccounts.filter {
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Deleta uma conta
    fun deleteAccount(account: AccountApiData) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.accountApi.deleteAccount(account.id)
                if (response.isSuccessful) {
                    toastMessage = "Conta deletada!"
                    // Remove localmente para não precisar recarregar tudo da API
                    _allAccounts = _allAccounts.filter { it.id != account.id }
                    applySearchFilter()
                } else {
                    toastMessage = if (response.code() == 409) {
                        "Não é possível deletar conta com movimentações."
                    } else {
                        "Erro ao deletar: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                toastMessage = "Erro de conexão ao deletar"
            }
        }
    }

    // Limpa a mensagem de Toast após exibida
    fun clearToastMessage() {
        toastMessage = null
    }

    // Reseta o estado (útil no Logout)
    fun resetState() {
        _allAccounts = emptyList()
        uiAccounts = emptyList()
        searchQuery = ""
        banksMap = emptyMap()
    }
}