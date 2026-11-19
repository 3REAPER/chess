package com.example.chess.domain

enum class FigureColor {
    White, Black;

    fun invert(): FigureColor {
        return if (this == White){
            Black
        }else{
            White
        }
    }
}