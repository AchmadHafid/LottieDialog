@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RawRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.airbnb.lottie.LottieAnimationView
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.zpack.extension.view.gone
import io.github.achmadhafid.zpack.extension.view.makeRoundedCornerOnTop
import io.github.achmadhafid.zpack.extension.view.onSingleClick
import io.github.achmadhafid.zpack.extension.view.setBackgroundColorRes
import io.github.achmadhafid.zpack.extension.view.setPaddingRes
import io.github.achmadhafid.zpack.extension.view.visible
import io.github.achmadhafid.zpack.extension.view.visibleOrGone
import io.github.achmadhafid.zpack.extension.view.withTintRes

data class LottieDialogAnimation(
    @RawRes
    var fileRes: Int? = null,
    @ColorRes @AttrRes
    var bgColorRes: Int? = null,
    @DimenRes
    var heightRes: Int? = null,
    @DimenRes
    var paddingRes: Int? = null,
    @DimenRes
    var paddingTopRes: Int? = null,
    @DimenRes
    var paddingBottomRes: Int? = null,
    @DimenRes
    var paddingLeftRes: Int? = null,
    @DimenRes
    var paddingRightRes: Int? = null,
    var showCloseButton: Boolean = true,
    @ColorRes @AttrRes
    var closeButtonColorRes: Int? = null,
    var lottieAnimationProperties: LottieAnimationView.() -> Unit = {}
) {
    @Suppress("ComplexMethod")
    operator fun invoke(
        animationLayout: FrameLayout,
        animationView: LottieAnimationView,
        btnClose: ImageButton? = null,
        dialog: Dialog,
        type: LottieDialogType
    ) {
        fileRes?.let {
            animationView.visible()
            animationView.setAnimation(it)
            animationView.apply(lottieAnimationProperties)
        } ?: run {
            animationView.gone()
            btnClose?.gone()
            return
        }

        bgColorRes?.let {
            animationView.setBackgroundColorRes(it)
            if (type == LottieDialogType.BOTTOM_SHEET) {
                animationView.makeRoundedCornerOnTop(R.dimen.lottie_dialog_corner_radius_bottom_sheet)
            }
        }

        heightRes?.let {
            with(animationLayout.layoutParams as ConstraintLayout.LayoutParams) {
                height = animationLayout.resources.getDimensionPixelSize(it)
                animationLayout.layoutParams = this
            }
        }

        paddingRes?.let {
            animationView.setPaddingRes(it)
        } ?: run {
            with(animationView.resources) {
                animationView.updatePadding(
                    left   = paddingLeftRes?.let { getDimensionPixelSize(it) } ?: 0,
                    top    = paddingTopRes?.let { getDimensionPixelSize(it) } ?: 0,
                    right  = paddingRightRes?.let { getDimensionPixelSize(it) } ?: 0,
                    bottom = paddingBottomRes?.let { getDimensionPixelSize(it) } ?: 0
                )
            }
        }

        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { withTintRes(it) }
            onSingleClick { dialog.cancel() }
        }
    }
}

fun LottieDialogAnimation.withProperties(properties: LottieAnimationView.() -> Unit) {
    lottieAnimationProperties = properties
}
