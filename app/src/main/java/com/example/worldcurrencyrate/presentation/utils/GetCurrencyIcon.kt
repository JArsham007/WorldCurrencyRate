package com.example.worldcurrencyrate.presentation.utils

import android.content.Context

fun String.getCurrencyIcon(
    context: Context
): Int {

    val res = context.resources

    return res.getIdentifier(this, "drawable", context.packageName)

}