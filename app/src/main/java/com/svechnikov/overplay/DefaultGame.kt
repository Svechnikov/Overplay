package com.svechnikov.overplay

import android.location.Location
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.svechnikov.overplay.rotation.handlers.RotationHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DefaultGame(
    private val player: SimpleExoPlayer,
    private val playerView: StyledPlayerView,
    private val sensorEvents: SensorEvents,
    private val rotationHandler: RotationHandler,
) : Game {

    private var prevLocation: Location? = null

    override fun start(lifecycleOwner: LifecycleOwner) {
        playerView.let {
            it.player = player
            it.isVisible = false
        }
        player.apply {
            setMediaItem(MediaItem.fromUri(URL))
            prepare()
        }
        lifecycleOwner.lifecycle.coroutineScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(DELAY)

                playerView.isVisible = true
                playFromStart()
            }
        }
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                sensorEvents.start()
            }

            override fun onPause(owner: LifecycleOwner) {
                sensorEvents.stop()
                player.stop()
                playerView.isVisible = false
            }
            override fun onDestroy(owner: LifecycleOwner) = player.release()
        })

        sensorEvents.apply {
            setLocationListener(::checkLocation)
            setRotationListener(rotationHandler::handleRotation)
            setShakeListener(player::pause)
        }
    }

    private fun playFromStart() {
        player.apply {
            seekToDefaultPosition()
            play()
        }
    }

    private fun checkLocation(location: Location) {
        val prevLocation = this.prevLocation ?: location.also { prevLocation = it }

        if (prevLocation.distanceTo(location) >= PAUSE_DISTANCE_METERS) {
            this.prevLocation = location
            playFromStart()
        }
    }

    private companion object {
        const val URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
        const val DELAY = 4_000L
        const val PAUSE_DISTANCE_METERS = 10f
    }
}