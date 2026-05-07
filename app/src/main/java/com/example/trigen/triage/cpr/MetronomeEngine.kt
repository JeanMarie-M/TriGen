package com.example.trigen.triage.cpr

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sin

@Singleton
class MetronomeEngine @Inject constructor() {

    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var thread: Thread? = null

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BEEP_FREQUENCY = 880.0
        private const val BEEP_DURATION_MS = 80
    }

    fun start(bpm: Int, onBeat: () -> Unit) {
        stop()
        isPlaying = true
        val intervalMs = (60_000.0 / bpm).toLong()

        thread = Thread {
            while (isPlaying) {
                playBeep()
                onBeat()
                try {
                    Thread.sleep(intervalMs)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }.apply { start() }
    }

    fun stop() {
        isPlaying = false
        thread?.interrupt()
        thread = null
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun updateBpm(bpm: Int, onBeat: () -> Unit) {
        start(bpm, onBeat)
    }

    private fun playBeep() {
        val numSamples = (SAMPLE_RATE * BEEP_DURATION_MS / 1000.0).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i * BEEP_FREQUENCY / SAMPLE_RATE
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            buffer[i] = (sin(angle) * envelope * Short.MAX_VALUE * 0.8).toInt().toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep(BEEP_DURATION_MS.toLong())
        track.stop()
        track.release()
    }
}