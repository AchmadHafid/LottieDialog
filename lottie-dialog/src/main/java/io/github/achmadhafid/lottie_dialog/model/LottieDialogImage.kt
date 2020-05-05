package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.zpack.extension.view.gone
import io.github.achmadhafid.zpack.extension.view.makeRoundedCornerOnTop
import io.github.achmadhafid.zpack.extension.view.onSingleClick
import io.github.achmadhafid.zpack.extension.view.setBackgroundColorRes
import io.github.achmadhafid.zpack.extension.view.setImageTintList
import io.github.achmadhafid.zpack.extension.view.setPaddingRes
import io.github.achmadhafid.zpack.extension.view.visible
import io.github.achmadhafid.zpack.extension.view.visibleOrGone

data class LottieDialogImage(
    @DrawableRes
    var imageRes: Int? = null,
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
    var closeButtonColorRes: Int? = null
) {
    operator fun invoke(
        imageViewLayout: FrameLayout,
        imageView: ImageView,
        btnClose: ImageButton? = null,
        dialog: Dialog,
        type: LottieDialogType
    ) {
        imageRes?.let {
            imageView.visible()
            imageView.setImageResource(it)
        } ?: run {
            imageView.gone()
            btnClose?.gone()
            return
        }

        bgColorRes?.let {
            imageView.setBackgroundColorRes(it)
            if (type == LottieDialogType.BOTTOM_SHEET) {
                imageView.makeRoundedCornerOnTop(R.dimen.lottie_dialog_corner_radius_bottom_sheet)
            }
        }

        heightRes?.let {
            with(imageViewLayout.layoutParams as ConstraintLayout.LayoutParams) {
                height = imageViewLayout.resources.getDimensionPixelSize(it)
                imageViewLayout.layoutParams = this
            }
        }

        paddingRes?.let {
            imageView.setPaddingRes(it)
        } ?: run {
            with(imageView.resources) {
                imageView.updatePadding(
                    left   = paddingLeftRes?.let { getDimensionPixelSize(it) } ?: 0,
                    top    = paddingTopRes?.let { getDimensionPixelSize(it) } ?: 0,
                    right  = paddingRightRes?.let { getDimensionPixelSize(it) } ?: 0,
                    bottom = paddingBottomRes?.let { getDimensionPixelSize(it) } ?: 0
                )
            }
        }

        btnClose?.visibleOrGone { showCloseButton }
        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { setImageTintList(it) }
            onSingleClick { dialog.cancel() }
        }
    }
}
