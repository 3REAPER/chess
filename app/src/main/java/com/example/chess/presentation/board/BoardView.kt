package com.example.chess.presentation.board

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chess.R
import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.GameMap
import com.example.chess.domain.Position
import com.example.chess.domain.Square
import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook
import com.example.chess.presentation.ui.theme.BlackDesk
import com.example.chess.presentation.ui.theme.ChessTheme
import com.example.chess.presentation.ui.theme.WhiteDesk

@Composable
fun BoardView(
    modifier: Modifier = Modifier,
    gameMap: GameMap,
    onClickFigure: (Figure) -> Unit,
    onClickMove: (Position) -> Unit,
    isWhiteMove: Boolean
) {
    val list = mutableListOf<Square>()
    for (i in gameMap.map.indices) {
        val array = gameMap.map[gameMap.map.size - i - 1]
        array.forEach {
            list.add(it)
        }
    }

    Column() {

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(gameMap.historyManager.getHistory()) { count, item ->
                Text(item.toString())
            }
        }


        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
        ) {
            itemsIndexed(list) { count, item ->
                val color =
                    if (count % 2 == 0 && count.div(8) % 2 == 0 || count % 2 == 1 && count.div(8) % 2 == 1) {
                        WhiteDesk
                    } else {
                        BlackDesk
                    }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(color)
                        .clickable(true) {
                            if (item.isAttack) {
                                onClickMove(Position(8 - (count) / 8, (count) % 8 + 1))
                            } else if (item.figure != null && ((item.figure.color == FigureColor.White && isWhiteMove) || (item.figure.color == FigureColor.Black && !isWhiteMove))) {
                                onClickFigure(item.figure)
                            }
                        }
                ) {


                    if (item.figure != null) {
                        var isCheck = false
                        val drawable = when (item.figure) {
                            is Pawn -> {
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_pawn)
                                } else {
                                    painterResource(R.drawable.black_pawn)
                                }
                            }

                            is Rook -> {
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_rook)
                                } else {
                                    painterResource(R.drawable.black_rook)
                                }
                            }

                            is Knight -> {
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_knight)
                                } else {
                                    painterResource(R.drawable.black_knight)
                                }
                            }

                            is Bishop -> {
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_bishop)
                                } else {
                                    painterResource(R.drawable.black_bishop)
                                }
                            }

                            is Queen -> {
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_queen)
                                } else {
                                    painterResource(R.drawable.black_queen)
                                }
                            }

                            is King -> {
                                isCheck = item.figure.isCheck
                                if (item.figure.color == FigureColor.White) {
                                    painterResource(R.drawable.white_king)
                                } else {
                                    painterResource(R.drawable.black_king)
                                }
                            }

                            else -> painterResource(R.drawable.white_pawn)
                        }

                        val backgroundCheck = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF0000),
                                        Color(0x00FF0000)
                                    )
                                )
                            )
                        Box(modifier = if (isCheck) backgroundCheck else Modifier) {
                            Image(
                                painter = drawable,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = ""
                            )

                        }
                    }

                    if (item.isAttack) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }
            }
        }

        if (gameMap.isCheckMateBlack || gameMap.isCheckMateWhite) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("CheckMate")
            }
        }

    }
}

@Composable
@Preview(showBackground = false)
fun BoardView_Preview() {
    ChessTheme {
        val gameMap = GameMap()
        gameMap.init()
        BoardView(
            gameMap = gameMap,
            onClickMove = {},
            onClickFigure = {},
            isWhiteMove = true
        )
    }
}