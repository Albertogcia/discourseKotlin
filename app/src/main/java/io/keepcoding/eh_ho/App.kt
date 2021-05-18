package io.keepcoding.eh_ho

import android.app.Application
import android.content.Context
import io.keepcoding.eh_ho.di.DIProvider

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DIProvider.init(this)
    }
}
