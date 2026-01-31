package br.edu.ifpb.finsupp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.ui.theme.*
import br.edu.ifpb.finsupp.ui.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = koinViewModel(), onLoginSuccess: (String) -> Unit) {
    var isLoginTab by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(loginViewModel.loginSuccess) {
        if (loginViewModel.loginSuccess) {
            onLoginSuccess(loginViewModel.userName)
            loginViewModel.loginSuccess = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF172554))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AttachMoney, "Logo", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("FinSupp", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Your complete financial management solution", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(InputBackground, RoundedCornerShape(8.dp)).padding(4.dp)
                ) {
                    TabButton("Sign In", isLoginTab) { isLoginTab = true }
                    TabButton("Create Account", !isLoginTab) { isLoginTab = false }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (isLoginTab) {
                    Text("Welcome back", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("Sign in to access your account", color = TextGray, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField("Email", loginViewModel.email, { loginViewModel.email = it }, Icons.Default.Email, "Digite seu e-mail")
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField("Password", loginViewModel.password, { loginViewModel.password = it }, Icons.Default.Lock, "Digite sua senha",true)

                    ErrorMessage(loginViewModel.loginError)
                    Spacer(modifier = Modifier.height(24.dp))

                    ActionButton(text = "Sign In", isLoading = loginViewModel.isLoading) {
                        loginViewModel.performLogin()
                    }

                } else {
                    Text("Create an account", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("Sign up to get started with FinSupp", color = TextGray, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField("Full Name", loginViewModel.registerName, { loginViewModel.registerName = it }, Icons.Default.Person, "Digite seu nome completo")
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField("Email", loginViewModel.registerEmail, { loginViewModel.registerEmail = it }, Icons.Default.Email, "Digite seu e-mail")
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField("Password", loginViewModel.registerPassword, { loginViewModel.registerPassword = it }, Icons.Default.Lock, "Digite sua senha", true)

                    ErrorMessage(loginViewModel.loginError)
                    Spacer(modifier = Modifier.height(24.dp))

                    ActionButton(text = "Create Account", isLoading = loginViewModel.isLoading) {
                        loginViewModel.performRegister()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(error: String?) {
    if (error != null) {
        Text(text = error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun ActionButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(text, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .background(if (isSelected) CardBackground else Color.Transparent, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) Color.White else TextGray, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector, placeholderText: String, isPassword: Boolean = false,) {
    Column {
        Text(label, color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp), singleLine = true,
            leadingIcon = { Icon(icon, null, tint = TextGray) },
            placeholder = { Text(text = placeholderText, color = TextGray.copy(alpha = 0.5f)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground, unfocusedContainerColor = InputBackground,
                focusedBorderColor = PrimaryBlue, unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White, unfocusedTextColor = Color.White
            )
        )
    }
}