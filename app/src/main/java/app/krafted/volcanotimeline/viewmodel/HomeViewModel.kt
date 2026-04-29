package app.krafted.volcanotimeline.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.volcanotimeline.data.EruptionRepository
import app.krafted.volcanotimeline.data.db.RoundProgressDao
import app.krafted.volcanotimeline.data.model.EruptionRound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoundCardState(
    val round: EruptionRound,
    val isUnlocked: Boolean,
    val isCompleted: Boolean,
    val bestScore: Int
)

data class HomeUiState(
    val roundCards: List<RoundCardState> = emptyList(),
    val totalScore: Int = 0,
    val allCompleted: Boolean = false,
    val loadError: Boolean = false
)

class HomeViewModel(
    private val repository: EruptionRepository,
    private val roundProgressDao: RoundProgressDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadRounds()
    }

    private fun loadRounds() {
        val rounds = repository.getEruptionRounds()
        if (repository.hasLoadError()) {
            _uiState.update { it.copy(loadError = true) }
            return
        }
        viewModelScope.launch {
            roundProgressDao.getAllRoundProgress().collect { progressList ->
                val progressMap = progressList.associateBy { it.roundId }
                val cards = rounds.map { round ->
                    val progress = progressMap[round.roundId]
                    val isCompleted = progress?.isCompleted == true
                    val previousCompleted = if (round.roundId == 1) true
                        else progressMap[round.roundId - 1]?.isCompleted == true
                    RoundCardState(
                        round = round,
                        isUnlocked = round.roundId == 1 || previousCompleted,
                        isCompleted = isCompleted,
                        bestScore = progress?.bestScore ?: 0
                    )
                }
                val totalScore = cards.sumOf { it.bestScore }
                val allCompleted = cards.all { it.isCompleted }
                _uiState.update {
                    HomeUiState(
                        roundCards = cards,
                        totalScore = totalScore,
                        allCompleted = allCompleted
                    )
                }
            }
        }
    }

    class Factory(
        private val repository: EruptionRepository,
        private val roundProgressDao: RoundProgressDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository, roundProgressDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
