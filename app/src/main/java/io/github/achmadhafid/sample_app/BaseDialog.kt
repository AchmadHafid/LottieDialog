@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.zpack.ktx.d

object BaseDialog {

    val requestSomething = lottieConfirmationDialogBuilder {
        onShow { d("showing dialog") }
        onCancel { d("dialog canceled") }
        onDismiss { d("dialog dismissed") }
    }

    val doSomething = lottieLoadingDialogBuilder {
        onShow { d("showing dialog") }
        onCancel { d("dialog canceled") }
        onDismiss { d("dialog dismissed") }
    }

    val askSomething = lottieInputDialogBuilder {
        onShow { d("showing dialog") }
        onCancel { d("dialog canceled") }
        onDismiss { d("dialog dismissed") }
    }

}
