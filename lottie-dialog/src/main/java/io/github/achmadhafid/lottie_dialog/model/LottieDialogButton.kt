@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.zpack.extension.view.onSingleClick
import io.github.achmadhafid.zpack.extension.view.visible
import io.github.achmadhafid.zpack.extension.view.withTextRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

data class LottieDialogButton(
    var text: CharSequence? = null,
    @StringRes
    var textRes: Int? = null,
    @DrawableRes
    var iconRes: Int? = null,
    @MaterialButton.IconGravity
    var iconGravity: Int? = null,
    var actionDelay: Long? = null,
    @DimenRes
    var cornerRadiusRes: Int? = null,
    internal var onClickListener: ((Dialog) -> Unit)? = null
) {
    internal operator fun invoke(
        dialog: Dialog,
        button: MaterialButton,
        coroutineScope: CoroutineScope
    ) {
        textRes?.let { button withTextRes it }
        text?.let { button.text = it }
        iconRes?.let { button.setIconResource(it) }
        iconGravity?.let { button.iconGravity = it }
        cornerRadiusRes?.let { button.setCornerRadiusResource(it) }

        with(button) {
            onSingleClick {
                onClickListener?.let {
                    coroutineScope.launch {
                        delay(max(0L, actionDelay ?: 0L))
                        it(dialog)
                    }
                }
                dialog.dismiss()
            }
            visible()
        }
    }
}

fun LottieDialogButton.onClick(function: (Dialog) -> Unit) {
    onClickListener = function
}
