package com.example.chess.presentation

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.chess.domain.GameMap
import com.example.chess.domain.Position
import com.example.chess.domain.PromotionPiece
import com.example.chess.presentation.ui.theme.ChessTheme

internal const val PREVIEW_CHECKMATE_GAME =
    "e2e4 a7a6 e4e5 d7d5 e5d6 a6a5 g1h3 a5a4 f1c4 b8d7 e1g1 a4a3 d1f3 h7h6 f3f7"

internal const val PREVIEW_BLACK_WINS_GAME = "f2f3 e7e5 g2g4 d8h4"

internal const val PREVIEW_STALEMATE_GAME =
    "e2e3 a7a5 d1h5 a8a6 h5a5 h7h5 h2h4 a6h6 a5c7 f7f6 " +
            "c7d7 e8f7 d7b7 d8d3 b7b8 d3h7 b8c8 f7g6 c8e6"

internal const val PREVIEW_PROMOTION_GAME = "a2a4 b7b5 a4b5 a7a6 b5a6 c8b7 a6b7 b8c6 b7a8"

internal fun previewGame(
    moves: String = "",
    promotion: PromotionPiece = PromotionPiece.Queen
): GameMap {
    val game = GameMap()
    game.init()
    moves.split(" ")
        .filter { it.isNotBlank() }
        .forEach { move ->
            game.selectFigure(previewPosition(move.substring(0, 2)))
            game.moveFigure(previewPosition(move.substring(2, 4)), promotion)
        }
    return game
}

private fun previewPosition(square: String) =
    Position(row = square[1].digitToInt(), column = square[0] - 'a' + 1)

@Composable
internal fun ChessPreview(content: @Composable () -> Unit) {
    ChessTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

@Preview(name = "Светлая", showBackground = true)
@Preview(
    name = "Тёмная",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
internal annotation class ThemePreviews
