package br.edu.ifpb.finsupp.ui.viewmodel.uiState

import br.edu.ifpb.finsupp.network.model.Bank

data class EditAccountUiState(
    // Campos do formulário
    val description: String = "",
    val balance: String = "",
    val closingDay: String = "",
    val dueDay: String = "",
    val selectedType: String = "CHECKING",

    // Seleção de Banco
    val selectedBank: Bank? = null,
    val bankList: List<Bank> = emptyList(),

    // Estados de controle
    val isLoading: Boolean = false,
    val isLoadingBanks: Boolean = true,
    val toastMessage: String? = null,
    val updateSuccess: Boolean = false
)