@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog

data class LottieDialogCancelOption(
    var onBackPressed: Boolean = true,
    var onTouchOutside: Boolean = true
) {
    operator fun invoke(dialog: Dialog) {
        dialog.setCancelable(onBackPressed)
        dialog.setCanceledOnTouchOutside(onTouchOutside)
    }
}
