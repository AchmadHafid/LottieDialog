@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.lottie_dialog.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.lottie_dialog.inflateView
import io.github.achmadhafid.lottie_dialog.model.LottieDialogAnimation
import io.github.achmadhafid.lottie_dialog.model.LottieDialogButton
import io.github.achmadhafid.lottie_dialog.model.LottieDialogCancelOption
import io.github.achmadhafid.lottie_dialog.model.LottieDialogImage
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnCancelListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnDismissListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnShowListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogText
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.showLottieDialog
import io.github.achmadhafid.zpack.extension.view.clearConstraint
import io.github.achmadhafid.zpack.extension.view.f
import io.github.achmadhafid.zpack.extension.view.gone
import io.github.achmadhafid.zpack.extension.view.setEndMargin
import kotlinx.coroutines.CoroutineScope

data class LottieConfirmationDialog(
    var type: LottieDialogType = LottieDialogType.BOTTOM_SHEET,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    internal var image: LottieDialogImage? = null,
    internal var animation: LottieDialogAnimation? = null,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var content: LottieDialogText? = null,
    internal var positiveButton: LottieDialogButton = LottieDialogButton(textRes = android.R.string.ok),
    internal var negativeButton: LottieDialogButton? = null,
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener()
) {
    @SuppressWarnings("LongMethod", "NestedBlockDepth")
    operator fun invoke(
        dialog: Dialog,
        view: View,
        coroutineScope: CoroutineScope,
        useInsideFragment: Boolean = false
    ): Dialog {
        val root: ConstraintLayout = view.f(R.id.root)
        val illustrationLayout: FrameLayout = view.f(R.id.lottie_dialog_illustration_layout)
        val illustrationAnim: LottieAnimationView = view.f(R.id.lottie_dialog_view_anim)
        val illustrationImage: ImageView = view.f(R.id.lottie_dialog_view_image)
        val btnClose: ImageButton = view.f(R.id.lottie_dialog_btn_close)
        val tvTitle: TextView = view.f(R.id.lottie_dialog_tv_title)
        val tvContent: TextView = view.f(R.id.lottie_dialog_tv_content)
        val btnPositive: MaterialButton = view.f(R.id.lottie_dialog_btn_positive)
        val btnNegative: MaterialButton = view.f(R.id.lottie_dialog_btn_negative)

        animation?.let {
            it(illustrationLayout, illustrationAnim, btnClose, dialog, type)
            tvTitle.gravity = Gravity.CENTER
            tvContent.gravity = Gravity.CENTER
//            if (negativeButton == null) {
//                btnPositive.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                with(view.resources.getDimensionPixelSize(R.dimen.large)) {
//                    btnPositive.constraintMarginStart = this
//                    btnPositive.constraintMarginEnd = this
//                }
//            }
        } ?: run {
            illustrationAnim.gone()
            btnClose.gone()

            image?.let {
                it(illustrationLayout, illustrationImage, btnClose, dialog, type)
                tvTitle.gravity = Gravity.CENTER
                tvContent.gravity = Gravity.CENTER
//                if (negativeButton == null) {
//                    btnPositive.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
//                    with(view.resources.getDimensionPixelSize(R.dimen.large)) {
//                        btnPositive.constraintMarginStart = this
//                        btnPositive.constraintMarginEnd = this
//                    }
//                }
            } ?: run {
                illustrationImage.gone()
                btnClose.gone()
                illustrationLayout.gone()

                view.clearConstraint {
                    clear(R.id.lottie_dialog_btn_positive, ConstraintSet.START)
                    clear(R.id.lottie_dialog_btn_negative, ConstraintSet.START)
                }
                with(view.resources.getDimensionPixelSize(R.dimen.medium)) {
                    root.setEndMargin(R.id.lottie_dialog_btn_positive, this)
                    root.setEndMargin(R.id.lottie_dialog_btn_negative, this)
                }
            }
        }

        title(tvTitle)
        content?.invoke(tvContent) ?: tvContent.gone()
        positiveButton(dialog, btnPositive, coroutineScope)
        negativeButton?.invoke(dialog, btnNegative, coroutineScope)
        cancelAbility(dialog)
        onShowListener(dialog)

        if (!useInsideFragment) {
            onDismissListener(dialog)
            onCancelListener(dialog)
        }

        return dialog
    }

    companion object {
        internal fun create(
            context: Context,
            coroutineScope: CoroutineScope,
            layoutInflater: LayoutInflater,
            vararg builders: LottieConfirmationDialog.() -> Unit
        ): Pair<Dialog, ((DialogInterface) -> Unit)?>  {
            val lottieDialog = LottieConfirmationDialog()

            builders.forEach { lottieDialog.apply(it) }

            val (dialog, view) = inflateView(
                context,
                layoutInflater,
                R.layout.lottie_dialog_confirmation,
                lottieDialog.type,
                lottieDialog.theme
            )

            return lottieDialog(dialog, view, coroutineScope) to lottieDialog.onDismissListener.onDismissListener
        }
    }
}

