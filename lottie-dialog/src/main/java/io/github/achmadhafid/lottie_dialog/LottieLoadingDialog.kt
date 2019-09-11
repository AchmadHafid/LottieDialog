@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import io.github.achmadhafid.lottie_dialog.model.LottieDialogAnimation
import io.github.achmadhafid.lottie_dialog.model.LottieDialogCancelOption
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnCancelListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnDismissListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnShowListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnTimeoutListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogText
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.zpack.ktx.addNavigationBarPadding
import io.github.achmadhafid.zpack.ktx.fullScreen
import io.github.achmadhafid.zpack.ktx.visibleOrGone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

data class LottieLoadingDialog(
    var type: LottieDialogType = LottieDialogType.DIALOG,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    var timeout: Long? = null,
    var showTimeOutProgress: Boolean = true,
    internal var job: ((DialogInterface, CoroutineScope) -> Job)? = null,
    internal var animation: LottieDialogAnimation = LottieDialogAnimation(),
    internal var title: LottieDialogText = LottieDialogText(),
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener(),
    internal var onTimeoutListener: LottieDialogOnTimeoutListener = LottieDialogOnTimeoutListener()
) {
    val jobs = mutableListOf<Job>()

    operator fun invoke(
        dialog: Dialog,
        view: View,
        coroutineScope: CoroutineScope,
        useInsideFragment: Boolean = false
    ): Dialog {
        val animationView : LottieAnimationView = view.findViewById(R.id.lottie_dialog_view_anim)
        val tvTitle       : TextView            = view.findViewById(R.id.lottie_dialog_tv_title)
        val pbTimeout     : ProgressBar         = view.findViewById(R.id.lottie_dialog_progress_bar_timeout)

        animation(animationView, null, dialog, type)
        title(tvTitle.apply {
            if (type == LottieDialogType.BOTTOM_SHEET && isPhysicalNavigationAvailable) {
                addNavigationBarPadding()
            }
        })
        cancelAbility(dialog)
        onShowListener(dialog)

        job?.let { jobs.add(it(dialog, coroutineScope)) }

        timeout?.let {
            if (it > 0) {
                pbTimeout.visibleOrGone { showTimeOutProgress }
                onTimeoutListener(dialog, it, pbTimeout, coroutineScope)?.let { job ->
                    jobs.add(job)
                }
            }
        }

        dialog.window?.fullScreen()

        if (!useInsideFragment) {
            onDismissListener.invoke(dialog, jobs)
            onCancelListener(dialog)
            dialog.show()
        }

        return dialog
    }

    companion object {
        internal fun create(
            context: Context,
            layoutInflater: LayoutInflater,
            coroutineScope: CoroutineScope,
            vararg builders: LottieLoadingDialog.() -> Unit
        ): Dialog {
            val lottieDialog = LottieLoadingDialog()
            builders.forEach { lottieDialog.apply(it) }

            val (dialog, view) = inflateView(
                context, layoutInflater, R.layout.lottie_dialog_loading,
                lottieDialog.type, lottieDialog.theme
            )

            return lottieDialog.invoke(dialog, view, coroutineScope)
        }
    }
}

//region Builder Only Function

fun lottieLoadingDialogBuilder(builder: LottieLoadingDialog.() -> Unit) = builder

//endregion
//region Activity Extension

@Suppress("SpreadOperator")
fun FragmentActivity.lottieLoadingDialog(
    vararg builders: LottieLoadingDialog.() -> Unit,
    builder: LottieLoadingDialog.() -> Unit
) = LottieLoadingDialog.create(this, layoutInflater, lifecycleScope, *builders, builder)

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieLoadingDialog(
    vararg builders: LottieLoadingDialog.() -> Unit,
    builder: LottieLoadingDialog.() -> Unit
) = LottieLoadingDialog.create(
    requireContext(),
    layoutInflater,
    viewLifecycleOwner.lifecycleScope,
    *builders,
    builder
)

//endregion
//region Job DSL

fun LottieLoadingDialog.withJob(block: (DialogInterface, CoroutineScope) -> Job) {
    job = block
}

fun LottieLoadingDialog.withoutJob() {
    job = null
}

//endregion
//region Animation DSL

fun LottieLoadingDialog.withAnimation(@RawRes lottieFileRes: Int) {
    animation.fileRes = lottieFileRes
}

fun LottieLoadingDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    animation.apply(builder)
}

//endregion
//region Text DSL

fun LottieLoadingDialog.withTitle(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieLoadingDialog.withTitle(text: CharSequence) {
    title.text = text
}

fun LottieLoadingDialog.withTitle(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

//endregion
//region Cancel Option DSL

fun LottieLoadingDialog.withCancelOption(builder: LottieDialogCancelOption.() -> Unit) {
    cancelAbility.apply(builder)
}

//endregion
//region Listener DSL

fun LottieLoadingDialog.onShow(function: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(function)
}

fun LottieLoadingDialog.onDismiss(function: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(function)
}

fun LottieLoadingDialog.onCancel(function: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(function)
}

fun LottieLoadingDialog.onTimeout(function: () -> Unit) {
    onTimeoutListener = LottieDialogOnTimeoutListener(function)
}

//endregion