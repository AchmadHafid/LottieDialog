@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
import io.github.achmadhafid.simplepref.extension.simplePref
import io.github.achmadhafid.simplepref.extension.simplePrefNullable
import io.github.achmadhafid.zpack.ktx.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    //region View Binding

    private val appBarLayout: AppBarLayout by bindView(R.id.appBarLayout)
    private val scrollView: NestedScrollView by bindView(R.id.scrollView)
    private val navController: NavController by bindNavController(R.id.nav_host_fragment)

    //endregion
    //region Preference

    private var appTheme: Int? by simplePrefNullable()
    private var currentFragment by simplePref { navController.graph.startDestination }

    //endregion
    //region Lifecycle Callback

    @Suppress("ComplexMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMaterialToolbar(R.id.toolbar)
        appBarLayout.setSelectedOnScrollDown(scrollView)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                currentFragment = destination.id
            }
            supportActionBar?.subtitle = when (destination.id) {
                R.id.confirmationDialogFragment -> "Confirmation"
                R.id.loadingDialogFragment      -> "Loading"
                R.id.inputDialogFragment        -> "Input"
                else -> return@addOnDestinationChangedListener
            }
        }
        if (navController.graph.startDestination != currentFragment) {
            when (currentFragment) {
                R.id.confirmationDialogFragment -> R.id.action_confirmation_dialog
                R.id.loadingDialogFragment      -> R.id.action_input_dialog
                R.id.inputDialogFragment        -> R.id.action_input_dialog
                else -> null
            }?.let { navController.navigate(it) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_select_type_confirmation -> {
                navController.navigate(R.id.action_confirmation_dialog)
                true
            }
            R.id.action_select_type_input -> {
                navController.navigate(R.id.action_input_dialog)
                true
            }
            R.id.action_select_type_loading -> {
                navController.navigate(R.id.action_loading_dialog)
                true
            }
            R.id.action_toggle_theme -> {
                appTheme = toggleTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp() =
        navController.navigateUp() || super.onSupportNavigateUp()

    //endregion

}




















