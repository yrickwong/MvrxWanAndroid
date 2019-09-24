package com.yrickwong.tech.mvrx.core

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRx

//Mvrx + Epoxy
private const val TAG = "BaseEpoxyFragment"

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

//    protected fun subscribeVM(vararg viewModels: BaseMvRxViewModel<*>) {
//        viewModels.forEach {
//            it.subscribe(owner = this, subscriber = {
//                postInvalidate()
//            })
//        }
//    }

    abstract fun epoxyController(): MvRxEpoxyController

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {

        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    protected fun navigateTo(@IdRes actionId: Int, arg: Parcelable? = null) {
        /**
         * If we put a parcelable arg in [MvRx.KEY_ARG] then MvRx will attempt to call a secondary
         * constructor on any MvRxState objects and pass in this arg directly.
         * @see [com.yrickwong.tech.mvrx.feature.wechat.KnowledgeArgs]
         */
        val bundle = arg?.let { Bundle().apply { putParcelable(MvRx.KEY_ARG, it) } }
        findNavController().navigate(actionId, bundle)
    }
}