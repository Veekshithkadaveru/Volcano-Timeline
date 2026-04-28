package app.krafted.volcanotimeline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.volcanotimeline.data.EruptionRepository
import app.krafted.volcanotimeline.data.db.RoundProgress
import app.krafted.volcanotimeline.data.db.RoundProgressDao
import app.krafted.volcanotimeline.data.model.Eruption
import app.krafted.volcanotimeline.data.model.EruptionRound
import app.krafted.volcanotimeline.game.OrderingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val currentRound: EruptionRound? = null,
    val cardOrder: List<Eruption> = emptyList(),
    val isConfirmed: Boolean = false,
    val results: List<Boolean> = emptyList(),
    val score: Int = 0,
    val attemptNumber: Int = 1,
    val revealedFunFacts: List<Boolean> = emptyList(),
    val draggedEruptionId: String? = null,
    val dragOffset: Float = 0f
)

class GameViewModel(
    private val repository: EruptionRepository,
    private val roundProgressDao: RoundProgressDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun loadRound(roundId: Int) {
        val round = repository.getRound(roundId)
        if (round != null) {
            val shuffled = round.eruptions.shuffled()
            _uiState.update {
                GameUiState(
                    currentRound = round,
                    cardOrder = shuffled,
                    isConfirmed = false,
                    results = List(shuffled.size) { false },
                    score = 0,
                    attemptNumber = 1,
                    revealedFunFacts = List(shuffled.size) { false }
                )
            }
        }
    }

    fun swapCards(fromIndex: Int, toIndex: Int) {
        if (_uiState.value.isConfirmed) return
        
        _uiState.update { state ->
            val mutableCards = state.cardOrder.toMutableList()
            if (fromIndex in mutableCards.indices && toIndex in mutableCards.indices) {
                val item = mutableCards.removeAt(fromIndex)
                mutableCards.add(toIndex, item)
            }
            state.copy(cardOrder = mutableCards)
        }
    }

    fun setDragState(eruptionId: String?, offset: Float) {
        _uiState.update { it.copy(draggedEruptionId = eruptionId, dragOffset = offset) }
    }

    fun confirmOrder() {
        val state = _uiState.value
        val round = state.currentRound ?: return
        
        val correctOrder = OrderingEngine.getSortedByYear(round.eruptions)
        val validation = OrderingEngine.validateOrder(state.cardOrder, correctOrder)
        val correctCount = OrderingEngine.countCorrect(validation)
        val isPerfect = correctCount == state.cardOrder.size
        
        val score = calculateScore(correctCount, state.attemptNumber, round.difficulty, isPerfect)
        
        _uiState.update { 
            it.copy(
                isConfirmed = true,
                results = validation,
                score = score
            )
        }
        
        if (isPerfect) {
            viewModelScope.launch {
                val currentProgress = roundProgressDao.getRoundProgress(round.roundId).firstOrNull()
                val currentBest = currentProgress?.bestScore ?: 0
                val bestScore = if (score > currentBest) score else currentBest
                
                roundProgressDao.upsertRoundProgress(
                    RoundProgress(
                        roundId = round.roundId,
                        isCompleted = true,
                        bestScore = bestScore,
                        attempts = (currentProgress?.attempts ?: 0) + state.attemptNumber
                    )
                )
            }
        }
    }

    fun nextAttempt() {
        _uiState.update { 
            it.copy(
                isConfirmed = false,
                attemptNumber = it.attemptNumber + 1,
                results = List(it.cardOrder.size) { false }
            )
        }
    }
    
    fun revealFunFact(index: Int) {
        _uiState.update { state ->
            val newRevealed = state.revealedFunFacts.toMutableList()
            if (index in newRevealed.indices) {
                newRevealed[index] = true
            }
            state.copy(revealedFunFacts = newRevealed)
        }
    }

    private fun calculateScore(correctCount: Int, attempts: Int, difficulty: String, isPerfect: Boolean): Int {
        val positionPoints = correctCount * 100
        val multiplier = when (difficulty.lowercase()) {
            "easy" -> 1.0f
            "medium" -> 1.5f
            "hard" -> 2.0f
            "expert" -> 3.0f
            "master" -> 4.0f
            "legend" -> 5.0f
            else -> 1.0f
        }
        val penaltyMultiplier = Math.max(0.1f, 1.0f - ((attempts - 1) * 0.2f))
        var score = (positionPoints * multiplier * penaltyMultiplier).toInt()
        
        if (isPerfect && attempts == 1) {
            score += 500
        }
        
        return score
    }

    class Factory(
        private val repository: EruptionRepository,
        private val roundProgressDao: RoundProgressDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                return GameViewModel(repository, roundProgressDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
