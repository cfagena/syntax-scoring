package com.agena.android.syntaxscoring

import android.app.Application
import com.agena.android.syntaxscoring.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class SyntaxScoringApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@SyntaxScoringApp)
            modules(appModule)
        }
    }
}
