package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.ui.theme.DarkBackground
import br.edu.ifpb.finsupp.ui.theme.PrimaryBlue
import br.edu.ifpb.finsupp.ui.viewmodel.EditAccountViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    accountId: Int,
    initialDescription: String,
    initialBankId: Int,
    initialType: String,
    initialBalance: Double,
    initialClosing: Int,
    initialDue: Int,
    viewModel: EditAccountViewModel = koinViewModel(),
    onBack: () -> Unit,
    onUpdateSuccess: () -> Unit
) {
    val context = LocalContext.current

    // estados visuais locais (Dropdowns)
    var expandedBank by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    // inicializa o ViewModel com os dados recebidos
    LaunchedEffect(Unit) {
        viewModel.initialize(
            id = accountId,
            initDesc = initialDescription,
            initBankId = initialBankId,
            initType = initialType,
            initBalance = initialBalance,
            initClosing = initialClosing,
            initDue = initialDue
        )
    }

    // observa sucesso na atualização
    LaunchedEffect(viewModel.updateSuccess) {
        if (viewModel.updateSuccess) {
            onUpdateSuccess()
            viewModel.onNavigatedAway()
        }
    }

    // observa mensagens (Toasts)
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Account", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Update your account information", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // campos ligados ao ViewModel
            DarkInput(
                label = "Description",
                value = viewModel.description,
                onValueChange = { viewModel.description = it }
            )

            // banco
            Text("Bank", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedTextField(
                    value = viewModel.selectedBank?.name ?: "Carregando...",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth().clickable { expandedBank = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B), unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White, disabledContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedBank = true })
                DropdownMenu(
                    expanded = expandedBank,
                    onDismissRequest = { expandedBank = false },
                    modifier = Modifier.background(Color(0xFF1E293B)).heightIn(max = 250.dp)
                ) {
                    viewModel.bankList.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(bank.name, color = Color.White) },
                            onClick = {
                                viewModel.selectedBank = bank
                                expandedBank = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // tipo de conta
            Text("Account Type", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth().clickable { expandedType = true }) {
                OutlinedTextField(
                    value = viewModel.selectedType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B), unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White
                    ),
                    enabled = false
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedType = true })
                DropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false },
                    modifier = Modifier.background(Color(0xFF1E293B))
                ) {
                    listOf("CHECKING", "SAVINGS", "INVESTMENT").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = Color.White) },
                            onClick = {
                                viewModel.selectedType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // saldo e datas
            DarkInput(
                label = "Balance",
                value = viewModel.balance,
                onValueChange = { viewModel.balance = it },
                isNumber = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.weight(1f)) {
                    DarkInput("Closing Day", viewModel.closingDay, { viewModel.closingDay = it }, true)
                }
                Box(Modifier.weight(1f)) {
                    DarkInput("Payment Due Day", viewModel.dueDay, { viewModel.dueDay = it }, true)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // botão de update
            Button(
                onClick = { viewModel.updateAccount() }, // Chama o VM
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Update", fontWeight = FontWeight.Bold)
            }
        }
    }
}