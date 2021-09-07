package com.yingenus.pocketchinese.model.pinplayer

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.yingenus.pocketchinese.logErrorMes
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subscribers.DefaultSubscriber

class PinPlayer {

    private var subscriber : FlowSubscriber<ToneSoundDescriptor>? = null

    fun registerObserver( toneObserver : Observable<Tone>, context: Context){
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setVolume(1f,1f)
        mediaPlayer.isLooping = false
        subscriber = object : FlowSubscriber<ToneSoundDescriptor>() {
            override fun onComplete() {

            }

            override fun onNext(t: ToneSoundDescriptor?) {
                Log.d("start play", t!!.tone)
                val afd = t.assetFile
                mediaPlayer.reset()
                mediaPlayer.setDataSource(afd.fileDescriptor,afd.startOffset,afd.length)
                mediaPlayer.prepare()
                mediaPlayer.start()
            }

            override fun onError(t: Throwable?) {
                if (t is IllegalArgumentException){
                    Log.i("pinplayer",t.logErrorMes())
                }else{
                    Log.e("pinplayer",t!!.logErrorMes())
                }
            }


            override fun onStart() {
                mediaPlayer.setOnCompletionListener { _ -> request(1) }
                request(1)
            }
        }
        toneObserver
                .map { tone -> DescriptorExtractor.getSoundDescriptor(tone.sound,context) }
                .doFinally {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                    mediaPlayer.release()
                    if(subscriber != null) subscriber = null
                }
                .toFlowable(BackpressureStrategy.BUFFER)
                .subscribe(subscriber)
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