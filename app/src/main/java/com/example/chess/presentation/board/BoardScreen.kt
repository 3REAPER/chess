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

    BoardView(
        modifier,
        gameMap.value,
        {viewModel.selectFigure(it.position)},
        {viewModel.moveFigure(it)},
        isWhiteMove.value
    )
}