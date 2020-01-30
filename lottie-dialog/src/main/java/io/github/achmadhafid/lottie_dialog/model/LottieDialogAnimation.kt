package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.zpack.ktx.gone
import io.github.achmadhafid.zpack.ktx.makeRoundedCornerOnTop
import io.github.achmadhafid.zpack.ktx.onSingleClick
import io.github.achmadhafid.zpack.ktx.setBackgroundColorRes
import io.github.achmadhafid.zpack.ktx.setImageTintListRes
import io.github.achmadhafid.zpack.ktx.setPaddingRes
import io.github.achmadhafid.zpack.ktx.show
import io.github.achmadhafid.zpack.ktx.visibleOrGone

data class LottieDialogAnimation(
    @RawRes
    var fileRes: Int? = null,
    @ColorRes @AttrRes
    var bgColorRes: Int? = null,
    @DimenRes
    var heightRes: Int? = null,
    @DimenRes
    var paddingRes: Int? = null,
    var animationSpeed: Float = 1f,
    @LottieDrawable.RepeatMode
    var repeatMode: Int = LottieDrawable.RESTART,
    var showCloseButton: Boolean = true,
    @ColorRes @AttrRes
    var closeButtonColorRes: Int? = null
) {
    operator fun invoke(
        animationView: LottieAnimationView,
        btnClose: ImageButton? = null,
        dialog: Dialog,
        type: LottieDialogType
    ) {
        fileRes?.let {
            animationView.show()
            animationView.setAnimation(it)
            animationView.speed      = animationSpeed
            animationView.repeatMode = repeatMode
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
            with(animationView.layoutParams as FrameLayout.LayoutParams) {
                height = animationView.resources.getDimensionPixelSize(it)
                animationView.layoutParams = this
            }
        }

        paddingRes?.let {
            animationView.setPaddingRes(it)
        }

        btnClose?.visibleOrGone { showCloseButton }
        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { setImageTintListRes(it) }
            onSingleClick { dialog.cancel() }
        }
    }
}
