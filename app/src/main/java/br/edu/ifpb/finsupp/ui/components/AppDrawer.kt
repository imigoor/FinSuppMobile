package br.edu.ifpb.finsupp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.ifpb.finsupp.ui.theme.DarkBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    onNavigateToHome: () -> Unit,
    onNavigateToAccounts: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = DarkBackground,
        drawerContentColor = Color.White
    ) {
        // cabeçalho do menu hamburguer
        Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("FinSupp", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        HorizontalDivider(color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(16.dp))

        // itens do menu hamburguer

        DrawerItem(
            icon = Icons.Default.Dashboard,
            label = "Dashboard",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToHome()
                }
            }
        )

        DrawerItem(
            icon = Icons.Default.CreditCard,
            label = "Accounts",
            onClick = {
                scope.launch {
                    drawerState.close()
                    onNavigateToAccounts()
                }
            }
        )

        DrawerItem(
            icon = Icons.Default.Category,
            label = "Categories",
            onClick = { scope.launch { drawerState.close() } } // Futuro
        )

        DrawerItem(
            icon = Icons.Default.SwapHoriz,
            label = "Transactions",
            onClick = { scope.launch { drawerState.close() } } // Futuro
        )

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(color = Color(0xFF1E293B))

        NavigationDrawerItem(
            label = { Text("Logout", fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Red) },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    onLogout()
                }
            },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                unselectedTextColor = Color.Red
            ),
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, null) },
        selected = false,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = Color.Gray,
            unselectedIconColor = Color.Gray
        ),
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}