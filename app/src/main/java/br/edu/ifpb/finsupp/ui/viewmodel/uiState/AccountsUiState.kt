package br.edu.ifpb.finsupp.ui.viewmodel.uiState

import br.edu.ifpb.finsupp.network.model.AccountApiData

data class AccountsUiState(
    val accounts: List<AccountApiData> = emptyList(), // Lista filtrada (o que aparece na tela)
    val banksMap: Map<Int, String> = emptyMap(),      // Mapa de bancos
    val isLoading: Boolean = false,                   // Carregando?
    val searchQuery: String = "",                     // Texto da busca
    val toastMessage: String? = null                  // Mensagem de erro/sucesso
)