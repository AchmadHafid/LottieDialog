@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog.core

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.SurfaceColors
import io.github.achmadhafid.lottie_dialog.R

abstract class LottieLoadingDialogFragment : BottomSheetDialogFragment() {

    private var loadingDialog: LottieLoadingDialog? = null

    override fun getTheme() = R.style.Theme_LottieDialog_BottomSheet_DayNight

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.lottie_dialog_loading, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = LottieLoadingDialog().apply(dialogBuilder)
        dialog?.let {
            loadingDialog!!.invoke(it, view, viewLifecycleOwner.lifecycleScope, true)
            it.window?.navigationBarColor = SurfaceColors.SURFACE_3.getColor(view.context)
        } ?: Log.d("LottieDialog", "no dialog attached")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingDialog = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        loadingDialog?.onCancelListener
            ?.onCancelListener
            ?.invoke(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        loadingDialog?.jobs?.run {
            forEach { job -> job.cancel() }
            clear()
        }
        loadingDialog?.onDismissListener
            ?.onDismissListener
            ?.invoke(dialog)
    }

    abstract val dialogBuilder: (LottieLoadingDialog.() -> Unit)

}
