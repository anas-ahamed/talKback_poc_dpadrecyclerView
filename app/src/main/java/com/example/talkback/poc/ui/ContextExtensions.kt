package com.example.talkback.poc.ui

import android.content.Context
import android.view.accessibility.AccessibilityManager

fun Context.isAccessibilityEnabled(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return am.isEnabled
}