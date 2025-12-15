package br.edu.ifpb.finsupp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.finsupp.ui.components.AppDrawer
import br.edu.ifpb.finsupp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String,
    onLogout: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onNavigateToTransactions: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                drawerState = drawerState,
                scope = scope,
                onNavigateToHome = { scope.launch { drawerState.close() } },
                onNavigateToAccounts = onNavigateToAccounts,
                onNavigateToTransactions = { // <--- Conectado aqui
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
                        title = { Text("Painel", color = Color.White, fontWeight = FontWeight.Bold) },
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
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Olá, ", color = Color.Gray, fontSize = 20.sp)
                        Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Seus dados aparecerão aqui", color = Color.Gray)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, null) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent, unselectedTextColor = Color.Gray, unselectedIconColor = Color.Gray),
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}