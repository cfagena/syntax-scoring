package com.agena.android.syntaxscoring.di

import com.agena.android.syntaxscoring.MainViewModel
import com.agena.android.syntaxscoring.domain.CompleteAllLinesUseCase
import com.agena.android.syntaxscoring.domain.RemoveCorruptedLinesUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { RemoveCorruptedLinesUseCase() }

    single { CompleteAllLinesUseCase() }

    viewModel {
        MainViewModel(
            removeCorruptedLinesUseCase = get(),
            completeAllLinesUseCase = get()
        )
    }

    // Simple Presenter Factory
//    factory { MySimplePresenter(get()) }
}
