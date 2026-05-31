package com.avangard.stock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avangard.stock.data.model.DashboardStats
import com.avangard.stock.data.model.Product
import com.avangard.stock.data.model.StockTransaction
import com.avangard.stock.data.model.TransactionType
import com.avangard.stock.ui.theme.*
import com.avangard.stock.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val stats by viewModel.dashboardStats.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Avangard Stock",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sklad boshqaruv tizimi",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Stats Cards
        item {
            StatsCardsRow(stats)
        }

        // Umumiy oborot
        item {
            RevenueCard(stats)
        }

        // Kam qolgan mahsulotlar
        if (lowStockProducts.isNotEmpty()) {
            item {
                Text(
                    text = "⚠️ Kam qolgan mahsulotlar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AlertRed
                )
            }
            items(lowStockProducts.take(3)) { product ->
                LowStockItem(product)
            }
        }

        // So'nggi tranzaksiyalar
        item {
            Text(
                text = "So'nggi amallar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(recentTransactions.take(10)) { transaction ->
            TransactionItem(transaction)
        }

        if (recentTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hali tranzaksiyalar yo'q.\nYangi amal qo'shishni boshlang!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCardsRow(stats: DashboardStats) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StatCard(
                title = "Mahsulotlar",
                value = "${stats.totalProducts}",
                icon = Icons.Filled.Inventory,
                color = AvangardTeal
            )
        }
        item {
            StatCard(
                title = "Omborda",
                value = "${stats.totalStockQuantity} dona",
                icon = Icons.Filled.Warehouse,
                color = SoldBlue
            )
        }
        item {
            StatCard(
                title = "Kirdi",
                value = "${stats.totalIncoming}",
                icon = Icons.Filled.ArrowDownward,
                color = IncomeGreen
            )
        }
        item {
            StatCard(
                title = "Sotildi",
                value = "${stats.totalSold}",
                icon = Icons.Filled.ArrowUpward,
                color = SoldBlue
            )
        }
        item {
            StatCard(
                title = "Qaytdi",
                value = "${stats.totalReturned}",
                icon = Icons.Filled.Undo,
                color = ReturnOrange
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun RevenueCard(stats: DashboardStats) {
    val formatter = NumberFormat.getNumberInstance(Locale("uz", "UZ"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AvangardTeal)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Umumiy oborot",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatter.format(stats.totalRevenue)} so'm",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Xarajat", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    Text(
                        "${formatter.format(stats.totalCost)} so'm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text("Foyda", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    Text(
                        "${formatter.format(stats.totalProfit)} so'm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (stats.totalProfit >= 0) AvangardGold else AlertRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LowStockItem(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, fontWeight = FontWeight.Medium)
                Text(
                    "Minimal: ${product.minStockAlert} dona",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text(
                "${product.stockQuantity} dona",
                color = AlertRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: StockTransaction) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val (icon, color, label) = when (transaction.type) {
        TransactionType.INCOMING -> Triple(Icons.Filled.ArrowDownward, IncomeGreen, "Kirdi")
        TransactionType.SOLD -> Triple(Icons.Filled.ArrowUpward, SoldBlue, "Sotildi")
        TransactionType.RETURNED -> Triple(Icons.Filled.Undo, ReturnOrange, "Qaytdi")
    }
    val formatter = NumberFormat.getNumberInstance(Locale("uz", "UZ"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(label, fontWeight = FontWeight.Medium, color = color)
                    Text(
                        "${transaction.quantity} dona • ${dateFormat.format(Date(transaction.date))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Text(
                "${formatter.format(transaction.totalAmount)} so'm",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
