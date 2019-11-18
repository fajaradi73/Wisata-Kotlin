package com.fajarproject.travels.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.fajarproject.travels.R
import com.fajarproject.travels.mapsTravels.adapter.IconMenuAdapter
import com.skydoves.powermenu.*


object PowerMenuUtil {

    fun getHamburgerPowerMenu(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?>?,
        onDismissedListener: OnDismissedListener?
    ): PowerMenu? {
        return PowerMenu.Builder(context)
            .addItem(PowerMenuItem("Novel", true))
            .addItem(PowerMenuItem("Poetry", false))
            .addItem(PowerMenuItem("Art", false))
            .addItem(PowerMenuItem("Journals", false))
            .addItem(PowerMenuItem("Travel", false))
            .setAutoDismiss(true)
            .setLifecycleOwner(lifecycleOwner)
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT)
            .setCircularEffect(CircularEffect.BODY)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .setTextColor(ContextCompat.getColor(context, R.color.material_blue_grey_800))
            .setTextSize(12)
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .setOnDismissListener(onDismissedListener)
            .setPreferenceName("HamburgerPowerMenu")
            .setInitializeRule(Lifecycle.Event.ON_CREATE, 0)
            .build()
    }
    fun getIconPowerMenu(
        context: Context?,
        lifecycleOwner: LifecycleOwner,
        onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?>?
    ): PowerMenu? {
        return PowerMenu.Builder(context!!)
            .addItem(PowerMenuItem("Default", R.drawable.ic_default))
            .addItem(PowerMenuItem("Satellite", R.drawable.ic_satelite))
            .addItem(PowerMenuItem("Medan", R.drawable.ic_terrain))
            .setLifecycleOwner(lifecycleOwner)
            .setSelectedMenuColor(R.color.colorPrimary)
            .setSelectedTextColor(R.color.white)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .setAnimation(MenuAnimation.FADE)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .build()
    }

    fun getCustomDialogPowerMenu(
        context: Context, lifecycleOwner: LifecycleOwner
    ): CustomPowerMenu<*, *>? {
        return CustomPowerMenu.Builder(
            context,
            IconMenuAdapter()
        )
            .setHeaderView(R.layout.layout_custom_power_menu)
            .setLifecycleOwner(lifecycleOwner)
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setWidth(800)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .build()
    }
}