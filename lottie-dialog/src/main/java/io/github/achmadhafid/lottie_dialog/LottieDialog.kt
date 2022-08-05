@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.absoluteValue

internal val LottieDialogHolder = hashMapOf<LifecycleOwner, Triple<Dialog, Int, String>>()
internal val LottieObserverHolder = hashMapOf<LifecycleOwner, LifecycleObserver>()

internal fun LifecycleOwner.showLottieDialog(
    dialog: Dialog,
    id: String = "",
    onDismissListener: ((DialogInterface) -> Unit)? = null,
    priority: Int
) {
    fun showDialog(id: String) {
        dialog.setOnDismissListener {
            if (LottieDialogHolder[this]?.first == it) {
                LottieDialogHolder.remove(this)
            }
            onDismissListener?.invoke(it)
        }
        dialog.show()
        LottieDialogHolder[this] = Triple(dialog, priority.absoluteValue, id)
    }

    LottieDialogHolder[this]?.let {
        if (priority.absoluteValue <= it.second) {
            it.first.dismiss()
            showDialog(it.third)
        }
    } ?: showDialog(id)

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
    id: String = "",
    onDismissListener: ((DialogInterface) -> Unit)? = null,
    priority: Int
) = viewLifecycleOwner.showLottieDialog(dialog, id, onDismissListener, priority)

fun LifecycleOwner.dismissLottieDialog(dialogId: String = "") {
    LottieDialogHolder[this]?.let { (dialog, _, id) ->
        if (dialogId.isBlank() || dialogId == id) {
            dialog.dismiss()
        }
    }
}

@Suppress("unused")
fun Fragment.dismissLottieDialog(id: String = "") = viewLifecycleOwner.dismissLottieDialog(id)
