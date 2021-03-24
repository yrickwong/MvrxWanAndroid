package com.yrickwong.tech.pictureapp

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyItemSpacingDecorator
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.*
import com.google.android.material.snackbar.Snackbar
import com.yrickwong.tech.pictureapp.core.BaseEpoxyFragment
import com.yrickwong.tech.pictureapp.core.MvRxViewModel
import com.yrickwong.tech.pictureapp.core.simpleController
import com.yrickwong.tech.pictureapp.feature.PictureState
import com.yrickwong.tech.pictureapp.feature.PictureViewModel
import com.yrickwong.tech.pictureapp.widgets.loadingRow
import com.yrickwong.tech.pictureapp.widgets.pictureSquare


private const val SPAN_COUNT = 3
private const val TAG = "wangyi"

enum class LayoutManagerType {
    GRID_STYLE,
    LIST_STYLE
}

data class StyleState(@PersistState val layoutManagerType: LayoutManagerType = LayoutManagerType.LIST_STYLE) :
    MvRxState

//在ViewModel中操作state均是在background线程
class StyleViewModel(styleState: StyleState) : MvRxViewModel<StyleState>(styleState) {

    fun changeStyle(style: LayoutManagerType) {
        setState {
            copy(layoutManagerType = style)
        }
    }

}

class MainFragment : BaseEpoxyFragment() {
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: EpoxyRecyclerView
    private lateinit var searchView: SearchView
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
            refreshLayout = findViewById(R.id.swipeRefreshLayout)
            recyclerView = findViewById(R.id.recycleView)
            recyclerView.setController(epoxyController)
            recyclerView.addItemDecoration(EpoxyItemSpacingDecorator(8.dp))
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
        searchView = menu.findItem(R.id.item_searchview).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        styleViewModel.selectSubscribe(StyleState::layoutManagerType) { type ->
            when (type) {
                LayoutManagerType.GRID_STYLE -> {
                    Log.d(TAG, "invalidate: GRID_STYLE")
                    val gridLayoutManager = GridLayoutManager(
                        requireContext(),
                        SPAN_COUNT
                    )
                    epoxyController.spanCount = SPAN_COUNT
                    recyclerView.apply {
                        layoutManager = gridLayoutManager
                    }
                }
                LayoutManagerType.LIST_STYLE -> {
                    Log.d(TAG, "invalidate: LIST_STYLE")
                    val gridLayoutManager = GridLayoutManager(
                        requireContext(),
                        1
                    )
                    epoxyController.spanCount = 1
                    gridLayoutManager.spanSizeLookup = epoxyController.spanSizeLookup
                    recyclerView.apply {
                        layoutManager = gridLayoutManager
                    }
                }
            }
        }
        pictureViewModel.selectSubscribe(
            PictureState::request,
            PictureState::pictures,
            UniqueOnly("pictureViewModel")
        ) { request, pictures ->
            when (request) {
                is Success -> {
                    refreshLayout.isRefreshing = false
                }
                is Fail -> {
                    refreshLayout.isRefreshing = false
                    Snackbar.make(recyclerView, "image request failed!", Snackbar.LENGTH_SHORT)
                        .show()
                }
                is Loading -> {
                }
                else -> {

                }
            }
        }
        refreshLayout.setOnRefreshListener(pictureViewModel::reloadData)
    }

    override fun epoxyController() = simpleController(pictureViewModel) { pictureState ->
        pictureState.pictures.forEach { pic ->
            pictureSquare {
                id(pic.id)
                picture(pic)
                clickListener { _ ->

                }
            }
        }
        if (pictureState.pictures.isNotEmpty()) {
            loadingRow {
                id("loading${pictureState.pictures.size}")
                onBind { _, _, _ -> pictureViewModel.fetchNextPage() }
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