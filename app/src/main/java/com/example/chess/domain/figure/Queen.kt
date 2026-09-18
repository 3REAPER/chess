package com.example.chess.domain.figure

import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.Position

class Queen(position: Position, color: FigureColor): Figure(position, color) {

    override fun getMoves(): List<Position> {
        val result = mutableListOf<Position>()
        val directions = listOf(
            1 to 0, -1 to 0, 0 to 1, 0 to -1,
            1 to 1, 1 to -1, -1 to 1, -1 to -1
        )

        for ((dr, dc) in directions) {
            var row = position.row + dr
            var col = position.column + dc
            while (row in 1..8 && col in 1..8) {
                result.add(Position(row, col))
                row += dr
                col += dc
            }
        }

        return result
    }

    override fun getAttack(): List<Position> = getMoves()
}