package com.yrickwong.tech.pictureapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.activityViewModel
import com.yrickwong.tech.pictureapp.core.BaseEpoxyFragment
import com.yrickwong.tech.pictureapp.core.simpleController
import com.yrickwong.tech.pictureapp.feature.PictureViewModel
import com.yrickwong.tech.pictureapp.views.loadingRow
import com.yrickwong.tech.pictureapp.views.pictureSquare

class MainFragment : BaseEpoxyFragment() {


    private lateinit var recyclerView: EpoxyRecyclerView
    private lateinit var searchView: SearchView

    private val pictureViewModel: PictureViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false).apply {
            recyclerView = findViewById(R.id.recycleView)
            searchView = findViewById(R.id.sv_place)
            recyclerView.setController(epoxyController)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun epoxyController() = simpleController(pictureViewModel) { pictureState ->
        pictureState.pictures.forEach { art ->
            pictureSquare {
                id(1)
                picture(art)
            }
        }

//        loadingRow {
//            // Changing the ID will force it to rebind when new data is loaded even if it is
//            // still on screen which will ensure that we trigger loading again.
//            id("loading${pictureState.page}")
//            onBind { _, _, _ ->
//                if (pictureState.page > 0) {
//                    pictureViewModel.fetchNextPage()
//                }
//            }
//        }
    }
}