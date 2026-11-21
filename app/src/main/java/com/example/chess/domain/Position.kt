package com.example.chess.domain

data class Position(
    val row: Int,
    val column: Int
){
    override fun toString(): String {
        val list = listOf("a", "b", "c", "d", "e", "f", "g", "h")
        return "${list[column - 1]}${row + 1}"
    }
}
