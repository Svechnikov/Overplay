package com.svechnikov.overplay

import android.location.Location
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DefaultGame(
    private val player: SimpleExoPlayer,
    private val playerView: StyledPlayerView,
    private val sensorEvents: SensorEvents,
) : Game {

    override fun start(lifecycleOwner: LifecycleOwner) {
        playerView.isVisible = false
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
            override fun onPause(owner: LifecycleOwner) {
                player.stop()
                playerView.isVisible = false
            }
            override fun onDestroy(owner: LifecycleOwner) = player.release()
        })

        sensorEvents.apply {
            setLocationListener(::checkLocation)
            setRotationListener(::checkRotation)
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

    }

    private fun checkRotation(x: Int, y: Int, z: Int) {

    }

    private companion object {
        const val URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
        const val DELAY = 4_000L
    }
}