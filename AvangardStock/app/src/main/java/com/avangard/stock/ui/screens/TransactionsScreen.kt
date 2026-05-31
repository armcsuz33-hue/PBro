package com.avangard.stock.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import com.avangard.stock.ui.theme.*
import com.avangard.stock.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: TransactionViewModel = viewModel()) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val products by viewModel.products.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tranzaksiyalar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Barcha kirdi-chiqdi amallari",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterType == null,
                onClick = { viewModel.setFilter(null) },
                label = { Text("Barchasi") },
                leadingIcon = if (filterType == null) {
                    { Icon(Icons.Filled.Done, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
            FilterChip(
                selected = filterType == TransactionType.INCOMING,
                onClick = { viewModel.setFilter(TransactionType.INCOMING) },
                label = { Text("Kirdi") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = IncomeGreen.copy(alpha = 0.2f)
                )
            )
            FilterChip(
                selected = filterType == TransactionType.SOLD,
                onClick = { viewModel.setFilter(TransactionType.SOLD) },
                label = { Text("Sotildi") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SoldBlue.copy(alpha = 0.2f)
                )
            )
            FilterChip(
                selected = filterType == TransactionType.RETURNED,
                onClick = { viewModel.setFilter(TransactionType.RETURNED) },
                label = { Text("Qaytdi") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ReturnOrange.copy(alpha = 0.2f)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Jami
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Jami amallar:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${transactions.size} ta",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tranzaksiyalar ro'yxati
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Hali tranzaksiyalar yo'q",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions) { transaction ->
                    TransactionDetailCard(transaction, products)
                }
            }
        }
    }
}

@Composable
fun TransactionDetailCard(transaction: StockTransaction, products: List<Product>) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val formatter = NumberFormat.getNumberInstance(Locale("uz", "UZ"))
    val product = products.find { it.id == transaction.productId }

    val (icon, color, label) = when (transaction.type) {
        TransactionType.INCOMING -> Triple(Icons.Filled.ArrowDownward, IncomeGreen, "KIRDI")
        TransactionType.SOLD -> Triple(Icons.Filled.ArrowUpward, SoldBlue, "SOTILDI")
        TransactionType.RETURNED -> Triple(Icons.Filled.Undo, ReturnOrange, "QAYTDI")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, contentDescription = null, tint = color)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product?.name ?: "Mahsulot #${transaction.productId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = color.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${transaction.quantity} dona",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                if (transaction.note.isNotEmpty()) {
                    Text(
                        text = "📝 ${transaction.note}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Summa
            Text(
                text = "${formatter.format(transaction.totalAmount)}\nso'm",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
