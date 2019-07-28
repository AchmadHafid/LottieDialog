@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ramotion.fluidslider.FluidSlider
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.simplepref.extension.clearLocalPref
import io.github.achmadhafid.simplepref.extension.liveDataPref
import io.github.achmadhafid.simplepref.extension.simplePref
import io.github.achmadhafid.zpack.ktx.d
import io.github.achmadhafid.zpack.ktx.resolveColor
import io.github.achmadhafid.zpack.ktx.toastShort
import kotlin.math.floor

class ConfirmationDialogFragment : Fragment(R.layout.fragment_confirmation_dialog) {

    //region Preference

    private var themeDayNight by simplePref(isLocal = true) { true }
    private var themeLight by simplePref(isLocal = true) { false }
    private var themeDark by simplePref(isLocal = true) { false }
    private var typeDialog by simplePref(isLocal = true) { true }
    private var typeBottomSheet by simplePref(isLocal = true) { false }
    private var showAnimation by simplePref(isLocal = true) { false }
    private var animationCentered by simplePref(isLocal = true) { true }
    private var animationFull by simplePref(isLocal = true) { false }
    private var closeButton by simplePref(isLocal = true) { true }
    private var negativeButton by simplePref(isLocal = true) { false }
    private var showIcon by simplePref(isLocal = true) { false }
    private var iconSvg by simplePref(isLocal = true) { true }
    private var iconBitmap by simplePref(isLocal = true) { false }
    private var actionDelay by simplePref(isLocal = true) { 0L }
    private var useCustomText by simplePref(isLocal = true) { false }
    private var cancelOnBackPressed by simplePref(isLocal = true) { true }
    private var cancelOnTouchOutside by simplePref(isLocal = true) { true }

