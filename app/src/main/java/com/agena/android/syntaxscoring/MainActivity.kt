package com.agena.android.syntaxscoring

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agena.android.syntaxscoring.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setObservers()
        setExampleInput()
        setOnClickAction()
    }

    private fun setObservers() {
        viewModel.input.observe(this) { input ->
            input?.let {
                binding.inputTextview.text = EXAMPLE_INPUT
            }
        }
    }

    private fun setExampleInput() {
        viewModel.setInput(EXAMPLE_INPUT)
    }

    private fun setOnClickAction() {
        binding.processButton.setOnClickListener {
            viewModel.process()
        }
    }
}

const val EXAMPLE_INPUT = "[({(<(())[]>[[{[]{<()<>>\n" +
    "[(()[<>])]({[<{<<[]>>(\n" +
    "{([(<{}[<>[]}>{[]{[(<()>\n" +
    "(((({<>}<{<{<>}{[]{[]{}\n" +
    "[[<[([]))<([[{}[[()]]]\n" +
    "[{[{({}]{}}([{[{{{}}([]\n" +
    "{<[[]]>}<{[{[{[]{()[[[]\n" +
    "[<(<(<(<{}))><([]([]()\n" +
    "<{([([[(<>()){}]>(<<{{\n" +
    "<{([{{}}[<[[[<>{}]]]>[]]"
