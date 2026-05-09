package com.finoldigital.ygolp.presentation

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class SoundManager(private val context: Context) : DefaultLifecycleObserver {

    private val activePlayers = mutableMapOf<Int, MediaPlayer>()
    var isMuted: Boolean = false

    fun play(@RawRes soundResId: Int, onCompletion: (() -> Unit)? = null) {
        if (isMuted) {
            onCompletion?.invoke()
            return
        }

        // Stop and release any existing player for this sound
        activePlayers[soundResId]?.let { existing ->
            existing.setOnCompletionListener(null)
            existing.release()
            activePlayers.remove(soundResId)
        }

        val player = MediaPlayer.create(context, soundResId) ?: return
        activePlayers[soundResId] = player
        player.setOnCompletionListener {
            it.release()
            activePlayers.remove(soundResId)
            onCompletion?.invoke()
        }
        player.start()
    }

    fun releaseAll() {
        activePlayers.values.forEach { player ->
            player.setOnCompletionListener(null)
            player.release()
        }
        activePlayers.clear()
    }

    override fun onStop(owner: LifecycleOwner) {
        releaseAll()
    }
}

