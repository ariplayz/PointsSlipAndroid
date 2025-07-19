// MainActivity.kt
package com.DelphiGamerz.PointsSlip // Updated package name

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Assuming your theme is also under this new package structure
import com.DelphiGamerz.PointsSlip.ui.theme.PointsSlipTheme
// SettingsDataStore is now in the same package, direct import might not be needed
// or would be 'import com.DelphiGamerz.PointsSlip.SettingsDataStore' if in a sub-package.
// Since it's top-level in the same package, it should be directly accessible.

// Data Class
data class NumericListItemData(
    val id: Int,
    val label: String,
    val pointsPerUnit: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            PointsSlipTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PointsSlipTopBar(
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme }
                        )
                    }
                ) { innerPadding ->
                    val context = LocalContext.current
                    // Ensure SettingsDataStore is correctly referenced
                    val settingsDataStore = remember { SettingsDataStore(context) }

                    NumericPointsScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        settingsDataStore = settingsDataStore
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsSlipTopBar(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Points Slip", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                    contentDescription = "Toggle theme"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

@Composable
fun NumericUpDownDisplay(
    label: String,
    currentCount: Int,
    onCountChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onCountChange(currentCount - 1) }) {
                    Text(
                        text = "-",
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = currentCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                IconButton(onClick = { onCountChange(currentCount + 1) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase count",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Composable
fun NumericPointsScreen(
    modifier: Modifier = Modifier,
    settingsDataStore: SettingsDataStore // SettingsDataStore from the same package
) {
    val listItemsData = remember {
        listOf(
            NumericListItemData(1, "Pages Read:", 10),
            NumericListItemData(2, "Videos/Lectures (per minute):", 5),
            NumericListItemData(3, "Passing Theory Checkout (per page):", 3),
            NumericListItemData(4, "Giving Theory Checkout (per page):", 3),
            NumericListItemData(5, "Finding MUs (per word):", 5),
            NumericListItemData(6, "Reviewed Anki Cards (per card):", 1),
            NumericListItemData(7, "New Anki Cards (per card):", 2),
            NumericListItemData(8, "Briefs/Phrases Added (per item):", 3),
            NumericListItemData(9, "Steno Practice (per minute):", 2),
            NumericListItemData(10, "Transcription Practice (per minute audio):", 8),
            NumericListItemData(11, "Shadowing Practice (per minute):", 4),
            NumericListItemData(12, "Dictionary Work (per entry):", 2),
            NumericListItemData(13, "Speed Building Drills (per drill):", 15),
            NumericListItemData(14, "Accuracy Practice (per page):", 7),
            NumericListItemData(15, "Theory Review Session (per hour):", 20),
            NumericListItemData(16, "Attend Study Group (per hour):", 15),
            NumericListItemData(17, "Present Topic in Study Group:", 25),
            NumericListItemData(18, "Completed Assignment:", 30),
            NumericListItemData(19, "Research Topic (per hour):", 12),
            NumericListItemData(20, "CAT Software Practice (per hour):", 10),
            NumericListItemData(21, "Professional Development (per hour):", 18)
        )
    }

    var itemCounts by remember { mutableStateOf(List(listItemsData.size) { 0 }) }
    // val coroutineScope = rememberCoroutineScope() // Not explicitly used here now for saving

    LaunchedEffect(Unit) {
        val loadedCounts = settingsDataStore.loadInitialItemCounts(listItemsData.size)
        if (loadedCounts.isNotEmpty() && loadedCounts.size == listItemsData.size) {
            itemCounts = loadedCounts
        } else if (loadedCounts.isEmpty() && listItemsData.isNotEmpty()) {
            itemCounts = List(listItemsData.size) { 0 }
        }
    }

    LaunchedEffect(itemCounts) {
        settingsDataStore.saveItemCounts(itemCounts)
    }

    val totalSum by remember(itemCounts, listItemsData) {
        derivedStateOf {
            var sum = 0
            listItemsData.forEachIndexed { index, itemData ->
                if (index < itemCounts.size) {
                    sum += itemCounts[index] * itemData.pointsPerUnit
                }
            }
            sum
        }
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            itemsIndexed(listItemsData, key = { _, item -> item.id }) { index, currentItemData ->
                NumericUpDownDisplay(
                    label = "${currentItemData.label} (${currentItemData.pointsPerUnit} pts)",
                    currentCount = itemCounts.getOrElse(index) { 0 },
                    onCountChange = { newCount ->
                        val updatedCount = newCount.coerceAtLeast(0)
                        itemCounts = itemCounts.toMutableList().also {
                            if (index < it.size) {
                                it[index] = updatedCount
                            }
                        }
                    }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Text(
                text = "Total Points: $totalSum",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
