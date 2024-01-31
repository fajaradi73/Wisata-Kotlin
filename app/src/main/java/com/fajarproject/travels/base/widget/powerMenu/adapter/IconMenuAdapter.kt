package com.fajarproject.travels.base.widget.powerMenu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.fajarproject.travels.R
import com.fajarproject.travels.base.widget.powerMenu.model.IconPowerMenuItem
import com.skydoves.powermenu.MenuBaseAdapter


class IconMenuAdapter : MenuBaseAdapter<IconPowerMenuItem?>() {
    private var position = 0

    override fun getView(index: Int, view: View?, viewGroup: ViewGroup): View? {
        var thisView: View? = view
        val context: Context = viewGroup.context
        if (thisView == null) {
            val inflater: LayoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            thisView = inflater.inflate(R.layout.item_icon_menu, viewGroup, false)
        }
        val (icon1, title1) = getItem(index) as IconPowerMenuItem
        val icon: ImageView? = thisView?.findViewById(R.id.item_icon)
        icon?.setImageDrawable(icon1)
        val title: TextView? = thisView?.findViewById(R.id.item_title)
        val parentView : ConstraintLayout? = thisView?.findViewById(R.id.parentView)
        if (position == index){
            parentView?.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
        }else{
            parentView?.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }
        title?.text = title1

        return super.getView(index, view, viewGroup)
    }

    override fun setSelectedPosition(position: Int) {
        super.setSelectedPosition(position)
        this.position = position
//        notifyDataSetChanged()
    }

}