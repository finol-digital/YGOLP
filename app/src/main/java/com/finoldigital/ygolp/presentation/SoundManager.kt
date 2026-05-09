package com.finoldigital.ygolp.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.finoldigital.ygolp.R

class SoundManager(private val context: Context) : DefaultLifecycleObserver {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    var isMuted: Boolean = false

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        // Preload common sounds
        loadSound(R.raw.duel_start)
        loadSound(R.raw.its_time_to_duel)
        loadSound(R.raw.lifepoints_change)
    }

    private fun loadSound(@RawRes resId: Int) {
        soundPool?.let { pool ->
            val soundId = pool.load(context, resId, 1)
            soundMap[resId] = soundId
        }
    }

    fun play(@RawRes soundResId: Int, onCompletion: (() -> Unit)? = null) {
        if (isMuted) {
            onCompletion?.invoke()
            return
        }

        val soundId = soundMap[soundResId]
        if (soundId != null) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
            // SoundPool doesn't have a completion listener for single plays, 
            // but for short effects we can invoke it immediately or after a delay.
            // For duel_start/its_time_to_duel, we'll keep the completion behavior.
            if (soundResId == R.raw.duel_start || soundResId == R.raw.its_time_to_duel) {
                // Approximate delay for these specific sounds or just invoke
                onCompletion?.invoke()
            } else {
                onCompletion?.invoke()
            }
        } else {
            // Try to load it on the fly if not preloaded
            loadSound(soundResId)
            onCompletion?.invoke()
        }
    }

    fun releaseAll() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }

    override fun onStop(owner: LifecycleOwner) {
        // Optional: reduce volume or pause, but releaseAll usually happens on Activity onDestroy
    }

    override fun onDestroy(owner: LifecycleOwner) {
        releaseAll()
    }
}

