package com.example.chess.domain.history

import com.example.chess.domain.Figure
import com.example.chess.domain.Position

interface HistoryManager {

    fun writeMove(from: Figure, to: Position, toFigure: Figure?, isCheck: Boolean)

    fun getHistory(): List<History>
}