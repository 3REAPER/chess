package com.example.chess.domain.figure

import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.Position

class Pawn(position: Position, color: FigureColor) : Figure(position, color) {

    override fun getMoves(): List<Position> {
        val result = mutableListOf<Position>()
        val dx = if (color == FigureColor.White) {
            +1
        } else {
            -1
        }

        if (position.row + dx in 1..8) {
            result.add(Position(position.row + dx, position.column))
        }

        if (position.row == 2 && color == FigureColor.White) {
            result.add(Position(position.row + 2 * dx, position.column))
        } else if (position.row == 7 && color == FigureColor.Black) {
            result.add(Position(position.row + 2 * dx, position.column))
        }

        return result
    }

    override fun getAttack(): List<Position> {
        val result = mutableListOf<Position>()
        val dx = if (color == FigureColor.White) {
            +1
        } else {
            -1
        }

        if (position.row + dx in 1..8) {
            if (position.column + 1 in 1..8) {
                result.add(Position(position.row + dx, position.column + 1))
            }
            if (position.column - 1 in 1..8) {
                result.add(Position(position.row + dx, position.column - 1))
            }
        }

        return result
    }
}