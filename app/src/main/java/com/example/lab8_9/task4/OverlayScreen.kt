package com.example.lab8_9.task4

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Analytics {
    var userType: String = "Guest"
}

@Composable
fun OverlayScreen(modifier: Modifier = Modifier) {
    var userType by remember { mutableStateOf("Guest") }
    var counter by remember { mutableIntStateOf(0) }
    var triggerRecompositionCount by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf(listOf<String>()) }

    fun addLog(text: String) {
        Log.d("Task4SideEffect", text)
        logs = logs + text
    }

    SideEffect {
        counter++
        Analytics.userType = userType
        val msg = "SideEffect ishladi (#$counter): Analytics.userType = $userType"
        Log.d("Task4SideEffect", msg)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "4-topshiriq: SideEffect va analytics",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Compose state'ni tashqi obyektga (Analytics) xavfsiz uzatish uchun SideEffect ishlatiladi.",
            style = MaterialTheme.typography.bodyMedium
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Joriy userType: $userType", fontWeight = FontWeight.Bold)
                Text("Analytics.userType: ${Analytics.userType}")
                Text("SideEffect ishlash soni (counter): $counter")
                Text("Recomposition trigger soni: $triggerRecompositionCount")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { userType = if (userType == "Guest") "Admin" else "Guest" },
                modifier = Modifier.weight(1f)
            ) {
                Text("UserType'ni o'zgartirish")
            }

            Button(
                onClick = { triggerRecompositionCount++ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Faqat Recompose qilish")
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Kuzatish va Tushuntirish:", fontWeight = FontWeight.Bold)
                Text("• Holat (userType) o'zgarmasa ham, 'Faqat Recompose qilish' tugmasini bosganda SideEffect yana ishlaydi va counter oshadi. Sababi: SideEffect Compose recomposition muvaffaqiyatli yakunlangach har safar chaqiriladi.", fontSize = 13.sp)
                Text("• Ma'ruzadagi attempt modeli: SideEffect Compose state'ni Compose dunyosidan tashqi non-Compose dunyoga (analytics, tracking) xavfsiz tarzda faqat recomposition muvaffaqiyatli o'tgandagina sync qilish uchun ishlatilishini anglatadi.", fontSize = 13.sp)
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
                    Text("Hozircha loglar yo'q.", color = Color.Gray, fontSize = 12.sp)
                }
                logs.forEach { log ->
                    Text(log, fontFamily = FontFamily.Monospace, color = Color(0xFF80D8FF), fontSize = 12.sp)
                }
            }
        }
    }
}
