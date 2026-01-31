package br.edu.ifpb.finsupp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.finsupp.network.model.*
import br.edu.ifpb.finsupp.network.TokenManager
import br.edu.ifpb.finsupp.network.service.AuthApi
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel(private val api: AuthApi) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var registerName by mutableStateOf("")
    var registerEmail by mutableStateOf("")
    var registerPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    var userName by mutableStateOf("")

    fun performLogin() {
        if (email.isBlank() || password.isBlank()) {
            loginError = "Preencha todos os campos"
            return
        }
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val request = LoginRequest(email, password)
                val response = api.login(request)
                //val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body()?.type == "Success") {
                    val userData = response.body()?.data
                    TokenManager.token = userData?.token
                    userName = userData?.name ?: "Usuário"
                    loginSuccess = true
                } else {
                    tratarErro(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                loginError = "Falha na conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun performRegister() {
        if (registerName.isBlank() || registerEmail.isBlank() || registerPassword.isBlank()) {
            loginError = "Preencha todos os campos"
            return
        }
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val request = RegisterRequest(registerName, registerEmail, registerPassword)
                val response = api.register(request)
                //val response = RetrofitClient.api.register(request)

                if (response.isSuccessful) {
                    val userData = response.body()?.data
                    TokenManager.token = userData?.token
                    userName = userData?.name ?: registerName
                    loginSuccess = true
                } else {
                    tratarErro(response.errorBody()?.string(), response.code())
                }
            } catch (e: Exception) {
                loginError = "Falha na conexão: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun tratarErro(errorJson: String?, code: Int) {
        if (errorJson != null) {
            try {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorJson, LoginResponse::class.java)
                loginError = errorResponse.message
            } catch (e: Exception) {
                loginError = "Erro ao processar resposta"
            }
        } else {
            loginError = "Erro na operação ($code)"
        }
    }
}