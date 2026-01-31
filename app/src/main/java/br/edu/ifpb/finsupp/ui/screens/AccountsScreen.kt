package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.network.model.AccountApiData
import br.edu.ifpb.finsupp.ui.components.AppDrawer
import br.edu.ifpb.finsupp.ui.theme.*
import br.edu.ifpb.finsupp.ui.viewmodel.AccountsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    userName: String,
    viewModel: AccountsViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAddAccount: () -> Unit,
    onNavigateToEditAccount: (AccountApiData) -> Unit,
    onNavigateToTransactions: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // carrega dados ao entrar ou trocar de usuário
    LaunchedEffect(userName) {
        viewModel.loadData()
    }

    // observa mensagens de erro/sucesso para mostrar Toast
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                drawerState = drawerState,
                scope = scope,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccounts = {
                    scope.launch { drawerState.close() }
                },
                onNavigateToTransactions = { // <--- Conectado aqui
                    scope.launch {
                        drawerState.close()
                        onNavigateToTransactions()
                    }
                },
                onLogout = {
                    viewModel.resetState() // limpa o estado antes de sair
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
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
                    .padding(horizontal = 24.dp)
            ) {
                // cabeçalho e botão Add
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Accounts", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { onNavigateToAddAccount() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // campo de busca ligado ao ViewModel
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search accounts...", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F172A),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // estado da lista (Loading, Vazio, Preenchido)
                if (viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else if (viewModel.uiAccounts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            if (viewModel.searchQuery.isNotEmpty()) "No results found."
                            else "No accounts found. Create your first\naccount to get started.",
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = viewModel.uiAccounts) { account ->
                            val bankName = viewModel.banksMap[account.bank] ?: "Bank ${account.bank}"

                            AccountItemCard(
                                account = account,
                                bankName = bankName,
                                onDeleteClick = { viewModel.deleteAccount(account) },
                                onEditClick = { onNavigateToEditAccount(account) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountItemCard(
    account: AccountApiData,
    bankName: String,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = account.description, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = bankName, color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "R$ ${String.format("%.2f", account.balance)}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(text = account.accountType, color = Color(0xFF94A3B8), fontSize = 14.sp)
            Text(
                text = "Closing day: ${account.closingDay}, Due day: ${account.paymentDueDay}",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit")
                }

                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", color = Color.White)
                }
            }
        }
    }
}