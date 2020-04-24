@file:Suppress("TooManyFunctions", "unused")

package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import io.github.achmadhafid.lottie_dialog.model.LottieDialogAnimation
import io.github.achmadhafid.lottie_dialog.model.LottieDialogCancelOption
import io.github.achmadhafid.lottie_dialog.model.LottieDialogImage
import io.github.achmadhafid.lottie_dialog.model.LottieDialogInput
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnCancelListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnDismissListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnInputListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogOnShowListener
import io.github.achmadhafid.lottie_dialog.model.LottieDialogText
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.zpack.ktx.ctx
import io.github.achmadhafid.zpack.ktx.f
import io.github.achmadhafid.zpack.ktx.gone

data class LottieInputDialog(
    var type: LottieDialogType = LottieDialogType.DIALOG,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
    internal var image: LottieDialogImage? = null,
    internal var animation: LottieDialogAnimation? = null,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var content: LottieDialogText? = null,
    internal var input: LottieDialogInput = LottieDialogInput(),
    internal var cancelAbility: LottieDialogCancelOption = LottieDialogCancelOption(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener(),
    internal var onInputListener: LottieDialogOnInputListener = LottieDialogOnInputListener()
) {
    operator fun invoke(
        dialog: Dialog,
        view: View,
        useInsideFragment: Boolean = false
    ): Dialog {
        val illustrationLayout : FrameLayout         = view.f(R.id.lottie_dialog_illustration_layout)
        val illustrationAnim   : LottieAnimationView = view.f(R.id.lottie_dialog_view_anim)
        val illustrationImage  : ImageView           = view.f(R.id.lottie_dialog_view_image)
        val btnClose           : ImageButton         = view.f(R.id.lottie_dialog_btn_close)
        val tvTitle            : TextView            = view.f(R.id.lottie_dialog_tv_title)
        val tvContent          : TextView            = view.f(R.id.lottie_dialog_tv_content)
        val edtInput           : EditText            = view.f(R.id.lottie_dialog_edt_input)
        val btnClear           : ImageButton         = view.f(R.id.lottie_dialog_btn_clear)
        val btnDone            : ImageButton         = view.f(R.id.lottie_dialog_btn_done)
        val btnExtra           : ImageButton         = view.f(R.id.lottie_dialog_btn_extra_action)

        animation?.invoke(illustrationLayout, illustrationAnim, btnClose, dialog, type) ?: run {
            illustrationAnim.gone()
            btnClose.gone()

            image?.invoke(illustrationLayout, illustrationImage, btnClose, dialog, type) ?: run {
                illustrationImage.gone()
                btnClose.gone()
                illustrationLayout.gone()
            }
        }
        title.invoke(tvTitle)
        content?.invoke(tvContent) ?: tvContent.gone()
        input(dialog, edtInput, btnClear, btnDone, btnExtra, onInputListener)
        cancelAbility(dialog)
        onShowListener(dialog)

        val inputState = when {
            type == LottieDialogType.DIALOG || (animation == null && image == null) -> SOFT_INPUT_STATE_VISIBLE
            else -> SOFT_INPUT_STATE_HIDDEN
        }
        dialog.window?.setSoftInputMode(inputState or SOFT_INPUT_ADJUST_RESIZE)

        if (!useInsideFragment) {
            onDismissListener(dialog)
            onCancelListener(dialog)
            dialog.show()
        }

        return dialog
    }

    companion object {
        internal fun create(
            context: Context,
            layoutInflater: LayoutInflater,
            vararg builders: LottieInputDialog.() -> Unit
        ): Dialog {
            val lottieDialog = LottieInputDialog()
            builders.forEach { lottieDialog.apply(it) }

            val (dialog, view) = inflateView(
                context, layoutInflater, R.layout.lottie_dialog_input,
                lottieDialog.type, lottieDialog.theme
            )

            return lottieDialog.invoke(dialog, view)
        }
    }
}

//region Builder Only Function

fun lottieInputDialogBuilder(builder: LottieInputDialog.() -> Unit) = builder

//endregion
//region Activity Extension

@Suppress("SpreadOperator")
fun AppCompatActivity.lottieInputDialog(
    vararg builders: LottieInputDialog.() -> Unit,
    builder: LottieInputDialog.() -> Unit
) = LottieInputDialog.create(this, layoutInflater, *builders, builder)

//endregion
//region Fragment Extension

@Suppress("SpreadOperator")
fun Fragment.lottieInputDialog(
    vararg builders: LottieInputDialog.() -> Unit,
    builder: LottieInputDialog.() -> Unit
) = ctx?.let {
    LottieInputDialog.create(it, layoutInflater, *builders, builder)
}

//endregion
//region Animation DSL

fun LottieInputDialog.withAnimation(@RawRes lottieFileRes: Int) {
    if (animation == null) {
        animation = LottieDialogAnimation(lottieFileRes)
    }
    animation!!.fileRes = lottieFileRes
}

fun LottieInputDialog.withAnimation(builder: LottieDialogAnimation.() -> Unit) {
    if (animation == null) {
        animation = LottieDialogAnimation()
    }
    animation!!.apply(builder)
}

fun LottieInputDialog.withoutAnimation() {
    animation = null
}

//endregion
//region Image DSL

fun LottieInputDialog.withImage(@DrawableRes imageRes: Int) {
    if (image == null) {
        image = LottieDialogImage(imageRes)
    }
    image!!.imageRes = imageRes
}

fun LottieInputDialog.withImage(builder: LottieDialogImage.() -> Unit) {
    if (image == null) {
        image = LottieDialogImage()
    }
    image!!.apply(builder)
}

fun LottieInputDialog.withoutImage() {
    image = null
}

//endregion
//region Text DSL

fun LottieInputDialog.withTitle(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieInputDialog.withTitle(text: CharSequence) {
    title.text = text
}

fun LottieInputDialog.withTitle(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

fun LottieInputDialog.withContent(@StringRes textRes: Int) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.textRes = textRes
}

fun LottieInputDialog.withContent(text: CharSequence) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.text = text
}

fun LottieInputDialog.withContent(builder: LottieDialogText.() -> Unit) {
    if (content == null) {
        content = LottieDialogText()
    }
    content!!.apply(builder)
}

fun LottieInputDialog.withoutContent() {
    content = null
}

//endregion
//region Input DSL

fun LottieInputDialog.withInputSpec(builder: LottieDialogInput.() -> Unit) {
    input.apply(builder)
}

fun LottieDialogInput.isValidInput(function: (String) -> Boolean) {
    inputValidator = function
}

//endregion
//region Cancel Option DSL

fun LottieInputDialog.withCancelOption(builder: LottieDialogCancelOption.() -> Unit) {
    cancelAbility.apply(builder)
}

//endregion
//region Dialog Listener DSL

fun LottieInputDialog.onShow(function: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(function)
}

fun LottieInputDialog.onDismiss(function: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(function)
}

fun LottieInputDialog.onCancel(function: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(function)
}

//endregion
//region Input Listener DSL

fun LottieInputDialog.onValidInput(function: (String) -> Unit) {
    onInputListener.onValidInputListener = function
}

fun LottieInputDialog.onInvalidInput(function: () -> Unit) {
    onInputListener.onInvalidInputListener = function
}

//endregion
