package com.fajarproject.travels.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

/**
 * Created by Fajar Adi Prasetyo on 09/01/20.
 */

@Parcel
data class ProfileModel(

	@field:SerializedName("password")
	val password: Boolean? = false,

	@field:SerializedName("login_type")
	val loginType: Int? = 0,

	@field:SerializedName("jumlahFavorite")
	val jumlahFavorite: Int? = 0,

	@field:SerializedName("mobile_phone")
	val mobilePhone: String? = "",

	@field:SerializedName("last_login")
	val lastLogin: Long? = 0,

	@field:SerializedName("birth_date")
	val tanggal_lahir: Long? = 0,

	@field:SerializedName("login_with")
	val loginWith: String? = "",

	@field:SerializedName("gender")
	val gender: String? = "",

	@field:SerializedName("birth_place")
	val tempat_lahir: String? = "",

	@field:SerializedName("picture_background")
	val picture_background: String? = "",

	@field:SerializedName("fullname")
	val fullname: String? = "",

	@field:SerializedName("firebase_token")
	val firebaseToken: String? = "",

	@field:SerializedName("email")
	val email: String? = "",

	@field:SerializedName("picture")
	val picture: String? = "",

	@field:SerializedName("username")
	val username: String? = ""
) : Parcelable {
	constructor(parcel: android.os.Parcel) : this(
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Long::class.java.classLoader) as? Long,
		parcel.readValue(Long::class.java.classLoader) as? Long,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
		parcel.writeValue(password)
		parcel.writeValue(loginType)
		parcel.writeValue(jumlahFavorite)
		parcel.writeString(mobilePhone)
		parcel.writeValue(lastLogin)
		parcel.writeValue(tanggal_lahir)
		parcel.writeString(loginWith)
		parcel.writeString(gender)
		parcel.writeString(tempat_lahir)
		parcel.writeString(picture_background)
		parcel.writeString(fullname)
		parcel.writeString(firebaseToken)
		parcel.writeString(email)
		parcel.writeString(picture)
		parcel.writeString(username)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ProfileModel> {
		override fun createFromParcel(parcel: android.os.Parcel): ProfileModel {
			return ProfileModel(parcel)
		}

		override fun newArray(size: Int): Array<ProfileModel?> {
			return arrayOfNulls(size)
		}
	}
}