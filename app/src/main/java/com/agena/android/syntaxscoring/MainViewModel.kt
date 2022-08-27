package com.agena.android.syntaxscoring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _input = MutableLiveData("")
    val input: LiveData<String> = _input

    fun setInput(value: String) {
        _input.value = value
    }

    fun process() {
        TODO("Not yet implemented")
    }
}
