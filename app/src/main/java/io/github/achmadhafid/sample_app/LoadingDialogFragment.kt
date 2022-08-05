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
import com.github.florent37.viewanimator.ViewAnimator
import io.github.achmadhafid.lottie_dialog.core.lottieLoadingDialog
import io.github.achmadhafid.lottie_dialog.core.onCancel
import io.github.achmadhafid.lottie_dialog.core.onTimeout
import io.github.achmadhafid.lottie_dialog.core.withAnimation
import io.github.achmadhafid.lottie_dialog.core.withCancelOption
import io.github.achmadhafid.lottie_dialog.core.withImage
import io.github.achmadhafid.lottie_dialog.core.withTitle
import io.github.achmadhafid.lottie_dialog.core.withoutAnimation
import io.github.achmadhafid.lottie_dialog.core.withoutImage
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.model.withProperties
import io.github.achmadhafid.sample_app.databinding.FragmentLoadingDialogBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.core.simplePrefClearAllLocal
import io.github.achmadhafid.simplepref.livedata.simplePrefLiveData
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.delegate.viewLifecycleVar
import io.github.achmadhafid.zpack.extension.d
import io.github.achmadhafid.zpack.extension.resolveColor
import io.github.achmadhafid.zpack.extension.toastShort
import kotlin.math.floor

class LoadingDialogFragment : Fragment(), SimplePref {

    //region Resource Binding

    private val maxTimeout by lazy { resources.getInteger(R.integer.loading_timeout).toLong() }

    //endregion
    //region Preference

    private var typeDialogL by simplePref { true }
    private var typeBottomSheet by simplePref { false }
    private var themeDayNight by simplePref { true }
    private var themeLight by simplePref { false }
    private var themeDark by simplePref { false }
    private var showAnimation by simplePref { false }
    private var showImage by simplePref { false }
    private var useCustomText by simplePref { false }
    private var cancelOnBackPressed by simplePref { true }
    private var cancelOnTouchOutside by simplePref { true }
    private var timeoutValue by simplePref { maxTimeout }
    private var showTimeoutProgressView by simplePref { true }

    //endregion
    //region View Binding

    private var _binding: FragmentLoadingDialogBinding? by viewLifecycleVar()
    private val binding get() = _binding!!

    //endregion
    //region Lifecycle Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingDialogBinding.inflate(inflater, container, false)
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
                        lottieLoadingDialog(Int.MAX_VALUE, BaseDialog.doSomething) {
                            //region setup type
                            when {
                                typeDialogL -> type = LottieDialogType.DIALOG
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
                            //region setup timeout
                            timeout = timeoutValue
                            showTimeOutProgress = showTimeoutProgressView
                            //endregion
                            //region setup animation
                            when {
                                showAnimation -> {
                                    withAnimation {
                                        fileRes = R.raw.lottie_animation_loading
                                        withProperties {
                                            speed = 2.0f
                                        }
                                    }
                                }
                                showImage -> {
                                    withImage {
                                        imageRes = R.drawable.offline
                                        paddingRes = R.dimen.medium
                                        heightRes = R.dimen.lottie_dialog_loading_image_height
                                    }
                                }
                                else -> {
                                    withoutAnimation()
                                    withoutImage()
                                }
                            }
                            //endregion
                            //region setup title
                            withTitle {
                                text = "Please wait..."
                                if (useCustomText) {
                                    fontRes = R.font.lobster_two_bold
                                    styleRes = R.style.AppTheme_TextAppearance
                                }
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
                            onTimeout {
                                toastShort("Timeout")
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
        //region setup dialog type

        binding.toggleButtonGroupDialogType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnDialogTypeDialog.id -> typeDialogL = isChecked
                binding.btnDialogTypeBottomSheet.id -> typeBottomSheet = isChecked
            }
        }
        simplePrefLiveData(typeDialogL, ::typeDialogL).observe(viewLifecycleOwner) {
            binding.btnDialogTypeDialog.onCheckStateChange(it)
        }
        simplePrefLiveData(typeBottomSheet, ::typeBottomSheet).observe(viewLifecycleOwner) {
            binding.btnDialogTypeBottomSheet.onCheckStateChange(it)
        }

        //endregion
        //region setup theme

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
        //region setup animation type options

        binding.smShowLottieAnimation.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) binding.smShowImage.isChecked = false
                showAnimation = isChecked
            }
            simplePrefLiveData(showAnimation, ::showAnimation).observe(viewLifecycleOwner) {
                isChecked = it
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
            }
        }

        //endregion
        //region setup custom text

        binding.smUseCustomText.apply {
            setOnCheckedChangeListener { _, isChecked ->
                useCustomText = isChecked
            }
            simplePrefLiveData(useCustomText, ::useCustomText).observe(viewLifecycleOwner) {
                isChecked = it
            }
        }

        //endregion
        //region setup cancel options

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
        //region setup timeout

        @Suppress("MagicNumber")
        with(binding.sbActionTimeout) {
            colorBubble = context.resolveColor(R.attr.colorPrimary)
            colorBubbleText = context.resolveColor(R.attr.colorOnPrimary)
            colorBar = colorBubble

            val partition = 100
            fun posValue() = (floor(maxTimeout * position / partition) * partition).toLong()

            positionListener = {
                bubbleText = "${posValue()}"
            }

            val height by lazy { binding.tvActionTimeout.height.toFloat() }
            val animation by lazy { ViewAnimator.animate(binding.tvActionTimeout) }

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
                timeoutValue = posValue()
            }

            simplePrefLiveData(timeoutValue, ::timeoutValue).observe(viewLifecycleOwner) {
                position = timeoutValue / maxTimeout.toFloat()
                bubbleText = "$timeoutValue"
            }
        }

        binding.smShowTimeoutProgress.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showTimeoutProgressView = isChecked
            }
            simplePrefLiveData(showTimeoutProgressView, ::showTimeoutProgressView)
                .observe(viewLifecycleOwner) { isChecked = it }
        }

        //endregion
    }

    //endregion

}
