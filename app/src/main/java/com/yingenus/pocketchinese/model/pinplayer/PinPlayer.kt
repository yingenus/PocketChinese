package com.yingenus.pocketchinese.model.pinplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.yingenus.pocketchinese.logErrorMes
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subscribers.DefaultSubscriber

class PinPlayer {

    private var subscriber : FlowSubscriber<ToneSoundDescriptor>? = null
    var mediaPlayer = MediaPlayer()

    fun registerObserver( toneObserver : Observable<Tone>, context: Context){

        if (subscriber != null){
            subscriber?.cancelSubscriber()
            subscriber = null
            mediaPlayer = MediaPlayer()

        }

        mediaPlayer.setVolume(1f,1f)
        mediaPlayer.isLooping = false
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
        subscriber = object : FlowSubscriber<ToneSoundDescriptor>() {
            override fun onComplete() {

            }

            override fun onNext(t: ToneSoundDescriptor?) {
                Log.d("start play", t!!.tone)
                try {
                    val afd = t.assetFile
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }catch (e : Exception){
                    Log.i("pinplayer",e.logErrorMes())
                    cancel()
                }
            }

            override fun onError(t: Throwable?) {
                if (t is IllegalArgumentException){
                    Log.i("pinplayer",t.logErrorMes())
                }else{
                    Log.e("pinplayer",t!!.logErrorMes())
                }
            }


            override fun onStart() {
                mediaPlayer.setOnCompletionListener { _ ->
                    mediaPlayer.reset()
                    request(1)
                }
                request(1)
            }
        }
        toneObserver
                .map { tone -> DescriptorExtractor.getSoundDescriptor(tone.sound,context) }
                .doFinally {
                    try {
                        if (mediaPlayer.isPlaying) mediaPlayer.stop()
                        mediaPlayer.release()
                    }catch (e : IllegalStateException){

                    }
                    if(subscriber != null) subscriber = null
                }
                .toFlowable(BackpressureStrategy.BUFFER)
                .subscribe(subscriber)
    }

    fun isPlaying(): Boolean{
        return try {
            mediaPlayer.isPlaying
        }catch (e : IllegalStateException){
            false
        }
    }

    fun release(){
        subscriber?.cancelSubscriber()
        subscriber = null
    }


    private abstract class FlowSubscriber<T> : DefaultSubscriber<T>(){
        fun cancelSubscriber(){
            super.cancel()
        }
    }

}