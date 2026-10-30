package com.example.lab8_9.task2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Task2Screen(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var clickCount by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "2-topshiriq: Snackbar tugmasi & rememberCoroutineScope",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tugma bosilganda callback ichidan coroutine ishga tushirilib Snackbar ko'rsatiladi.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = {
                    clickCount++
                    scope.launch {
                        // showSnackbar suspend funksiya bo'lib, coroutine scope talab qiladi
                        snackbarHostState.showSnackbar(
                            message = "Salom Snackbar! Bosishlar soni: $clickCount",
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            ) {
                Text("Snackbar ko'rsatish")
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nega LaunchedEffect bu yerda ishlamaydi?", fontWeight = FontWeight.Bold)
                    Text(
                        "• LaunchedEffect faqat composition (ekranga birinchi chiqish yoki kalit o'zgarishi) paytida ishga tushadi.\n" +
                                "• Tugma bosilishi (click listener) esa composition emas, foydalanuvchi hodisasi (event callback) hisoblanadi. Event ichida coroutine ishga tushirish uchun rememberCoroutineScope ishlatiladi."
                    )
                    Text("Ikkinchi marta bosilganda nima bo'ladi?", fontWeight = FontWeight.Bold)
                    Text(
                        "• Agar birinchi Snackbar hali ko'rinib turganda ikkinchi marta bossangiz, yangi snackbar navbatga turadi (enqueue bo'ladi) yoki joriy snackbar tugagach navbatdagi ko'rsatiladi."
                    )
                }
            }
        }
    }
}
