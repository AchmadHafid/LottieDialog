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

internal fun inflateView(
    context: Context,
    layoutInflater: LayoutInflater,
    @LayoutRes layout: Int,
    dialogType: LottieDialogType,
    dialogTheme: LottieDialogTheme
): Pair<Dialog, View> {
    @Suppress("InflateParams")
    val view = layoutInflater.inflate(layout, null)
    val dialog = when (dialogType) {
        LottieDialogType.DIALOG       -> createDialog(context, view, dialogTheme)
        LottieDialogType.BOTTOM_SHEET -> createBottomSheet(context, view, dialogTheme)
    }
    return Pair(dialog, view)
}

private fun createDialog(
    context: Context,
    view: View,
    dialogTheme: LottieDialogTheme
): Dialog {
    val theme = when (dialogTheme) {
        LottieDialogTheme.LIGHT     -> R.style.LottieDialogTheme_Dialog_Light
        LottieDialogTheme.DARK      -> R.style.LottieDialogTheme_Dialog_Dark
        LottieDialogTheme.DAY_NIGHT -> R.style.LottieDialogTheme_Dialog_DayNight
    }
    return MaterialAlertDialogBuilder(context, theme)
        .setView(view)
        .create()
        .apply {
            window?.setBackgroundDrawableResource(R.drawable.lottie_dialog_bg_rounded_corner_dialog)
        }
}

private fun createBottomSheet(
    context: Context,
    view: View,
    dialogTheme: LottieDialogTheme
): Dialog {
    val theme = when (dialogTheme) {
        LottieDialogTheme.LIGHT     -> R.style.LottieDialogTheme_BottomSheet_Light
        LottieDialogTheme.DARK      -> R.style.LottieDialogTheme_BottomSheet_Dark
        LottieDialogTheme.DAY_NIGHT -> R.style.LottieDialogTheme_BottomSheet_DayNight
    }
    return BottomSheetDialog(context, theme).apply { setContentView(view) }
}
