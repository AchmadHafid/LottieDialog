@file:Suppress("PackageNaming")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import io.github.achmadhafid.lottie_dialog.core.isValidInput
import io.github.achmadhafid.lottie_dialog.core.lottieInputDialog
import io.github.achmadhafid.lottie_dialog.core.onCancel
import io.github.achmadhafid.lottie_dialog.core.onInvalidInput
import io.github.achmadhafid.lottie_dialog.core.onValidInput
import io.github.achmadhafid.lottie_dialog.core.withAnimation
import io.github.achmadhafid.lottie_dialog.core.withCancelOption
import io.github.achmadhafid.lottie_dialog.core.withContent
import io.github.achmadhafid.lottie_dialog.core.withImage
import io.github.achmadhafid.lottie_dialog.core.withInputSpec
import io.github.achmadhafid.lottie_dialog.core.withTitle
import io.github.achmadhafid.lottie_dialog.core.withoutAnimation
import io.github.achmadhafid.lottie_dialog.core.withoutContent
import io.github.achmadhafid.lottie_dialog.model.LottieDialogInput
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.sample_app.databinding.FragmentInputDialogBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.core.simplePrefClearAllLocal
import io.github.achmadhafid.simplepref.livedata.simplePrefLiveData
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.delegate.viewLifecycleVar
import io.github.achmadhafid.zpack.extension.d
import io.github.achmadhafid.zpack.extension.toastShort

class InputDialogFragment : Fragment(), SimplePref {

    //region Preference

    private var typeDialog by simplePref { true }
    private var typeBottomSheet by simplePref { false }
    private var themeDayNight by simplePref { true }
    private var themeLight by simplePref { false }
    private var themeDark by simplePref { false }
    private var inputTypeText by simplePref { true }
    private var inputTypeNumeric by simplePref { false }
    private var inputTypePhone by simplePref { false }
    private var inputTypePin by simplePref { false }
    private var inputTypePassword by simplePref { false }
    private var showAnimation by simplePref { false }
    private var showImage by simplePref { false }
    private var closeButton by simplePref { true }
    private var useCustomText by simplePref { false }
    private var cancelOnBackPressed by simplePref { true }
    private var cancelOnTouchOutside by simplePref { true }

    //endregion
    //region View Binding

    private var _binding: FragmentInputDialogBinding? by viewLifecycleVar()
    private val binding get() = _binding!!

    //endregion
    //region Lifecycle Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region setup toolbar menu

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.fragment_action_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_show_dialog -> {
                        lottieInputDialog(Int.MAX_VALUE, BaseDialog.askSomething) {
                            //region setup type
                            when {
                                typeDialog -> type = LottieDialogType.DIALOG
                                typeBottomSheet -> type = LottieDialogType.BOTTOM_SHEET
                            }
                            //endregion
                            //region setup theme
                            when {
                                themeDayNight -> theme = LottieDialogTheme.DAY_NIGHT
                                themeLight -> theme = LottieDialogTheme.LIGHT
                                themeDark -> theme = LottieDialogTheme.DARK
                            }
                            //endregion
                            //region setup animation
                            when {
                                showAnimation -> {
                                    withAnimation {
                                        fileRes = R.raw.lottie_animation_input
                                        showCloseButton = closeButton
                                    }
                                }
                                showImage -> {
                                    withImage {
                                        imageRes = R.drawable.offline
                                        paddingTopRes = R.dimen.medium
                                        showCloseButton = closeButton
                                    }
                                }
                                else -> {
                                    withoutAnimation()
                                    withoutContent()
                                }
                            }
                            //endregion
                            //region setup title
                            withTitle {
                                text = when {
                                    inputTypeText -> "Enter your name"
                                    inputTypeNumeric -> "Enter some number"
                                    inputTypePhone -> "Enter your phone"
                                    inputTypePin -> "Enter OTP"
                                    inputTypePassword -> "Enter your password"
                                    else -> null
                                }
                                if (useCustomText) {
                                    fontRes = R.font.lobster_two_bold
                                    styleRes = R.style.AppTheme_TextAppearance
                                }
                            }
                            //endregion
                            //region setup content
                            withContent {
                                text = when {
                                    inputTypeText -> "Please enter your full name"
                                    inputTypeNumeric -> "It will be secret between us!"
                                    inputTypePhone -> "We will contact you if we need to confirm your order"
                                    inputTypePin -> "Please check your SMS inbox for to get the OTP"
                                    inputTypePassword -> "Please consider to change it everyday!"
                                    else -> null
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
                                        inputType = LottieDialogInput.Type.TEXT
                                        initialValue = "Your name"
                                        isValidInput { it.isNotEmpty() && it.length < 20 }
                                    }
                                    inputTypeNumeric -> {
                                        inputType = LottieDialogInput.Type.NUMERIC
                                        inputFormat = "[000000]"
                                        isValidInput { it.startsWith("9") }
                                    }
                                    inputTypePhone -> {
                                        inputType = LottieDialogInput.Type.PHONE
                                        inputFormat = "{08}[00]-[0000]-[0099]"
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
                                        inputType = LottieDialogInput.Type.PIN
                                        inputFormat = "[______]"
                                    }
                                    inputTypePassword -> {
                                        inputType = LottieDialogInput.Type.PASSWORD
                                        inputFormat = "[__________]"
                                    }
                                }
                                //endregion
                            }
                            //endregion
                            //region setup cancel options
                            withCancelOption {
                                onBackPressed = cancelOnBackPressed
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
                        simplePrefClearAllLocal()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        //endregion
        //region setup dialog type options

        binding.toggleButtonGroupDialogType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnDialogTypeDialog.id -> typeDialog = isChecked
                binding.btnDialogTypeBottomSheet.id -> typeBottomSheet = isChecked
            }
        }
        simplePrefLiveData(typeDialog, ::typeDialog).observe(viewLifecycleOwner) {
            binding.btnDialogTypeDialog.onCheckStateChange(it)
        }
        simplePrefLiveData(typeBottomSheet, ::typeBottomSheet).observe(viewLifecycleOwner) {
            binding.btnDialogTypeBottomSheet.onCheckStateChange(it)
        }

