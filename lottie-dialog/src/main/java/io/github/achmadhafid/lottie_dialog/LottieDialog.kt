@file:Suppress("TooManyFunctions", "WildcardImport")

package io.github.achmadhafid.lottie_dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//region Main DSL Builder & Extension

data class LottieDialog(
    var type: Type = Type.Dialog,
    var autoDismiss: Boolean = true,
    internal var title: LottieDialogText = LottieDialogText(),
    internal var content: LottieDialogText = LottieDialogText(),
    internal var animation: LottiDialogAnimation = LottiDialogAnimation(),
    internal var positiveButton: LottieDialogButton = LottieDialogButton(textRes = android.R.string.ok),
    internal var negativeButton: LottieDialogButton? = null,
    internal var cancelAbility: LottieDialogCancelAbility = LottieDialogCancelAbility(),
    internal var listener: LottieDialogListener = LottieDialogListener()
) {
    internal operator fun invoke(dialog: AppCompatDialog, view: View) {
        val lav: LottieAnimationView? = animation.lottieFileRes?.let {
            view.findViewById(R.id.lottie_dialog_animation_view)
        }
        val tvTitle    : TextView       = view.findViewById(R.id.lottie_dialog_tv_title)
        val tvContent  : TextView       = view.findViewById(R.id.lottie_dialog_tv_content)
        val btnPositive: MaterialButton = view.findViewById(R.id.lottie_dialog_btn_positive)
        val btnNegative: MaterialButton = view.findViewById(R.id.lottie_dialog_btn_negative)

        lav?.let { animation(type, it) }
        title(tvTitle)
        content(tvContent)
        positiveButton(dialog, btnPositive, autoDismiss)
        negativeButton?.invoke(dialog, btnNegative, autoDismiss)
        cancelAbility(dialog)
        listener(dialog)

        dialog.show()
    }

    enum class Type { Dialog, BottomSheet }
}

@SuppressLint("InflateParams")
private fun lottieDialog(context: Context, layoutInflater: LayoutInflater, builder: LottieDialog.() -> Unit) {
    val lottieDialog = LottieDialog().apply(builder)
    val layout       = lottieDialog.animation.lottieFileRes?.let { R.layout.lottie_dialog } ?: R.layout.no_lottie_dialog
    val view         = layoutInflater.inflate(layout, null)
    val dialog       = when (lottieDialog.type) {
        LottieDialog.Type.Dialog -> MaterialAlertDialogBuilder(context)
            .setView(view)
            .create()
        LottieDialog.Type.BottomSheet -> BottomSheetDialog(context, R.style.LottieDialogTheme_BottomSheet)
            .apply { setContentView(view) }
    }

    lottieDialog(dialog, view)
}

fun AppCompatActivity.lottieDialog(builder: LottieDialog.() -> Unit) {
    lottieDialog(this, layoutInflater, builder)
}

fun Fragment.lottieDialog(builder: LottieDialog.() -> Unit) {
    context?.let { lottieDialog(it, layoutInflater, builder) }
}

fun <T: View> T.lottieDialogOnClick(activity: AppCompatActivity, builder: LottieDialog.() -> Unit) : T {
    setOnClickListener { activity.lottieDialog(builder) }
    return this
}

fun <T: View> T.lottieDialogOnClick(fragment: Fragment, builder: LottieDialog.() -> Unit) : T {
    setOnClickListener { fragment.lottieDialog(builder) }
    return this
}

//endregion
//region Animation DSL Builder & Extension

data class LottiDialogAnimation internal constructor(
    @RawRes
    var lottieFileRes: Int? = null,
    @ColorRes
    var bgColorRes: Int? = null,
    @DrawableRes
    var paddingRes: Int? = null
) {
    internal operator fun invoke(type: LottieDialog.Type, view: LottieAnimationView) {
        lottieFileRes?.let { view.setAnimation(it) }
        bgColorRes?.let {
            view.setBackgroundColor(ContextCompat.getColor(view.context, it))
            if (type == LottieDialog.Type.BottomSheet) {
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        val radius = view?.resources?.getDimension(R.dimen.bottom_sheet_corner_radius) ?: return
                        outline?.setRoundRect(
                            0, 0,
                            view.width, view.height + radius.toInt(),
                            radius
                        )

                    }
                }
                view.clipToOutline = true
            }
        }
        paddingRes?.let { view.setPadding(view.resources.getDimension(it).toInt()) }
    }
}

