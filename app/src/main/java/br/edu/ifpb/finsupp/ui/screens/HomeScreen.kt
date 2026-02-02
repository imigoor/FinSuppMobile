package br.edu.ifpb.finsupp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.finsupp.ui.components.AppDrawer
import br.edu.ifpb.finsupp.ui.theme.*
import br.edu.ifpb.finsupp.ui.viewmodel.AccountsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    viewModel: AccountsViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val state = viewModel.uiState

    // Carrega dados para somar o saldo real
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val totalBalance = state.accounts.sumOf { it.balance }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                drawerState = drawerState,
                scope = scope,
                onNavigateToHome = { scope.launch { drawerState.close() } },
                onNavigateToAccounts = onNavigateToAccounts,
                onNavigateToTransactions = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToTransactions()
                    }
                },
                onLogout = onLogout
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("FinSupp", color = Color.White, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
                    )
                },
                containerColor = DarkBackground
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp)
                ) {
                    // SAUDAÇÃO
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Olá, ", color = Color.Gray, fontSize = 20.sp)
                        Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CARTÃO AZUL (BONITÃO: WIFI + VISA + SALDO REAL)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A))
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            // SpaceBetween joga o wifi pra cima e o Visa pra baixo
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

                                // TOPO: TEXTO + WIFI
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Saldo Total Estimado", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                    // Símbolo do Contactless
                                    Icon(Icons.Default.Wifi, null, tint = Color.White.copy(alpha = 0.6f))
                                }

                                // MEIO: SALDO DINÂMICO
                                if (state.isLoading) {
                                    Text("Carregando...", color = Color.White.copy(alpha = 0.7f), fontSize = 24.sp)
                                } else {
                                    Text(
                                        text = "R$ ${String.format("%.2f", totalBalance)}",
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // EMBAIXO: VISA
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "VISA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 22.sp,
                                        modifier = Modifier.align(Alignment.BottomEnd) // Joga pra direita
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTÕES DE ATALHO
                    Text("Acesso Rápido", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        QuickActionItem(icon = Icons.Default.CreditCard, label = "Contas", onClick = onNavigateToAccounts)
                        QuickActionItem(icon = Icons.Default.SwapHoriz, label = "Extrato", onClick = onNavigateToTransactions)
                    }
                }
            }
        }
    )
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) {
            Icon(icon, null, tint = PrimaryBlue)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = TextGray, fontSize = 12.sp)
    }
}