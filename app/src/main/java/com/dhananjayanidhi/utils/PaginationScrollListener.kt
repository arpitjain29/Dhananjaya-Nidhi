package com.dhananjayanidhi.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//abstract class PaginationScrollListener
///**
// * Supporting only LinearLayoutManager for now.
// *
// * @param layoutManager
// */
//    (private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
//
//    // abstract val totalPageCount: Int
//
//    // protected abstract val isLastPage: Boolean
//
//    protected abstract val isLoading: Boolean
//
//
//    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//        super.onScrolled(recyclerView, dx, dy)
//
//        val visibleItemCount = layoutManager.childCount
//        val totalItemCount = layoutManager.itemCount
//        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//        if (isLoading) {
//            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
//                loadMoreItems()
//            }
//        }
//
//    }
//
//    protected abstract fun loadMoreItems()
//}

abstract class PaginationScrollListener(
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    protected abstract val isLoading: Boolean
    protected abstract val isLastPage: Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && !isLastPage) {   // ✅ केवल तभी load करना है
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                && firstVisibleItemPosition >= 0
            ) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
}