package br.edu.ifpb.finsupp.ui.viewmodel.uiState

import br.edu.ifpb.finsupp.network.model.Bank

data class AddAccountUiState(
    // Campos do formulário
    val description: String = "",
    val balance: String = "",
    val closingDay: String = "",
    val dueDay: String = "",
    val selectedType: String = "CHECKING",
    val selectedBank: Bank? = null,

    // Dados de lista
    val bankList: List<Bank> = emptyList(),

    // Estados de controle
    val isLoading: Boolean = false,
    val isLoadingBanks: Boolean = true,
    val toastMessage: String? = null,
    val saveSuccess: Boolean = false
)