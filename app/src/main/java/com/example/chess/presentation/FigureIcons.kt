package com.example.chess.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.chess.R
import com.example.chess.domain.Figure
import com.example.chess.domain.FigureColor
import com.example.chess.domain.PromotionPiece
import com.example.chess.domain.figure.Bishop
import com.example.chess.domain.figure.King
import com.example.chess.domain.figure.Knight
import com.example.chess.domain.figure.Pawn
import com.example.chess.domain.figure.Queen
import com.example.chess.domain.figure.Rook

@DrawableRes
fun Figure.drawableRes(): Int {
    val isWhite = color == FigureColor.White
    return when (this) {
        is Pawn -> if (isWhite) R.drawable.white_pawn else R.drawable.black_pawn
        is Rook -> if (isWhite) R.drawable.white_rook else R.drawable.black_rook
        is Knight -> if (isWhite) R.drawable.white_knight else R.drawable.black_knight
        is Bishop -> if (isWhite) R.drawable.white_bishop else R.drawable.black_bishop
        is Queen -> if (isWhite) R.drawable.white_queen else R.drawable.black_queen
        is King -> if (isWhite) R.drawable.white_king else R.drawable.black_king
        else -> R.drawable.white_pawn
    }
}

@DrawableRes
fun kingDrawableRes(color: FigureColor): Int =
    if (color == FigureColor.White) R.drawable.white_king else R.drawable.black_king

@DrawableRes
fun PromotionPiece.drawableRes(color: FigureColor): Int {
    val isWhite = color == FigureColor.White
    return when (this) {
        PromotionPiece.Queen -> if (isWhite) R.drawable.white_queen else R.drawable.black_queen
        PromotionPiece.Rook -> if (isWhite) R.drawable.white_rook else R.drawable.black_rook
        PromotionPiece.Bishop -> if (isWhite) R.drawable.white_bishop else R.drawable.black_bishop
        PromotionPiece.Knight -> if (isWhite) R.drawable.white_knight else R.drawable.black_knight
    }
}

@StringRes
fun PromotionPiece.nameRes(): Int = when (this) {
    PromotionPiece.Queen -> R.string.piece_queen
    PromotionPiece.Rook -> R.string.piece_rook
    PromotionPiece.Bishop -> R.string.piece_bishop
    PromotionPiece.Knight -> R.string.piece_knight
}
