package com.svechnikov.overplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlaybackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createDefaultGame().start(this)
    }

    private fun createDefaultGame(): Game {
        val playerView = StyledPlayerView(this).also(::setContentView)
        val player = SimpleExoPlayer.Builder(this).build()

        return Game.createDefault(player, playerView)
    }
}