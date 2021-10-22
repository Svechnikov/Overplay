package com.svechnikov.overplay.moving

interface MoveAwayDetector {
    var distanceMeters: Float
    fun setListener(listener: () -> Unit)
    fun start()
    fun stop()
}