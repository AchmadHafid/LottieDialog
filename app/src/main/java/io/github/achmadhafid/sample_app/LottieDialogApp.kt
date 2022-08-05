@file:Suppress("PackageNaming")

package io.github.achmadhafid.sample_app

import android.app.Application
import com.google.android.material.button.MaterialButton
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

fun MaterialButton.onCheckStateChange(isChecked: Boolean) {
    if (!isChecked) isCheckable = true
    this.isChecked = isChecked
    if (isChecked) isCheckable = false
}
