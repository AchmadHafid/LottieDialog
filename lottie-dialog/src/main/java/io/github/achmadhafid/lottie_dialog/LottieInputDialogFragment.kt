package io.github.achmadhafid.lottie_dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class LottieInputDialogFragment : BottomSheetDialogFragment() {

    private var inputDialog: LottieInputDialog? = null
    protected val editTextInput: EditText?
        get() = view?.findViewById(R.id.lottie_dialog_edt_input)

    override fun getTheme(): Int {
        return R.style.LottieDialogTheme_BottomSheet_DayNight
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.lottie_dialog_input, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputDialog = LottieInputDialog().apply(dialogBuilder)
        inputDialog!!.invoke(dialog!!, view, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputDialog = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        inputDialog?.onCancelListener
            ?.onCancelListener
            ?.invoke(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        inputDialog?.onDismissListener
            ?.onDismissListener
            ?.invoke(dialog)
    }

    abstract val dialogBuilder: (LottieInputDialog.() -> Unit)

}
