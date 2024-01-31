package com.fajarproject.travels.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.parceler.Parcel

/**
 * Created by Fajar Adi Prasetyo on 12/01/20.
 */

@Parcel
data class PictureItem(

	@SerializedName("id_wisata")
	@Expose
	val idWisata: String? = "",

	@SerializedName("nama_wisata")
	@Expose
	val namaWisata: String? = "",

	@SerializedName("create_date")
	@Expose
	val createDate: Long? = 0,

	@SerializedName("picture")
	@Expose
	val picture: String? = ""
) : Parcelable {
	constructor(parcel: android.os.Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Long::class.java.classLoader) as? Long,
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: android.os.Parcel, flags: Int) {
		parcel.writeString(idWisata)
		parcel.writeString(namaWisata)
		parcel.writeValue(createDate)
		parcel.writeString(picture)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<PictureItem> {
		override fun createFromParcel(parcel: android.os.Parcel): PictureItem {
			return PictureItem(parcel)
		}

		override fun newArray(size: Int): Array<PictureItem?> {
			return arrayOfNulls(size)
		}
	}

}