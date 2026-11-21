package com.example.chess.domain.history

class HistoryManagerImpl : HistoryManager {
    private val _list: ArrayList<History> = arrayListOf()

    override fun writeMove(move: History) {
        _list.add(move)
    }

    override fun getHistory(): List<History> {
        return _list.toList()
    }
}
