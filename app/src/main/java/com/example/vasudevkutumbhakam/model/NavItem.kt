package com.example.vasudevkutumbhakam.model

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


data class NavItem(
    val container: LinearLayout,
    val icon: ImageView,
    val text: TextView,
    val icons: Int,
    val selectedIcon: Int
)