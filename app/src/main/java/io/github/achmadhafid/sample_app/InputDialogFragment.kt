package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import io.github.achmadhafid.lottie_dialog.isValidInput
import io.github.achmadhafid.lottie_dialog.lottieInputDialog
import io.github.achmadhafid.lottie_dialog.model.LottieDialogInput
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.onCancel
import io.github.achmadhafid.lottie_dialog.onInvalidInput
import io.github.achmadhafid.lottie_dialog.onValidInput
import io.github.achmadhafid.lottie_dialog.withAnimation
import io.github.achmadhafid.lottie_dialog.withCancelOption
import io.github.achmadhafid.lottie_dialog.withContent
import io.github.achmadhafid.lottie_dialog.withInputSpec
import io.github.achmadhafid.lottie_dialog.withTitle
import io.github.achmadhafid.lottie_dialog.withoutAnimation
import io.github.achmadhafid.simplepref.extension.clearLocalPref
import io.github.achmadhafid.simplepref.extension.liveDataPref
import io.github.achmadhafid.simplepref.extension.simplePref
import io.github.achmadhafid.zpack.ktx.d
import io.github.achmadhafid.zpack.ktx.toastShort

class InputDialogFragment : Fragment(R.layout.fragment_input_dialog) {

    //region Preference

    private var typeDialog by simplePref(isLocal = true) { true }
    private var typeBottomSheet by simplePref(isLocal = true) { false }
    private var themeDayNight by simplePref(isLocal = true) { true }
    private var themeLight by simplePref(isLocal = true) { false }
    private var themeDark by simplePref(isLocal = true) { false }
    private var inputTypeText by simplePref(isLocal = true) { true }
    private var inputTypeNumeric by simplePref(isLocal = true) { false }
    private var inputTypePhone by simplePref(isLocal = true) { false }
    private var inputTypePin by simplePref(isLocal = true) { false }
    private var inputTypePassword by simplePref(isLocal = true) { false }
    private var showAnimation by simplePref(isLocal = true) { false }
    private var closeButton by simplePref(isLocal = true) { true }
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

        val tgDialogType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_dialog_type)
        val btnDialogTypeDialog: MaterialButton = view.findViewById(R.id.btn_dialog_type_dialog)
        val btnDialogTypeBottomSheet: MaterialButton = view.findViewById(R.id.btn_dialog_type_bottom_sheet)
        val tgTheme: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_theme)
        val btnThemeDayNight: MaterialButton = view.findViewById(R.id.btn_theme_day_night)
        val btnThemeLight: MaterialButton = view.findViewById(R.id.btn_theme_light)
        val btnThemeDark: MaterialButton = view.findViewById(R.id.btn_theme_dark)
        val tgInputType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_input_type)
        val btnInputTypeText: MaterialButton = view.findViewById(R.id.btn_input_type_text)
        val btnInputTypeNumeric: MaterialButton = view.findViewById(R.id.btn_input_type_numeric)
        val btnInputTypePhone: MaterialButton = view.findViewById(R.id.btn_input_type_phone)
        val btnInputTypePin: MaterialButton = view.findViewById(R.id.btn_input_type_pin)
        val btnInputTypePassword: MaterialButton = view.findViewById(R.id.btn_input_type_password)
        val smShowLottieAnimation: SwitchMaterial = view.findViewById(R.id.sm_showLottieAnimation)
        val smShowLottieAnimationCloseButton: SwitchMaterial = view.findViewById(R.id.sm_showLottieAnimationCloseButton)
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
        //region setup input type options

        tgInputType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                btnInputTypeText.id     -> inputTypeText     = isChecked
                btnInputTypeNumeric.id  -> inputTypeNumeric  = isChecked
                btnInputTypePhone.id    -> inputTypePhone    = isChecked
                btnInputTypePin.id      -> inputTypePin      = isChecked
                btnInputTypePassword.id -> inputTypePassword = isChecked
            }
        }
        listOf(
            ::inputTypeText     to btnInputTypeText,
            ::inputTypeNumeric  to btnInputTypeNumeric,
            ::inputTypePhone    to btnInputTypePhone,
            ::inputTypePin      to btnInputTypePin,
            ::inputTypePassword to btnInputTypePassword
        ).forEach { (pref, btn) ->
            liveDataPref(pref) {
                btn.apply {
                    isChecked   = it
                    isCheckable = !it
                }
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
                smShowLottieAnimationCloseButton.isEnabled = isChecked
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
                lottieInputDialog(BaseDialog.askSomething) {
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
                            fileRes         = R.raw.lottie_animation_input
                            showCloseButton = closeButton
                        }
                    } else {
                        withoutAnimation()
                    }
                    //endregion
                    //region setup title
                    withTitle {
                        text = when {
                            inputTypeText     -> "Enter your name"
                            inputTypeNumeric  -> "Enter some number"
                            inputTypePhone    -> "Enter your phone"
                            inputTypePin      -> "Enter OTP"
                            inputTypePassword -> "Enter your password"
                            else              -> null
                        }
                        if (useCustomText) {
                            fontRes  = R.font.lobster_two_bold
                            styleRes = R.style.AppTheme_TextAppearance
                        }
                    }
                    //endregion
                    //region setup content
                    withContent {
                        text = when {
                            inputTypeText     -> "Please enter your full name"
                            inputTypeNumeric  -> "It will be secret between us!"
                            inputTypePhone    -> "We will contact you if we need to confirm your order"
                            inputTypePin      -> "Please check your SMS inbox for to get the OTP"
                            inputTypePassword -> "Please consider to change it everyday!"
                            else              -> null
                        }
                        if (useCustomText) {
                            fontRes = R.font.sofia
                        }
                    }
                    //endregion
                    //region setup input spec
                    @Suppress("MagicNumber")
                    withInputSpec {
                        //region sample input spec
                        when {
                            inputTypeText -> {
                                inputType    = LottieDialogInput.Type.TEXT
                                initialValue = "Your name"
                                isValidInput { it.isNotEmpty() && it.length < 20 }
                            }
                            inputTypeNumeric -> {
                                inputType   = LottieDialogInput.Type.NUMERIC
                                inputFormat = "[000000]"
                                isValidInput { it.startsWith("9") }
                            }
                            inputTypePhone -> {
                                inputType    = LottieDialogInput.Type.PHONE
                                inputFormat  = "{08}[00]-[0000]-[0099]"
                                initialValue = "08"
                                isValidInput { phone ->
                                    with(phone) {
                                        startsWith("08") &&
                                                length >= 10 &&
                                                length <= 12
                                    }
                                }
                            }
                            inputTypePin -> {
                                inputType   = LottieDialogInput.Type.PIN
                                inputFormat = "[______]"
                            }
                            inputTypePassword -> {
                                inputType   = LottieDialogInput.Type.PASSWORD
                                inputFormat = "[__________]"
                            }
                        }
                        //endregion
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
                        d("loading dialog canceled")
                        toastShort("You canceled the dialog")
                    }
                    onValidInput { input ->
                        toastShort("You entered: $input")
                    }
                    onInvalidInput {
                        toastShort("Invalid input")
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
