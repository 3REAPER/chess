package com.example.chess.domain

import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook
import com.example.chess.domain.history.Castling
import com.example.chess.domain.history.HistoryManagerImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameMapTest {

    @Test
    fun whiteKingSideCastling() {
        val game = newGame()
        game.play("e2e4 e7e5 g1f3 b8c6 f1c4 f8c5")

        assertTrue("g1" in game.legalMoves("e1"))
        game.move("e1", "g1")

        assertTrue(game.at("g1") is King)
        assertTrue(game.at("f1") is Rook)
        assertNull(game.at("e1"))
        assertNull(game.at("h1"))
        val last = game.historyManager.getHistory().last()
        assertEquals(Castling.KingSide, last.castling)
        assertEquals("O-O", last.notation())
    }

    @Test
    fun blackQueenSideCastling() {
        val game = newGame()
        game.play("a2a3 d7d6 a3a4 c8e6 a4a5 b8c6 h2h3 d8d7 h3h4")

        assertTrue("c8" in game.legalMoves("e8"))
        game.move("e8", "c8")

        assertTrue(game.at("c8") is King)
        assertTrue(game.at("d8") is Rook)
        assertNull(game.at("a8"))
        assertNull(game.at("e8"))
        assertEquals("O-O-O", game.historyManager.getHistory().last().notation())
    }

    @Test
    fun noCastlingAfterKingMoved() {
        val game = newGame()
        game.play("e2e4 e7e5 g1f3 b8c6 f1c4 f8c5 e1e2 g8f6 e2e1 f6g8")

        assertFalse("g1" in game.legalMoves("e1"))
    }

    @Test
    fun noCastlingAfterRookMoved() {
        val game = newGame()
        game.play("e2e4 e7e5 g1f3 b8c6 f1c4 f8c5 h1g1 g8f6 g1h1 f6g8")

        assertFalse("g1" in game.legalMoves("e1"))
    }

    @Test
    fun noCastlingWhenPiecesBetween() {
        val game = newGame()
        assertFalse("g1" in game.legalMoves("e1"))
        assertFalse("c1" in game.legalMoves("e1"))
    }

    @Test
    fun noCastlingThroughAttackedSquare() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Rook(pos("h1"), FigureColor.White),
            Rook(pos("f8"), FigureColor.Black),
            King(pos("a8"), FigureColor.Black, false)
        )
        assertFalse("g1" in game.legalMoves("e1"))
    }

    @Test
    fun noCastlingWhenInCheck() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Rook(pos("h1"), FigureColor.White),
            Rook(pos("e8"), FigureColor.Black),
            King(pos("a8"), FigureColor.Black, false)
        )
        assertFalse("g1" in game.legalMoves("e1"))
    }

    @Test
    fun queenSideCastlingAllowedWhenOnlyB1Attacked() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Rook(pos("a1"), FigureColor.White),
            Rook(pos("b8"), FigureColor.Black),
            King(pos("h8"), FigureColor.Black, false)
        )
        assertTrue("c1" in game.legalMoves("e1"))
    }

    @Test
    fun whiteEnPassant() {
        val game = newGame()
        game.play("e2e4 a7a6 e4e5 d7d5")

        assertTrue("d6" in game.legalMoves("e5"))
        game.move("e5", "d6")

        assertTrue(game.at("d6") is Pawn)
        assertNull(game.at("d5"))
        assertNull(game.at("e5"))
        val last = game.historyManager.getHistory().last()
        assertTrue(last.isEnPassant)
        assertTrue(last.capturedFigure is Pawn)
        assertEquals("exd6", last.notation())
    }

    @Test
    fun blackEnPassant() {
        val game = newGame()
        game.play("a2a3 e7e5 a3a4 e5e4 d2d4")

        assertTrue("d3" in game.legalMoves("e4"))
        game.move("e4", "d3")

        assertNull(game.at("d4"))
        assertEquals(FigureColor.Black, game.at("d3")?.color)
    }

    @Test
    fun enPassantOnlyRightAfterDoubleStep() {
        val game = newGame()
        game.play("e2e4 a7a6 e4e5 d7d5 h2h3 h7h6")

        assertFalse("d6" in game.legalMoves("e5"))
    }

    @Test
    fun noEnPassantAfterTwoSingleSteps() {
        val game = newGame()
        game.play("e2e4 d7d6 e4e5 d6d5")

        assertFalse("d6" in game.legalMoves("e5"))
    }

    @Test
    fun enPassantForbiddenWhenItOpensKing() {
        val game = board(
            King(pos("a5"), FigureColor.White, false),
            Pawn(pos("b5"), FigureColor.White),
            Pawn(pos("c7"), FigureColor.Black),
            Rook(pos("h5"), FigureColor.Black),
            King(pos("h8"), FigureColor.Black, false)
        )
        game.move("c7", "c5")

        val moves = game.legalMoves("b5")
        assertTrue("b6" in moves)
        assertFalse("c6" in moves)
    }

    @Test
    fun enPassantSavesFromCheckSoItIsNotMate() {
        val game = board(
            King(pos("a1"), FigureColor.White, false),
            Pawn(pos("g2"), FigureColor.White),
            Queen(pos("e7"), FigureColor.White),
            Knight(pos("e5"), FigureColor.White),
            Rook(pos("a6"), FigureColor.White),
            King(pos("h5"), FigureColor.Black, false),
            Pawn(pos("f4"), FigureColor.Black)
        )
        game.move("g2", "g4")

        val last = game.historyManager.getHistory().last()
        assertTrue(last.isCheck)
        assertFalse(last.isCheckMate)
        assertFalse(game.isCheckMateBlack)
        assertEquals(setOf("g3"), game.legalMoves("f4"))
    }

    @Test
    fun sameNetWithoutEnPassantIsMate() {
        val game = board(
            King(pos("a1"), FigureColor.White, false),
            Pawn(pos("g2"), FigureColor.White),
            Queen(pos("e7"), FigureColor.White),
            Knight(pos("e5"), FigureColor.White),
            Rook(pos("a6"), FigureColor.White),
            King(pos("h5"), FigureColor.Black, false)
        )
        game.move("g2", "g4")

        assertTrue(game.isCheckMateBlack)
        assertEquals("g4#", game.historyManager.getHistory().last().notation())
    }

    @Test
    fun notationUsesCorrectSquares() {
        val game = newGame()
        game.play("e2e4 e7e5 g1f3")

        val notations = game.historyManager.getHistory().map { it.notation() }
        assertEquals(listOf("e4", "e5", "Nf3"), notations)
    }

    @Test
    fun foolsMateIsRecorded() {
        val game = newGame()
        game.play("f2f3 e7e5 g2g4 d8h4")

        assertTrue(game.isCheckMateWhite)
        assertEquals("Qh4#", game.historyManager.getHistory().last().notation())
    }

    @Test
    fun checkmateResultNamesWinner() {
        val game = newGame()
        game.play("f2f3 e7e5 g2g4 d8h4")

        assertEquals(GameResult.Checkmate(winner = FigureColor.Black), game.result)
    }

    @Test
    fun promotionMoveIsDetected() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Pawn(pos("a7"), FigureColor.White),
            King(pos("h6"), FigureColor.Black, false)
        )
        game.selectFigure(pos("a7"))
        assertTrue(game.isPromotionMove(pos("a8")))
        assertFalse(game.isPromotionMove(pos("b8")))

        val start = newGame()
        start.selectFigure(pos("e2"))
        assertFalse(start.isPromotionMove(pos("e4")))
    }

    @Test
    fun pawnPromotesToQueenByDefault() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Pawn(pos("a7"), FigureColor.White),
            King(pos("h6"), FigureColor.Black, false)
        )
        game.move("a7", "a8")

        val queen = game.at("a8")
        assertTrue(queen is Queen)
        assertEquals(FigureColor.White, queen?.color)
        val last = game.historyManager.getHistory().last()
        assertTrue(last.figure is Pawn)
        assertTrue(last.promotedTo is Queen)
        assertEquals("a8=Q", last.notation())
    }

    @Test
    fun pawnCanUnderpromoteToKnight() {
        val game = board(
            King(pos("e1"), FigureColor.White, false),
            Pawn(pos("a7"), FigureColor.White),
            King(pos("h6"), FigureColor.Black, false)
        )
        game.selectFigure(pos("a7"))
        assertTrue(game.moveFigure(pos("a8"), PromotionPiece.Knight))

        assertTrue(game.at("a8") is Knight)
        assertEquals("a8=N", game.historyManager.getHistory().last().notation())
    }

    @Test
    fun promotionWithCaptureGivesCheck() {
        val game = board(
            King(pos("h1"), FigureColor.White, false),
            Pawn(pos("b7"), FigureColor.White),
            Rook(pos("a8"), FigureColor.Black),
            King(pos("e8"), FigureColor.Black, false)
        )
        game.move("b7", "a8")

        val last = game.historyManager.getHistory().last()
        assertTrue(last.isCheck)
        assertEquals("bxa8=Q+", last.notation())
        assertEquals("bxa8=", last.notation(withPromotionLetter = false, withCheckSuffix = false))
    }

    @Test
    fun blackPawnPromotesOnFirstRank() {
        val game = board(
            King(pos("d5"), FigureColor.White, false),
            Pawn(pos("h2"), FigureColor.Black),
            King(pos("a8"), FigureColor.Black, false)
        )
        game.selectFigure(pos("h2"))
        assertTrue(game.isPromotionMove(pos("h1")))
        assertTrue(game.moveFigure(pos("h1"), PromotionPiece.Rook))

        assertTrue(game.at("h1") is Rook)
        assertEquals(FigureColor.Black, game.at("h1")?.color)
        assertEquals("h1=R", game.historyManager.getHistory().last().notation())
    }

    @Test
    fun stalemateIsDetected() {
        val game = board(
            King(pos("h1"), FigureColor.White, false),
            Queen(pos("b5"), FigureColor.White),
            King(pos("a8"), FigureColor.Black, false)
        )
        game.move("b5", "b6")

        assertTrue(game.isStalemate)
        assertFalse(game.isCheckMate)
        assertEquals(GameResult.Stalemate(sideWithoutMoves = FigureColor.Black), game.result)
        assertEquals("Qb6", game.historyManager.getHistory().last().notation())

        val reloaded = GameMap.createMap(game.map, game.historyManager)
        assertEquals(GameResult.Stalemate(sideWithoutMoves = FigureColor.Black), reloaded.result)
    }

    @Test
    fun noStalemateWhenAnotherPieceCanMove() {
        val game = board(
            King(pos("h1"), FigureColor.White, false),
            Queen(pos("b5"), FigureColor.White),
            King(pos("a8"), FigureColor.Black, false),
            Pawn(pos("h7"), FigureColor.Black)
        )
        game.move("b5", "b6")

        assertFalse(game.isStalemate)
        assertNull(game.result)
    }

    @Test
    fun pinnedPawnCannotBreakStalemate() {
        val game = newGame()
        game.play(
            "e2e3 a7a5 d1h5 a8a6 h5a5 h7h5 h2h4 a6h6 a5c7 f7f6 " +
                    "c7d7 e8f7 d7b7 d8d3 b7b8 d3h7 b8c8 f7g6 c8e6"
        )

        assertEquals(GameResult.Stalemate(sideWithoutMoves = FigureColor.Black), game.result)
    }

    @Test
    fun noStalemateInInitialPosition() {
        val game = GameMap.createMap(newGame().map, HistoryManagerImpl())
        assertNull(game.result)
    }

    @Test
    fun illegalMoveIsRejected() {
        val game = newGame()
        game.selectFigure(pos("e2"))
        assertFalse(game.moveFigure(pos("e5")))
        assertTrue(game.at("e2") is Pawn)
        assertTrue(game.historyManager.getHistory().isEmpty())
    }

    private fun newGame() = GameMap().apply { init() }

    private fun board(vararg figures: Figure): GameMap {
        val map = Array(GameMap.ROWS_COUNT) {
            Array(GameMap.COLUMN_COUNT) { Square(null, false, false) }
        }
        figures.forEach {
            map[it.position.row - 1][it.position.column - 1] = Square(it, false, false)
        }
        return GameMap.createMap(map, HistoryManagerImpl())
    }

    private fun pos(square: String) = Position(square[1].digitToInt(), square[0] - 'a' + 1)

    private fun GameMap.at(square: String): Figure? {
        val p = pos(square)
        return map[p.row - 1][p.column - 1].figure
    }

    private fun GameMap.legalMoves(from: String): Set<String> {
        selectFigure(pos(from))
        val result = mutableSetOf<String>()
        map.forEachIndexed { row, squares ->
            squares.forEachIndexed { column, square ->
                if (square.isAttack) result += Position(row + 1, column + 1).toString()
            }
        }
        return result
    }

    private fun GameMap.move(from: String, to: String) {
        selectFigure(pos(from))
        assertTrue("Ход $from-$to должен быть разрешён", moveFigure(pos(to)))
    }

    private fun GameMap.play(moves: String) {
        moves.split(" ").forEach { move(it.substring(0, 2), it.substring(2, 4)) }
    }
}
