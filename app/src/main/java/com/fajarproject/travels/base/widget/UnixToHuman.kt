package com.fajarproject.travels.base.widget

import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by Fajar Adi Prasetyo on 29/01/2020.
 */
object UnixToHuman {
	private const val SECOND_MILLIS = 1000
	private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
	private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
	private const val DAY_MILLIS = 24 * HOUR_MILLIS
	private const val WEEK_MILLIS = 7 * DAY_MILLIS

	fun getTimeAgo(times: Long): String {
		var time = times
		if (time < 1000000000000L) { // if timestamp given in seconds, convert to millis
			time *= 1000
		}
		val now = System.currentTimeMillis()
		var diff = now - time
		return if (diff > 0) {
			when {
				diff < MINUTE_MILLIS -> {
					"baru saja"
				}
				diff < 2 * MINUTE_MILLIS -> {
					"menit yang lalu"
				}
				diff < 50 * MINUTE_MILLIS -> {
					(diff / MINUTE_MILLIS).toString() + " menit yang lalu"
				}
				diff < 90 * MINUTE_MILLIS -> {
					"1 jam yang lalu"
				}
				diff < 24 * HOUR_MILLIS -> {
					(diff / HOUR_MILLIS).toString() + " jam yang lalu"
				}
				diff < 48 * HOUR_MILLIS -> {
					"kemarin"
				}
				diff < 7 * DAY_MILLIS -> {
					(diff / DAY_MILLIS).toString() + " hari yang lalu"
				}
				diff < 2 * WEEK_MILLIS -> {
					"Seminggu yang lalu"
				}
				diff < WEEK_MILLIS * 3 -> {
					(diff / WEEK_MILLIS).toString() + " minggu yang lalu"
				}
				else -> {
					val date = Date(time)
					val df =
						SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
					df.format(date)
				}
			}
		} else {
			diff = time - now
			when {
				diff < MINUTE_MILLIS -> {
					"menit ini"
				}
				diff < 2 * MINUTE_MILLIS -> {
					"semenit yang akan datang"
				}
				diff < 50 * MINUTE_MILLIS -> {
					(diff / MINUTE_MILLIS).toString() + " menit yang akan datang"
				}
				diff < 90 * MINUTE_MILLIS -> {
					"sejam yang akan datang"
				}
				diff < 24 * HOUR_MILLIS -> {
					(diff / HOUR_MILLIS).toString() + " jam yang akan datang"
				}
				diff < 48 * HOUR_MILLIS -> {
					"besok"
				}
				diff < 7 * DAY_MILLIS -> {
					(diff / DAY_MILLIS).toString() + " hari yang akan datang"
				}
				diff < 2 * WEEK_MILLIS -> {
					"minggu depan"
				}
				diff < WEEK_MILLIS * 3 -> {
					(diff / WEEK_MILLIS).toString() + " minggu yang akan datang"
				}
				else -> {
					val date = Date(time)
					val df =
						SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
					df.format(date)
				}
			}
		}
	}
}