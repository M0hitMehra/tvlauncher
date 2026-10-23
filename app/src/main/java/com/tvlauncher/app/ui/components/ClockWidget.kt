package com.tvlauncher.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            time = timeFormat.format(now)
            date = dateFormat.format(now)
            delay(1000)
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = time,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            lineHeight = 60.sp
        )
        Text(
            text = date,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