fun LottieDialog.animation(builder: LottiDialogAnimation.() -> Unit) {
    animation = LottiDialogAnimation().apply(builder)
}

//endregion
//region Text DSL Builder & Extension

data class LottieDialogText internal constructor(
    var text: CharSequence? = "",
    @StringRes
    var textRes: Int? = null,
    @StyleRes
    var styleRes: Int? = null,
    @FontRes
    var fontRes: Int? = null
) {
    internal operator fun invoke(view: TextView) {
        view.text = textRes?.let { view.resources.getString(it) } ?: text
        styleRes?.let { TextViewCompat.setTextAppearance(view, it) }
        fontRes?.let { view.typeface = ResourcesCompat.getFont(view.context, it) }
    }
}

fun LottieDialog.title(builder: LottieDialogText.() -> Unit) {
    title = LottieDialogText().apply(builder)
}

fun LottieDialog.content(builder: LottieDialogText.() -> Unit) {
    content = LottieDialogText().apply(builder)
}

//endregion
//region Action Button DSL Builder & Extension

data class LottieDialogButton internal constructor(
    @StringRes
    var textRes: Int,
    var text: String? = null,
    @DrawableRes
    var iconRes: Int? = null,
    @MaterialButton.IconGravity
    var iconGravity: Int? = null,
    internal var onClickListener: ((AppCompatDialog) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog, button: MaterialButton, autoDismiss: Boolean) {
        button.visibility = View.VISIBLE
        button.text = text ?: dialog.context.getString(textRes)
        iconRes?.let { button.icon = ContextCompat.getDrawable(dialog.context, it) }
        iconGravity?.let { button.iconGravity = it }
        onClickListener?.let {
            button.setOnClickListener {
                it(dialog)
                if (autoDismiss) dialog.dismiss()
            }
        }
    }
}

fun LottieDialog.positiveButton(builder: LottieDialogButton.() -> Unit) {
    positiveButton = LottieDialogButton(textRes = android.R.string.ok).apply(builder)
}

fun LottieDialog.negativeButton(builder: LottieDialogButton.() -> Unit) {
    negativeButton = LottieDialogButton(textRes = android.R.string.cancel).apply(builder)
}

fun LottieDialogButton.onClick(builder: (AppCompatDialog) -> Unit) {
    onClickListener = builder
}

//endregion
//region Cancel DSL Builder & Extension

data class LottieDialogCancelAbility internal constructor(
    var onBackPressed: Boolean = true,
    var onTouchOutside: Boolean = true
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setCancelable(onBackPressed)
        dialog.setCanceledOnTouchOutside(onTouchOutside)
    }
}

fun LottieDialog.cancel(builder: LottieDialogCancelAbility.() -> Unit) {
    cancelAbility = LottieDialogCancelAbility().apply(builder)
}

//endregion
//region Listener DSL Builder & Extension

data class LottieDialogListener internal constructor(
    internal var onShowListener: ((DialogInterface) -> Unit)? = null,
    internal var onDismissListener: ((DialogInterface) -> Unit)? = null,
    internal var onCancelListener: ((DialogInterface) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        onShowListener?.let { dialog.setOnShowListener(it) }
        onDismissListener?.let { dialog.setOnDismissListener(it) }
        onCancelListener?.let { dialog.setOnCancelListener(it) }
    }
}

fun LottieDialog.listener(builder: LottieDialogListener.() -> Unit) {
    listener = LottieDialogListener().apply(builder)
}

fun LottieDialogListener.onShow(builder: (DialogInterface) -> Unit) {
    onShowListener = builder
}

fun LottieDialogListener.onDismiss(builder: (DialogInterface) -> Unit) {
    onDismissListener = builder
}

fun LottieDialogListener.onCancel(builder: (DialogInterface) -> Unit) {
    onCancelListener = builder
}

//endregion
