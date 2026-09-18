package com.example.chess.domain

data class Square(
    val figure: Figure?,
    var isAttack: Boolean,
    var isSelected: Boolean
)
