@file:Suppress("TooManyFunctions", "WildcardImport", "MagicNumber")

package io.github.achmadhafid.sample_app

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

@MainThread
inline fun <reified V : View> AppCompatActivity.bindView(@IdRes id: Int) =
    lazy(LazyThreadSafetyMode.NONE) { findViewById<V>(id) }

fun Context.resolveColor(@ColorRes @AttrRes id: Int) = with(TypedValue()) {
    if (theme.resolveAttribute(id, this, true)) {
        data
    } else {
        ContextCompat.getColor(this@resolveColor, id)
    }
}

fun AppCompatActivity.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.toastShort(@StringRes messageRes: Int) {
    Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.isDarkThemeEnable() = resources.configuration.uiMode and
        Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

fun AppCompatActivity.toggleTheme() {
    if (isDarkThemeEnable()) lightTheme()
    else darkTheme()
}

fun lightTheme() = AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
fun darkTheme() = AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

fun atLeastOneIsChecked(vararg buttons: MaterialButton) {
    buttons.forEach { button ->
        button.addOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                button.isCheckable = false
                buttons.forEach {
                    if (it !== button) it.isCheckable = true
                }
            }
        }
    }
}

fun atLeastOneIsCheckedV2(vararg buttons: CompoundButton) {
    for((index, button) in buttons.withIndex()) {
        button.setOnCheckedChangeListener { _, _ ->
            buttons.forEach {
                if (it.isChecked) return@setOnCheckedChangeListener
            }
            if (index != buttons.size - 1) buttons[index + 1].isChecked = true
            else buttons[0].isChecked = true
        }
    }
}

fun atLeastOneIsCheckedWithConstraint(switch: CompoundButton, vararg buttons: MaterialButton) {
    val listener = MaterialButton.OnCheckedChangeListener { button, isChecked ->
        if (isChecked) {
            button.isCheckable = false
            buttons.forEach {
                if (it !== button) it.isCheckable = true
            }
        }
    }
    switch.setOnCheckedChangeListener { _, isChecked ->
        if (isChecked) {
            buttons.forEach {
                it.isEnabled   = true
                it.isCheckable = true
                it.addOnCheckedChangeListener(listener)
            }
            buttons[0].isChecked = true
        } else {
            buttons.forEach {
                it.removeOnCheckedChangeListener(listener)
                it.isCheckable = true
                it.isChecked   = false
                it.isEnabled   = false
            }
        }
    }
}

fun Context.doOnce(tag: String, block: () -> Unit) {
    val sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
    val beenDone = sharedPreferences.getBoolean(tag, false)

    if (!beenDone) {
        block()
        sharedPreferences.edit()
            .putBoolean(tag, true)
            .apply()
    }
}
