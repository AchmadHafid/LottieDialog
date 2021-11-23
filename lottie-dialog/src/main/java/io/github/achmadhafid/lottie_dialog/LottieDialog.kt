package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.absoluteValue

internal val LottieDialogHolder   = hashMapOf<LifecycleOwner, Pair<Dialog, Int>>()
internal val LottieObserverHolder = hashMapOf<LifecycleOwner, LifecycleObserver>()

internal fun LifecycleOwner.showLottieDialog(
    dialog: Dialog,
    onDismissListener: ((DialogInterface) -> Unit)? = null,
    priority: Int
) {
    fun showDialog() {
        dialog.setOnDismissListener {
            if (LottieDialogHolder[this]?.first == it) {
                LottieDialogHolder.remove(this)
            }
            onDismissListener?.invoke(it)
        }
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
        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                dismissLottieDialog()
                lifecycle.removeObserver(this)
                LottieObserverHolder.remove(this@showLottieDialog)
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        LottieObserverHolder[this] = lifecycleObserver
    }
}

internal fun Fragment.showLottieDialog(
    dialog: Dialog,
    onDismissListener: ((DialogInterface) -> Unit)? = null,
    priority: Int
) = viewLifecycleOwner.showLottieDialog(dialog, onDismissListener, priority)

fun LifecycleOwner.dismissLottieDialog() {
    LottieDialogHolder[this]?.first?.dismiss()
}

@Suppress("unused")
fun Fragment.dismissLottieDialog() = viewLifecycleOwner.dismissLottieDialog()
