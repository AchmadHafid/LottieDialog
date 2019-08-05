@file:Suppress("WildcardImport")

package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.button.MaterialButton
import io.github.achmadhafid.zpack.ktx.*
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
    internal var onClickListener: ((Dialog) -> Unit)? = null
) {
    internal operator fun invoke(
        dialog: Dialog,
        button: MaterialButton,
        coroutineScope: CoroutineScope
    ) {
        textRes    ?.let { button.setTextRes(it) }
        text       ?.let { button.text = it }
        iconRes    ?.let { button.setIconRes(it) }
        iconGravity?.let { button.iconGravity = it }

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
            show()
        }
    }
}

fun LottieDialogButton.onClick(function: (Dialog) -> Unit) {
    onClickListener = function
}
