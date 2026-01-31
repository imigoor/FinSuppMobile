package br.edu.ifpb.finsupp

import android.app.Application
import br.edu.ifpb.finsupp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FinSuppApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Contexto do Android (necessário para o Room depois)
            androidContext(this@FinSuppApplication)
            // Carrega seus módulos (vamos criar esse appModule abaixo)
            modules(appModule)
        }
    }
}