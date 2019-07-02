package io.github.achmadhafid.sample_app

import android.util.Log
import io.github.achmadhafid.lottie_dialog.lottieDialogBuilder
import io.github.achmadhafid.lottie_dialog.onCancel
import io.github.achmadhafid.lottie_dialog.onDismiss
import io.github.achmadhafid.lottie_dialog.onShow

object ConfirmationDialog {

    val requestPermission = lottieDialogBuilder {
        onShow {
            Log.d("LottieDialog", "showing dialog")
        }
        onCancel {
            Log.d("LottieDialog", "dialog canceled")
        }
        onDismiss {
            Log.d("LottieDialog", "dialog dismissed")
        }
    }

}
