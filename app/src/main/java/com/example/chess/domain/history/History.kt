package com.example.chess.domain.history

import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.Position
import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook

data class History(
    val figure: Figure,
    val from: Position,
    val to: Position,
    val capturedFigure: Figure?,
    val isCheck: Boolean = false,
    val isCheckMate: Boolean = false,
    val castling: Castling? = null,
    val isEnPassant: Boolean = false,
    val promotedTo: Figure? = null
) {
    val color: FigureColor
        get() = figure.color

    val isCapture: Boolean
        get() = capturedFigure != null

    val checkSuffix: String
        get() = when {
            isCheckMate -> "#"
            isCheck -> "+"
            else -> ""
        }

    fun notation(
        withPieceLetter: Boolean = true,
        withPromotionLetter: Boolean = true,
        withCheckSuffix: Boolean = true
    ): String {
        val suffix = if (withCheckSuffix) checkSuffix else ""

        castling?.let { return it.notation + suffix }

        val piece = if (withPieceLetter) figure.getName() else ""
        val capture = when {
            !isCapture -> ""
            figure is Pawn -> "${from.file}x"
            else -> "x"
        }
        val promotion = promotedTo?.let {
            "=" + if (withPromotionLetter) it.getName() else ""
        }.orEmpty()

        return "$piece$capture$to$promotion$suffix"
    }

    override fun toString(): String = notation()

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
