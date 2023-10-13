package com.example.mychat.model

import android.os.Parcel
import android.os.Parcelable
//Var here must similar to otherwise in Hashmap
data class Users (
    val userid: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val useremail: String? = "",
):Parcelable{
    // Parcelable is an Android interface that allows you to pass objects between components
    // (like Activities and Fragments) using Intents.
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userid)
        parcel.writeString(status)
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(useremail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }

}


