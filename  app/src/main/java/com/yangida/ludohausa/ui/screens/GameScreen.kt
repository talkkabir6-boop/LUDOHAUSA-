package com.yangida.ludohausa.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.yangida.ludohausa.game.GameEngine
import com.yangida.ludohausa.game.Player
import com.yangida.ludohausa.game.Token
import com.yangida.ludohausa.ui.theme.LudoDarkBlue
import com.yangida.ludohausa.ui.theme.LudoGold

@Composable
fun GameScreen(navController: NavController) {
    val gameEngine = remember { GameEngine() }
    LaunchedEffect(Unit) {
        gameEngine.initializeGame(listOf(
            Player(0, "DAN AMADU", 0xFFE53935, false),
            Player(1, "DAN BABA", 0xFF43A047, true),
            Player(2, "MAI GIDAN RIJIYA", 0xFFFDD835, true),
            Player(3, "NURA EVER", 0xFF1E88E5, true)
        ))
    }
    val players by gameEngine.players.collectAsState()
    val currentPlayerIndex by gameEngine.currentPlayerIndex.collectAsState()
    val gameState by gameEngine.gameState.collectAsState()
    val message by gameEngine.message.collectAsState()
    val highlightedTokens by gameEngine.highlightedTokens.collectAsState()
    val winner by gameEngine.winner.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE3F2FD))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (players.isNotEmpty()) {
                val currentPlayer = players[currentPlayerIndex]
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(currentPlayer.color))) {
                    Text(text = "LOKACIN ${currentPlayer.name}", modifier = Modifier.fillMaxWidth().padding(16.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Box(modifier = Modifier.size(360.dp).background(Color.White, RoundedCornerShape(8.dp))) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val boardSize = size.minDimension
                    val cellSize = boardSize / 15f
                    drawRect(color = Color.White, size = Size(boardSize, boardSize))
                    drawRect(color = Color(0xFFE53935), topLeft = Offset(0f, 0f), size = Size(cellSize * 6f, cellSize * 6f))
                    drawRect(color = Color(0xFF43A047), topLeft = Offset(boardSize - cellSize * 6f, 0f), size = Size(cellSize * 6f, cellSize * 6f))
                    drawRect(color = Color(0xFFFDD835), topLeft = Offset(0f, boardSize - cellSize * 6f), size = Size(cellSize * 6f, cellSize * 6f))
                    drawRect(color = Color(0xFF1E88E5), topLeft = Offset(boardSize - cellSize * 6f, boardSize - cellSize * 6f), size = Size(cellSize * 6f, cellSize * 6f))
                    drawRect(color = Color.Gray, topLeft = Offset(cellSize * 6f, cellSize * 6f), size = Size(cellSize * 3f, cellSize * 3f))
                    
                    players.forEach { player ->
                        player.tokens.forEach { token ->
                            if (!token.isInHome && !token.isFinished) {
                                val path = mutableListOf<Offset>()
                                for (i in 0..5) path.add(Offset((13 - i) * cellSize, 6 * cellSize))
                                for (i in 6..11) path.add(Offset(6 * cellSize, (i - 6) * cellSize))
                                for (i in 12..17) path.add(Offset((i - 12) * cellSize, 8 * cellSize))
                                for (i in 18..23) path.add(Offset(8 * cellSize, (23 - i) * cellSize))
                                val p = if (token.position < path.size) path[token.position] else Offset.Zero
                                val center = Offset(p.x + cellSize / 2, p.y + cellSize / 2)
                                val isHighlighted = highlightedTokens.contains(token)
                                drawCircle(color = if (isHighlighted) Color.White else Color(player.color), radius = cellSize * 0.4f, center = center)
                                if (isHighlighted) drawCircle(color = Color(player.color), radius = cellSize * 0.3f, center = center)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            if (message.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = LudoGold)) {
                    Text(text = message, modifier = Modifier.fillMaxWidth().padding(16.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp).background(Color.White, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text(text = gameEngine.getDiceValue().toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { if (gameState == GameEngine.GameState.IDLE) gameEngine.rollDice() }, enabled = gameState == GameEngine.GameState.IDLE, colors = ButtonDefaults.buttonColors(containerColor = LudoDarkBlue)) {
                    Text("JUYA DICE", color = Color.White)
                }
            }
        }
        if (winner != null) {
            AlertDialog(onDismissRequest = { }, title = { Text("TAYA MURNA!") }, text = { Text("${winner!!.name} YA CI WASA!") }, confirmButton = { Button(onClick = { gameEngine.resetGame() }) { Text("SAKE WASA") } }, dismissButton = { Button(onClick = { navController.popBackStack() }) { Text("KOMAWA") } })
        }
    }
}