        //endregion
        //region setup theme options

        binding.toggleButtonGroupTheme.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnThemeDayNight.id -> themeDayNight = isChecked
                binding.btnThemeLight.id -> themeLight = isChecked
                binding.btnThemeDark.id -> themeDark = isChecked
            }
        }
        simplePrefLiveData(themeDayNight, ::themeDayNight).observe(viewLifecycleOwner) {
            binding.btnThemeDayNight.onCheckStateChange(it)
        }
        simplePrefLiveData(themeLight, ::themeLight).observe(viewLifecycleOwner) {
            binding.btnThemeLight.onCheckStateChange(it)
        }
        simplePrefLiveData(themeDark, ::themeDark).observe(viewLifecycleOwner) {
            binding.btnThemeDark.onCheckStateChange(it)
        }

        //endregion
        //region setup input type options

        binding.toggleButtonGroupInputType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnInputTypeText.id -> inputTypeText = isChecked
                binding.btnInputTypeNumeric.id -> inputTypeNumeric = isChecked
                binding.btnInputTypePhone.id -> inputTypePhone = isChecked
                binding.btnInputTypePin.id -> inputTypePin = isChecked
                binding.btnInputTypePassword.id -> inputTypePassword = isChecked
            }
        }
        listOf(
            Triple(binding.btnInputTypeText, inputTypeText, ::inputTypeText),
            Triple(binding.btnInputTypeNumeric, inputTypeNumeric, ::inputTypeNumeric),
            Triple(binding.btnInputTypePhone, inputTypePhone, ::inputTypePhone),
            Triple(binding.btnInputTypePin, inputTypePin, ::inputTypePin),
            Triple(binding.btnInputTypePassword, inputTypePassword, ::inputTypePassword)
        ).forEach { (btn, pref, prop) ->
            simplePrefLiveData(pref, prop).observe(viewLifecycleOwner) {
                btn.onCheckStateChange(it)
            }
        }

        //endregion
        //region setup animation type options

        binding.smShowLottieAnimation.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) binding.smShowImage.isChecked = false
                showAnimation = isChecked
            }
            simplePrefLiveData(showAnimation, ::showAnimation).observe(viewLifecycleOwner) {
                isChecked = it
                binding.smShowLottieAnimationCloseButton.isEnabled = isChecked || showImage
            }
        }

        //endregion
        //region setup image option

        binding.smShowImage.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) binding.smShowLottieAnimation.isChecked = false
                showImage = isChecked
            }
            simplePrefLiveData(showImage, ::showImage).observe(viewLifecycleOwner) {
                isChecked = it
                binding.smShowLottieAnimationCloseButton.isEnabled = showAnimation || showImage
            }
        }

        //endregion
        //region setup close button

        binding.smShowLottieAnimationCloseButton.apply {
            setOnCheckedChangeListener { _, isChecked ->
                closeButton = isChecked
            }
            simplePrefLiveData(closeButton, ::closeButton).observe(viewLifecycleOwner) {
                isChecked = it
            }
        }

        //endregion
        //region setup custom text options

        binding.smUseCustomText.apply {
            setOnCheckedChangeListener { _, isChecked ->
                useCustomText = isChecked
            }
            simplePrefLiveData(useCustomText, ::useCustomText).observe(viewLifecycleOwner) {
                isChecked = it
            }
        }

        //endregion
        //region setup auto dismiss & cancel options

        binding.smCancelOnBackPressed.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnBackPressed = isChecked
            }
            simplePrefLiveData(cancelOnBackPressed, ::cancelOnBackPressed).observe(
                viewLifecycleOwner
            ) {
                isChecked = it
            }
        }

        binding.smCancelOnTouchOutside.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnTouchOutside = isChecked
            }
            simplePrefLiveData(cancelOnTouchOutside, ::cancelOnTouchOutside).observe(
                viewLifecycleOwner
            ) {
                isChecked = it
            }
        }

        //endregion
    }

    //endregion

}
