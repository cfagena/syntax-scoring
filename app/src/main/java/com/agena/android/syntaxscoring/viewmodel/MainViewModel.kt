package com.agena.android.syntaxscoring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agena.android.syntaxscoring.domain.SyntaxScoreUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val syntaxScoreUseCase: SyntaxScoreUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : ViewModel() {

    companion object {
        private const val TAG: String = "MainViewModel"
    }

    private val _score1 = MutableLiveData(0L)
    private val _score2 = MutableLiveData(0L)

    val mediatorLiveData = MediatorLiveData<Pair<Long, Long>>()

    private val _inputData = MutableLiveData(listOf<String>())
    val inputData: LiveData<List<String>> = _inputData

    init {
        mediatorLiveData.addSource(_score1) {
            it?.let {
                mediatorLiveData.value = Pair(it, _score2.value ?: 0)
            }
        }
        mediatorLiveData.addSource(_score2) {
            it?.let {
                mediatorLiveData.value = Pair(_score1.value ?: 0, it)
            }
        }
    }

    fun setInputData(inputString: String) {
        if (inputString.isNotBlank())
            _inputData.value = inputString.lines()
    }

    fun removeCorruptedLines() {
        _score1.value = 0
        _score2.value = 0

        _inputData.value?.let {
            viewModelScope.launch(dispatcher) {
                syntaxScoreUseCase(it).also {
                    _score1.value = it.first
                    _score2.value = it.second
                }
            }
        }
    }

    fun reset() {
        _score1.value = 0
        _score2.value = 0
    }
}
