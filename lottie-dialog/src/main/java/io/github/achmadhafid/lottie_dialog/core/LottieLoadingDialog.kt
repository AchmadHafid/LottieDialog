@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.lottie_dialog.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.lottie_dialog.inflateView
import io.github.achmadhafid.lottie_dialog.model.LottieDialogAnimation
import io.github.achmadhafid.lottie_dialog.model.LottieDialogCancelOption
import io.github.achmadhafid.lottie_dialog.model.LottieDialogImage
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnCancelListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnDismissListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnShowListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnTimeoutListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogText
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.showLottieDialog
import io.github.achmadhafid.zpack.ktx.addNavigationBarPadding
import io.github.achmadhafid.zpack.ktx.atLeastOreoMR1
import io.github.achmadhafid.zpack.ktx.f
import io.github.achmadhafid.zpack.ktx.gone
import io.github.achmadhafid.zpack.ktx.hasSoftNavigationKeys
import io.github.achmadhafid.zpack.ktx.visibleOrGone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

data class LottieLoadingDialog(
    var type: LottieDialogType = LottieDialogType.DIALOG,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    var timeout: Long? = null,
    var showTimeOutProgress: Boolean = true,
    internal var job: ((DialogInterface, CoroutineScope) -> Job)? = null,
    internal var image: LottieDialogImage? = null,
    internal var animation: LottieDialogAnimation? = null,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener(),
    internal var onTimeoutListener: LottieDialogOnTimeoutListener = LottieDialogOnTimeoutListener()
) {
    val jobs = mutableListOf<Job>()

    @Suppress("ComplexMethod")
    operator fun invoke(
        dialog: Dialog,
        view: View,
        coroutineScope: CoroutineScope,
        useInsideFragment: Boolean = false
    ): Dialog {
        val illustrationLayout: FrameLayout = view.f(R.id.lottie_dialog_illustration_layout)
        val illustrationAnim: LottieAnimationView = view.f(R.id.lottie_dialog_view_anim)
        val illustrationImage: ImageView = view.f(R.id.lottie_dialog_view_image)
        val tvTitle: TextView = view.f(R.id.lottie_dialog_tv_title)
        val pbTimeout: ProgressBar = view.f(R.id.lottie_dialog_progress_bar_timeout)

        animation?.invoke(illustrationLayout, illustrationAnim, null, dialog, type) ?: run {
            illustrationAnim.gone()
            image?.invoke(illustrationLayout, illustrationImage, null, dialog, type) ?: run {
                illustrationImage.gone()
                illustrationLayout.gone()
            }
        }
        title(tvTitle.apply {
            if (atLeastOreoMR1() && type == LottieDialogType.BOTTOM_SHEET && dialog.context.hasSoftNavigationKeys) {
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

        if (atLeastOreoMR1()) {
            view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        if (!useInsideFragment) {
            onDismissListener.invoke(dialog, jobs)
            onCancelListener(dialog)
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
                context,
                layoutInflater,
                R.layout.lottie_dialog_loading,
                lottieDialog.type,
                lottieDialog.theme
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
fun AppCompatActivity.lottieLoadingDialog(
    vararg builders: LottieLoadingDialog.() -> Unit,
    builder: LottieLoadingDialog.() -> Unit
) {
    LottieLoadingDialog.create(
        this,
        layoutInflater,
        lifecycleScope,
        *builders,
        builder
    ).let { showLottieDialog(it) }
}

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieLoadingDialog(
    vararg builders: LottieLoadingDialog.() -> Unit,
    builder: LottieLoadingDialog.() -> Unit
) {
    LottieLoadingDialog.create(
        requireContext(),
        layoutInflater,
        viewLifecycleOwner.lifecycleScope,
        *builders,
        builder
    ).let { showLottieDialog(it) }
}

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
    if (animation == null) {
        animation = LottieDialogAnimation(lottieFileRes)
    }
    animation!!.fileRes = lottieFileRes
}

fun LottieLoadingDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    if (animation == null) {
        animation = LottieDialogAnimation()
    }
    animation!!.apply(builder)
}

fun LottieLoadingDialog.withoutAnimation() {
    animation = null
}

//endregion
//region Image DSL

fun LottieLoadingDialog.withImage(@DrawableRes imageRes: Int) {
    if (image == null) {
        image = LottieDialogImage(imageRes)
    }
    image!!.imageRes = imageRes
}

fun LottieLoadingDialog.withImage(builder: LottieDialogImage.() -> Unit) {
    if (image == null) {
        image = LottieDialogImage()
    }
    image!!.apply(builder)
}

fun LottieLoadingDialog.withoutImage() {
    image = null
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
