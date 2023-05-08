package com.example.servertelnetlegacyalignet

import android.os.Parcelable
import java.io.Serializable

data class Tests(val host:String, val port:String, val success:Boolean):Serializable
