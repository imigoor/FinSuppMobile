package br.edu.ifpb.finsupp.network.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val type: String,
    val data: UserData? = null
)

data class UserData(
    val name: String,
    val token: String
)