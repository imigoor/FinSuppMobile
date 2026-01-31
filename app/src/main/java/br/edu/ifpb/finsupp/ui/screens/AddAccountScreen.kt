package br.edu.ifpb.finsupp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.finsupp.ui.theme.DarkBackground
import br.edu.ifpb.finsupp.ui.theme.PrimaryBlue
import br.edu.ifpb.finsupp.ui.viewmodel.AddAccountViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    viewModel: AddAccountViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current

    // estados visuais locais
    var expandedType by remember { mutableStateOf(false) }
    var expandedBank by remember { mutableStateOf(false) }

    // carrega bancos ao abrir a tela
    LaunchedEffect(Unit) {
        viewModel.loadBanks()
    }

    // observa sucesso para navegar de volta
    LaunchedEffect(viewModel.saveSuccess) {
        if (viewModel.saveSuccess) {
            onSaveSuccess()
            viewModel.onNavigatedAway()
        }
    }

    // observa mensagens de erro/sucesso (Toast)
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold) },
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
            Text("Add a new bank account to track your finances", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // campos ligados ao ViewModel
            DarkInput(
                label = "Description",
                value = viewModel.description,
                onValueChange = { viewModel.description = it }
            )

            // selecao de banco
            Text("Bank", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedTextField(
                    value = if (viewModel.isLoadingBanks) "Carregando bancos..." else (viewModel.selectedBank?.name ?: "Selecione um banco"),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth().clickable {
                        if (!viewModel.isLoadingBanks) expandedBank = true
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B), unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        disabledTextColor = Color.Gray, disabledContainerColor = Color(0xFF1E293B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = false
                )
                Box(modifier = Modifier.matchParentSize().clickable { if (!viewModel.isLoadingBanks) expandedBank = true })

                DropdownMenu(
                    expanded = expandedBank,
                    onDismissRequest = { expandedBank = false },
                    modifier = Modifier.background(Color(0xFF1E293B)).heightIn(max = 250.dp)
                ) {
                    viewModel.bankList.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(bank.name, color = Color.White) },
                            onClick = {
                                viewModel.selectedBank = bank // Atualiza no VM
                                expandedBank = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // tipo de conta
            Text("Account Type", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedTextField(
                    value = viewModel.selectedType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B), unfocusedContainerColor = Color(0xFF1E293B),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        disabledTextColor = Color.Gray, disabledContainerColor = Color(0xFF1E293B)
                    ),
                    enabled = false
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedType = true })
                DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }, modifier = Modifier.background(Color(0xFF1E293B))) {
                    DropdownMenuItem(text = { Text("CHECKING") }, onClick = { viewModel.selectedType = "CHECKING"; expandedType = false })
                    DropdownMenuItem(text = { Text("SAVINGS") }, onClick = { viewModel.selectedType = "SAVINGS"; expandedType = false })
                    DropdownMenuItem(text = { Text("INVESTMENT") }, onClick = { viewModel.selectedType = "INVESTMENT"; expandedType = false })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // saldo e data
            DarkInput(
                label = "Initial Balance",
                value = viewModel.balance,
                onValueChange = { viewModel.balance = it },
                isNumber = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    DarkInput(
                        label = "Closing Day",
                        value = viewModel.closingDay,
                        onValueChange = { viewModel.closingDay = it },
                        isNumber = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    DarkInput(
                        label = "Payment Due Day",
                        value = viewModel.dueDay,
                        onValueChange = { viewModel.dueDay = it },
                        isNumber = true
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // botão de salvar
            Button(
                onClick = { viewModel.createAccount() }, // Chama o VM
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Create", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DarkInput(label: String, value: String, onValueChange: (String) -> Unit, isNumber: Boolean = false) {
    Column {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}