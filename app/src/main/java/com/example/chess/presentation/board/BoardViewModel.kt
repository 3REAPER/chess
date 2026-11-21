package com.example.chess.presentation.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.domain.GameMap
import com.example.chess.domain.Position
import com.example.chess.domain.PromotionPiece
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BoardViewModel : ViewModel() {
    private val _gameMap: MutableStateFlow<GameMap> = MutableStateFlow(GameMap())
    val gameMap = _gameMap.asStateFlow()

    private val _isWhiteMove: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isWhiteMove = _isWhiteMove.asStateFlow()

    private val _pendingPromotion: MutableStateFlow<Position?> = MutableStateFlow(null)
    val pendingPromotion = _pendingPromotion.asStateFlow()

    init {
        _gameMap.value.init()
    }

    fun moveFigure(to: Position) {
        if (_gameMap.value.isPromotionMove(to)) {
            _pendingPromotion.value = to
            return
        }
        makeMove(to, PromotionPiece.Queen)
    }

    fun promote(piece: PromotionPiece) {
        val to = _pendingPromotion.value ?: return
        _pendingPromotion.value = null
        makeMove(to, piece)
    }

    fun cancelPromotion() {
        _pendingPromotion.value = null
    }

    fun selectFigure(position: Position) {
        viewModelScope.launch {
            _gameMap.value.selectFigure(position)
            val newMap = _gameMap.value.map
            _gameMap.emit(GameMap.createMap(newMap, _gameMap.value.historyManager))
        }

    }

    fun restart() {
        viewModelScope.launch {
            val newGame = GameMap()
            newGame.init()
            _pendingPromotion.emit(null)
            _isWhiteMove.emit(true)
            _gameMap.emit(newGame)
        }
    }

    private fun makeMove(to: Position, promotion: PromotionPiece) {
        viewModelScope.launch {
            val isMoved = _gameMap.value.moveFigure(to, promotion)
            val newMap = _gameMap.value.map
            if (isMoved) {
                _isWhiteMove.update { return@update !it }
            }
            _gameMap.emit(GameMap.createMap(newMap, _gameMap.value.historyManager))
        }
    }
}
