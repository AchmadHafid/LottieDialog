@file:Suppress("unused")

package io.github.achmadhafid.lottie_dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class LottieConfirmationDialogFragment : BottomSheetDialogFragment() {

    private var confirmationDialog: LottieConfirmationDialog? = null

    override fun getTheme(): Int {
        return R.style.LottieDialogTheme_BottomSheet_DayNight
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.lottie_dialog_confirmation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmationDialog = LottieConfirmationDialog().apply(dialogBuilder)
        confirmationDialog!!.invoke(dialog, view, viewLifecycleOwner.lifecycleScope, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        confirmationDialog = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        confirmationDialog?.onCancelListener
            ?.onCancelListener
            ?.invoke(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        confirmationDialog?.onDismissListener
            ?.onDismissListener
            ?.invoke(dialog)
    }

    abstract val dialogBuilder: (LottieConfirmationDialog.() -> Unit)

}
