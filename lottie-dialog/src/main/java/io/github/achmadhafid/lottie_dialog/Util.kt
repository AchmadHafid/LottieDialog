@file:Suppress("WildcardImport", "TooManyFunctions")

package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType

@Suppress("ComplexMethod", "InflateParams")
internal fun inflateDialogView(
    context: Context,
    layoutInflater: LayoutInflater,
    @LayoutRes layout: Int,
    dialogType: LottieDialogType,
    dialogTheme: LottieDialogTheme
): Pair<Dialog, View> {

    val view = layoutInflater.inflate(layout, null)
    val dialog = when (dialogType) {
        LottieDialogType.DIALOG -> {
            val theme = when (dialogTheme) {
                LottieDialogTheme.LIGHT     -> R.style.LottieDialogTheme_Dialog_Light
                LottieDialogTheme.DARK      -> R.style.LottieDialogTheme_Dialog_Dark
                LottieDialogTheme.DAY_NIGHT -> R.style.LottieDialogTheme_Dialog_DayNight
            }
            MaterialAlertDialogBuilder(context, theme)
                .setView(view)
                .create()
                .apply {
                    window?.setBackgroundDrawableResource(R.drawable.lottie_dialog_bg_rounded_corner_dialog)
                }
        }
        LottieDialogType.BOTTOM_SHEET -> {
            val theme = when (dialogTheme) {
                LottieDialogTheme.LIGHT     -> R.style.LottieDialogTheme_BottomSheet_Light
                LottieDialogTheme.DARK      -> R.style.LottieDialogTheme_BottomSheet_Dark
                LottieDialogTheme.DAY_NIGHT -> R.style.LottieDialogTheme_BottomSheet_DayNight
            }
            BottomSheetDialog(context, theme)
                .apply { setContentView(view) }
        }
    }

    return Pair(dialog, view)

}
