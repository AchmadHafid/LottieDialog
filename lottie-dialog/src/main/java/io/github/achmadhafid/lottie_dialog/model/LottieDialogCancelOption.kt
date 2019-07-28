package io.github.achmadhafid.lottie_dialog.model

import androidx.appcompat.app.AppCompatDialog

data class LottieDialogCancelOption internal constructor(
    var onBackPressed: Boolean = true,
    var onTouchOutside: Boolean = true
) {
    internal operator fun invoke(dialog: AppCompatDialog) {
        dialog.setCancelable(onBackPressed)
        dialog.setCanceledOnTouchOutside(onTouchOutside)
    }
}
