package br.edu.ifpb.finsupp.ui.viewmodel.uiState

data class LoginUiState(
    // Campos de Login
    val email: String = "",
    val password: String = "",

    // Campos de Registro
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",

    // Controle
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false,
    val userName: String = ""
)