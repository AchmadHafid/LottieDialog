@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.SurfaceColors
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
        LottieDialogTheme.LIGHT     -> R.style.Theme_LottieDialog_Light
        LottieDialogTheme.DARK      -> R.style.Theme_LottieDialog_Dark
        LottieDialogTheme.DAY_NIGHT -> R.style.Theme_LottieDialog_DayNight
    }
    val cornerSize = context.resources.getDimension(R.dimen.lottie_dialog_corner_radius_dialog)
    val bgShape = RoundRectShape(
        floatArrayOf(cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize, cornerSize),
        null,
        null
    )
    val bgDrawable = ShapeDrawable(bgShape).apply { paint.color = SurfaceColors.SURFACE_3.getColor(view.context) }

    return MaterialAlertDialogBuilder(context, theme)
        .setView(view)
        .create()
        .apply {
            window?.setBackgroundDrawable(bgDrawable)
        }
}

private fun createBottomSheet(
    context: Context,
    view: View,
    dialogTheme: LottieDialogTheme
): Dialog {
    val theme = when (dialogTheme) {
        LottieDialogTheme.LIGHT     -> R.style.Theme_LottieDialog_BottomSheet_Light
        LottieDialogTheme.DARK      -> R.style.Theme_LottieDialog_BottomSheet_Dark
        LottieDialogTheme.DAY_NIGHT -> R.style.Theme_LottieDialog_BottomSheet_DayNight
    }
    return BottomSheetDialog(context, theme).apply { setContentView(view) }
}
