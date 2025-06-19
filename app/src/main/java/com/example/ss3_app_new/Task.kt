package com.example.ss3_app_new

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val title: String,
    val description: String,
    val dateTime: Long
) : Parcelable
