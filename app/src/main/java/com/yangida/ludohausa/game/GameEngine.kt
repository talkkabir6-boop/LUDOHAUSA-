package com.yangida.ludohausa.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Token(val id: Int, val playerId: Int, var position: Int = -1, var isInHome: Boolean = true, var isFinished: Boolean = false) {
    fun reset() { position = -1; isInHome = true; isFinished = false }
}

data class Player(val id: Int, val name: String, val color: Long, val isComputer: Boolean, val tokens: MutableList<Token> = mutableListOf()) {
    init { repeat(4) { index -> tokens.add(Token(id = index, playerId = id)) } }
    fun reset() { tokens.forEach { it.reset() } }
    fun hasWon(): Boolean = tokens.all { it.isFinished }
    fun getFinishedTokens(): List<Token> = tokens.filter { it.isFinished }
}

class Board {
    companion object {
        const val FINAL_POSITION = 56
        val SAFE_POSITIONS = listOf(0, 8, 13, 21, 26, 34, 39, 47)
        val STARTING_POSITIONS = mapOf(0 to 0, 1 to 13, 2 to 26, 3 to 39)
        val HOME_LANE_ENTRY = mapOf(0 to 50, 1 to 11, 2 to 24, 3 to 37)
    }
    fun isSafePosition(position: Int): Boolean = SAFE_POSITIONS.contains(position)
    fun getStartPosition(playerId: Int): Int = STARTING_POSITIONS[playerId] ?: 0
    fun getHomeLaneEntry(playerId: Int): Int = HOME_LANE_ENTRY[playerId] ?: 0
    fun calculateNewPosition(currentPosition: Int, diceValue: Int, playerId: Int): Int {
        if (currentPosition == -1) return getStartPosition(playerId)
        val homeLaneEntry = getHomeLaneEntry(playerId)
        if (currentPosition <= homeLaneEntry && currentPosition + diceValue > homeLaneEntry) {
            return 51 + ((currentPosition + diceValue) - homeLaneEntry)
        }
        val newPosition = currentPosition + diceValue
        if (newPosition > FINAL_POSITION) return currentPosition
        return newPosition
    }
}

class GameEngine {
    private val board = Board()
    private var diceValue: Int = 1
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players
    private val _currentPlayerIndex = MutableStateFlow(0)
    val currentPlayerIndex: StateFlow<Int> = _currentPlayerIndex
    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message
    private val _highlightedTokens = MutableStateFlow<List<Token>>(emptyList())
    val highlightedTokens: StateFlow<List<Token>> = _highlightedTokens
    private val _winner = MutableStateFlow<Player?>(null)
    val winner: StateFlow<Player?> = _winner

    enum class GameState { IDLE, WAITING_FOR_MOVE, WIN }

    fun initializeGame(players: List<Player>) {
        _players.value = players
        _currentPlayerIndex.value = 0
        _gameState.value = GameState.IDLE
        _message.value = "LOKACIN ${players[0].name}"
        _winner.value = null
    }

    fun rollDice(): Int {
        if (_gameState.value != GameState.IDLE) return diceValue
        diceValue = (1..6).random()
        _gameState.value = GameState.WAITING_FOR_MOVE
        val currentPlayer = _players.value[_currentPlayerIndex.value]
        val movableTokens = currentPlayer.tokens.filter { 
            if (it.isFinished) false 
            else if (it.isInHome) diceValue == 6 
            else board.calculateNewPosition(it.position, diceValue, it.playerId) <= Board.FINAL_POSITION
        }
        _highlightedTokens.value = movableTokens
        if (movableTokens.isEmpty()) {
            _message.value = "BA KA DA MATSI MAI YIWU"
            endTurn()
        } else {
            _message.value = if (diceValue == 6) "MADALLA! ZAƁI ƊAN WASANKA" else "ZAƁI ƊAN WASANKA"
        }
        return diceValue
    }

    fun moveToken(token: Token): Boolean {
        if (_gameState.value != GameState.WAITING_FOR_MOVE) return false
        if (!_highlightedTokens.value.contains(token)) return false
        val currentPlayer = _players.value[_currentPlayerIndex.value]
        if (token.isInHome) {
            token.position = board.getStartPosition(currentPlayer.id)
            token.isInHome = false
            _message.value = "AN FITO DA ƊAN WASA!"
        } else {
            val newPosition = board.calculateNewPosition(token.position, diceValue, currentPlayer.id)
            if (newPosition == Board.FINAL_POSITION) {
                token.position = newPosition
                token.isFinished = true
                if (currentPlayer.hasWon()) {
                    _winner.value = currentPlayer
                    _gameState.value = GameState.WIN
                    _message.value = "TAYA MURNA! ${currentPlayer.name} YA CI WASA!"
                    return true
                }
            } else {
                token.position = newPosition
            }
        }
        _players.value = _players.value.toMutableList()
        if (diceValue != 6) endTurn()
        else { _gameState.value = GameState.IDLE; _highlightedTokens.value = emptyList() }
        return true
    }

    private fun endTurn() {
        _highlightedTokens.value = emptyList()
        val nextIndex = (_currentPlayerIndex.value + 1) % _players.value.size
        _currentPlayerIndex.value = nextIndex
        _gameState.value = GameState.IDLE
        _message.value = "LOKACIN ${_players.value[_currentPlayerIndex.value].name}"
    }

    fun getDiceValue(): Int = diceValue
    fun resetGame() { initializeGame(_players.value) }
}