package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.service.AccountApi
import br.edu.ifpb.finsupp.network.service.BankApi
import br.edu.ifpb.finsupp.repository.AccountRepository
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.AccountsUiState
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val repository: AccountRepository,
    private val bankApi: BankApi
) : ViewModel() {

    private var _allAccountsCache = listOf<AccountApiData>()

    var logoutSuccess by mutableStateOf(false)
        private set

    // o estado unico da tela padrao ui state
    var uiState by mutableStateOf(AccountsUiState())
        private set

    fun loadData() {
        viewModelScope.launch {
            // Atualiza estado para loading
            uiState = uiState.copy(isLoading = true)

            try {
                val currentBanks = try {
                    val res = bankApi.getBanks()
                    if (res.isSuccessful) {
                        res.body()?.dataList?.associate { it.id to it.name } ?: emptyMap()
                    } else uiState.banksMap
                } catch (e: Exception) {
                    uiState.banksMap
                }

                // room
                _allAccountsCache = repository.getAccounts()

                uiState = uiState.copy(
                    isLoading = false,
                    banksMap = currentBanks,
                    accounts = filterList(uiState.searchQuery, _allAccountsCache),
                    toastMessage = if (_allAccountsCache.isEmpty()) "Nenhuma conta encontrada." else null
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    toastMessage = "Erro ao carregar dados: ${e.message}"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        // Atualiza a query E a lista filtrada ao mesmo tempo
        uiState = uiState.copy(
            searchQuery = query,
            accounts = filterList(query, _allAccountsCache)
        )
    }

    private fun filterList(query: String, list: List<AccountApiData>): List<AccountApiData> {
        return if (query.isBlank()) list
        else list.filter { it.description.contains(query, ignoreCase = true) }
    }

    fun deleteAccount(account: AccountApiData) {
        viewModelScope.launch {
            try {
                val success = repository.deleteAccount(account.id)
                if (success) {
                    _allAccountsCache = _allAccountsCache.filter { it.id != account.id }

                    uiState = uiState.copy(
                        accounts = filterList(uiState.searchQuery, _allAccountsCache),
                        toastMessage = "Conta deletada!"
                    )
                } else {
                    uiState = uiState.copy(toastMessage = "Não é possível deletar conta com movimentações.")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(toastMessage = "Erro de conexão ao deletar")
            }
        }
    }

    fun clearToastMessage() {
        uiState = uiState.copy(toastMessage = null)
    }

    fun performLogout() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true) // Mostra loading enquanto limpa
            try {
                repository.clearLocalData() // ESPERA limpar o banco
            } catch (e: Exception) {
                // Mesmo se der erro no banco, força a saída
            }

            // Limpa memória RAM
            _allAccountsCache = emptyList()
            uiState = AccountsUiState()

            // Avisa a tela que acabou
            logoutSuccess = true
        }
    }

    fun onLogoutHandled() {
        logoutSuccess = false
    }
}