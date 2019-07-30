@file:Suppress("TooManyFunctions", "WildcardImport", "unused")

package io.github.achmadhafid.lottie_dialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import io.github.achmadhafid.lottie_dialog.model.*
import io.github.achmadhafid.zpack.ktx.adjustKeyboard
import io.github.achmadhafid.zpack.ktx.gone

data class LottieInputDialog(
    var type: LottieDialogType = LottieDialogType.DIALOG,
    var theme: LottieDialogTheme = LottieDialogTheme.DAY_NIGHT,
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
    internal operator fun invoke(dialog: AppCompatDialog, view: View): AppCompatDialog {
        val viewAnim  : LottieAnimationView = view.findViewById(R.id.lottie_dialog_view_anim)
        val btnClose  : ImageButton         = view.findViewById(R.id.lottie_dialog_btn_close)
        val tvTitle   : TextView            = view.findViewById(R.id.lottie_dialog_tv_title)
        val tvContent : TextView            = view.findViewById(R.id.lottie_dialog_tv_content)
        val edtInput  : EditText            = view.findViewById(R.id.lottie_dialog_edt_input)
        val btnClear  : ImageButton         = view.findViewById(R.id.lottie_dialog_btn_clear)
        val btnDone   : ImageButton         = view.findViewById(R.id.lottie_dialog_btn_done)
        val btnExtra  : ImageButton         = view.findViewById(R.id.lottie_dialog_btn_extra_action)

        animation?.invoke(viewAnim, btnClose, dialog, type) ?: run {
            viewAnim.gone()
            btnClose.gone()
        }
        title.invoke(tvTitle)
        content?.invoke(tvContent) ?: tvContent.gone()
        input(dialog, edtInput, btnClear, btnDone, btnExtra, onInputListener)
        cancelAbility(dialog)
        onShowListener(dialog)
        onDismissListener(dialog)
        onCancelListener(dialog)

        return dialog.apply {
            window?.adjustKeyboard()
            show()
        }
    }

    companion object {
        internal fun create(
            context: Context,
            layoutInflater: LayoutInflater,
            vararg builders: LottieInputDialog.() -> Unit
        ): AppCompatDialog {
            val lottieDialog = LottieInputDialog()
            builders.forEach { lottieDialog.apply(it) }

            val (dialog, view) = inflateDialogView(
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

fun AppCompatActivity.lottieInputDialog(builder: LottieInputDialog.() -> Unit) =
    LottieInputDialog.create(this, layoutInflater, builder)

fun AppCompatActivity.lottieInputDialog(
    parentBuilder: LottieInputDialog.() -> Unit,
    childBuilder: LottieInputDialog.() -> Unit
) = LottieInputDialog.create(this, layoutInflater, parentBuilder, childBuilder)

//endregion
//region Fragment Extension

fun Fragment.lottieInputDialog(builder: LottieInputDialog.() -> Unit) =
    context?.let {
        LottieInputDialog.create(it, layoutInflater, builder)
    } ?: TODO("Context required")

fun Fragment.lottieInputDialog(
    parentBuilder: LottieInputDialog.() -> Unit,
    childBuilder: LottieInputDialog.() -> Unit
) = context?.let {
    LottieInputDialog.create(it, layoutInflater, parentBuilder, childBuilder)
} ?: TODO("Context required")

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
    onInputListener.onValidInput = function
}

fun LottieInputDialog.onInvalidInput(function: () -> Unit) {
    onInputListener.onInvalidInput = function
}

//endregion
