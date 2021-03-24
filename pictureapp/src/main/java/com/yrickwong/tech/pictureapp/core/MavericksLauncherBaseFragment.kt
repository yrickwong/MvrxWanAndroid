package com.yrickwong.tech.pictureapp.core

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.MavericksView

/**
 */
abstract class MavericksLauncherBaseFragment : Fragment(), MavericksView {

    protected val epoxyController by lazy { epoxyController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!onBackPressed()) {
                        // Pass on back press to other listeners, or the activity's default handling
                        isEnabled = false
                        requireActivity().onBackPressed()
                        isEnabled = true
                    }
                }
            }
        )
    }


    protected open fun onBackPressed(): Boolean = false

    override fun invalidate() {
        epoxyController.requestModelBuild()
    }

    /**
     * Provide the EpoxyController to use when building models for this Fragment.
     * Basic usages can simply use [simpleController]
     */
    abstract fun epoxyController(): EpoxyController

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }
}
