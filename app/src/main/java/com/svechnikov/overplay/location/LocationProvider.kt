package com.svechnikov.overplay.location

import android.location.Location

interface LocationProvider {
    fun setListener(listener: (Location) -> Unit)
    fun start()
    fun stop()
}