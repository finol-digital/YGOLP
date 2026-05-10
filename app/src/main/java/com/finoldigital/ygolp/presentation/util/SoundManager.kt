package com.finoldigital.ygolp.presentation.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.annotation.RawRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.finoldigital.ygolp.R

class SoundManager(private val context: Context) : DefaultLifecycleObserver {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    private var activeMediaPlayer: MediaPlayer? = null
    var isMuted: Boolean = false
    private var released: Boolean = false

    // Sounds that require completion callbacks use MediaPlayer
    private val mediaPlayerSounds = setOf(R.raw.duel_start, R.raw.its_time_to_duel)

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        // Preload SoundPool sounds (fire-and-forget only)
        loadSound(R.raw.lifepoints_change)
    }

    private fun loadSound(@RawRes resId: Int) {
        soundPool?.let { pool ->
            val soundId = pool.load(context, resId, 1)
            soundMap[resId] = soundId
        }
    }

    fun play(@RawRes soundResId: Int, onCompletion: (() -> Unit)? = null) {
        if (released) {
            onCompletion?.invoke()
            return
        }

        if (isMuted) {
            onCompletion?.invoke()
            return
        }

        if (soundResId in mediaPlayerSounds) {
            // Stop and release any existing MediaPlayer to prevent overlap
            activeMediaPlayer?.let { mp ->
                mp.stop()
                mp.release()
            }
            activeMediaPlayer = null

            val mp = MediaPlayer.create(context, soundResId)
            if (mp != null) {
                activeMediaPlayer = mp
                mp.setOnCompletionListener {
                    it.release()
                    if (activeMediaPlayer == it) {
                        activeMediaPlayer = null
                    }
                    onCompletion?.invoke()
                }
                mp.start()
            } else {
                onCompletion?.invoke()
            }
        } else {
            // Fire-and-forget via SoundPool
            val soundId = soundMap[soundResId]
            if (soundId != null) {
                soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
            } else {
                loadSound(soundResId)
                // Play after loading on next call
            }
            onCompletion?.invoke()
        }
    }

    fun releaseAll() {
        released = true
        activeMediaPlayer?.let { mp ->
            mp.stop()
            mp.release()
        }
        activeMediaPlayer = null
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }

    override fun onStop(owner: LifecycleOwner) {
        // Optional: reduce volume or pause
    }

    override fun onDestroy(owner: LifecycleOwner) {
        releaseAll()
    }
}