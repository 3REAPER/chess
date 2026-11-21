package com.example.chess.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.R
import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.GameResult
import com.example.chess.domain.PromotionPiece
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.history.History
import com.example.chess.presentation.ui.theme.WhiteDesk

private val NumberColumnWidth = 36.dp

@Composable
fun MoveHistoryView(
    history: List<History>,
    modifier: Modifier = Modifier,
    result: GameResult? = null
) {
    val movePairs = remember(history) { history.chunked(2) }
    val listState = rememberLazyListState()

    LaunchedEffect(history.size, result) {
        if (movePairs.isNotEmpty()) {
            val lastItem = if (result != null) movePairs.size else movePairs.lastIndex
            listState.animateScrollToItem(lastItem)
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (history.isNotEmpty()) {
                    val fullMoves = (history.size + 1) / 2
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = pluralStringResource(R.plurals.moves_count, fullMoves, fullMoves),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            if (history.isEmpty()) {
                EmptyHistory()
            } else {
                HistoryHeaderRow()
                Spacer(Modifier.height(4.dp))
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(movePairs) { index, pair ->
                        val whiteIndex = index * 2
                        MoveRow(
                            number = index + 1,
                            whiteMove = pair[0],
                            blackMove = pair.getOrNull(1),
                            isWhiteLast = whiteIndex == history.lastIndex,
                            isBlackLast = whiteIndex + 1 == history.lastIndex,
                            isStriped = index % 2 == 1
                        )
                    }
                    if (result != null) {
                        item {
                            ResultFooter(result = result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryHeaderRow() {
    val style = MaterialTheme.typography.labelSmall
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.history_column_number),
            style = style,
            color = color,
            modifier = Modifier.width(NumberColumnWidth)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            SideDot(isWhite = true)
            Text(stringResource(R.string.history_column_white), style = style, color = color)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            SideDot(isWhite = false)
            Text(stringResource(R.string.history_column_black), style = style, color = color)
        }
    }
}

@Composable
private fun MoveRow(
    number: Int,
    whiteMove: History,
    blackMove: History?,
    isWhiteLast: Boolean,
    isBlackLast: Boolean,
    isStriped: Boolean
) {
    val background = if (isStriped) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "$number.",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(NumberColumnWidth)
        )
        Box(modifier = Modifier.weight(1f)) {
            MoveCell(move = whiteMove, isLast = isWhiteLast)
        }
        Box(modifier = Modifier.weight(1f)) {
            if (blackMove != null) {
                MoveCell(move = blackMove, isLast = isBlackLast)
            }
        }
    }
}

@Composable
private fun MoveCell(
    move: History,
    isLast: Boolean
) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when {
        move.isCheckMate -> colors.errorContainer to colors.onErrorContainer
        isLast -> colors.primaryContainer to colors.onPrimaryContainer
        else -> Color.Transparent to colors.onSurface
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(container)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        val fontWeight = if (isLast || move.isCheckMate) FontWeight.Bold else FontWeight.Medium

        if (move.figure !is Pawn) {
            PieceBadge(figure = move.figure)
        }

        val promotedTo = move.promotedTo
        if (promotedTo != null) {
            MoveText(
                text = move.notation(
                    withPieceLetter = false,
                    withPromotionLetter = false,
                    withCheckSuffix = false
                ),
                color = content,
                fontWeight = fontWeight
            )
            PieceBadge(figure = promotedTo, size = 20.dp)
            if (move.checkSuffix.isNotEmpty()) {
                MoveText(text = move.checkSuffix, color = content, fontWeight = fontWeight)
            }
        } else {
            MoveText(
                text = move.notation(withPieceLetter = false),
                color = content,
                fontWeight = fontWeight
            )
        }

        if (move.isEnPassant) {
            SpecialMoveTag(text = stringResource(R.string.history_en_passant))
        }
    }
}

@Composable
private fun MoveText(
    text: String,
    color: Color,
    fontWeight: FontWeight
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = fontWeight,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ResultFooter(result: GameResult) {
    val colors = MaterialTheme.colorScheme
    val (score, label) = when (result) {
        is GameResult.Checkmate -> {
            val score = if (result.winner == FigureColor.White) "1–0" else "0–1"
            score to stringResource(R.string.result_checkmate)
        }

        is GameResult.Stalemate -> "½–½" to stringResource(R.string.result_stalemate)
    }
    val (container, content) = when (result) {
        is GameResult.Checkmate -> colors.primaryContainer to colors.onPrimaryContainer
        is GameResult.Stalemate -> colors.secondaryContainer to colors.onSecondaryContainer
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(CircleShape)
                .background(container)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = score,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = content
            )
            Text(
                text = "·",
                style = MaterialTheme.typography.titleSmall,
                color = content
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = content
            )
        }
    }
}

@Composable
private fun SpecialMoveTag(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        maxLines = 1,
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 6.dp, vertical = 1.dp)
    )
}

@Composable
fun PieceBadge(
    figure: Figure,
    size: Dp = 22.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(WhiteDesk)
    ) {
        Image(
            painter = painterResource(figure.drawableRes()),
            contentDescription = null,
            modifier = Modifier.size(size * 0.86f)
        )
    }
}

@Composable
private fun SideDot(isWhite: Boolean) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outline)
            .padding(1.dp)
            .clip(CircleShape)
            .background(if (isWhite) Color.White else Color(0xFF1F1F1F))
    )
}

@Composable
private fun EmptyHistory() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Image(
                painter = painterResource(R.drawable.white_pawn),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .alpha(0.8f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.history_empty),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.history_empty_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
@Preview
fun MoveHistoryView_Preview() {
    ChessPreview {
        val game = previewGame(PREVIEW_CHECKMATE_GAME)
        MoveHistoryView(
            history = game.historyManager.getHistory(),
            result = game.result,
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp)
                .padding(12.dp)
        )
    }
}

