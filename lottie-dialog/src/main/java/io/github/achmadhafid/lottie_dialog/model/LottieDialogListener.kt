package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.content.DialogInterface
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
        pbTimeout: LinearProgressIndicator,
        coroutineScope: CoroutineScope
    ): Job {
        val oneSecond = 1000L
        pbTimeout.max = timeout.toInt()
        pbTimeout.progress = 0

        return coroutineScope.launch {
            var timeLeft = timeout
            while (timeLeft > 0) {
                if (isActive.not()) {
                    return@launch
                }

                val delayDuration = if (timeLeft > oneSecond) oneSecond else timeLeft
                delay(delayDuration)
                timeLeft -= delayDuration

                if (pbTimeout.isVisible) {
                    pbTimeout.setProgressCompat(timeout.toInt() - timeLeft.toInt(), true)
                }
            }
            delay(oneSecond / 2)
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
