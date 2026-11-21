package com.example.chess.domain

import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook
import com.example.chess.domain.history.Castling
import com.example.chess.domain.history.History
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

    val isCheckMate: Boolean
        get() = isCheckMateWhite || isCheckMateBlack

    var stalemateSide: FigureColor? = null
        private set

    val isStalemate: Boolean
        get() = stalemateSide != null

    val result: GameResult?
        get() = when {
            isCheckMateWhite -> GameResult.Checkmate(winner = FigureColor.Black)
            isCheckMateBlack -> GameResult.Checkmate(winner = FigureColor.White)
            else -> stalemateSide?.let { GameResult.Stalemate(sideWithoutMoves = it) }
        }

    fun init() {
        isCheckMateWhite = false
        isCheckMateBlack = false
        stalemateSide = null

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

    fun isPromotionMove(to: Position): Boolean {
        val from = findSelectedPosition() ?: return false
        val figure = figureAt(from)
        return figure is Pawn &&
                map[getRow(to)][getColumn(to)].isAttack &&
                to.row == promotionRow(figure.color)
    }

    fun moveFigure(to: Position, promotion: PromotionPiece = PromotionPiece.Queen): Boolean {
        val from = findSelectedPosition()
        val canMove = map[getRow(to)][getColumn(to)].isAttack
        val figure = from?.let { figureAt(it) }

        if (from == null || figure == null || !canMove) {
            clearHighlights()
            return false
        }

        var capturedFigure = figureAt(to)
        var castling: Castling? = null
        var isEnPassant = false

        if (figure is Pawn && from.column != to.column && capturedFigure == null) {
            val capturedPosition = Position(from.row, to.column)
            capturedFigure = figureAt(capturedPosition)
            setFigure(capturedPosition, null)
            isEnPassant = true
        }

        if (figure is King && abs(to.column - from.column) == 2) {
            castling = if (to.column > from.column) Castling.KingSide else Castling.QueenSide
            val rookFrom = rookStartPosition(from.row, castling)
            val rookTo = rookCastlingPosition(from.row, castling)
            val rook = figureAt(rookFrom)
            rook?.changePosition(rookTo)
            rook?.hasMoved = true
            setFigure(rookFrom, null)
            setFigure(rookTo, rook)
        }

        setFigure(from, null)
        figure.changePosition(to)
        figure.hasMoved = true
        setFigure(to, figure)

        var promotedTo: Figure? = null
        if (figure is Pawn && to.row == promotionRow(figure.color)) {
            promotedTo = promotion.create(to, figure.color).also { it.hasMoved = true }
            setFigure(to, promotedTo)
        }

        clearHighlights()

        val move = History(
            figure = figure,
            from = from,
            to = to,
            capturedFigure = capturedFigure,
            castling = castling,
            isEnPassant = isEnPassant,
            promotedTo = promotedTo
        )

        setCheckIfKingAttackedOrCheckMate(lastMove = move)

        val opponent = figure.color.invert()
        historyManager.writeMove(
            move.copy(
                isCheck = isCheck(map, opponent),
                isCheckMate = if (opponent == FigureColor.White) isCheckMateWhite else isCheckMateBlack
            )
        )
        return true
    }

    private fun setCheckIfKingAttackedOrCheckMate(lastMove: History? = lastMove()) {
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

        isCheckMateWhite = isWhiteInCheck && !hasLegalMoves(FigureColor.White, lastMove)
        isCheckMateBlack = isBlackInCheck && !hasLegalMoves(FigureColor.Black, lastMove)

        val sideToMove = lastMove?.color?.invert() ?: FigureColor.White
        val isSideToMoveInCheck =
            if (sideToMove == FigureColor.White) isWhiteInCheck else isBlackInCheck
        stalemateSide = if (!isSideToMoveInCheck && !hasLegalMoves(sideToMove, lastMove)) {
            sideToMove
        } else {
            null
        }
    }

    private fun getLegalMoves(position: Position, lastMove: History? = lastMove()): List<Position> {
        val figure = figureAt(position) ?: return emptyList()

        val moves = figure.getMoves().filter {
            map[getRow(it)][getColumn(it)].figure == null && isPathClear(position, it)
        }

        val attacks = figure.getAttack().filter {
            val target = map[getRow(it)][getColumn(it)].figure
            target != null && target.color != figure.color && isPathClear(position, it)
        }

        val specialMoves = when (figure) {
            is King -> getCastlingMoves(figure)
            is Pawn -> getEnPassantMoves(figure, lastMove)
            else -> emptyList()
        }

        return (moves + attacks + specialMoves).filter {
            !isCheck(simulateMove(position, it), figure.color)
        }
    }

    private fun getCastlingMoves(king: King): List<Position> {
        if (king.hasMoved || king.position.column != KING_START_COLUMN) return emptyList()

        val row = king.position.row
        val enemy = king.color.invert()
        if (isSquareAttacked(map, king.position, enemy)) return emptyList()

        return Castling.entries.mapNotNull { castling ->
            val rook = figureAt(rookStartPosition(row, castling))
            if (rook !is Rook || rook.color != king.color || rook.hasMoved) return@mapNotNull null

            val (emptyColumns, safeColumns) = when (castling) {
                Castling.KingSide -> (6..7) to (6..7)
                Castling.QueenSide -> (2..4) to (3..4)
            }

            val isPathEmpty = emptyColumns.all { figureAt(Position(row, it)) == null }
            val isPathSafe = safeColumns.none { isSquareAttacked(map, Position(row, it), enemy) }

            if (isPathEmpty && isPathSafe) {
                Position(row, kingCastlingColumn(castling))
            } else {
                null
            }
        }
    }

    private fun getEnPassantMoves(pawn: Pawn, lastMove: History?): List<Position> {
        lastMove ?: return emptyList()
        val movedPawn = lastMove.figure

        val isEnemyDoubleStep = movedPawn is Pawn &&
                movedPawn.color != pawn.color &&
                abs(lastMove.to.row - lastMove.from.row) == 2
        val isStandingNext = lastMove.to.row == pawn.position.row &&
                abs(lastMove.to.column - pawn.position.column) == 1

        if (!isEnemyDoubleStep || !isStandingNext || figureAt(lastMove.to) !== movedPawn) {
            return emptyList()
        }

        val direction = if (pawn.color == FigureColor.White) 1 else -1
        return listOf(Position(pawn.position.row + direction, lastMove.to.column))
    }

    private fun hasLegalMoves(color: FigureColor, lastMove: History?): Boolean =
        map.any { row ->
            row.any { square ->
                val figure = square.figure
                figure != null &&
                        figure.color == color &&
                        getLegalMoves(figure.position, lastMove).isNotEmpty()
            }
        }

    private fun simulateMove(from: Position, to: Position): Array<Array<Square>> {
        val newMap = Array(map.size) { row -> map[row].copyOf() }
        val figure = newMap[getRow(from)][getColumn(from)].figure

        if (figure is Pawn && from.column != to.column && newMap[getRow(to)][getColumn(to)].figure == null) {
            newMap[getRow(from)][getColumn(to)] = Square(null, false, false)
        }

        if (figure is King && abs(to.column - from.column) == 2) {
            val castling = if (to.column > from.column) Castling.KingSide else Castling.QueenSide
            val rookFrom = rookStartPosition(from.row, castling)
            val rookTo = rookCastlingPosition(from.row, castling)
            newMap[getRow(rookTo)][getColumn(rookTo)] = newMap[getRow(rookFrom)][getColumn(rookFrom)]
            newMap[getRow(rookFrom)][getColumn(rookFrom)] = Square(null, false, false)
        }

        newMap[getRow(to)][getColumn(to)] = newMap[getRow(from)][getColumn(from)]
        newMap[getRow(from)][getColumn(from)] = Square(null, false, false)
        return newMap
    }

    private fun isCheck(desk: Array<Array<Square>> = map, kingColor: FigureColor): Boolean {
        desk.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, square ->
                val king = square.figure
                if (king is King && king.color == kingColor) {
                    return isSquareAttacked(desk, Position(rowIndex + 1, columnIndex + 1), kingColor.invert())
                }
            }
        }
        return false
    }

    private fun isSquareAttacked(
        desk: Array<Array<Square>>,
        target: Position,
        attackerColor: FigureColor
    ): Boolean = desk.any { row ->
        row.any { square ->
            val figure = square.figure
            figure != null &&
                    figure.color == attackerColor &&
                    target in figure.getAttack() &&
                    isPathClear(figure.position, target, desk)
        }
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

    private fun findSelectedPosition(): Position? {
        map.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, square ->
                if (square.isSelected && square.figure != null) {
                    return Position(rowIndex + 1, columnIndex + 1)
                }
            }
        }
        return null
    }

    private fun lastMove(): History? = historyManager.getHistory().lastOrNull()

    private fun figureAt(position: Position): Figure? =
        map[getRow(position)][getColumn(position)].figure

    private fun setFigure(position: Position, figure: Figure?) {
        map[getRow(position)][getColumn(position)] = Square(
            figure = figure,
            isAttack = false,
            isSelected = false
        )
    }

    private fun rookStartPosition(row: Int, castling: Castling) =
        Position(row, if (castling == Castling.KingSide) 8 else 1)

    private fun rookCastlingPosition(row: Int, castling: Castling) =
        Position(row, if (castling == Castling.KingSide) 6 else 4)

    private fun kingCastlingColumn(castling: Castling) =
        if (castling == Castling.KingSide) 7 else 3

    private fun promotionRow(color: FigureColor) =
        if (color == FigureColor.White) ROWS_COUNT else 1

    private fun getRow(position: Position) =
        position.row - 1

    private fun getColumn(position: Position) =
        position.column - 1

    companion object {
        const val ROWS_COUNT = 8
        const val COLUMN_COUNT = 8
        private const val KING_START_COLUMN = 5

        fun createMap(map: Array<Array<Square>>, historyManager: HistoryManager): GameMap {
            val result = GameMap()
            result.map = map
            result.historyManager = historyManager

            result.setCheckIfKingAttackedOrCheckMate()
            return result
        }
    }
}
