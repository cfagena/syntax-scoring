package com.agena.android.syntaxscoring.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.agena.android.syntaxscoring.MainViewModel
import com.agena.android.syntaxscoring.R
import com.agena.android.syntaxscoring.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setInputData()
        setOnClickAction()
        setObservers()
    }

    private fun setObservers() {

        mainViewModel.inputData.observe(this) {
            if (!it.isNullOrEmpty())
                binding.inputTextview.text = TextUtils.join("\n", it)
        }

        mainViewModel.mediatorLiveData.observe(this) {
            it?.let {
                binding.scoreTextview.text = resources.getString(R.string.score, it.first, it.second)
            }
        }
    }

    private fun setInputData() {
        if (mainViewModel.inputData.value.isNullOrEmpty()) {
            mainViewModel.setInputData(fetchFromAsset())
        }
    }

    private fun setOnClickAction() {
        binding.processButton.setOnClickListener {
            mainViewModel.removeCorruptedLines()
        }

        binding.resetButton.setOnClickListener {
            mainViewModel.reset()
            mainViewModel.setInputData(fetchFromAsset())
        }
    }

    private fun fetchFromAsset(): String {
        var string = ""
        try {
            val inputStream = assets.open("input.txt")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer).toString()

            string = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return string
    }
}
