@file:Suppress("TooManyFunctions", "WildcardImport", "unused")

package io.github.achmadhafid.lottie_dialog

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
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

//region Main DSL Builder & Extension

data class LottieDialog(
    var type: Type = Type.DIALOG,
    var theme: Theme = Theme.DAY_NIGHT,
    var autoDismiss: Boolean = true,
    internal var animation: LottiDialogAnimation = LottiDialogAnimation(),
    internal var title: LottieDialogText = LottieDialogText(),
    internal var content: LottieDialogText = LottieDialogText(),
    internal var positiveButton: LottieDialogButton = LottieDialogButton(textRes = android.R.string.ok),
    internal var negativeButton: LottieDialogButton? = null,
    internal var cancelAbility: LottieDialogCancelAbility = LottieDialogCancelAbility(),
    internal var onShowListener: LottieDialogOnShowListener = LottieDialogOnShowListener(),
    internal var onDismissListener: LottieDialogOnDismissListener = LottieDialogOnDismissListener(),
    internal var onCancelListener: LottieDialogOnCancelListener = LottieDialogOnCancelListener()
) {
    internal operator fun invoke(dialog: AppCompatDialog, view: View, lifecycleScope: LifecycleCoroutineScope) {
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
        positiveButton(dialog, btnPositive, autoDismiss, lifecycleScope)
        negativeButton?.invoke(dialog, btnNegative, autoDismiss, lifecycleScope)
        cancelAbility(dialog)
        onShowListener(dialog)
        onDismissListener(dialog)
        onCancelListener(dialog)

        dialog.show()
    }

    enum class Type { DIALOG, BOTTOM_SHEET }
    enum class Theme { LIGHT, DARK, DAY_NIGHT }
}

@Suppress("ComplexMethod", "InflateParams")
private fun lottieDialog(
    context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    layoutInflater: LayoutInflater,
    vararg builders: LottieDialog.() -> Unit
) {
    val lottieDialog = LottieDialog()
    builders.forEach { lottieDialog.apply(it) }

    val layout = lottieDialog.animation.lottieFileRes?.let {
        R.layout.lottie_dialog
    } ?: R.layout.no_lottie_dialog

    val view = layoutInflater.inflate(layout, null)

    val dialog = when (lottieDialog.type) {
        LottieDialog.Type.DIALOG -> {
            val theme = when (lottieDialog.theme) {
                LottieDialog.Theme.LIGHT     -> R.style.LottieDialogTheme_Dialog_Light
                LottieDialog.Theme.DARK      -> R.style.LottieDialogTheme_Dialog_Dark
                LottieDialog.Theme.DAY_NIGHT -> R.style.LottieDialogTheme_Dialog_DayNight
            }
            MaterialAlertDialogBuilder(context, theme)
                .setView(view)
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(R.drawable.lottie_dialog_bg_rounded_corner_dialog)
                }
        }
        LottieDialog.Type.BOTTOM_SHEET -> {
            val theme = when (lottieDialog.theme) {
                LottieDialog.Theme.LIGHT     -> R.style.LottieDialogTheme_BottomSheet_Light
                LottieDialog.Theme.DARK      -> R.style.LottieDialogTheme_BottomSheet_Dark
                LottieDialog.Theme.DAY_NIGHT -> R.style.LottieDialogTheme_BottomSheet_DayNight
            }
            BottomSheetDialog(context, theme)
                .apply { setContentView(view) }
        }
    }
    lottieDialog(dialog, view, lifecycleScope)
}

fun AppCompatActivity.lottieDialog(builder: LottieDialog.() -> Unit) {
    lottieDialog(this, lifecycleScope, layoutInflater, builder)
}

fun AppCompatActivity.lottieDialog(defaultBuilder: LottieDialog.() -> Unit, customBuilder: LottieDialog.() -> Unit) {
    lottieDialog(this, lifecycleScope, layoutInflater, defaultBuilder, customBuilder)
}

fun Fragment.lottieDialog(builder: LottieDialog.() -> Unit) {
    context?.let { lottieDialog(it, viewLifecycleOwner.lifecycleScope, layoutInflater, builder) }
}

