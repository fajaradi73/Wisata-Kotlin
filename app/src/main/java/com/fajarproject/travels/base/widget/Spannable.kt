package com.fajarproject.travels.base.widget

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View


/**
 * Create by Fajar Adi Prasetyo on 12/01/2020.
 */
abstract class Spannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = false

    override fun onClick(widget: View) {

    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#18dcff")
    }

    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }
}