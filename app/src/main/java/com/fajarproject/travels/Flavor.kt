package com.fajarproject.travels

import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE
import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE_BACKGROUND
import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE_BACKGROUND_DEV
import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE_DEV
import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE_PROFILE
import com.fajarproject.travels.config.apiUrl.Companion.BASE_IMAGE_PROFILE_DEV
import com.fajarproject.travels.config.apiUrl.Companion.BASE_URL
import com.fajarproject.travels.config.apiUrl.Companion.BASE_URL_DEV

/**
 * Created by Fajar Adi Prasetyo on 26/01/24.
 */

enum class Flavor { DEVELOPMENT, PRODUCTION }

class FlavorConfig {
	companion object {
		var flavor: Flavor? = null

		fun baseUrl(): String {
			assert(flavor != null) { "Need initiated flavor type first " }
			return if (flavor == Flavor.DEVELOPMENT) {
				BASE_URL_DEV
			} else {
				BASE_URL
			}
		}

		fun baseImage(): String {
			assert(flavor != null) { "Need initiated flavor type first " }
			return if (flavor == Flavor.DEVELOPMENT) {
				BASE_IMAGE_DEV
			} else {
				BASE_IMAGE
			}
		}

		fun baseImageProfile(): String {
			assert(flavor != null) { "Need initiated flavor type first " }
			return if (flavor == Flavor.DEVELOPMENT) {
				BASE_IMAGE_PROFILE_DEV
			} else {
				BASE_IMAGE_PROFILE
			}
		}

		fun baseImageBackground(): String {
			assert(flavor != null) { "Need initiated flavor type first " }
			return if (flavor == Flavor.DEVELOPMENT) {
				BASE_IMAGE_BACKGROUND_DEV
			} else {
				BASE_IMAGE_BACKGROUND
			}
		}
	}

}