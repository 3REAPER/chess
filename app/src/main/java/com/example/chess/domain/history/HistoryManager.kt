package com.example.chess.domain.history

interface HistoryManager {

    fun writeMove(move: History)

    fun getHistory(): List<History>
}