//region Builder Only Function

fun lottieConfirmationDialogBuilder(builder: LottieConfirmationDialog.() -> Unit) = builder

//endregion
//region Activity Extension

@Suppress("SpreadOperator")
fun AppCompatActivity.lottieConfirmationDialog(
    priority: Int,
    vararg builders: LottieConfirmationDialog.() -> Unit,
    builder: LottieConfirmationDialog.() -> Unit
) {
    LottieConfirmationDialog.create(
        this,
        lifecycleScope,
        layoutInflater,
        *builders,
        builder
    ).let { showLottieDialog(it.first, it.second, priority) }
}

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieConfirmationDialog(
    priority: Int,
    vararg builders: LottieConfirmationDialog.() -> Unit,
    builder: LottieConfirmationDialog.() -> Unit
) {
    LottieConfirmationDialog.create(
        requireContext(),
        viewLifecycleOwner.lifecycleScope,
        layoutInflater,
        *builders,
        builder
    ).let { showLottieDialog(it.first, it.second, priority) }
}

//endregion
//region Animation DSL

fun LottieConfirmationDialog.withAnimation(@RawRes lottieFileRes: Int) {
    if (animation == null) {
        animation = LottieDialogAnimation(lottieFileRes)
    }
    animation!!.fileRes = lottieFileRes
}

fun LottieConfirmationDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    if (animation == null) {
        animation = LottieDialogAnimation()
    }
    animation!!.apply(builder)
}

fun LottieConfirmationDialog.withoutAnimation() {
    animation = null
}

//endregion
//region Image DSL

fun LottieConfirmationDialog.withImage(@DrawableRes imageRes: Int) {
    if (image == null) {
        image = LottieDialogImage(imageRes)
    }
    image!!.imageRes = imageRes
}

fun LottieConfirmationDialog.withImage(builder: LottieDialogImage.() -> Unit) {
    if (image == null) {
        image = LottieDialogImage()
    }
    image!!.apply(builder)
}

fun LottieConfirmationDialog.withoutImage() {
    image = null
}

//endregion
//region Text DSL

fun LottieConfirmationDialog.withTitle(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieConfirmationDialog.withTitle(text: CharSequence) {
    title.text = text
}

fun LottieConfirmationDialog.withTitle(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

fun LottieConfirmationDialog.withContent(@StringRes textRes: Int) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.textRes = textRes
}

fun LottieConfirmationDialog.withContent(text: CharSequence) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.text = text
}

fun LottieConfirmationDialog.withContent(builder: LottieDialogText.() -> Unit) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.apply(builder)
}

fun LottieConfirmationDialog.withoutContent() {
    content = null
}

//endregion
//region Action Button DSL

fun LottieConfirmationDialog.withPositiveButton(@StringRes textRes: Int) {
    positiveButton.textRes = textRes
}

fun LottieConfirmationDialog.withPositiveButton(text: CharSequence) {
    positiveButton.text = text
}

fun LottieConfirmationDialog.withPositiveButton(builder: LottieDialogButton.() -> Unit) {
    positiveButton.apply(builder)
}

fun LottieConfirmationDialog.withNegativeButton(@StringRes textRes: Int) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = textRes)
    }
    negativeButton!!.textRes = textRes
}

fun LottieConfirmationDialog.withNegativeButton(text: CharSequence) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel, text = text)
    }
    negativeButton!!.text = text
}

fun LottieConfirmationDialog.withNegativeButton(builder: LottieDialogButton.() -> Unit) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel)
    }
    negativeButton!!.apply(builder)
}

fun LottieConfirmationDialog.withoutNegativeButton() {
    negativeButton = null
}

//endregion
//region Cancel Option DSL

fun LottieConfirmationDialog.withCancelOption(builder: LottieDialogCancelOption.() -> Unit) {
    cancelAbility.apply(builder)
}

//endregion
//region Listener DSL

fun LottieConfirmationDialog.onShow(function: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(function)
}

fun LottieConfirmationDialog.onDismiss(function: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(function)
}

fun LottieConfirmationDialog.onCancel(function: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(function)
}

//endregion
