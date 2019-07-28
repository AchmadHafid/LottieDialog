package io.github.achmadhafid.lottie_dialog.model

import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import io.github.achmadhafid.zpack.ktx.setFontRes
import io.github.achmadhafid.zpack.ktx.setTextAppearanceRes
import io.github.achmadhafid.zpack.ktx.setTextRes

data class LottieDialogText internal constructor(
    var text: CharSequence? = null,
    @StringRes
    var textRes: Int? = null,
    @StyleRes
    var styleRes: Int? = null,
    @FontRes
    var fontRes: Int? = null
) {
    internal operator fun invoke(textView: TextView) {
        textRes ?.let { textView.setTextRes(it) }
        text    ?.let { textView.text     = it }
        styleRes?.let { textView.setTextAppearanceRes(it) }
        fontRes ?.let { textView.setFontRes(it) }
    }
}
