package com.example.chess.domain

import androidx.compose.ui.graphics.Color
import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook

class GameMap() {
    var map = Array(ROWS_COUNT) {
        Array(COLUMN_COUNT) {
            Square(
                figure = null,
                isAttack = false,
                isSelected = false
            )
        }
    }
        private set

    fun init() {
        repeat(8) {
            map[1][it] = Square(Pawn(Position(2, it + 1), FigureColor.White), false, false)
        }

        repeat(8) {
            map[6][it] = Square(Pawn(Position(7, it + 1), FigureColor.Black), false, false)
        }

        map[0][0] = Square(Rook(Position(1, 1), FigureColor.White), false, false)
        map[0][7] = Square(Rook(Position(1, 8), FigureColor.White), false, false)
        map[7][0] = Square(Rook(Position(8, 1), FigureColor.Black), false, false)
        map[7][7] = Square(Rook(Position(8, 8), FigureColor.Black), false, false)

        map[0][1] = Square(Knight(Position(1, 2), FigureColor.White), false, false)
        map[0][6] = Square(Knight(Position(1, 7), FigureColor.White), false, false)
        map[7][1] = Square(Knight(Position(8, 2), FigureColor.Black), false, false)
        map[7][6] = Square(Knight(Position(8, 7), FigureColor.Black), false, false)

        map[0][2] = Square(Bishop(Position(1, 3), FigureColor.White), false, false)
        map[0][5] = Square(Bishop(Position(1, 6), FigureColor.White), false, false)
        map[7][2] = Square(Bishop(Position(8, 3), FigureColor.Black), false, false)
        map[7][5] = Square(Bishop(Position(8, 6), FigureColor.Black), false, false)

        map[0][3] = Square(Queen(Position(1, 4), FigureColor.White), false, false)
        map[7][3] = Square(Queen(Position(8, 4), FigureColor.Black), false, false)

        map[0][4] = Square(King(Position(1, 5), FigureColor.White), false, false)
        map[7][4] = Square(King(Position(8, 5), FigureColor.Black), false, false)
    }

    fun selectFigure(position: Position) {
        require(map[getRow(position)][getColumn(position)].figure != null)

        map.forEachIndexed { index, squares ->
            squares.forEachIndexed { index2, square ->
                map[index][index2].isSelected = false
                map[index][index2].isAttack = false
            }
        }

        val square = map[getRow(position)][getColumn(position)]
        square.isSelected = true
        val figure = map[getRow(position)][getColumn(position)].figure!!

        figure.getMoves().forEach {
            if (map[getRow(it)][getColumn(it)].figure == null) {
                map[getRow(it)][getColumn(it)].isAttack = true
            }
        }

        figure.getAttack().forEach {
            if (map[getRow(it)][getColumn(it)].figure != null && map[getRow(it)][getColumn(it)].figure?.color != figure.color){
                map[getRow(it)][getColumn(it)].isAttack = true
            }
        }

    }

    fun moveFigure(to: Position) {
        var position: Position? = null
        map.forEach {
            it.forEach {
                if (it.isSelected){
                    position = it.figure?.position
                    return@forEach
                }
            }
        }

        position?.let {
            val figure = map[getRow(it)][getColumn(it)].figure
            figure?.changePosition(to)
            map[getRow(it)][getColumn(it)] = Square(
                figure = null,
                isAttack = false,
                isSelected = false
            )
            map[getRow(to)][getColumn(to)] = Square(
                figure = figure,
                isAttack = false,
                isSelected = false
            )
        }

        map.forEachIndexed { index, squares ->
            squares.forEachIndexed { index2, square ->
                map[index][index2].isSelected = false
                map[index][index2].isAttack = false
            }
        }

    }

    private fun getRow(position: Position) =
        position.row - 1

    private fun getColumn(position: Position) =
        position.column - 1

    companion object {
        const val ROWS_COUNT = 8
        const val COLUMN_COUNT = 8

        fun createMap(map: Array<Array<Square>>): GameMap{
            val result = GameMap()
            result.map = map

            return result
        }
    }
}