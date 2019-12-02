package com.yrickwong.tech.pictureapp

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.*
import com.yrickwong.tech.pictureapp.core.BaseEpoxyFragment
import com.yrickwong.tech.pictureapp.core.MvRxViewModel
import com.yrickwong.tech.pictureapp.core.simpleController
import com.yrickwong.tech.pictureapp.feature.PictureViewModel
import com.yrickwong.tech.pictureapp.views.loadingRow
import com.yrickwong.tech.pictureapp.views.pictureSquare


private const val SPAN_COUNT = 3
private const val TAG = "wangyi"

enum class LayoutManagerType {
    GRID_STYLE,
    LIST_STYLE
}

data class StyleState(@PersistState val layoutManagerType: LayoutManagerType = LayoutManagerType.GRID_STYLE) :
    MvRxState

class StyleViewModel(styleState: StyleState) : MvRxViewModel<StyleState>(styleState) {

    fun changeStyle(style: LayoutManagerType) {
        setState {
            copy(layoutManagerType = style)
        }
    }

}

class MainFragment : BaseEpoxyFragment() {


    private lateinit var recyclerView: EpoxyRecyclerView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private val pictureViewModel: PictureViewModel by activityViewModel()
    private val styleViewModel: StyleViewModel by activityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            recyclerView = findViewById(R.id.recycleView)
            recyclerView.setController(epoxyController)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.grid_style -> {
                styleViewModel.changeStyle(LayoutManagerType.GRID_STYLE)
            }
            R.id.list_style -> {
                styleViewModel.changeStyle(LayoutManagerType.LIST_STYLE)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        searchView =
            menu.findItem(R.id.item_searchview).actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (TextUtils.isEmpty(query)) {
                    requireActivity().showToast("请求内容不能为空!")
                } else {
                    pictureViewModel.fetchData(query!!)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        styleViewModel.subscribe { styleState ->
            when (styleState.layoutManagerType) {
                LayoutManagerType.GRID_STYLE -> {
                    Log.d(TAG, "invalidate: GRID_STYLE")
                    val gridLayoutManager = GridLayoutManager(
                        requireContext(),
                        SPAN_COUNT
                    )
                    epoxyController.spanCount = SPAN_COUNT
                    gridLayoutManager.spanSizeLookup = epoxyController.spanSizeLookup
                    recyclerView.apply {
                        if (itemDecorationCount == 0) {
                            addItemDecoration(
                                EpoxyItemSpacingDecorator(8.dp)
                            )
                        }
                        layoutManager = gridLayoutManager
                    }
                }
                LayoutManagerType.LIST_STYLE -> {
                    Log.d(TAG, "invalidate: LIST_STYLE")
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    recyclerView.apply {
                        if (itemDecorationCount == 0) {
                            addItemDecoration(
                                EpoxyItemSpacingDecorator(8.dp)
                            )
                        }
                        layoutManager = linearLayoutManager
                    }
                }
            }
        }
    }

    override fun epoxyController() = simpleController(pictureViewModel) { pictureState ->
        pictureState.pictures.forEach { pic ->
            pictureSquare {
                id(pic.id.hashCode())
                picture(pic)
            }
        }
        //判断条件是什么？
        loadingRow {
            // Changing the ID will force it to rebind when new data is loaded even if it is
            // still on screen which will ensure that we trigger loading again.
            id("loading${pictureState.page}")
            onBind { _, _, _ ->
                if (pictureState.page > 0) {
                    pictureViewModel.fetchNextPage()
                }
            }
        }
    }
}

fun Context.showToast(text: CharSequence) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

val Float.dp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()


val Float.sp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics
    )


val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()