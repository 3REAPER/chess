package com.example.chess.presentation.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BoardScreen(
    modifier: Modifier
){
    val viewModel: BoardViewModel = viewModel()

    val gameMap = viewModel.gameMap.collectAsState()
    val isWhiteMove = viewModel.isWhiteMove.collectAsState()
    val pendingPromotion = viewModel.pendingPromotion.collectAsState()

    BoardView(
        modifier = modifier,
        gameMap = gameMap.value,
        onClickFigure = { viewModel.selectFigure(it.position) },
        onClickMove = { viewModel.moveFigure(it) },
        onRestart = { viewModel.restart() },
        isWhiteMove = isWhiteMove.value,
        isPromotionPending = pendingPromotion.value != null,
        onPromote = { viewModel.promote(it) },
        onCancelPromotion = { viewModel.cancelPromotion() }
    )
}
