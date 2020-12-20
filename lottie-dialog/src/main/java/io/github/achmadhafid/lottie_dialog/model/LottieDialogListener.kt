package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.content.DialogInterface
import android.widget.ProgressBar
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LottieDialogOnShowListener(
    var onShowListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setOnShowListener(onShowListener)
    }
}

data class LottieDialogOnDismissListener(
    var onDismissListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog, jobs: MutableList<Job> = mutableListOf()) {
        dialog.setOnDismissListener {
            jobs.forEach { job -> job.cancel() }
            jobs.clear()
            onDismissListener?.invoke(it)
        }
    }
}

data class LottieDialogOnCancelListener(
    var onCancelListener: ((DialogInterface) -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setOnCancelListener(onCancelListener)
    }
}

data class LottieDialogOnTimeoutListener(
    var onTimeoutListener: (() -> Unit)? = null
) {
    @Suppress("MagicNumber")
    operator fun invoke(
        dialog: Dialog,
        timeout: Long,
        pbTimeout: ProgressBar,
        coroutineScope: CoroutineScope
    ): Job {
        val progressBarUpdateFrequency = 100

        pbTimeout.max      = timeout.toInt()
        pbTimeout.progress = 0

        return coroutineScope.launch {
            val delay = timeout / progressBarUpdateFrequency
            for (index in 1..progressBarUpdateFrequency) {
                delay(delay)
                if (pbTimeout.isVisible) {
                    pbTimeout.progress = (index * delay).toInt()
                }
            }
            dialog.dismiss()
            onTimeoutListener?.invoke()
        }
    }
}

data class LottieDialogOnInputListener(
    var onValidInputListener: ((String) -> Unit)? = null,
    var onInvalidInputListener: (() -> Unit)? = null
) {
    operator fun invoke(dialog: Dialog, input: String, isValid: Boolean) {
        if (isValid) {
            dialog.dismiss()
            onValidInputListener?.invoke(input)
        } else {
            onInvalidInputListener?.invoke()
        }
    }
}
