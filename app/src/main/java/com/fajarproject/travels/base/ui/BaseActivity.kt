package com.fajarproject.travels.base.ui

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

import butterknife.ButterKnife
import retrofit2.Call
import rx.subscriptions.CompositeSubscription

/**
 * Created by Fajar Adi Prasetyo on 06/01/20.
 */

open class BaseActivity : AppCompatActivity() {
    var activity: Activity? = null
    private var compositeSubscription: CompositeSubscription? = null
    private var calls: MutableList<Call<Any?>>? = null

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        activity = this
        ButterKnife.bind(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        onCancelled()
        onUnsubscribe()
    }

    private fun onCancelled() {
        if (calls != null && calls!!.size > 0) {
            for (call in calls!!) {
                if (!call.isCanceled) {
                    call.cancel()
                }
            }
        }
    }

    fun addCalls(call: Call<Any?>) {
        if (calls == null) {
            calls = ArrayList()
        }
        calls!!.add(call)
    }

    private fun onUnsubscribe() {
        if (compositeSubscription != null && compositeSubscription!!.hasSubscriptions()) {
            compositeSubscription!!.unsubscribe()
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}