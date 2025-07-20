package com.DelphiGamerz.PointsSlip.ui.theme // Ensure this package matches your project structure

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes // Correct import for Material 3 Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),  // Default Material 3 uses 4.dp, but 8.dp is common
    large = RoundedCornerShape(16.dp)   // Default Material 3 uses 0.dp, but 12.dp or 16.dp is common
)

