@file:Suppress("PackageNaming")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import io.github.achmadhafid.sample_app.databinding.ActivityMainBinding
import io.github.achmadhafid.simplepref.SimplePref
import io.github.achmadhafid.simplepref.simplePref
import io.github.achmadhafid.zpack.extension.toggleTheme

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, SimplePref {

    //region Preference

    private var appTheme: Int? by simplePref()
    private var currentFragment by simplePref { navController.graph.startDestinationId }

    //endregion
    //region View Binding

    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy {
        findNavController(R.id.nav_host_fragment)
    }

    //endregion
    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        navController.addOnDestinationChangedListener(this)

        if (savedInstanceState == null)
            loadLastDestination()
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
    //region Navigation Helper

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
            currentFragment = destination.id
        }
        supportActionBar?.subtitle = when (destination.id) {
            R.id.confirmationDialogFragment -> "Confirmation"
            R.id.loadingDialogFragment -> "Loading"
            R.id.inputDialogFragment -> "Input"
            else -> ""
        }
    }

    private fun loadLastDestination() {
        if (navController.graph.startDestinationId != currentFragment) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, true)
                .build()
            when (currentFragment) {
                R.id.confirmationDialogFragment -> R.id.action_confirmation_dialog
                R.id.loadingDialogFragment -> R.id.action_loading_dialog
                R.id.inputDialogFragment -> R.id.action_input_dialog
                else -> null
            }?.let { navController.navigate(it, null, navOptions) }
        }
    }

    //endregion

}
