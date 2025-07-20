package com.DelphiGamerz.PointsSlip

import android.os.Build // Still potentially useful for other SDK checks
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
// Removed RenderEffect, Shader, asComposeRenderEffect, graphicsLayer imports as they are no longer used for blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DelphiGamerz.PointsSlip.ui.theme.PointsSlipTheme

// Modifier Extension for Glassmorphism (No Blur Version) - THIS IS THE ONE WE KEEP
@Composable
fun Modifier.glassmorphism(
    cornerRadius: Dp,
    glassColor: Color,
    borderColor: Color,
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(elevation = shadowElevation, shape = shape)
        .clip(shape)
        .background(glassColor)
        .border(
            width = borderWidth,
            color = borderColor,
            shape = shape
        )
}


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
            // Default to dark theme for the dark green theme
            var isDarkTheme by remember { mutableStateOf(true) }

            PointsSlipTheme(
                darkTheme = isDarkTheme,
                dynamicColor = false // Crucial for forcing your green theme
            ) {
                // This gradientBrush provides the primary background.
                // Let's ensure its colors are opaque and visible for the dark theme.
                val gradientBrush = if (isDarkTheme) {
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // Dark Green Surface
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f), // Dark Green SurfaceVariant
                            MaterialTheme.colorScheme.background.copy(alpha = 0.9f)  // Dark Green Background
                        )
                    )
                } else { // Light theme gradient
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                        )
                    )
                }

                // Root Box - THIS IS WHAT DRAWS THE MAIN BACKGROUND
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush) // If this brush is transparent, screen will be white
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent, // Correct, so Box provides background
                        topBar = {
                            PointsSlipTopBar(
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { isDarkTheme = !isDarkTheme },
                                modifier = Modifier.glassmorphism(
                                    cornerRadius = 0.dp,
                                    // These glass colors should be semi-transparent versions of your theme colors
                                    glassColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f),
                                    borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    shadowElevation = 6.dp
                                )
                            )
                        }
                    ) { innerPadding ->
                        val context = LocalContext.current
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
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .glassmorphism( // Calls the version in this file
                cornerRadius = 12.dp,
                glassColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                shadowElevation = 4.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onCountChange(currentCount - 1) }) {
                    Text(
                        text = "-",
                        style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Normal),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = currentCount.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { onCountChange(currentCount + 1) }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase count",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NumericPointsScreen(
    modifier: Modifier = Modifier,
    settingsDataStore: SettingsDataStore
) {
    val listItemsData = remember {
        listOf(
            NumericListItemData(1, "Pages Read:", 10),
            NumericListItemData(2, "Videos/Live or Recorded Lectures/Teacher Instruction:", 5),
            NumericListItemData(3, "Passing a Theory Checkout:", 3),
            NumericListItemData(4, "Giving a Theory Checkout:", 3),
            NumericListItemData(5, "Finding MUs:", 5),
            NumericListItemData(6, "Giving a Checkout on a Demo:", 3),
            NumericListItemData(7, "Each Definition, Derivation, Idiom, or Synonym:", 3),
            NumericListItemData(8, "Giving/Receiving Word Clearing:", 150),
            NumericListItemData(9, "Theory Coaching - Student and Coach:", 5),
            NumericListItemData(10, "Any drill that takes 15 minutes or less:", 40),
            NumericListItemData(11, "Verbatim Learning:", 10),
            NumericListItemData(12, "Any practical over 15 mins:", 150),
            NumericListItemData(13, "Finishing a practical between 15 mins and an hour:", 100),
            NumericListItemData(14, "Finishing a practical over an hour:", 500),
            NumericListItemData(15, "Checksheet Requirement Demo:", 5),
            NumericListItemData(16, "Self-Originated Demo:", 3),
            NumericListItemData(17, "Clay Demo:", 50),
            NumericListItemData(18, "Essays, Charts, Diagrams:", 10),
            NumericListItemData(19, "Course Completion:", 2000),
            NumericListItemData(20, "Course Completion Bonus (For each day ahead of target):", 2000),
            NumericListItemData(21, "Points for each day you are overdue on a course:", -200)
        )
    }

    var itemCounts by remember { mutableStateOf(List(listItemsData.size) { 0 }) }

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
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .glassmorphism( // Calls the version in this file
                    cornerRadius = 12.dp,
                    glassColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                    shadowElevation = 4.dp
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    itemCounts = List(listItemsData.size) { 0 }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset Counts",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Reset", style = MaterialTheme.typography.labelLarge)
            }

            Text(
                text = "Total: $totalSum",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.End
            )
        }
    }
}
