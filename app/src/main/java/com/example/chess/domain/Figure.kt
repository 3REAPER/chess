package com.example.chess.domain

abstract class Figure(
    var position: Position,
    val color: FigureColor
) {

    var hasMoved: Boolean = false

    abstract fun getMoves(): List<Position>

    abstract fun getAttack(): List<Position>

    fun changePosition(newPos: Position){
        position = newPos
    }

    fun List<Position>.filterPosition(): List<Position>{
        return this.filter { return@filter it.column in 1..8 && it.row in 1..8 }
    }
}
