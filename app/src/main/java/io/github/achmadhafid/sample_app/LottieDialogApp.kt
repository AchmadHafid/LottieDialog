package io.github.achmadhafid.sample_app

import android.app.Application
import io.github.achmadhafid.simplepref.extension.simplePrefNullable
import io.github.achmadhafid.zpack.ktx.applyTheme

@Suppress("unused")
class LottieDialogApp : Application() {

    private var appTheme: Int? by simplePrefNullable()

    override fun onCreate() {
        super.onCreate()
        appTheme?.let { applyTheme(it) }
    }
}
