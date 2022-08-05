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
import io.github.achmadhafid.lottie_dialog.core.lottieConfirmationDialog
import io.github.achmadhafid.lottie_dialog.core.onCancel
import io.github.achmadhafid.lottie_dialog.core.withAnimation
import io.github.achmadhafid.lottie_dialog.core.withCancelOption
import io.github.achmadhafid.lottie_dialog.core.withContent
import io.github.achmadhafid.lottie_dialog.core.withImage
import io.github.achmadhafid.lottie_dialog.core.withNegativeButton
import io.github.achmadhafid.lottie_dialog.core.withPositiveButton
import io.github.achmadhafid.lottie_dialog.core.withTitle
import io.github.achmadhafid.lottie_dialog.core.withoutAnimation
import io.github.achmadhafid.lottie_dialog.core.withoutImage
import io.github.achmadhafid.lottie_dialog.core.withoutNegativeButton
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.model.onClick
import io.github.achmadhafid.sample_app.databinding.FragmentConfirmationDialogBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.core.simplePrefClearAllLocal
import io.github.achmadhafid.simplepref.livedata.simplePrefLiveData
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.delegate.viewLifecycleVar
import io.github.achmadhafid.zpack.extension.d
import io.github.achmadhafid.zpack.extension.resolveColor
import io.github.achmadhafid.zpack.extension.toastShort
import kotlin.math.floor

class ConfirmationDialogFragment : Fragment(), SimplePref {

    //region Preference

    private var themeDayNight by simplePref { true }
    private var themeLight by simplePref { false }
    private var themeDark by simplePref { false }
    private var typeDialog by simplePref { true }
    private var typeBottomSheet by simplePref { false }
    private var showAnimation by simplePref { false }
    private var showImage by simplePref { false }
    private var animationCentered by simplePref { true }
    private var animationFull by simplePref { false }
    private var closeButton by simplePref { true }
    private var negativeButton by simplePref { false }
    private var showIcon by simplePref { false }
    private var iconSvg by simplePref { true }
    private var iconBitmap by simplePref { false }
    private var actionDelay by simplePref { 0L }
    private var useCustomText by simplePref { false }
    private var cancelOnBackPressed by simplePref { true }
    private var cancelOnTouchOutside by simplePref { true }

    //endregion
    //region View Binding

    private var _binding: FragmentConfirmationDialogBinding? by viewLifecycleVar()
    private val binding get() = _binding!!