    //endregion

    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_action_bar, menu)
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region bind views

        val tgTheme: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_theme)
        val btnThemeDayNight: MaterialButton = view.findViewById(R.id.btn_theme_day_night)
        val btnThemeLight: MaterialButton = view.findViewById(R.id.btn_theme_light)
        val btnThemeDark: MaterialButton = view.findViewById(R.id.btn_theme_dark)
        val tgDialogType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_dialog_type)
        val btnDialogTypeDialog: MaterialButton = view.findViewById(R.id.btn_dialog_type_dialog)
        val btnDialogTypeBottomSheet: MaterialButton = view.findViewById(R.id.btn_dialog_type_bottom_sheet)
        val smShowLottieAnimation: SwitchMaterial = view.findViewById(R.id.sm_showLottieAnimation)
        val tgAnimationType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_animation_type)
        val smShowLottieAnimationCloseButton: SwitchMaterial = view.findViewById(R.id.sm_showLottieAnimationCloseButton)
        val btnAnimationCentered: MaterialButton = view.findViewById(R.id.btn_animation_centered)
        val btnAnimationFull: MaterialButton = view.findViewById(R.id.btn_animation_full)
        val smShowNegativeButton: SwitchMaterial = view.findViewById(R.id.sm_showNegativeButton)
        val smShowButtonIcon: SwitchMaterial = view.findViewById(R.id.sm_showButtonIcon)
        val tgIconType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_icon_type)
        val btnIconSvg: MaterialButton = view.findViewById(R.id.btn_icon_svg)
        val btnIconBitmap: MaterialButton = view.findViewById(R.id.btn_icon_bitmap)
        val tvActionDelay: TextView = view.findViewById(R.id.tv_actionDelay)
        val sbActionDelay: FluidSlider = view.findViewById(R.id.sb_actionDelay)
        val smUseCustomText: SwitchMaterial = view.findViewById(R.id.sm_useCustomText)
        val smCancelOnBackPressed: SwitchMaterial = view.findViewById(R.id.sm_cancelOnBackPressed)
        val smCancelOnTouchOutside: SwitchMaterial = view.findViewById(R.id.sm_cancelOnTouchOutside)

        //endregion

        //region setup theme options

        tgTheme.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                btnThemeDayNight.id -> themeDayNight = isChecked
                btnThemeLight.id    -> themeLight    = isChecked
                btnThemeDark.id     -> themeDark     = isChecked
            }
        }
        liveDataPref(::themeDayNight) {
            btnThemeDayNight.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        liveDataPref(::themeLight) {
            btnThemeLight.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        liveDataPref(::themeDark) {
            btnThemeDark.apply {
                isChecked   = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup dialog type options

        tgDialogType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                btnDialogTypeDialog.id      -> typeDialog      = isChecked
                btnDialogTypeBottomSheet.id -> typeBottomSheet = isChecked
            }
        }
        liveDataPref(::typeDialog) {
            btnDialogTypeDialog.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        liveDataPref(::typeBottomSheet) {
            btnDialogTypeBottomSheet.apply {
                isChecked   = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup animation type options

        smShowLottieAnimation.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showAnimation = isChecked
            }
            liveDataPref(::showAnimation) {
                isChecked = it
                val btnList = listOf(
                    btnAnimationCentered to animationCentered,
                    btnAnimationFull     to animationFull
                )
                if (isChecked) {
                    btnList.apply {
                        map { item -> item.first }.forEach { button ->
                            button.apply {
                                isEnabled   = true
                                isCheckable = true
                            }
                        }
                        forEach { item ->
                            item.apply {
                                first.isChecked = second
                            }
                        }
                    }
                } else {
                    btnList.map { item -> item.first }.forEach { button ->
                        button.apply {
                            isCheckable = true
                            isChecked = false
                            isEnabled = false
                        }
                    }
                }
                smShowLottieAnimationCloseButton.isEnabled = isChecked
            }
        }
        tgAnimationType.addOnButtonCheckedListener { _, id, isChecked ->
            if (smShowLottieAnimation.isChecked) {
                when (id) {
                    btnAnimationCentered.id -> animationCentered = isChecked
                    btnAnimationFull.id     -> animationFull     = isChecked
                }
            }
        }
        btnAnimationCentered.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (smShowLottieAnimation.isChecked) {
                    animationCentered = isChecked
                }
            }
            liveDataPref(::animationCentered) {
                isChecked   = it
                isCheckable = !it
            }
        }
        btnAnimationFull.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (smShowLottieAnimation.isChecked) {
                    animationFull = isChecked
                }
            }
            liveDataPref(::animationFull) {
                isChecked   = it
                isCheckable = !it
            }
        }
        smShowLottieAnimationCloseButton.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (smShowLottieAnimation.isChecked) {
                    closeButton = isChecked
                }
            }
            liveDataPref(::closeButton) {
                isChecked = it
            }
        }

        //endregion
        //region setup negative button options

        smShowNegativeButton.apply {
            setOnCheckedChangeListener { _, isChecked ->
                negativeButton = isChecked
            }
            liveDataPref(::negativeButton) {
                isChecked = it
            }
        }

        //endregion
        //region setup icon type options

        smShowButtonIcon.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showIcon = isChecked
            }
            liveDataPref(::showIcon) {
                isChecked = it
                val btnList = listOf(
                    btnIconSvg    to iconSvg,
                    btnIconBitmap to iconBitmap
                )
                if (isChecked) {
                    btnList.apply {
                        map { item -> item.first }.forEach { button ->
                            button.apply {
                                isEnabled   = true
                                isCheckable = true
                            }
                        }
                        forEach { item ->
                            item.apply {
                                first.isChecked = second
                            }
                        }
                    }
                } else {
                    btnList.map { item -> item.first }.forEach { button ->
                        button.apply {
                            isCheckable = true
                            isChecked   = false
                            isEnabled   = false
                        }
                    }
                }
            }
        }
        tgIconType.addOnButtonCheckedListener { _, id, isChecked ->
            if (smShowButtonIcon.isChecked) {
                when (id) {
                    btnIconSvg.id    -> iconSvg    = isChecked
                    btnIconBitmap.id -> iconBitmap = isChecked
                }
            }
        }
        btnIconSvg.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (smShowButtonIcon.isChecked) {
                    iconSvg = isChecked
                }
            }
            liveDataPref(::iconSvg) {
                isChecked   = it
                isCheckable = !it
            }
        }
        btnIconBitmap.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (smShowButtonIcon.isChecked) {
                    iconBitmap = isChecked
                }
            }
            liveDataPref(::iconBitmap) {
                isChecked   = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup action delay

        @Suppress("MagicNumber")
        with(sbActionDelay) {
            colorBubble     = context.resolveColor(R.attr.colorPrimary)
            colorBubbleText = context.resolveColor(R.attr.colorOnPrimary)
            colorBar        = colorBubble

            positionListener = {
                bubbleText = "${(floor(1000 * position / 50) * 50).toLong()}"
            }

            val height by lazy { tvActionDelay.height.toFloat() }
            val animation by lazy { ViewAnimator.animate(tvActionDelay) }

            beginTrackingListener = {
                animation.translationY(0F, -height)
                    .alpha(1F, 0F)
                    .duration(200L)
                    .start()
            }
            endTrackingListener = {
                animation.translationY(-height, 0F)
                    .alpha(0F, 1F)
                    .duration(400L)
                    .start()
                actionDelay = (floor(1000 * position / 50) * 50).toLong()
            }

            liveDataPref(::actionDelay) {
                position   = actionDelay / 1000F
                bubbleText = "$actionDelay"
            }
        }

        //endregion
        //region setup custom text options

        smUseCustomText.apply {
            setOnCheckedChangeListener { _, isChecked ->
                useCustomText = isChecked
            }
            liveDataPref(::useCustomText) {
                isChecked = it
            }
        }

        //endregion
        //region setup auto dismiss & cancel options

        smCancelOnBackPressed.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnBackPressed = isChecked
            }
            liveDataPref(::cancelOnBackPressed) {
                isChecked = it
            }
        }

        smCancelOnTouchOutside.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnTouchOutside = isChecked
            }
            liveDataPref(::cancelOnTouchOutside) {
                isChecked = it
            }
        }

        //endregion
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_dialog -> {
                lottieConfirmationDialog(BaseDialog.requestSomething) {
                    //region setup type
                    when {
                        typeDialog      -> type = LottieDialogType.DIALOG
                        typeBottomSheet -> type = LottieDialogType.BOTTOM_SHEET
                    }
                    //endregion
                    //region setup theme
                    when {
                        themeDayNight -> theme = LottieDialogTheme.DAY_NIGHT
                        themeLight    -> theme = LottieDialogTheme.LIGHT
                        themeDark     -> theme = LottieDialogTheme.DARK
                    }
                    //endregion
                    //region setup animation
                    if (showAnimation) {
                        withAnimation {
                            if (animationCentered) {
                                fileRes    = R.raw.lottie_animation_location
                                paddingRes = R.dimen.lottie_dialog_animation_padding
                            }
                            if (animationFull) {
                                fileRes    = R.raw.lottie_animation_notification
                                bgColorRes = R.color.bg_dialog_notification
                            }
                            showCloseButton = closeButton
                        }
                    } else {
                        withoutAnimation()
                    }
                    //endregion
                    //region setup title
                    withTitle {
                        textRes = when {
                            animationCentered -> R.string.dialog_location_title
                            animationFull     -> R.string.dialog_notification_title
                            else              -> R.string.dialog_location_title
                        }
                        if (useCustomText) {
                            fontRes  = R.font.lobster_two_bold
                            styleRes = R.style.AppTheme_TextAppearance
                        }
                    }
                    //endregion
                    //region setup content
                    withContent {
                        textRes = when {
                            animationCentered -> R.string.dialog_location_content
                            animationFull     -> R.string.dialog_notification_content
                            else              -> R.string.dialog_location_content
                        }
                        if (useCustomText) {
                            fontRes = R.font.sofia
                        }
                    }
                    //endregion
                    //region setup positive button
                    withPositiveButton {
                        textRes     = android.R.string.ok
                        actionDelay = this@ConfirmationDialogFragment.actionDelay
                        if (showIcon) {
                            when {
                                iconSvg    -> iconRes = R.drawable.ic_check_black_18dp_svg
                                iconBitmap -> iconRes = R.drawable.ic_check_black_18dp
                            }
                        }
                        onClick { toastShort(R.string.message_on_positive) }
                    }
                    //endregion
                    //region setup negative button
                    if (negativeButton) {
                        withNegativeButton {
                            textRes     = R.string.negative_button
                            actionDelay = this@ConfirmationDialogFragment.actionDelay
                            if (showIcon) {
                                when {
                                    iconSvg    -> iconRes = R.drawable.ic_close_black_18dp_svg
                                    iconBitmap -> iconRes = R.drawable.ic_close_black_18dp
                                }
                            }
                            onClick { toastShort(R.string.message_on_negative) }
                        }
                    } else {
                        withoutNegativeButton()
                    }
                    //endregion
                    //region setup cancel options
                    withCancelOption {
                        onBackPressed  = cancelOnBackPressed
                        onTouchOutside = cancelOnTouchOutside
                    }
                    //endregion
                    //region setup listener
                    onCancel {
                        d("permission dialog canceled")
                        toastShort("You cancelled the dialog")
                    }
                    //endregion
                }
                true
            }
            R.id.action_reset_preferences -> {
                clearLocalPref()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

}
