package com.example.lab8_9.task6

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val throwable: Throwable) : UiState<Nothing>
}

suspend fun fakeLoadRepository(url: String): String {
    delay(1000)
    if (url.endsWith(".png", ignoreCase = true)) {
        return "Rasm yuklandi: $url"
    } else {
        throw IllegalArgumentException("Faqat .png fayllar qo'llab-quvvatlanadi!")
    }
}

@Composable
fun loadNetworkImage(url: String): State<UiState<String>> {
    return produceState<UiState<String>>(initialValue = UiState.Loading, url) {
        try {
            Log.d("Task6ProduceState", "Producer boshlandi: url=$url")
            val result = fakeLoadRepository(url)
            value = UiState.Success(result)
        } catch (e: Exception) {
            value = UiState.Error(e)
        } finally {
            Log.d("Task6ProduceState", "Producer finally: url=$url (Composable chiqib ketdi yoki URL o'zgardi)")
        }
    }
}

@Composable
fun Task6Screen(modifier: Modifier = Modifier) {
    var url by remember { mutableStateOf("image.png") }
    val imageState by loadNetworkImage(url)
    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Task6Screen", text)
        logs = logs + text
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "6-topshiriq: Bonus: produceState",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Suspend manbani (fakeLoadRepository) produceState yordamida Compose State'ga aylantirish.",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { url = "success_image.png" },
                modifier = Modifier.weight(1f)
            ) {
                Text(".png yuklash")
            }
            Button(
                onClick = { url = "document.pdf" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Xato url (.pdf)")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = imageState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                        Text(" Yuklanmoqda...", modifier = Modifier.padding(top = 48.dp))
                    }
                    is UiState.Success -> {
                        Text(state.data, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    is UiState.Error -> {
                        Text("Xatolik: ${state.throwable.localizedMessage}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tekshirish va Kuzatish:", fontWeight = FontWeight.Bold)
                Text("• URL almashtirilganda (masalan .png dan .pdf ga) eski producer coroutine bekor qilinadi (cancelled) va finally bloki chaqirilib, yangisi Loading holatidan boshlanadi.", fontSize = 13.sp)
                Text("• produceState o'xshash qiymatni qaytarganda recomposition'ni oldini oladi.", fontSize = 13.sp)
            }
        }

        Text("Loglar:", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = Color(0xFF1E1E1E)
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (logs.isEmpty()) {
                    Text("Tugmalarni bosib tekshiring...", color = Color.Gray, fontSize = 12.sp)
                }
                logs.forEach { log ->
                    Text(log, fontFamily = FontFamily.Monospace, color = Color(0xFF80D8FF), fontSize = 12.sp)
                }
            }
        }
    }
}
