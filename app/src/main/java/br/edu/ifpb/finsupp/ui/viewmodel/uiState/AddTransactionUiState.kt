package br.edu.ifpb.finsupp.ui.viewmodel.uiState

import br.edu.ifpb.finsupp.network.model.AccountApiData
import br.edu.ifpb.finsupp.network.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddTransactionUiState(
    // Campos do formulário
    val selectedTab: Int = 0,
    val description: String = "",
    val amount: String = "",
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),

    // Seleções
    val selectedAccount: AccountApiData? = null,
    val selectedToAccount: AccountApiData? = null,
    val selectedCategory: Category? = null,

    // Listas
    val accountsList: List<AccountApiData> = emptyList(),
    val categoriesList: List<Category> = emptyList(),

    // Controle
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val toastMessage: String? = null
)