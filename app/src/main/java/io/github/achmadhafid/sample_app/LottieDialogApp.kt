package io.github.achmadhafid.sample_app

import android.app.Application
import io.github.achmadhafid.simplepref.lifecycle.SimplePrefApplication
import io.github.achmadhafid.simplepref.lifecycle.SimplePrefLifecycleOwner
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.applyTheme

@Suppress("unused")
class LottieDialogApp : Application(), SimplePrefLifecycleOwner by SimplePrefApplication() {

    private var appTheme: Int? by simplePref()

    override fun onCreate() {
        super.onCreate()
        attachSimplePrefContext(this)
        appTheme?.let { applyTheme(it) }
    }
}
