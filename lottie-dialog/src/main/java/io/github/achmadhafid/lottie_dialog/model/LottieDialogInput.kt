@file:Suppress("PackageNaming", "unused")

package io.github.achmadhafid.lottie_dialog.model

import android.app.Dialog
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.doAfterTextChanged
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.github.achmadhafid.lottie_dialog.R
import io.github.achmadhafid.zpack.extension.view.INPUT_TYPE_HIDDEN_PASSWORD
import io.github.achmadhafid.zpack.extension.view.clear
import io.github.achmadhafid.zpack.extension.view.invisible
import io.github.achmadhafid.zpack.extension.view.setText
import io.github.achmadhafid.zpack.extension.view.togglePasswordVisibility
import io.github.achmadhafid.zpack.extension.view.visibleOrInvisible

data class LottieDialogInput(
    var inputType: Type = Type.TEXT,
    var inputFormat: String? = null,
    var initialValue: String? = null,
    internal var inputValidator: ((String) -> Boolean)? = null
) {
    private var input: String? = null

    @Suppress("ComplexMethod", "LongParameterList")
    operator fun invoke(
        dialog: Dialog,
        editText: EditText,
        btnClear: View,
        btnDone: View,
        btnExtra: ImageButton,
        inputListener: LottieDialogOnInputListener
    ) {
        inputFormat?.let {
            MaskedTextChangedListener.installOn(
                editText, it, object : MaskedTextChangedListener.ValueListener {
                    override fun onTextChanged(
                        maskFilled: Boolean,
                        extractedValue: String,
                        formattedValue: String,
                        tailPlaceholder: String
                    ) {
                        input = extractedValue
                        btnClear.visibleOrInvisible {
                            extractedValue.isNotEmpty()
                        }
                        btnDone.visibleOrInvisible {
                            maskFilled && inputValidator?.invoke(extractedValue) ?: true
                        }
                        if (inputType == Type.PASSWORD) {
                            togglePasswordVisibility(btnExtra, editText)
                        }
                    }
                })
        } ?: editText.doAfterTextChanged {
            input = it.toString()
            btnClear.visibleOrInvisible { it.toString().isNotEmpty() }
            btnDone.visibleOrInvisible {
                inputValidator?.invoke(it.toString()) ?: it.toString().isNotEmpty()
            }
            if (inputType == Type.PASSWORD) {
                togglePasswordVisibility(btnExtra, editText)
            }
        }

        editText.inputType = when (inputType) {
            Type.TEXT -> InputType.TYPE_CLASS_TEXT
            Type.NUMERIC -> InputType.TYPE_CLASS_NUMBER
            Type.PHONE -> InputType.TYPE_CLASS_PHONE
            Type.PIN -> InputType.TYPE_CLASS_TEXT.also {
                editText.filters += InputFilter.AllCaps()
            }

            Type.PASSWORD -> INPUT_TYPE_HIDDEN_PASSWORD.also { btnExtra.invisible() }
        }
        initialValue?.let {
            editText.setText(it, true)
        }

        btnClear.setOnClickListener {
            editText.clear()
        }
        btnDone.setOnClickListener {
            input?.let { value ->
                inputValidator?.let {
                    inputListener(dialog, value, it(value))
                } ?: inputListener(dialog, value, true)
            }
        }
        if (inputType == Type.PASSWORD) {
            btnExtra.setOnClickListener {
                editText.togglePasswordVisibility()
                togglePasswordVisibility(btnExtra, editText)
            }
        }
    }

    private fun togglePasswordVisibility(btnToggle: ImageButton, editText: EditText) {
        btnToggle.visibleOrInvisible { input?.isNotEmpty() ?: false }
        btnToggle.setImageResource(
            if (editText.inputType == INPUT_TYPE_HIDDEN_PASSWORD) {
                R.drawable.lottie_dialog_ic_round_visibility_24dp
            } else R.drawable.lottie_dialog_ic_round_visibility_off_24dp
        )
    }

    enum class Type {
        TEXT,
        NUMERIC,
        PHONE,
        PIN,
        PASSWORD
    }
}
