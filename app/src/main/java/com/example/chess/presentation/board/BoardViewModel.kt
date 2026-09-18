package com.example.chess.presentation.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.domain.GameMap
import com.example.chess.domain.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class BoardViewModel : ViewModel() {
    private val _gameMap: MutableStateFlow<GameMap> = MutableStateFlow(GameMap())
    val gameMap = _gameMap.asStateFlow()

    init {
        _gameMap.value.init()
    }

    fun moveFigure(to: Position) {
        viewModelScope.launch {
            _gameMap.value.moveFigure(to)
            val newMap = _gameMap.value.map
            _gameMap.emit(GameMap.createMap(newMap))
        }
    }

    fun selectFigure(position: Position) {
        viewModelScope.launch {
            _gameMap.value.selectFigure(position)
            val newMap = _gameMap.value.map
            _gameMap.emit(GameMap.createMap(newMap))
        }

    }
}