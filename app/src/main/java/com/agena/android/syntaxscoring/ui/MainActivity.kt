package com.agena.android.syntaxscoring.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agena.android.syntaxscoring.MainViewModel
import com.agena.android.syntaxscoring.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val input by lazy {
        readFromAsset()
    }

    private val listAdapter by lazy {
        LineResultAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setOnClickAction()
        setExampleInput()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.resultRecyclerview.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setExampleInput() {
        binding.inputTextview.text = input
        viewModel.input = input
    }

    private fun setOnClickAction() {
        binding.processButton.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                viewModel.process().collect {
                    listAdapter.addLine(it)
                    binding.resultRecyclerview.smoothScrollToPosition(listAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun readFromAsset(): String {
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