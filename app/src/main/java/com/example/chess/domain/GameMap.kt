package com.example.chess.domain

import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook
import com.example.chess.domain.history.HistoryManager
import com.example.chess.domain.history.HistoryManagerImpl
import kotlin.math.abs
import kotlin.math.sign

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
    var historyManager: HistoryManager = HistoryManagerImpl()
        private set

    var isCheckMateWhite = false
        private set

    var isCheckMateBlack = false
        private set

    fun init() {
        isCheckMateWhite = false
        isCheckMateBlack = false

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

        map[0][4] = Square(King(Position(1, 5), FigureColor.White, false), false, false)
        map[7][4] = Square(King(Position(8, 5), FigureColor.Black, false), false, false)
    }

    fun selectFigure(position: Position) {
        require(map[getRow(position)][getColumn(position)].figure != null)

        clearHighlights()

        map[getRow(position)][getColumn(position)].isSelected = true

        getLegalMoves(position).forEach {
            map[getRow(it)][getColumn(it)].isAttack = true
        }
    }

    fun moveFigure(to: Position) {
        var figure: Figure? = null
        map.forEach {
            it.forEach {
                if (it.isSelected) {
                    figure = it.figure
                    return@forEach
                }
            }
        }

        val toFigure = map[getRow(to)][getColumn(to)]
        val canMove = toFigure.isAttack

        figure?.position?.takeIf { canMove }?.let {
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

        setCheckIfKingAttackedOrCheckMate()
        clearHighlights()
        if (canMove) {
            figure?.let {
                historyManager.writeMove(it, to, toFigure.figure,isCheck(map, FigureColor.White) || isCheck(map, FigureColor.Black))
            }
        }

    }

    private fun setCheckIfKingAttackedOrCheckMate() {
        val isWhiteInCheck = isCheck(map, FigureColor.White)
        val isBlackInCheck = isCheck(map, FigureColor.Black)

        map.forEach {
            it.forEach { square ->
                val figure = square.figure
                if (figure is King) {
                    figure.isCheck =
                        if (figure.color == FigureColor.White) isWhiteInCheck else isBlackInCheck
                }
            }
        }

        isCheckMateWhite = isWhiteInCheck && !hasLegalMoves(FigureColor.White)
        isCheckMateBlack = isBlackInCheck && !hasLegalMoves(FigureColor.Black)

        if (isCheckMateBlack){
            print("s")
        }
    }

    private fun getLegalMoves(position: Position): List<Position> {
        val figure = map[getRow(position)][getColumn(position)].figure ?: return emptyList()

        val moves = figure.getMoves().filter {
            map[getRow(it)][getColumn(it)].figure == null && isPathClear(position, it)
        }

        val attacks = figure.getAttack().filter {
            val target = map[getRow(it)][getColumn(it)].figure
            target != null && target.color != figure.color && isPathClear(position, it)
        }

        return (moves + attacks).filter {
            !isCheck(simulateMove(position, it), figure.color)
        }
    }

    private fun hasLegalMoves(color: FigureColor): Boolean =
        map.any { row ->
            row.any { square ->
                val figure = square.figure
                figure != null &&
                        figure.color == color &&
                        getLegalMoves(figure.position).isNotEmpty()
            }
        }

    private fun simulateMove(from: Position, to: Position): Array<Array<Square>> {
        val newMap = Array(map.size) { row -> map[row].copyOf() }
        newMap[getRow(to)][getColumn(to)] = newMap[getRow(from)][getColumn(from)]
        newMap[getRow(from)][getColumn(from)] = Square(null, false, false)
        return newMap
    }

    private fun isCheck(desk: Array<Array<Square>> = map, kingColor: FigureColor): Boolean {
        desk.forEach {
            it.forEach { square ->
                square.figure?.let { figure ->
                    figure.getAttack().forEach {
                        val king = desk[getRow(it)][getColumn(it)].figure
                        if (king is King &&
                            king.color == kingColor &&
                            king.color != figure.color &&
                            isPathClear(figure.position, it, desk)
                        ) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    private fun isPathClear(from: Position, to: Position, desk: Array<Array<Square>> = map): Boolean {
        val deltaRow = to.row - from.row
        val deltaColumn = to.column - from.column

        val isStraight = deltaRow == 0 || deltaColumn == 0
        val isDiagonal = abs(deltaRow) == abs(deltaColumn)
        if (!isStraight && !isDiagonal) return true

        val stepRow = deltaRow.sign
        val stepColumn = deltaColumn.sign

        var row = from.row + stepRow
        var column = from.column + stepColumn
        while (row != to.row || column != to.column) {
            if (desk[row - 1][column - 1].figure != null) return false
            row += stepRow
            column += stepColumn
        }
        return true
    }

    private fun clearHighlights() {
        map.forEach { squares ->
            squares.forEach { square ->
                square.isSelected = false
                square.isAttack = false
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

        fun createMap(map: Array<Array<Square>>, historyManager: HistoryManager): GameMap {
            val result = GameMap()
            result.map = map
            result.historyManager = historyManager

            result.setCheckIfKingAttackedOrCheckMate()
            return result
        }
    }
}
