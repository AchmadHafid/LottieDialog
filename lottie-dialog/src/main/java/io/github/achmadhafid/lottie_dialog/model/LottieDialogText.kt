package io.github.achmadhafid.lottie_dialog.model

import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import io.github.achmadhafid.zpack.extension.view.withFontRes
import io.github.achmadhafid.zpack.extension.view.withTextAppearanceRes
import io.github.achmadhafid.zpack.extension.view.withTextRes

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
        text    ?.let { textView.text     = it }
        textRes ?.let { textView withTextRes it }
        styleRes?.let { textView withTextAppearanceRes it }
        fontRes ?.let { textView withFontRes it }
    }
}
