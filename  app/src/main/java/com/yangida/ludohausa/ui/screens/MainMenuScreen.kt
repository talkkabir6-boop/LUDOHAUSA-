package com.yangida.ludohausa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yangida.ludohausa.ui.theme.LudoDarkBlue
import com.yangida.ludohausa.ui.theme.LudoGold

@Composable
fun MainMenuScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(LudoDarkBlue, Color(0xFF1976D2))))) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("LUDO HAUSA", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = LudoGold)
            Text("YAN GIDA", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 48.dp))
            MenuButton("FARA WASA") { navController.navigate("select_players") }
            Spacer(Modifier.height(12.dp))
            MenuButton("WASA DA COMPUTER") { navController.navigate("game") }
            Spacer(Modifier.height(12.dp))
            MenuButton("WASAN 'YAN WASA HUƊU") { navController.navigate("game") }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = LudoGold, contentColor = Color.Black)) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}