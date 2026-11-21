package com.example.chess.domain.history

import com.example.chess.domain.Figure
import com.example.chess.domain.Position

class HistoryManagerImpl : HistoryManager {
    private val _list: ArrayList<History> = arrayListOf()
    override fun writeMove(
        from: Figure,
        to: Position,
        toFigure: Figure?,
        isCheck: Boolean
    ) {
        val move = History(from, to, toFigure,isCheck)
        _list.add(move)
    }

    override fun getHistory(): List<History> {
        return _list.toList()
    }
}