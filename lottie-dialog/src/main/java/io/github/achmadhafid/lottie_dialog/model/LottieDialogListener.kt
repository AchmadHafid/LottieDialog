@file:Suppress("WildcardImport")

package io.github.achmadhafid.lottie_dialog.model

import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatDialog
import io.github.achmadhafid.zpack.ktx.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LottieDialogOnShowListener internal constructor(
    internal var onShowListener: ((DialogInterface) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setOnShowListener(onShowListener)
    }
}

data class LottieDialogOnDismissListener internal constructor(
    internal var onDismissListener: ((DialogInterface) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog, job: Job? = null) {
        dialog.setOnDismissListener {
            job?.cancel()
            onDismissListener?.invoke(it)
        }
    }
}

data class LottieDialogOnCancelListener internal constructor(
    internal var onCancelListener: ((DialogInterface) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setOnCancelListener(onCancelListener)
    }
}

data class LottieDialogOnTimeoutListener internal constructor(
    internal var onTimeoutListener: (() -> Unit)? = null
) {
    @Suppress("MagicNumber")
    internal operator fun invoke(
        dialog: AppCompatDialog,
        timeout: Long,
        pbTimeout: ProgressBar,
        coroutineScope: CoroutineScope
    ): Job? {
        val progressBarUpdateFrequency = 200

        pbTimeout.max      = timeout.toInt()
        pbTimeout.progress = 0

        return coroutineScope.launch {
            for (index in 1..progressBarUpdateFrequency) {
                val length = timeout / progressBarUpdateFrequency
                delay(length)
                if (pbTimeout.isVisible) {
                    pbTimeout.progress = (index * length).toInt()
                }
            }
            dialog.dismiss()
            onTimeoutListener?.invoke()
        }
    }
}

data class LottieDialogOnInputListener(
    internal var onValidInput: ((String) -> Unit)? = null,
    internal var onInvalidInput: (() -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog, input: String, isValid: Boolean) {
        if (isValid) {
            dialog.dismiss()
            onValidInput?.invoke(input)
        } else {
            onInvalidInput?.invoke()
        }
    }
}
