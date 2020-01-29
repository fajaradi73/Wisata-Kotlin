package com.fajarproject.travels.base.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.fajarproject.travels.base.view.Callback


/**
 * Create by Fajar Adi Prasetyo on 29/01/2020.
 */

class ExpandTextView : AppCompatTextView {
	var expandState: Boolean = false
	var mCallback: Callback? = null
	var mText: String? = ""
	var maxLineCount = 3
	var ellipsizeText = "..."
	private var expandText = "Selengkapnya"
	var expandTextColor: Int = Color.parseColor("#18dcff")
	var collapseText = ""
	var collapseTextColor: Int = Color.parseColor("#18dcff")
	var collapseEnable = false
	var underlineEnable = true

	constructor(context: Context) : super(context) {
		initTextView()
	}

	constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
		initTextView()
	}

	private fun initTextView() {
		movementMethod = LinkMovementMethod.getInstance()
	}

	constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
		initTextView()
	}

	@SuppressLint("DrawAllocation")
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		if (mText.isNullOrEmpty()) {
			setMeasuredDimension(measuredWidth, measuredHeight)
		}
		val sl = StaticLayout(mText, paint, measuredWidth - paddingLeft - paddingRight, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)
		var lineCount = sl.lineCount
		if (lineCount > maxLineCount) {
			if (expandState) {
				text = mText
				if (collapseEnable) {
					val newEndLineText = mText + collapseText
					val spannableString = SpannableString(newEndLineText)
					spannableString.setSpan(object : Spannable(false) {
						override fun onClick(widget: View) {
							if (mCallback != null) {
								mCallback!!.onCollapseClick()
							}
						}
					}, newEndLineText.length - collapseText.length, newEndLineText.length, 0)
//					if (underlineEnable) {
//						spannableString.setSpan(UnderlineSpan(), newEndLineText.length - collapseText.length, newEndLineText.length, 0)
//					}
					spannableString.setSpan(ForegroundColorSpan(collapseTextColor), newEndLineText.length - collapseText.length, newEndLineText.length, 0)
					text = spannableString
				}
				if (mCallback != null) {
					mCallback!!.onExpand()
				}
			} else {
				lineCount = maxLineCount
				val dotWidth = paint.measureText(ellipsizeText + expandText)
				val start = sl.getLineStart(lineCount - 1)
				val end = sl.getLineEnd(lineCount - 1)
				val lineText = mText!!.substring(start, end)
				var endIndex = 0
				for (i in lineText.length - 1 downTo 0) {
					val str = lineText.substring(i, lineText.length)
					if (paint.measureText(str) >= dotWidth) {
						endIndex = i
						break
					}
				}
				val newEndLineText = mText!!.substring(0, start) + lineText.substring(0, endIndex) + ellipsizeText + expandText
				val spannableString = SpannableString(newEndLineText)
				spannableString.setSpan(object : Spannable(false) {
					override fun onClick(widget: View) {
						if (mCallback != null) {
							mCallback!!.onExpandClick()
						}
					}
				}, newEndLineText.length - expandText.length, newEndLineText.length, 0)
//				if (underlineEnable) {
//					spannableString.setSpan(UnderlineSpan(), newEndLineText.length - expandText.length, newEndLineText.length, 0)
//				}
				spannableString.setSpan(expandTextColor, newEndLineText.length - expandText.length, newEndLineText.length, 0)
				text = spannableString
				if (mCallback != null) {
					mCallback!!.onCollapse()
				}
			}
		} else {
			text = mText
			if (mCallback != null) {
				mCallback!!.onLoss()
			}

		}
		var lineHeight = 0
		for (i in 0 until lineCount) {
			val lineBound = Rect()
			sl.getLineBounds(i, lineBound)
			lineHeight += lineBound.height()
		}
		lineHeight = (paddingTop + paddingBottom + lineHeight * lineSpacingMultiplier).toInt()
		setMeasuredDimension(measuredWidth, lineHeight)
	}

	fun setText(text: String, expanded: Boolean, callback: Callback) {
		mText = text
		expandState = expanded
		mCallback = callback

		setText(text)
	}

	fun setChanged(expanded: Boolean) {
		expandState = expanded
		requestLayout()
	}
	fun setUndelineSpan(isUnderlineSpan: Boolean){
		underlineEnable = isUnderlineSpan
	}
}
