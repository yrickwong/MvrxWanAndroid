package com.yrickwong.tech.pictureapp.core

import android.os.Bundle
import android.util.Log
import com.airbnb.mvrx.BaseMvRxFragment

//Mvrx + Epoxy
private const val TAG = "wangyi"

abstract class BaseEpoxyFragment : BaseMvRxFragment() {

    protected val epoxyController by lazy { epoxyController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun invalidate() {
        Log.d(TAG, "invalidate: ")
        epoxyController.requestModelBuild()
    }


    abstract fun epoxyController(): MvRxEpoxyController

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {

        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

}