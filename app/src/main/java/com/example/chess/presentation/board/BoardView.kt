package com.example.chess.presentation.board

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.chess.R
import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.GameMap
import com.example.chess.domain.Position
import com.example.chess.domain.PromotionPiece
import com.example.chess.domain.figure.King
import com.example.chess.domain.history.History
import com.example.chess.presentation.GameOverOverlay
import com.example.chess.presentation.MoveHistoryView
import com.example.chess.presentation.PromotionPicker
import com.example.chess.presentation.drawableRes
import com.example.chess.presentation.ui.theme.BlackDesk
import com.example.chess.presentation.ui.theme.ChessTheme
import com.example.chess.presentation.ui.theme.LastMoveHighlight
import com.example.chess.presentation.ui.theme.MoveHint
import com.example.chess.presentation.ui.theme.SelectedHighlight
import com.example.chess.presentation.ui.theme.WhiteDesk
import kotlin.math.roundToInt

@Composable
fun BoardView(
    modifier: Modifier = Modifier,
    gameMap: GameMap,
    onClickFigure: (Figure) -> Unit,
    onClickMove: (Position) -> Unit,
    onRestart: () -> Unit = {},
    isWhiteMove: Boolean,
    isPromotionPending: Boolean = false,
    onPromote: (PromotionPiece) -> Unit = {},
    onCancelPromotion: () -> Unit = {}
) {
    val history = gameMap.historyManager.getHistory()
    val result = gameMap.result
    var isResultHidden by remember(result) { mutableStateOf(false) }

    Box(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            GameHeader(
                isWhiteMove = isWhiteMove,
                isGameOver = result != null,
                onRestart = onRestart
            )

            ChessBoard(
                gameMap = gameMap,
                lastMove = history.lastOrNull(),
                isWhiteMove = isWhiteMove,
                onClickFigure = onClickFigure,
                onClickMove = onClickMove
            )

            MoveHistoryView(
                history = history,
                result = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        PromotionPicker(
            color = when {
                !isPromotionPending -> null
                isWhiteMove -> FigureColor.White
                else -> FigureColor.Black
            },
            onSelect = onPromote,
            onDismiss = onCancelPromotion
        )

        GameOverOverlay(
            result = result,
            visible = !isResultHidden,
            history = history,
            onNewGame = onRestart,
            onViewBoard = { isResultHidden = true }
        )
    }
}

@Composable
private fun GameHeader(
    isWhiteMove: Boolean,
    isGameOver: Boolean,
    onRestart: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (isGameOver) {
            FilledTonalButton(onClick = onRestart) {
                Text(stringResource(R.string.new_game))
            }
        } else {
            TurnIndicator(isWhiteMove = isWhiteMove)
        }
    }
}

