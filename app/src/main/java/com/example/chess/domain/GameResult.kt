package com.example.chess.domain

sealed interface GameResult {

    data class Checkmate(val winner: FigureColor) : GameResult

    data class Stalemate(val sideWithoutMoves: FigureColor) : GameResult
}
