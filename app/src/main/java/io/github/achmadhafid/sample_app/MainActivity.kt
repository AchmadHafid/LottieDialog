@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.achmadhafid.lottie_dialog.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region View Binding

    private val appBarLayout: AppBarLayout by bindView(R.id.appBarLayout)
    private val scrollView: NestedScrollView by bindView(R.id.scrollView)
    private val btnDialogTypeDialog: MaterialButton by bindView(R.id.btn_dialog_type_dialog)
    private val btnDialogTypeBottomSheet: MaterialButton by bindView(R.id.btn_dialog_type_bottom_sheet)
    private val smShowLottieAnimation: SwitchMaterial by bindView(R.id.sm_showLottieAnimation)
    private val btnAnimationCentered: MaterialButton by bindView(R.id.btn_animation_centered)
    private val btnAnimationFull: MaterialButton by bindView(R.id.btn_animation_full)
    private val smShowNegativeButton: SwitchMaterial by bindView(R.id.sm_showNegativeButton)
    private val smShowButtonIcon: SwitchMaterial by bindView(R.id.sm_showButtonIcon)
    private val btnIconSvg: MaterialButton by bindView(R.id.btn_icon_svg)
    private val btnIconBitmap: MaterialButton by bindView(R.id.btn_icon_bitmap)
    private val smUseCustomText: SwitchMaterial by bindView(R.id.sm_useCustomText)
    private val smEnableAutoDismiss: SwitchMaterial by bindView(R.id.sm_enableAutoDismiss)
    private val smCancelOnBackPressed: SwitchMaterial by bindView(R.id.sm_cancelOnBackPressed)
    private val smCancelOnTouchOutside: SwitchMaterial by bindView(R.id.sm_cancelOnTouchOutside)
    private val btnShowDialog: MaterialButton by bindView(R.id.btn_showDialog)

    //endregion
    //region Properties

    private val isBottomSheet
        get() = btnDialogTypeBottomSheet.isChecked
    private val shouldShowAnimation
        get() = smShowLottieAnimation.isChecked
    private val centeredAnimation
        get() = btnAnimationCentered.isChecked
    private val fullSizeAnimation
        get() = btnAnimationFull.isChecked
    private val shouldShowNegativeButton
        get() = smShowNegativeButton.isChecked
    private val shouldShowButtonIcon
        get() = smShowButtonIcon.isChecked
    private val shouldUseIconSvg
        get() = btnIconSvg.isChecked
    private val shouldUseCustomText
        get() = smUseCustomText.isChecked
    private val shouldEnableAutoDismiss
        get() = smEnableAutoDismiss.isChecked
    private val shouldCancelOnBackPressed
        get() = smCancelOnBackPressed.isChecked
    private val shouldCancelOnTouchOutside
        get() = smCancelOnTouchOutside.isChecked

    //endregion

    //region Lifecycle Callback

    @Suppress("LongMethod", "ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
            appBarLayout.isSelected = scrollView.canScrollVertically(-1)
        })

        //region setup dialog type selection

        btnDialogTypeDialog.addOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnDialogTypeDialog.isCheckable      = false
                btnDialogTypeBottomSheet.isCheckable = true
            }
        }
        btnDialogTypeBottomSheet.addOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnDialogTypeDialog.isCheckable      = true
                btnDialogTypeBottomSheet.isCheckable = false
            }
        }
        btnDialogTypeDialog.isChecked = true

        //endregion
        //region setup animation type selection

        val onCheckedListenerCentered = MaterialButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnAnimationCentered.isCheckable = false
                btnAnimationFull.isCheckable     = true
            }
        }
        val onCheckedListenerFullSize = MaterialButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnAnimationFull.isCheckable     = false
                btnAnimationCentered.isCheckable = true
            }
        }
        smShowLottieAnimation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnAnimationCentered.isEnabled   = true
                btnAnimationCentered.isCheckable = true
                btnAnimationFull.isEnabled       = true
                btnAnimationFull.isCheckable     = true

                btnAnimationCentered.addOnCheckedChangeListener(onCheckedListenerCentered)
                btnAnimationFull.addOnCheckedChangeListener(onCheckedListenerFullSize)

                btnAnimationCentered.isChecked   = true
            } else {
                btnAnimationCentered.removeOnCheckedChangeListener(onCheckedListenerCentered)
                btnAnimationFull.removeOnCheckedChangeListener(onCheckedListenerFullSize)

                btnAnimationCentered.isCheckable   = true
                btnAnimationCentered.isChecked = false
                btnAnimationCentered.isEnabled = false
                btnAnimationFull.isCheckable   = true
                btnAnimationFull.isChecked     = false
                btnAnimationFull.isEnabled     = false
            }
        }

        //endregion
        //region setup icon type selection

        val onCheckedListenerSvg = MaterialButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnIconSvg.isCheckable    = false
                btnIconBitmap.isCheckable = true
            }
        }
        val onCheckedListenerBitmap = MaterialButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnIconBitmap.isCheckable = false
                btnIconSvg.isCheckable    = true
            }
        }
        smShowButtonIcon.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnIconSvg.isEnabled   = true
                btnIconSvg.isCheckable = true
                btnIconBitmap.isEnabled       = true
                btnIconBitmap.isCheckable     = true

                btnIconSvg.addOnCheckedChangeListener(onCheckedListenerCentered)
                btnIconBitmap.addOnCheckedChangeListener(onCheckedListenerFullSize)

                btnIconSvg.isChecked   = true
            } else {
                btnIconSvg.removeOnCheckedChangeListener(onCheckedListenerSvg)
                btnIconBitmap.removeOnCheckedChangeListener(onCheckedListenerBitmap)

                btnIconSvg.isCheckable   = true
                btnIconSvg.isChecked = false
                btnIconSvg.isEnabled = false
                btnIconBitmap.isCheckable   = true
                btnIconBitmap.isChecked     = false
                btnIconBitmap.isEnabled     = false
            }
        }

        //endregion
        //region setup auto dismiss & cancel options

        arrayOf(smEnableAutoDismiss, smCancelOnBackPressed, smCancelOnTouchOutside)
            .forEach {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked && !shouldCancelOnBackPressed && !shouldCancelOnTouchOutside) {
                        when (it) {
                            smEnableAutoDismiss -> smCancelOnBackPressed.isChecked = true
                            else -> smEnableAutoDismiss.isChecked = true
                        }
                    }
                }
            }

        //endregion
        //region build & show dialog

        btnShowDialog.setOnClickListener {
            lottieDialog {
                if (isBottomSheet) type = LottieDialog.Type.BottomSheet
                autoDismiss = shouldEnableAutoDismiss
                if (shouldShowAnimation) {
                    animation {
                        if (centeredAnimation) {
                            lottieFileRes = R.raw.lottie_animation_location
                            paddingRes = R.dimen.view_padding
                        }
                        if (fullSizeAnimation) {
                            lottieFileRes = R.raw.lottie_animation_notification
                            bgColorRes = R.color.bg_dialog_notification
                        }
                    }
                }
                title {
                    textRes = R.string.dialog_location_title
                    if (shouldUseCustomText) {
                        fontRes  = R.font.lobster_two_bold
                        styleRes = R.style.AppTheme_TextAppearance
                    }
                }
                content {
                    textRes = R.string.dialog_location_content
                    if (shouldUseCustomText) {
                        fontRes = R.font.sofia
                    }
                }
                positiveButton {
                    textRes = android.R.string.ok
                    if (shouldShowButtonIcon) {
                        iconRes = if (shouldUseIconSvg)
                            R.drawable.ic_check_black_18dp_svg
                        else
                            R.drawable.ic_check_black_18dp
                    }
                    onClick { toastShort(R.string.message_on_positive) }
                }
                if (shouldShowNegativeButton) {
                    negativeButton {
                        textRes = R.string.negative_button
                        if (shouldShowButtonIcon) {
                            iconRes = if (shouldUseIconSvg)
                                R.drawable.ic_close_black_18dp_svg
                            else
                                R.drawable.ic_close_black_18dp
                        }
                        onClick { toastShort(R.string.message_on_negative) }
                    }
                }
                cancel {
                    onBackPressed  = shouldCancelOnBackPressed
                    onTouchOutside = shouldCancelOnTouchOutside
                }
                listener {
                    onShow {
                        Log.d("LottieDialog", "showing permission dialog")
                    }
                    onCancel {
                        toastShort("You cancel the dialog")
                    }
                    onDismiss {
                        Log.d("LottieDialog", "permission dialog dismissed")
                    }
                }
            }
        }

        //endregion
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        toggleTheme()
        return true
    }

    //endregion

}