@Composable
private fun TurnIndicator(isWhiteMove: Boolean) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (isWhiteMove) Color.White else Color(0xFF1F1F1F))
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            AnimatedContent(targetState = isWhiteMove, label = "turn") { white ->
                Text(
                    text = stringResource(if (white) R.string.turn_white else R.string.turn_black),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private const val MOVE_ANIMATION_MS = 250

@Composable
private fun ChessBoard(
    gameMap: GameMap,
    lastMove: History?,
    isWhiteMove: Boolean,
    onClickFigure: (Figure) -> Unit,
    onClickMove: (Position) -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(elevation = 8.dp, shape = shape)
            .clip(shape)
    ) {
        val squareSize = maxWidth / GameMap.COLUMN_COUNT

        BoardGrid { position, modifier ->
            val square = gameMap.map[position.row - 1][position.column - 1]
            val figure = square.figure
            BoardSquare(
                figure = figure,
                isSelected = square.isSelected,
                isAttack = square.isAttack,
                isCheck = figure is King && figure.isCheck,
                position = position,
                isLastMove = lastMove != null && (position == lastMove.from || position == lastMove.to),
                isWhiteMove = isWhiteMove,
                onClickFigure = onClickFigure,
                onClickMove = onClickMove,
                modifier = modifier
            )
        }

        PiecesLayer(
            gameMap = gameMap,
            lastMove = lastMove,
            squareSize = squareSize
        )

        BoardGrid { position, modifier ->
            val square = gameMap.map[position.row - 1][position.column - 1]
            SquareOverlay(
                position = position,
                isAttack = square.isAttack,
                hasFigure = square.figure != null,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BoardGrid(
    square: @Composable (position: Position, modifier: Modifier) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (row in GameMap.ROWS_COUNT downTo 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (column in 1..GameMap.COLUMN_COUNT) {
                    square(
                        Position(row, column),
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun PiecesLayer(
    gameMap: GameMap,
    lastMove: History?,
    squareSize: Dp
) {
    val pieces = buildList {
        gameMap.map.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, square ->
                square.figure?.let { add(it to Position(rowIndex + 1, columnIndex + 1)) }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val capturedFigure = lastMove?.capturedFigure
        if (lastMove != null && capturedFigure != null) {
            key(lastMove) {
                CapturedPiece(
                    figure = capturedFigure,
                    position = if (lastMove.isEnPassant) {
                        Position(lastMove.from.row, lastMove.to.column)
                    } else {
                        lastMove.to
                    },
                    squareSize = squareSize
                )
            }
        }

        pieces.forEach { (figure, position) ->
            key(figure) {
                AnimatedPiece(
                    figure = figure,
                    position = position,
                    startPosition = if (lastMove != null && lastMove.promotedTo === figure) {
                        lastMove.from
                    } else {
                        position
                    },
                    squareSize = squareSize
                )
            }
        }
    }
}

@Composable
private fun AnimatedPiece(
    figure: Figure,
    position: Position,
    startPosition: Position,
    squareSize: Dp
) {
    val target = position.toBoardOffset()
    val offset = remember { Animatable(startPosition.toBoardOffset(), Offset.VectorConverter) }

    LaunchedEffect(target) {
        offset.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis = MOVE_ANIMATION_MS, easing = FastOutSlowInEasing)
        )
    }

    Image(
        painter = painterResource(figure.drawableRes()),
        contentDescription = null,
        modifier = Modifier
            .zIndex(if (offset.isRunning) 1f else 0f)
            .offset { offset.value.toIntOffset(squareSize.toPx()) }
            .size(squareSize)
    )
}

@Composable
private fun CapturedPiece(
    figure: Figure,
    position: Position,
    squareSize: Dp
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = MOVE_ANIMATION_MS, easing = FastOutLinearInEasing)
        )
    }

    val boardOffset = position.toBoardOffset()
    Image(
        painter = painterResource(figure.drawableRes()),
        contentDescription = null,
        modifier = Modifier
            .offset { boardOffset.toIntOffset(squareSize.toPx()) }
            .size(squareSize)
            .graphicsLayer {
                val scale = 1f - 0.3f * progress.value
                alpha = 1f - progress.value
                scaleX = scale
                scaleY = scale
            }
    )
}

private fun Position.toBoardOffset() = Offset(
    x = (column - 1).toFloat(),
    y = (GameMap.ROWS_COUNT - row).toFloat()
)

private fun Offset.toIntOffset(squarePx: Float) = IntOffset(
    x = (x * squarePx).roundToInt(),
    y = (y * squarePx).roundToInt()
)

private fun Position.isLightSquare() = (row + column) % 2 == 1

@Composable
private fun BoardSquare(
    figure: Figure?,
    isSelected: Boolean,
    isAttack: Boolean,
    isCheck: Boolean,
    position: Position,
    isLastMove: Boolean,
    isWhiteMove: Boolean,
    onClickFigure: (Figure) -> Unit,
    onClickMove: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (position.isLightSquare()) WhiteDesk else BlackDesk)
            .clickable {
                if (isAttack) {
                    onClickMove(position)
                } else if (figure != null && ((figure.color == FigureColor.White && isWhiteMove) || (figure.color == FigureColor.Black && !isWhiteMove))) {
                    onClickFigure(figure)
                }
            }
    ) {
        when {
            isSelected -> Highlight(SelectedHighlight)
            isLastMove -> Highlight(LastMoveHighlight)
        }

        if (isCheck) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFF0000),
                                Color(0x00FF0000)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun SquareOverlay(
    position: Position,
    isAttack: Boolean,
    hasFigure: Boolean,
    modifier: Modifier = Modifier
) {
    val coordinateColor = if (position.isLightSquare()) BlackDesk else WhiteDesk

    Box(modifier = modifier) {
        if (position.column == 1) {
            Coordinate(
                text = position.row.toString(),
                color = coordinateColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 3.dp, top = 1.dp)
            )
        }
        if (position.row == 1) {
            Coordinate(
                text = position.file,
                color = coordinateColor,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 3.dp, bottom = 1.dp)
            )
        }

        if (isAttack) {
            if (!hasFigure) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.3f)
                        .clip(CircleShape)
                        .background(MoveHint)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp)
                        .border(width = 4.dp, color = MoveHint, shape = CircleShape)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.Highlight(color: Color) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(color)
    )
}

@Composable
private fun Coordinate(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
@Preview(showBackground = true, heightDp = 860)
fun BoardView_Preview() {
    ChessTheme {
        val gameMap = GameMap()
        gameMap.init()
        BoardView(
            modifier = Modifier.fillMaxSize(),
            gameMap = gameMap,
            onClickMove = {},
            onClickFigure = {},
            isWhiteMove = true
        )
    }
}
