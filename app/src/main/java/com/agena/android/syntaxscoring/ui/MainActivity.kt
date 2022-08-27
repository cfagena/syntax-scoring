package com.agena.android.syntaxscoring.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.agena.android.syntaxscoring.MainViewModel
import com.agena.android.syntaxscoring.R
import com.agena.android.syntaxscoring.Stage
import com.agena.android.syntaxscoring.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModel()

    private val listAdapter by lazy {
        LineResultAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setOnClickAction()
        setObservers()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.resultRecyclerview.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setObservers() {
        mainViewModel.stage.observe(this) { stage ->
            stage?.let {
                setButtons(stage)
                setInputData(stage)
                binding.stageTextview.text = resources.getString(R.string.stage, it)
            }
        }

        mainViewModel.inputData.observe(this) {
            if (!it.isNullOrEmpty())
                binding.inputTextview.text = TextUtils.join("\n", it)
        }

        mainViewModel.score.observe(this) {
            it?.let {
                binding.scoreTextview.text = resources.getString(R.string.score, it)
            }
        }
    }

    private fun setButtons(stage: Stage) {
        when (stage) {
            Stage.START -> {
                binding.processPart1Button.isEnabled = true
                binding.processPart2Button.isEnabled = false
            }
            Stage.CORRUPTED_LINES_REMOVED -> {
                binding.processPart1Button.isEnabled = false
                binding.processPart2Button.isEnabled = true
            }
            Stage.ALL_LINES_COMPLETED -> {
                binding.processPart1Button.isEnabled = false
                binding.processPart2Button.isEnabled = false
            }
        }
    }

    private fun setInputData(stage: Stage) {
        if (Stage.START == stage) {
            mainViewModel.setInputData(fetchFromAsset())
        }
    }

    private fun setOnClickAction() {
        binding.processPart1Button.setOnClickListener {
            mainViewModel.removeCorruptedLines()
        }

        binding.processPart2Button.setOnClickListener {
            mainViewModel.completeUncompletedLines()
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
