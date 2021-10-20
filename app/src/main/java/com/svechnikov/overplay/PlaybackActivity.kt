package com.svechnikov.overplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlaybackActivity : AppCompatActivity() {

    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StyledPlayerView(this).let { playerView ->
            setContentView(playerView)
            player = Player(this, playerView, this)
        }
    }
}