fun Fragment.lottieDialog(defaultBuilder: LottieDialog.() -> Unit, customBuilder: LottieDialog.() -> Unit) {
    context?.let { lottieDialog(it, viewLifecycleOwner.lifecycleScope, layoutInflater, defaultBuilder, customBuilder) }
}

fun lottieDialogBuilder(builder: LottieDialog.() -> Unit) = builder

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
            if (type == LottieDialog.Type.BOTTOM_SHEET) {
                view.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        val radius = view?.resources?.getDimension(
                            R.dimen.lottie_dialog_corner_radius_bottom_sheet
                        ) ?: return
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

fun LottieDialog.animation(@RawRes lottieFileRes: Int) {
    animation.lottieFileRes = lottieFileRes
}

fun LottieDialog.animation(builder: LottiDialogAnimation.() -> Unit) {
    animation.apply(builder)
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

fun LottieDialog.title(@StringRes textRes: Int) {
    title.textRes = textRes
}

fun LottieDialog.title(text: CharSequence) {
    title.text = text
}

fun LottieDialog.title(builder: LottieDialogText.() -> Unit) {
    title.apply(builder)
}

fun LottieDialog.content(@StringRes textRes: Int) {
    content.textRes = textRes
}

fun LottieDialog.content(text: CharSequence) {
    content.text = text
}

fun LottieDialog.content(builder: LottieDialogText.() -> Unit) {
    content.apply(builder)
}

//endregion
//region Action Button DSL Builder & Extension

data class LottieDialogButton internal constructor(
    @StringRes
    var textRes: Int,
    var text: CharSequence? = null,
    @DrawableRes
    var iconRes: Int? = null,
    @MaterialButton.IconGravity
    var iconGravity: Int? = null,
    var actionDelay: Long? = null,
    internal var onClickListener: ((AppCompatDialog) -> Unit)? = null
) {
    internal operator fun invoke(
        dialog: AppCompatDialog,
        button: MaterialButton,
        autoDismiss: Boolean,
        lifecycleScope: LifecycleCoroutineScope
    ) {
        button.visibility = View.VISIBLE
        button.text = text ?: dialog.context.getString(textRes)
        iconRes?.let { button.icon = ContextCompat.getDrawable(dialog.context, it) }
        iconGravity?.let { button.iconGravity = it }

        button.setOnClickListener {
            button.isClickable = false
            onClickListener?.let {
                lifecycleScope.launch {
                    delay(max(0L, actionDelay ?: 0L))
                    it(dialog)
                }
            }
            if (autoDismiss) dialog.dismiss()
        }
    }
}

fun LottieDialog.positiveButton(@StringRes textRes: Int) {
    positiveButton.textRes = textRes
}

fun LottieDialog.positiveButton(text: CharSequence) {
    positiveButton.text = text
}

fun LottieDialog.positiveButton(builder: LottieDialogButton.() -> Unit) {
    positiveButton.apply(builder)
}

fun LottieDialog.negativeButton(@StringRes textRes: Int) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = textRes)
    } else {
        negativeButton?.textRes = textRes
    }
}

fun LottieDialog.negativeButton(text: CharSequence) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel, text = text)
    } else {
        negativeButton?.text = text
    }
}

fun LottieDialog.negativeButton(builder: LottieDialogButton.() -> Unit) {
    if (negativeButton == null) {
        negativeButton = LottieDialogButton(textRes = android.R.string.cancel)
    }
    negativeButton?.apply(builder)
}

fun LottieDialog.noNegativeButton() {
    negativeButton = null
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
    cancelAbility.apply(builder)
}

//endregion
//region Listener DSL Builder & Extension

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
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setOnDismissListener(onDismissListener)
    }
}

data class LottieDialogOnCancelListener internal constructor(
    internal var onCancelListener: ((DialogInterface) -> Unit)? = null
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setOnCancelListener(onCancelListener)
    }
}

fun LottieDialog.onShow(builder: (DialogInterface) -> Unit) {
    onShowListener = LottieDialogOnShowListener(builder)
}

fun LottieDialog.onDismiss(builder: (DialogInterface) -> Unit) {
    onDismissListener = LottieDialogOnDismissListener(builder)
}

fun LottieDialog.onCancel(builder: (DialogInterface) -> Unit) {
    onCancelListener = LottieDialogOnCancelListener(builder)
}

//endregion
