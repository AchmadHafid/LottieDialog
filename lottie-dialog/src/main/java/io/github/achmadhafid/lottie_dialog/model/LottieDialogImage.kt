package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.zpack.ktx.gone
import io.github.achmadhafid.zpack.ktx.makeRoundedCornerOnTop
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.setBackgroundColorRes
import io.github.achmadhafid.zpack.ktx.setImageTintListRes
import io.github.achmadhafid.zpack.ktx.setPaddingRes
import io.github.achmadhafid.zpack.ktx.show
import io.github.achmadhafid.zpack.ktx.visibleOrGone

data class LottieDialogImage(
    @DrawableRes
    var imageRes: Int? = null,
    @ColorRes @AttrRes
    var bgColorRes: Int? = null,
    @DimenRes
    var heightRes: Int? = null,
    @DimenRes
    var paddingRes: Int? = null,
    var showCloseButton: Boolean = true,
    @ColorRes @AttrRes
    var closeButtonColorRes: Int? = null
) {
    operator fun invoke(
        imageView: ImageView,
        btnClose: ImageButton? = null,
        dialog: Dialog,
        type: LottieDialogType
    ) {
        imageRes?.let {
            imageView.show()
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
            with(imageView.layoutParams as FrameLayout.LayoutParams) {
                height = imageView.resources.getDimensionPixelSize(it)
                imageView.layoutParams = this
            }
        }

        paddingRes?.let {
            imageView.setPaddingRes(it)
        }

        btnClose?.visibleOrGone { showCloseButton }
        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { setImageTintListRes(it) }
            onSingleClick { dialog.cancel() }
        }
    }
}
