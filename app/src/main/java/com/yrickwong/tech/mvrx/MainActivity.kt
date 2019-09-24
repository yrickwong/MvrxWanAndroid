package com.yrickwong.tech.mvrx

import android.animation.ObjectAnimator
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.MvRx
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yrickwong.tech.mvrx.feature.webview.WebViewDetailArgs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


const val TAG = "MainActivity"

class MainActivity : BaseMvRxActivity() {

    private var outAnimator: ObjectAnimator? = null
    private var inAnimator: ObjectAnimator? = null

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        currentNavController?.value?.addOnDestinationChangedListener { _, destination, arguments ->
            val args = arguments?.get(MvRx.KEY_ARG)
            when {
                args is WebViewDetailArgs -> tv_title.apply {
                    visibility = VISIBLE
                    text = args.title
                    postDelayed({
                        isSelected = true
                    }, 1000)
                }
                else -> tv_title.visibility = GONE
            }
            //隐藏 显示 BottomNavigationView
            if (destination.id == R.id.webview_fragment) {
                hideBottomNavigationView()
            } else {
                showBottomNavigationView()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_dashboard,
            R.navigation.nav_wechat,
            R.navigation.nav_navigator,
            R.navigation.nav_projects
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            Log.d(TAG, "setupBottomNavigationBar: $navController")
            toolbar.setupWithNavController(navController)
        })
        currentNavController = controller
    }

    private fun hideBottomNavigationView() {
        if (outAnimator == null) {
            outAnimator =
                ObjectAnimator.ofFloat(
                    bottomNavigationView,
                    "translationY",
                    0.0f,
                    bottomNavigationView.height.toFloat()
                )
            outAnimator!!.duration = 200
        }
        if (!outAnimator!!.isRunning && bottomNavigationView.translationY <= 0) {
            outAnimator!!.start()
        }
    }

    private fun showBottomNavigationView() {
        if (inAnimator == null) {
            inAnimator =
                ObjectAnimator.ofFloat(
                    bottomNavigationView,
                    "translationY",
                    bottomNavigationView.height.toFloat(),
                    0.0f
                )
            inAnimator!!.duration = 200
        }
        if (!inAnimator!!.isRunning && bottomNavigationView.translationY >= bottomNavigationView.height) {
            inAnimator!!.start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    /**
     * Overriding popBackStack is necessary in this case if the app is started from the deep link.
     */
    override fun onBackPressed() {
        if (currentNavController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }
}
