package br.edu.ifpb.finsupp.ui.viewmodel.uiState

import br.edu.ifpb.finsupp.network.model.TransactionApiData

data class TransactionsUiState(
    val uiTransactions: List<TransactionApiData> = emptyList(),
    val isLoading: Boolean = false,
    val toastMessage: String? = null
)