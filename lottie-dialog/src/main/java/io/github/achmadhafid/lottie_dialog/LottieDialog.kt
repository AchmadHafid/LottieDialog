package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.math.absoluteValue

internal val LottieDialogHolder   = hashMapOf<LifecycleOwner, Pair<Dialog, Int>>()
internal val LottieObserverHolder = hashMapOf<LifecycleOwner, LifecycleObserver>()

internal fun LifecycleOwner.showLottieDialog(dialog: Dialog, priority: Int) {
    fun showDialog() {
        dialog.show()
        LottieDialogHolder[this] = dialog to priority.absoluteValue
    }

    LottieDialogHolder[this]?.let {
        if (priority.absoluteValue <= it.second) {
            it.first.dismiss()
            showDialog()
        }
    } ?: showDialog()

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

internal fun Fragment.showLottieDialog(dialog: Dialog, priority: Int) =
    viewLifecycleOwner.showLottieDialog(dialog, priority)

fun LifecycleOwner.dismissLottieDialog() {
    LottieDialogHolder.let {
        it[this]?.first?.dismiss()
        it.remove(this)
    }
}

fun Fragment.dismissLottieDialog() = viewLifecycleOwner.dismissLottieDialog()
