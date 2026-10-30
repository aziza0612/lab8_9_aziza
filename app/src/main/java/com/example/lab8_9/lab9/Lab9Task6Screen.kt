package com.example.lab8_9.lab9

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
            Log.d("Lab9Task6", "Producer boshlandi: url=$url")
            val result = fakeLoadRepository(url)
            value = UiState.Success(result)
        } catch (e: Exception) {
            value = UiState.Error(e)
        } finally {
            Log.d("Lab9Task6", "Producer finally: url=$url")
        }
    }
}

@Composable
fun Lab9Task6Screen(modifier: Modifier = Modifier) {
    var url by remember { mutableStateOf("image.png") }
    val imageState by loadNetworkImage(url)
    var logs by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Lab 9 - 6-topshiriq: produceState",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
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
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = imageState) {
                    is UiState.Loading -> CircularProgressIndicator()
                    is UiState.Success -> Text(state.data, fontWeight = FontWeight.Bold)
                    is UiState.Error -> Text("Xato: ${state.throwable.localizedMessage}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
