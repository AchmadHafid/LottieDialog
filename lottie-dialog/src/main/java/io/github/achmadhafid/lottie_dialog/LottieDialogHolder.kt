package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

internal val LottieDialogHolder   = hashMapOf<LifecycleOwner, Dialog>()
internal val LottieObserverHolder = hashMapOf<LifecycleOwner, LifecycleObserver>()

internal fun LifecycleOwner.showLottieDialog(dialog: Dialog) {
    LottieDialogHolder[this]?.dismiss()
    LottieDialogHolder[this] = dialog
    dialog.show()

    if (!LottieObserverHolder.containsKey(this)) {
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                dismissLottieDialog()
                lifecycle.removeObserver(this)
                LottieObserverHolder.remove(this@showLottieDialog)
            }
        }.let {
            lifecycle.addObserver(it)
            LottieObserverHolder[this] = it
        }
    }
}

internal fun Fragment.showLottieDialog(dialog: Dialog) = viewLifecycleOwner.showLottieDialog(dialog)

fun LifecycleOwner.dismissLottieDialog() {
    LottieDialogHolder.let {
        it[this]?.dismiss()
        it.remove(this)
    }
}

fun Fragment.dismissLottieDialog() = viewLifecycleOwner.dismissLottieDialog()
