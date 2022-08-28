package com.agena.android.syntaxscoring.di

import com.agena.android.syntaxscoring.MainViewModel
import com.agena.android.syntaxscoring.domain.SyntaxScoreUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { SyntaxScoreUseCase() }

    viewModel {
        MainViewModel(
            syntaxScoreUseCase = get()
        )
    }
}
