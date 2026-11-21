package com.example.chess.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.R
import com.example.chess.domain.FigureColor
import com.example.chess.domain.GameMap
import com.example.chess.domain.GameResult
import com.example.chess.domain.history.History
import com.example.chess.presentation.ui.theme.Gold
import com.example.chess.presentation.ui.theme.GoldDark
import com.example.chess.presentation.ui.theme.GoldLight
import com.example.chess.presentation.ui.theme.Silver
import com.example.chess.presentation.ui.theme.SilverDark
import com.example.chess.presentation.ui.theme.SilverLight

@Composable
fun GameOverOverlay(
    result: GameResult?,
    visible: Boolean,
    history: List<History>,
    onNewGame: () -> Unit,
    onViewBoard: () -> Unit
) {
    var lastResult by remember { mutableStateOf<GameResult?>(null) }
    LaunchedEffect(result) {
        if (result != null) lastResult = result
    }
    val shownResult = result ?: lastResult

    AnimatedVisibility(
        visible = visible && result != null,
        enter = fadeIn(tween(durationMillis = 350)),
        exit = fadeOut(tween(durationMillis = 200))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.62f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 24.dp,
                modifier = Modifier
                    .widthIn(max = 380.dp)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter = scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + slideInVertically { it / 8 },
                        exit = scaleOut(targetScale = 0.9f)
                    )
            ) {
                if (shownResult != null) {
                    GameOverContent(
                        result = shownResult,
                        history = history,
                        onNewGame = onNewGame,
                        onViewBoard = onViewBoard
                    )
                }
            }
        }
    }
}

@Composable
private fun GameOverContent(
    result: GameResult,
    history: List<History>,
    onNewGame: () -> Unit,
    onViewBoard: () -> Unit
) {
    val fullMoves = (history.size + 1) / 2
    val decisiveMove = history.lastOrNull()

    val title: String
    val subtitle: String
    val description: String
    when (result) {
        is GameResult.Checkmate -> {
            val isWhiteWinner = result.winner == FigureColor.White
            title = stringResource(R.string.checkmate_title)
            subtitle = stringResource(
                if (isWhiteWinner) R.string.checkmate_winner_white else R.string.checkmate_winner_black
            )
            description = stringResource(
                if (isWhiteWinner) R.string.checkmate_loser_black else R.string.checkmate_loser_white
            )
        }

        is GameResult.Stalemate -> {
            title = stringResource(R.string.stalemate_title)
            subtitle = stringResource(R.string.stalemate_result)
            description = stringResource(
                if (result.sideWithoutMoves == FigureColor.White) R.string.stalemate_white else R.string.stalemate_black
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        when (result) {
            is GameResult.Checkmate -> WinnerMedallion(winner = result.winner)
            is GameResult.Stalemate -> DrawMedallion()
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatTile(
                value = "$fullMoves",
                label = stringResource(R.string.checkmate_stat_moves),
                modifier = Modifier.weight(1f)
            )
            StatTile(
                value = decisiveMove?.notation().orEmpty(),
                label = stringResource(R.string.checkmate_stat_last_move),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNewGame,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = stringResource(R.string.new_game),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = onViewBoard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.checkmate_view_board))
        }
    }
}

@Composable
private fun WinnerMedallion(winner: FigureColor) {
    Medallion(glow = Gold, colors = listOf(GoldLight, Gold, GoldDark)) {
        Image(
            painter = painterResource(kingDrawableRes(winner)),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
private fun DrawMedallion() {
    Medallion(glow = Silver, colors = listOf(SilverLight, Silver, SilverDark)) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(kingDrawableRes(FigureColor.Black)),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .offset(x = 12.dp, y = 2.dp)
            )
            Image(
                painter = painterResource(kingDrawableRes(FigureColor.White)),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .offset(x = (-12).dp, y = (-2).dp)
            )
        }
    }
}

@Composable
private fun Medallion(
    glow: Color,
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "medallion")
    val glowScale by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(124.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(glow.copy(alpha = 0.55f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(brush = Brush.linearGradient(colors = colors))
                .border(width = 3.dp, color = Color.White.copy(alpha = 0.7f), shape = CircleShape)
        ) {
            content()
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
@Preview(showBackground = true)
fun GameOverOverlay_BlackWins_Preview() {
    ChessPreview {
        val game = previewGame(PREVIEW_CHECKMATE_GAME)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(760.dp)
        ) {
            GameOverOverlay(
                result = game.result,
                visible = true,
                history = game.historyManager.getHistory(),
                onNewGame = {},
                onViewBoard = {}
            )
        }
    }
}
