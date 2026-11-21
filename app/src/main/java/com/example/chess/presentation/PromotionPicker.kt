package com.example.chess.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chess.R
import com.example.chess.domain.FigureColor
import com.example.chess.domain.PromotionPiece
import com.example.chess.presentation.ui.theme.WhiteDesk

@Composable
fun PromotionPicker(
    color: FigureColor?,
    onSelect: (PromotionPiece) -> Unit,
    onDismiss: () -> Unit
) {
    var lastColor by remember { mutableStateOf(FigureColor.White) }
    LaunchedEffect(color) {
        if (color != null) lastColor = color
    }
    val shownColor = color ?: lastColor

    AnimatedVisibility(
        visible = color != null,
        enter = fadeIn(tween(durationMillis = 200)),
        exit = fadeOut(tween(durationMillis = 150))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .safeDrawingPadding()
                .padding(24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 24.dp,
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .animateEnterExit(
                        enter = scaleIn(
                            initialScale = 0.85f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                        exit = scaleOut(targetScale = 0.9f)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.promotion_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.promotion_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PromotionPiece.entries.forEach { piece ->
                            PromotionOption(
                                piece = piece,
                                color = shownColor,
                                isRecommended = piece == PromotionPiece.Queen,
                                onClick = { onSelect(piece) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionOption(
    piece: PromotionPiece,
    color: FigureColor,
    isRecommended: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (isRecommended) colors.primaryContainer else colors.surfaceContainerHigh,
        border = if (isRecommended) BorderStroke(2.dp, colors.primary) else null,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(WhiteDesk)
            ) {
                Image(
                    painter = painterResource(piece.drawableRes(color)),
                    contentDescription = stringResource(piece.nameRes()),
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(piece.nameRes()),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isRecommended) FontWeight.Bold else FontWeight.Medium,
                color = if (isRecommended) colors.onPrimaryContainer else colors.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PromotionPicker_Black_Preview() {
    ChessPreview {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            PromotionPicker(
                color = FigureColor.Black,
                onSelect = {},
                onDismiss = {}
            )
        }
    }
}
