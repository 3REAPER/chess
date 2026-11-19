package com.example.chess.domain.figure

import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.Position

class King(position: Position, color: FigureColor, var isCheck: Boolean): Figure(position, color) {

    override fun getMoves(): List<Position> {
        val result = mutableListOf<Position>()
        val offsets = listOf(
            1 to 0, -1 to 0, 0 to 1, 0 to -1,
            1 to 1, 1 to -1, -1 to 1, -1 to -1
        )

        for ((dr, dc) in offsets) {
            val row = position.row + dr
            val col = position.column + dc
            if (row in 1..8 && col in 1..8) {
                result.add(Position(row, col))
            }
        }

        return result
    }

    override fun getAttack(): List<Position> = getMoves()
}