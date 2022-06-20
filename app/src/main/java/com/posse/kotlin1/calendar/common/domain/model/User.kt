package com.posse.kotlin1.calendar.common.domain.model

import android.net.Uri

data class User(
    val nickName: String,
    val email: String,
    val picture: Uri?,
    val isEditable: Boolean = false
)
