package io.github.achmadhafid.lottie_dialog.model

import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import io.github.achmadhafid.zpack.extension.view.setFontRes
import io.github.achmadhafid.zpack.extension.view.setTextAppearanceRes
import io.github.achmadhafid.zpack.extension.view.setTextRes

data class LottieDialogText(
    var text: CharSequence? = null,
    @StringRes
    var textRes: Int? = null,
    @StyleRes
    var styleRes: Int? = null,
    @FontRes
    var fontRes: Int? = null
) {
    operator fun invoke(textView: TextView) {
        textRes ?.let { textView.setTextRes(it) }
        text    ?.let { textView.text     = it }
        styleRes?.let { textView.setTextAppearanceRes(it) }
        fontRes ?.let { textView.setFontRes(it) }
    }
}
