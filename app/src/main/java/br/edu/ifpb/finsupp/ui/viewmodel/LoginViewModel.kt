package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.TokenManager
import br.edu.ifpb.finsupp.network.service.AuthApi
import br.edu.ifpb.finsupp.ui.viewmodel.uiState.LoginUiState
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel(private val api: AuthApi) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(v: String) { uiState = uiState.copy(email = v) }
    fun updatePassword(v: String) { uiState = uiState.copy(password = v) }
    fun updateRegisterName(v: String) { uiState = uiState.copy(registerName = v) }
    fun updateRegisterEmail(v: String) { uiState = uiState.copy(registerEmail = v) }
    fun updateRegisterPassword(v: String) { uiState = uiState.copy(registerPassword = v) }

    // reseta o flag de sucesso após navegação
    fun onLoginSuccessHandled() { uiState = uiState.copy(loginSuccess = false) }

    fun performLogin() {
        if (uiState.email.isBlank() || uiState.password.isBlank()) {
            uiState = uiState.copy(loginError = "Preencha todos os campos")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, loginError = null)
            try {
                val request = LoginRequest(uiState.email, uiState.password)
                val response = api.login(request)

                if (response.isSuccessful && response.body()?.type == "Success") {
                    val userData = response.body()?.data
                    TokenManager.token = userData?.token

                    uiState = uiState.copy(
                        userName = userData?.name ?: "Usuário",
                        loginSuccess = true
                    )
                } else {
                    tratarErro(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                uiState = uiState.copy(loginError = "Falha na conexão: ${e.message}")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun performRegister() {
        if (uiState.registerName.isBlank() || uiState.registerEmail.isBlank() || uiState.registerPassword.isBlank()) {
            uiState = uiState.copy(loginError = "Preencha todos os campos")
            return
        }
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, loginError = null)
            try {
                val request = RegisterRequest(uiState.registerName, uiState.registerEmail, uiState.registerPassword)
                val response = api.register(request)

                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    TokenManager.token = userData?.token

                    uiState = uiState.copy(
                        userName = userData?.name ?: uiState.registerName,
                        loginSuccess = true
                    )
                } else {
                    tratarErro(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                uiState = uiState.copy(loginError = "Falha na conexão: ${e.message}")
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private fun tratarErro(errorJson: String?, code: Int) {
        if (errorJson != null) {
            try {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, LoginResponse::class.java)
                uiState = uiState.copy(loginError = errorResponse.message)
            } catch (e: Exception) {
                uiState = uiState.copy(loginError = "Erro ao processar resposta")
            }
        } else {
            uiState = uiState.copy(loginError = "Erro na operação ($code)")
        }
    }
}