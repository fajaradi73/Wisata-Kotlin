package com.fajarproject.travels.base.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


/**
 * Create by Fajar Adi Prasetyo on 22/01/2020.
 */
abstract class PaginationScrollListener :
    RecyclerView.OnScrollListener {
    private var visibleThreshold = 5
    private var mLayoutManager: RecyclerView.LayoutManager
    private var firstVisibleItem: Int = 0

    constructor(layoutManager: LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val totalItemCount = mLayoutManager.itemCount
        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions =
                    (mLayoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)
                // get maximum element within the list
                visibleThreshold = (mLayoutManager as StaggeredGridLayoutManager).spanCount
                firstVisibleItem = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                visibleThreshold = (mLayoutManager as GridLayoutManager).spanCount
                firstVisibleItem = (mLayoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                visibleThreshold = (mLayoutManager as LinearLayoutManager).childCount
                firstVisibleItem = (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
        }

        if (!isLoading() && !isLastPage()) {
            if (visibleThreshold + firstVisibleItem >= totalItemCount
                && firstVisibleItem >= 0
                && totalItemCount >= getTotalPageCount()
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun getTotalPageCount(): Int
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean

    private fun getLastVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in firstVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i]
            } else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i]
            }
        }
        return maxSize
    }
}