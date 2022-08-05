@file:Suppress("PackageNaming")

package io.github.achmadhafid.sample_app

import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialogBuilder
import io.github.achmadhafid.lottie_dialog.core.lottieInputDialogBuilder
import io.github.achmadhafid.lottie_dialog.core.lottieLoadingDialogBuilder
import io.github.achmadhafid.lottie_dialog.core.onCancel
import io.github.achmadhafid.lottie_dialog.core.onDismiss
import io.github.achmadhafid.lottie_dialog.core.onShow
import io.github.achmadhafid.zpack.extension.d

object BaseDialog {

    val requestSomething =
        lottieConfirmationDialogBuilder {
            onShow { d("showing dialog") }
            onCancel { d("dialog canceled") }
            onDismiss { d("dialog dismissed") }
        }

    val doSomething =
        lottieLoadingDialogBuilder {
            onShow { d("showing dialog") }
            onCancel { d("dialog canceled") }
            onDismiss { d("dialog dismissed") }
        }

    val askSomething =
        lottieInputDialogBuilder {
            onShow { d("showing dialog") }
            onCancel { d("dialog canceled") }
            onDismiss { d("dialog dismissed") }
        }

}
