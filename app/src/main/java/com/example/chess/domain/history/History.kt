package com.example.chess.domain.history

import com.example.chess.domain.Figure
import com.example.chess.domain.Position
import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook

data class History(
    val from: Figure,
    val to: Position,
    val toFigure: Figure?,
    val isCheck: Boolean
) {
    override fun toString(): String {
        val isCheckString = if (isCheck){
            "+"
        }else{
            ""
        }

        val isToFigureHas = if (toFigure != null){
            "x"
        }else{
            ""
        }

        return "$isCheckString${from.getName()}$isToFigureHas$to"
    }

    private fun Figure.getName(): String {
        return when (this) {
            is Pawn -> ""
            is Queen -> "Q"
            is King -> "K"
            is Knight -> "N"
            is Bishop -> "B"
            is Rook -> "R"
            else -> "X"
        }
    }
}