    //endregion
    //region Lifecycle Callback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region setup toolbar menu

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_action_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_show_dialog -> {
                        lottieConfirmationDialog(
                            Int.MAX_VALUE,
                            "confirmation",
                            BaseDialog.requestSomething
                        ) {
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
                            //region setup animation or image
                            when {
                                showAnimation -> {
                                    withAnimation {
                                        if (animationCentered) {
                                            fileRes = R.raw.lottie_animation_location
                                            paddingTopRes = R.dimen.medium
                                        }
                                        if (animationFull) {
                                            fileRes = R.raw.lottie_animation_notification
                                            bgColorRes = R.color.bg_dialog_notification
                                        }
                                        showCloseButton = closeButton
                                    }
                                }
                                showImage -> {
                                    withImage {
                                        imageRes = R.drawable.offline
                                        paddingRes = R.dimen.medium
                                        showCloseButton = closeButton
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
                                textRes = when {
                                    animationCentered -> R.string.dialog_location_title
                                    animationFull -> R.string.dialog_notification_title
                                    else -> R.string.dialog_location_title
                                }
                                if (useCustomText) {
                                    fontRes = R.font.lobster_two_bold
                                    styleRes = R.style.AppTheme_TextAppearance
                                }
                            }
                            //endregion
                            //region setup content
                            withContent {
                                textRes = when {
                                    animationCentered -> R.string.dialog_location_content
                                    animationFull -> R.string.dialog_notification_content
                                    else -> R.string.dialog_location_content
                                }
                                if (useCustomText) {
                                    fontRes = R.font.sofia
                                }
                            }
                            //endregion
                            //region setup positive button
                            withPositiveButton {
                                textRes = android.R.string.ok
                                actionDelay = this@ConfirmationDialogFragment.actionDelay
                                if (showIcon) {
                                    when {
                                        iconSvg -> iconRes = R.drawable.ic_check_black_18dp_svg
                                        iconBitmap -> iconRes = R.drawable.ic_check_black_18dp
                                    }
                                }
                                onClick { toastShort(R.string.message_on_positive) }
                            }
                            //endregion
                            //region setup negative button
                            if (negativeButton) {
                                withNegativeButton {
                                    textRes = R.string.negative_button
                                    actionDelay = this@ConfirmationDialogFragment.actionDelay
                                    if (showIcon) {
                                        when {
                                            iconSvg -> iconRes = R.drawable.ic_close_black_18dp_svg
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
                                onBackPressed = cancelOnBackPressed
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
        //region setup animation type options

        binding.smShowLottieAnimation.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) binding.smShowImage.isChecked = false
                showAnimation = isChecked
            }
            simplePrefLiveData(showAnimation, ::showAnimation).observe(viewLifecycleOwner) {
                isChecked = it
                val btnList = listOf(
                    binding.btnAnimationCentered to animationCentered,
                    binding.btnAnimationFull to animationFull
                )
                if (isChecked) {
                    btnList.apply {
                        map { item -> item.first }.forEach { button ->
                            button.apply {
                                isEnabled = true
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
                binding.smShowLottieAnimationCloseButton.isEnabled = isChecked || showImage
            }
        }
        binding.toggleButtonGroupAnimationType.addOnButtonCheckedListener { _, id, isChecked ->
            if (binding.smShowLottieAnimation.isChecked) {
                when (id) {
                    binding.btnAnimationCentered.id -> animationCentered = isChecked
                    binding.btnAnimationFull.id -> animationFull = isChecked
                }
            }
        }
        binding.btnAnimationCentered.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (binding.smShowLottieAnimation.isChecked) {
                    animationCentered = isChecked
                }
            }
            simplePrefLiveData(animationCentered, ::animationCentered).observe(viewLifecycleOwner) {
                onCheckStateChange(it)
            }
        }
        binding.btnAnimationFull.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (binding.smShowLottieAnimation.isChecked) {
                    animationFull = isChecked
                }
            }
            simplePrefLiveData(animationFull, ::animationFull).observe(viewLifecycleOwner) {
                onCheckStateChange(it)
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
        //region setup negative button options

        binding.smShowNegativeButton.apply {
            setOnCheckedChangeListener { _, isChecked ->
                negativeButton = isChecked
            }
            simplePrefLiveData(negativeButton, ::negativeButton).observe(viewLifecycleOwner) {
                isChecked = it
            }
        }

        //endregion
        //region setup icon type options

        binding.smShowButtonIcon.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showIcon = isChecked
            }
            simplePrefLiveData(showIcon, ::showIcon).observe(viewLifecycleOwner) {
                isChecked = it
                val btnList = listOf(
                    binding.btnIconSvg to iconSvg,
                    binding.btnIconBitmap to iconBitmap
                )
                if (isChecked) {
                    btnList.apply {
                        map { item -> item.first }.forEach { button ->
                            button.apply {
                                isEnabled = true
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
            }
        }
        binding.toggleButtonGroupIconType.addOnButtonCheckedListener { _, id, isChecked ->
            if (binding.smShowButtonIcon.isChecked) {
                when (id) {
                    binding.btnIconSvg.id -> iconSvg = isChecked
                    binding.btnIconBitmap.id -> iconBitmap = isChecked
                }
            }
        }
        binding.btnIconSvg.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (binding.smShowButtonIcon.isChecked) {
                    iconSvg = isChecked
                }
            }
            simplePrefLiveData(iconSvg, ::iconSvg).observe(viewLifecycleOwner) {
                onCheckStateChange(it)
            }
        }
        binding.btnIconBitmap.apply {
            addOnCheckedChangeListener { _, isChecked ->
                if (binding.smShowButtonIcon.isChecked) {
                    iconBitmap = isChecked
                }
            }
            simplePrefLiveData(iconBitmap, ::iconBitmap).observe(viewLifecycleOwner) {
                onCheckStateChange(it)
            }
        }

        //endregion
        //region setup action delay

        @Suppress("MagicNumber")
        with(binding.sbActionDelay) {
            colorBubble = context.resolveColor(R.attr.colorPrimary)
            colorBubbleText = context.resolveColor(R.attr.colorOnPrimary)
            colorBar = colorBubble

            positionListener = {
                bubbleText = "${(floor(1000 * position / 50) * 50).toLong()}"
            }

            val height by lazy { binding.tvActionDelay.height.toFloat() }
            val animation by lazy { ViewAnimator.animate(binding.tvActionDelay) }

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

            simplePrefLiveData(actionDelay, ::actionDelay).observe(viewLifecycleOwner) {
                position = actionDelay / 1000F
                bubbleText = "$actionDelay"
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
