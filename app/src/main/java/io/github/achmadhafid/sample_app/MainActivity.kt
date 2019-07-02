@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.simplepref.extension.simplePref
import io.github.achmadhafid.simplepref.extension.simplePrefNullable
import io.github.achmadhafid.zpack.ktx.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region View Binding

    private val appBarLayout: AppBarLayout by bindView(R.id.appBarLayout)
    private val toolbar: MaterialToolbar by bindView(R.id.toolbar)
    private val scrollView: NestedScrollView by bindView(R.id.scrollView)
    private val tglGroupTheme: MaterialButtonToggleGroup by bindView(R.id.toggle_button_group_theme)
    private val btnThemeDayNight: MaterialButton by bindView(R.id.btn_theme_day_night)
    private val btnThemeLight: MaterialButton by bindView(R.id.btn_theme_light)
    private val btnThemeDark: MaterialButton by bindView(R.id.btn_theme_dark)
    private val tglGroupDialogType: MaterialButtonToggleGroup by bindView(R.id.toggle_button_group_dialog_type)
    private val btnDialogTypeDialog: MaterialButton by bindView(R.id.btn_dialog_type_dialog)
    private val btnDialogTypeBottomSheet: MaterialButton by bindView(R.id.btn_dialog_type_bottom_sheet)
    private val smShowLottieAnimation: SwitchMaterial by bindView(R.id.sm_showLottieAnimation)
    private val tglGroupAnimationType: MaterialButtonToggleGroup by bindView(R.id.toggle_button_group_animation_type)
    private val btnAnimationCentered: MaterialButton by bindView(R.id.btn_animation_centered)
    private val btnAnimationFull: MaterialButton by bindView(R.id.btn_animation_full)
    private val smShowNegativeButton: SwitchMaterial by bindView(R.id.sm_showNegativeButton)
    private val smShowButtonIcon: SwitchMaterial by bindView(R.id.sm_showButtonIcon)
    private val tglGroupIconType: MaterialButtonToggleGroup by bindView(R.id.toggle_button_group_icon_type)
    private val btnIconSvg: MaterialButton by bindView(R.id.btn_icon_svg)
    private val btnIconBitmap: MaterialButton by bindView(R.id.btn_icon_bitmap)
    private val smUseCustomText: SwitchMaterial by bindView(R.id.sm_useCustomText)
    private val smEnableAutoDismiss: SwitchMaterial by bindView(R.id.sm_enableAutoDismiss)
    private val smCancelOnBackPressed: SwitchMaterial by bindView(R.id.sm_cancelOnBackPressed)
    private val smCancelOnTouchOutside: SwitchMaterial by bindView(R.id.sm_cancelOnTouchOutside)

    //endregion
    //region Preferences

    private var appTheme: Int? by simplePrefNullable()
    private var tutorialShowDialog by simplePref { false }
    private var tutorialToggleTheme by simplePref { false }
    private var btnThemeDayNightIsChecked by simplePref { true }
    private var btnThemeLightIsChecked by simplePref { false }
    private var btnThemeDarkIsChecked by simplePref { false }
    private var btnDialogTypeDialogIsChecked by simplePref { true }
    private var btnDialogTypeBottomSheetIsChecked by simplePref { false }
    private var smShowLottieAnimationIsChecked by simplePref { false }
    private var btnAnimationCenteredIsChecked by simplePref { true }
    private var btnAnimationFullIsChecked by simplePref { false }
    private var smShowNegativeButtonIsChecked by simplePref { false }
    private var smShowButtonIconIsChecked by simplePref { false }
    private var btnIconSvgIsChecked by simplePref { true }
    private var btnIconBitmapIsChecked by simplePref { false }
    private var smUseCustomTextIsChecked by simplePref { false }
    private var smEnableAutoDismissIsChecked by simplePref { true }
    private var smCancelOnBackPressedIsChecked by simplePref { false }
    private var smCancelOnTouchOutsideIsChecked by simplePref { false }

    //endregion

    //region Lifecycle Callback

    @Suppress("ComplexMethod", "LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //region setup app bar & toolbar

        setMaterialToolbar(R.id.toolbar)
        appBarLayout.setSelectedOnScrollDown(scrollView)

        //endregion
        //region setup theme options

        val defaultTheme = when {
            btnThemeDayNightIsChecked -> btnThemeDayNight
            btnThemeLightIsChecked    -> btnThemeLight
            btnThemeDarkIsChecked     -> btnThemeDark
            else                      -> ERROR_NO_DEFAULT_CHECKED
        }
        tglGroupTheme.exactlyOneMustBeChecked(
            listOf(btnThemeDayNight, btnThemeLight, btnThemeDark),
            defaultTheme
        ) { checkedId, isChecked ->
            when (checkedId) {
                btnThemeDayNight.id -> btnThemeDayNightIsChecked = isChecked
                btnThemeLight.id    -> btnThemeLightIsChecked    = isChecked
                btnThemeDark.id     -> btnThemeDarkIsChecked     = isChecked
            }
        }

        //endregion
        //region setup dialog type options

        val defaultType = when {
            btnDialogTypeDialogIsChecked      -> btnDialogTypeDialog
            btnDialogTypeBottomSheetIsChecked -> btnDialogTypeBottomSheet
            else                              -> ERROR_NO_DEFAULT_CHECKED
        }
        tglGroupDialogType.exactlyOneMustBeChecked(
            listOf(btnDialogTypeDialog, btnDialogTypeBottomSheet),
            defaultType
        ) { checkedId, isChecked ->
            when (checkedId) {
                btnDialogTypeDialog.id      -> btnDialogTypeDialogIsChecked      = isChecked
                btnDialogTypeBottomSheet.id -> btnDialogTypeBottomSheetIsChecked = isChecked
            }
        }

        //endregion
        //region setup animation type options

        val defaultAnimation = when {
            btnAnimationCenteredIsChecked -> btnAnimationCentered
            btnAnimationFullIsChecked -> btnAnimationFull
            else -> ERROR_NO_DEFAULT_CHECKED
        }
        listOf(btnAnimationCentered, btnAnimationFull)
            .exactlyOneMustBeCheckedOrNone(
                tglGroupAnimationType,
                defaultAnimation,
                { checkedId, isChecked ->
                    when (checkedId) {
                        btnAnimationCentered.id -> btnAnimationCenteredIsChecked = isChecked
                        btnAnimationFull.id     -> btnAnimationFullIsChecked     = isChecked
                    }
                },
                smShowLottieAnimation,
                smShowLottieAnimationIsChecked,
                { isChecked -> smShowLottieAnimationIsChecked = isChecked }
            )

        //endregion
        //region setup negative button options

        smShowNegativeButton.setOnCheckedChangeListener { _, isChecked ->
            smShowNegativeButtonIsChecked = isChecked
        }
        smShowNegativeButton.isChecked = smShowNegativeButtonIsChecked

        //endregion
        //region setup icon type options

        val defaultIcon = when {
            btnIconSvgIsChecked    -> btnIconSvg
            btnIconBitmapIsChecked -> btnIconBitmap
            else                   -> ERROR_NO_DEFAULT_CHECKED
        }
        listOf(btnIconSvg, btnIconBitmap)
            .exactlyOneMustBeCheckedOrNone(
                tglGroupIconType,
                defaultIcon,
                { checkedId, isChecked ->
                    when (checkedId) {
                        btnIconSvg.id    -> btnIconSvgIsChecked    = isChecked
                        btnIconBitmap.id -> btnIconBitmapIsChecked = isChecked
                    }
                },
                smShowButtonIcon,
                smShowButtonIconIsChecked,
                { isChecked -> smShowButtonIconIsChecked = isChecked }
            )

        //endregion
        //region setup custom text options

        smUseCustomText.setOnCheckedChangeListener { _, isChecked ->
            smUseCustomTextIsChecked = isChecked
        }
        smUseCustomText.isChecked = smUseCustomTextIsChecked

        //endregion
        //region setup auto dismiss & cancel options

        listOf(
            smEnableAutoDismiss    to smEnableAutoDismissIsChecked,
            smCancelOnBackPressed  to smCancelOnBackPressedIsChecked,
            smCancelOnTouchOutside to smCancelOnTouchOutsideIsChecked
        ).atLeastOneIsChecked { id, isChecked ->
            when (id) {
                smEnableAutoDismiss.id    -> smEnableAutoDismissIsChecked    = isChecked
                smCancelOnBackPressed.id  -> smCancelOnBackPressedIsChecked  = isChecked
                smCancelOnTouchOutside.id -> smCancelOnTouchOutsideIsChecked = isChecked
            }
        }

        //endregion
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        showDialogTutorial()
        return super.onPrepareOptionsMenu(menu)
    }

    @Suppress("LongMethod", "ComplexMethod")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_show_dialog -> lottieDialog(ConfirmationDialog.requestPermission) {
                if (btnDialogTypeBottomSheet.isChecked) type = LottieDialog.Type.BOTTOM_SHEET
                theme = when {
                    btnThemeDayNight.isChecked -> LottieDialog.Theme.DAY_NIGHT
                    btnThemeLight.isChecked    -> LottieDialog.Theme.LIGHT
                    else                       -> LottieDialog.Theme.DARK
                }
                autoDismiss = smEnableAutoDismiss.isChecked
                if (smShowLottieAnimation.isChecked) {
                    animation {
                        if (btnAnimationCentered.isChecked) {
                            lottieFileRes = R.raw.lottie_animation_location
                            paddingRes    = R.dimen.lottie_dialog_animation_padding
                        }
                        if (btnAnimationFull.isChecked) {
                            lottieFileRes = R.raw.lottie_animation_notification
                            bgColorRes    = R.color.bg_dialog_notification
                        }
                    }
                }
                title {
                    textRes = if (btnAnimationFull.isChecked)
                        R.string.dialog_notification_title
                    else
                        R.string.dialog_location_title
                    if (smUseCustomText.isChecked) {
                        fontRes  = R.font.lobster_two_bold
                        styleRes = R.style.AppTheme_TextAppearance
                    }
                }
                content {
                    textRes = if (btnAnimationFull.isChecked)
                        R.string.dialog_notification_content
                    else
                        R.string.dialog_location_content
                    if (smUseCustomText.isChecked) {
                        fontRes = R.font.sofia
                    }
                }
                positiveButton {
                    textRes = android.R.string.ok
                    if (smShowButtonIcon.isChecked) {
                        iconRes = if (btnIconSvg.isChecked)
                            R.drawable.ic_check_black_18dp_svg
                        else
                            R.drawable.ic_check_black_18dp
                    }
                    onClick { toastShort(R.string.message_on_positive) }
                }
                if (smShowNegativeButton.isChecked) {
                    negativeButton {
                        textRes = R.string.negative_button
                        if (smShowButtonIcon.isChecked) {
                            iconRes = if (btnIconSvg.isChecked)
                                R.drawable.ic_close_black_18dp_svg
                            else
                                R.drawable.ic_close_black_18dp
                        }
                        onClick { toastShort(R.string.message_on_negative) }
                    }
                }
                cancel {
                    onBackPressed  = smCancelOnBackPressed.isChecked
                    onTouchOutside = smCancelOnTouchOutside.isChecked
                }
                onCancel {
                    Log.d("LottieDialog", "permission dialog canceled")
                    toastShort("You cancel the dialog")
                }
            }
            R.id.action_toggle_theme -> appTheme = toggleTheme()
        }
        return true
    }

    //endregion
    //region Private Helper

    private fun showDialogTutorial() {
        if (tutorialShowDialog) {
            Handler().post {
                tutorialShowDialog = true
                TapTargetView.showFor(
                    this,
                    TapTarget.forToolbarMenuItem(
                        toolbar,
                        R.id.action_show_dialog,
                        getString(R.string.tutorial_show_dialog_title),
                        getString(R.string.tutorial_show_dialog_description)
                    ).outerCircleColor(R.color.color_selection_overlay)
                        .outerCircleAlpha(1f)
                        .titleTextColorInt(resolveColor(R.attr.colorOnSurface))
                        .descriptionTextColorInt(resolveColor(R.attr.colorOnSurface))
                        .drawShadow(true)
                        .cancelable(true)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .targetRadius(resources.getInteger(R.integer.tutorial_target_radius)),
                    object : TapTargetView.Listener() {
                        override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                            toggleThemeTutorial()
                            super.onTargetDismissed(view, userInitiated)
                        }
                    }
                )
            }
        }
    }

    private fun toggleThemeTutorial() {
        if (tutorialToggleTheme) {
            Handler().post {
                tutorialToggleTheme = true
                TapTargetView.showFor(
                    this,
                    TapTarget.forToolbarMenuItem(
                        toolbar,
                        R.id.action_toggle_theme,
                        getString(R.string.tutorial_toggle_theme_title),
                        getString(R.string.tutorial_toggle_theme_description)
                    ).outerCircleColor(R.color.color_selection_overlay)
                        .outerCircleAlpha(1f)
                        .titleTextColorInt(resolveColor(R.attr.colorOnSurface))
                        .descriptionTextColorInt(resolveColor(R.attr.colorOnSurface))
                        .drawShadow(true)
                        .cancelable(true)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .targetRadius(resources.getInteger(R.integer.tutorial_target_radius))
                )
            }
        }
    }


    //endregion

}

val ERROR_NO_DEFAULT_CHECKED: Nothing = TODO("no default checked")
