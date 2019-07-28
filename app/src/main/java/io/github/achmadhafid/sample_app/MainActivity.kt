@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import com.google.android.material.appbar.AppBarLayout
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

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMaterialToolbar(R.id.toolbar)
        appBarLayout.setSelectedOnScrollDown(scrollView)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.subtitle = when (destination.id) {
                R.id.confirmationDialogFragment -> "Confirmation"
                R.id.loadingDialogFragment -> "Loading"
                R.id.inputDialogFragment -> "Input"
                else -> null
            }
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




















