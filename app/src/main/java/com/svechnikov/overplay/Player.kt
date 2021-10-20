package com.svechnikov.overplay

import android.content.Context
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Player(
    context: Context,
    playerView: StyledPlayerView,
    lifecycleOwner: LifecycleOwner,
) {

    private val player = SimpleExoPlayer.Builder(context).build().apply {
        playerView.player = this
        setMediaItem(MediaItem.fromUri(URL))
    }

    init {
        playerView.isVisible = false
        lifecycleOwner.lifecycle.coroutineScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                delay(DELAY)

                playerView.isVisible = true
                player.apply {
                    prepare()
                    seekToDefaultPosition()
                    play()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                player.stop()
                playerView.isVisible = false
            }
            override fun onDestroy(owner: LifecycleOwner) = player.release()
        })
    }

    private companion object {
        const val URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
        const val DELAY = 4_000L
    }
}