package com.agena.android.syntaxscoring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agena.android.syntaxscoring.domain.CompleteAllLinesUseCase
import com.agena.android.syntaxscoring.domain.RemoveCorruptedLinesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val removeCorruptedLinesUseCase: RemoveCorruptedLinesUseCase,
    private val completeAllLinesUseCase: CompleteAllLinesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : ViewModel() {

    companion object {
        private const val TAG: String = "MainViewModel"
    }

    private val _stage = MutableLiveData(Stage.START)
    val stage: LiveData<Stage> = _stage

    private val _score = MutableLiveData(0L)
    val score: LiveData<Long> = _score

    private val _inputData = MutableLiveData(listOf<String>())
    val inputData: LiveData<List<String>> = _inputData

    fun setInputData(inputString: String) {
        if (inputString.isNotBlank())
            _inputData.value = inputString.lines()
    }

    fun removeCorruptedLines() {
        _score.value = 0L

        _inputData.value?.let {
            viewModelScope.launch(dispatcher) {
                removeCorruptedLinesUseCase(it).also {
                    _score.value = it.first
                    _inputData.value = it.second
                    _stage.value = Stage.CORRUPTED_LINES_REMOVED
                }
            }
        }
    }

    fun reset() {
        _score.value = 0L
        _stage.value = Stage.START
    }

    fun completeUncompletedLines() {
        viewModelScope.launch(dispatcher) {
            inputData.value?.let {
                completeAllLinesUseCase(it).also {
                    _score.value = it
                    _stage.value = Stage.ALL_LINES_COMPLETED
                }
            }
        }
    }
}

enum class Stage {
    START,
    CORRUPTED_LINES_REMOVED,
    ALL_LINES_COMPLETED
}
