@file:Suppress("WildcardImport")

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
import io.github.achmadhafid.zpack.ktx.*

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
                        formattedValue: String
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
            btnDone.visibleOrInvisible { inputValidator?.invoke(it.toString()) ?: it.toString().isNotEmpty()}
            if (inputType == Type.PASSWORD) {
                togglePasswordVisibility(btnExtra, editText)
            }
        }

        editText.inputType = when (inputType) {
            Type.TEXT     -> InputType.TYPE_CLASS_TEXT
            Type.NUMERIC  -> InputType.TYPE_CLASS_NUMBER
            Type.PHONE    -> InputType.TYPE_CLASS_PHONE
            Type.PIN      -> InputType.TYPE_CLASS_TEXT.also {
                editText.filters += InputFilter.AllCaps()
            }
            Type.PASSWORD -> INPUT_TYPE_HIDDEN_PASSWORD.also { btnExtra.hide() }
        }
        initialValue?.let {
            editText.setText(it)
            editText.moveCursorToTheEnd()
        }

        btnClear.onSingleClick(autoReEnable = true) {
            editText.clear()
        }
        btnDone.onSingleClick(autoReEnable = true) {
            input?.let { value ->
                inputValidator?.let {
                    inputListener(dialog, value, it(value))
                } ?: inputListener(dialog, value, true)
            }
        }
        if (inputType == Type.PASSWORD) {
            btnExtra.onSingleClick(autoReEnable = true) {
                editText.togglePasswordVisibility()
                togglePasswordVisibility(btnExtra, editText)
            }
        }
    }

    private fun togglePasswordVisibility(btnToggle: ImageButton, editText: EditText) {
        btnToggle.visibleOrInvisible { input?.isNotEmpty() ?: false }
        btnToggle.setImageResource(
            if (editText.inputType == INPUT_TYPE_HIDDEN_PASSWORD)
                R.drawable.ic_round_visibility_24dp
            else
                R.drawable.ic_round_visibility_off_24dp
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
