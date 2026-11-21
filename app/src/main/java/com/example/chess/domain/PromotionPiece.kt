package com.example.chess.domain

import com.example.chess.domain.figure.Bishop as BishopFigure
import com.example.chess.domain.figure.Knight as KnightFigure
import com.example.chess.domain.figure.Queen as QueenFigure
import com.example.chess.domain.figure.Rook as RookFigure

enum class PromotionPiece {
    Queen, Rook, Bishop, Knight;

    fun create(position: Position, color: FigureColor): Figure = when (this) {
        Queen -> QueenFigure(position, color)
        Rook -> RookFigure(position, color)
        Bishop -> BishopFigure(position, color)
        Knight -> KnightFigure(position, color)
    }
}
