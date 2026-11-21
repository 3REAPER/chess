package com.example.chess.domain

data class Position(
    val row: Int,
    val column: Int
){
    val file: String
        get() = FILES[column - 1]

    override fun toString(): String {
        return "$file$row"
    }

    private companion object {
        val FILES = listOf("a", "b", "c", "d", "e", "f", "g", "h")
    }
}
