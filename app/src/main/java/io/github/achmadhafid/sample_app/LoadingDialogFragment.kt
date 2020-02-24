package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.florent37.viewanimator.ViewAnimator
import io.github.achmadhafid.lottie_dialog.lottieLoadingDialog
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.lottie_dialog.onCancel
import io.github.achmadhafid.lottie_dialog.onTimeout
import io.github.achmadhafid.lottie_dialog.withAnimation
import io.github.achmadhafid.lottie_dialog.withCancelOption
import io.github.achmadhafid.lottie_dialog.withImage
import io.github.achmadhafid.lottie_dialog.withTitle
import io.github.achmadhafid.lottie_dialog.withoutAnimation
import io.github.achmadhafid.lottie_dialog.withoutImage
import io.github.achmadhafid.sample_app.databinding.FragmentLoadingDialogBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.core.simplePrefClearAllLocal
import io.github.achmadhafid.simplepref.livedata.simplePrefLiveData
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.ktx.d
import io.github.achmadhafid.zpack.ktx.resolveColor
import io.github.achmadhafid.zpack.ktx.toastShort
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

    private var _binding: FragmentLoadingDialogBinding? = null
    private val binding get() = _binding!!

    //endregion

    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_action_bar, menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region setup dialog type

        binding.toggleButtonGroupDialogType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnDialogTypeDialog.id -> typeDialogL          = isChecked
                binding.btnDialogTypeBottomSheet.id -> typeBottomSheet = isChecked
            }
        }
        simplePrefLiveData(typeDialogL, ::typeDialogL) {
            binding.btnDialogTypeDialog.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        simplePrefLiveData(typeBottomSheet, ::typeBottomSheet) {
            binding.btnDialogTypeBottomSheet.apply {
                isChecked   = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup theme

        binding.toggleButtonGroupTheme.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                binding.btnThemeDayNight.id -> themeDayNight = isChecked
                binding.btnThemeLight.id    -> themeLight    = isChecked
                binding.btnThemeDark.id     -> themeDark     = isChecked
            }
        }
        simplePrefLiveData(themeDayNight, ::themeDayNight) {
            binding.btnThemeLight.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        simplePrefLiveData(themeLight, ::themeLight) {
            binding.btnThemeLight.apply {
                isChecked   = it
                isCheckable = !it
            }
        }
        simplePrefLiveData(themeDark, ::themeDark) {
            binding.btnThemeDark.apply {
                isChecked   = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup animation type options

        binding.smShowLottieAnimation.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) binding.smShowImage.isChecked = false
                showAnimation = isChecked
            }
            simplePrefLiveData(showAnimation, ::showAnimation) {
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
            simplePrefLiveData(showImage, ::showImage) {
                isChecked = it
            }
        }

        //endregion
        //region setup custom text

        binding.smUseCustomText.apply {
            setOnCheckedChangeListener { _, isChecked ->
                useCustomText = isChecked
            }
            simplePrefLiveData(useCustomText, ::useCustomText) {
                isChecked = it
            }
        }

        //endregion
        //region setup cancel options

        binding.smCancelOnBackPressed.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnBackPressed = isChecked
            }
            simplePrefLiveData(cancelOnBackPressed, ::cancelOnBackPressed) {
                isChecked = it
            }
        }

        binding.smCancelOnTouchOutside.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnTouchOutside = isChecked
            }
            simplePrefLiveData(cancelOnTouchOutside, ::cancelOnTouchOutside) {
                isChecked = it
            }
        }

        //endregion
        //region setup timeout

        @Suppress("MagicNumber")
        with(binding.sbActionTimeout) {
            colorBubble     = context.resolveColor(R.attr.colorPrimary)
            colorBubbleText = context.resolveColor(R.attr.colorOnPrimary)
            colorBar        = colorBubble

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

            simplePrefLiveData(timeoutValue, ::timeoutValue) {
                position   = timeoutValue / maxTimeout.toFloat()
                bubbleText = "$timeoutValue"
            }
        }

        binding.smShowTimeoutProgress.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showTimeoutProgressView = isChecked
            }
            simplePrefLiveData(showTimeoutProgressView, ::showTimeoutProgressView) {
                isChecked = it
            }
        }

        //endregion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_dialog -> {
                lottieLoadingDialog(BaseDialog.doSomething) {
                    //region setup type
                    when {
                        typeDialogL      -> type = LottieDialogType.DIALOG
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
                    //region setup timeout
                    timeout = timeoutValue
                    showTimeOutProgress = showTimeoutProgressView
                    //endregion
                    //region setup animation
                    when {
                        showAnimation -> {
                            withAnimation {
                                fileRes        = R.raw.lottie_animation_loading
                                animationSpeed = 2f
                            }
                        }
                        showImage -> {
                            withImage {
                                imageRes   = R.drawable.offline
                                paddingRes = R.dimen.lottie_dialog_animation_padding
                                heightRes  = R.dimen.huge_fit
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
                            fontRes  = R.font.lobster_two_bold
                            styleRes = R.style.AppTheme_TextAppearance
                        }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

}
