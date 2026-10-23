package com.tvlauncher.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsPanel(
    gridColumns: Int,
    showClock: Boolean,
    onGridColumnsChange: (Int) -> Unit,
    onShowClockChange: (Boolean) -> Unit,
    onOpenHiddenApps: () -> Unit,
    onSystemSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(Color(0xEE1A1A2E))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "⚙ Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grid Columns
            Text(
                text = "Grid Columns",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (3..7).forEach { col ->
                    ColumnButton(
                        label = col.toString(),
                        isSelected = gridColumns == col,
                        onClick = { onGridColumnsChange(col) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Show Clock Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Show Clock", color = Color.White, fontSize = 15.sp)
                Switch(
                    checked = showClock,
                    onCheckedChange = onShowClockChange
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hidden Apps
            FocusableSettingsButton(
                label = "Manage Hidden Apps",
                onClick = onOpenHiddenApps
            )

            Spacer(modifier = Modifier.height(12.dp))

            // System Settings
            FocusableSettingsButton(
                label = "System Settings",
                onClick = onSystemSettings
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ColumnButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> Color(0xFF4FC3F7)
                    isFocused  -> Color(0xFF1E3A5F)
                    else       -> Color(0xFF0D1B2A)
                }
            )
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color(0xFF29B6F6),
                shape = RoundedCornerShape(8.dp)
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if ((event.key == Key.DirectionCenter || event.key == Key.Enter) &&
                    event.type == KeyEventType.KeyUp
                ) {
                    onClick()
                    true
                } else false
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FocusableSettingsButton(
    label: String,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isFocused) Color(0xFF1E3A5F) else Color(0xFF0D1B2A))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color(0xFF29B6F6),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if ((event.key == Key.DirectionCenter || event.key == Key.Enter) &&
                    event.type == KeyEventType.KeyUp
                ) {
                    onClick()
                    true
                } else false
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HiddenAppsPanel(
    hiddenAppPackages: Set<String>,
    hiddenAppLabels: Map<String, String>,
    onUnhide: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(Color(0xEE1A1A2E))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "Hidden Apps",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (hiddenAppPackages.isEmpty()) {
                Text(
                    text = "No hidden apps",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(hiddenAppPackages.toList()) { pkg ->
                        val label = hiddenAppLabels[pkg] ?: pkg
                        HiddenAppRow(label = label, onUnhide = { onUnhide(pkg) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HiddenAppRow(label: String, onUnhide: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) Color(0xFF1E3A5F) else Color(0xFF0D1B2A))
            .border(
                width = if (isFocused) 1.dp else 0.dp,
                color = Color(0xFF29B6F6),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if ((event.key == Key.DirectionCenter || event.key == Key.Enter) &&
                    event.type == KeyEventType.KeyUp
                ) {
                    onUnhide()
                    true
                } else false
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Show",
            color = Color(0xFF4FC3F7),
            fontSize = 12.sp
        )
    }
}
