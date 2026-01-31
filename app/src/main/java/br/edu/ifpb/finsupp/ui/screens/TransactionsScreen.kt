package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.network.model.TransactionApiData
import br.edu.ifpb.finsupp.ui.components.AppDrawer
import br.edu.ifpb.finsupp.ui.theme.DarkBackground
import br.edu.ifpb.finsupp.ui.theme.PrimaryBlue
import br.edu.ifpb.finsupp.ui.viewmodel.TransactionsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    userName: String,
    viewModel: TransactionsViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToAdd: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.loadData() }

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                drawerState = drawerState, scope = scope,
                onNavigateToHome = onNavigateToHome,
                onNavigateToAccounts = onNavigateToAccounts,
                onNavigateToTransactions = {
                    scope.launch {
                        drawerState.close()
                        onNavigateToTransactions()
                    }
                },
                onLogout = onLogout
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
            containerColor = DarkBackground,
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToAdd, containerColor = PrimaryBlue) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Transactions", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (viewModel.uiTransactions.isEmpty()) {
                    Text("No transactions found.", color = Color.Gray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(viewModel.uiTransactions) { transaction ->
                            TransactionItem(transaction) { viewModel.deleteTransaction(transaction.id) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionApiData, onDelete: () -> Unit) {
    val isPositive = transaction.type == "DEPOSIT"
    val color = if (isPositive) Color.Green else Color.Red
    val signal = if (isPositive) "+" else "-"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.description, color = Color.White, fontWeight = FontWeight.Bold)
                Text(transaction.transactionDate, color = Color.Gray, fontSize = 12.sp)
                Text(transaction.type, color = Color.Gray, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$signal R$ ${String.format("%.2f", transaction.amount)}",
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}