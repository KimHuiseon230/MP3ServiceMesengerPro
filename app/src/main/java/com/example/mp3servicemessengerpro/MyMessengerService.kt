package com.example.mp3servicemessengerpro

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.example.mp3servicemesengerpro.R

class MyMessengerService : Service() {
    // 음악 재생 클래스 -> 원하는 음악 소스만 넣어주면됨.
    lateinit var mediaPlayer: MediaPlayer

    //메세지 전달 (송, 수신)
    lateinit var receiveMessenger: Messenger
    lateinit var replyMessenger: Messenger

    //1. 음악 개체를  생성한다.
    override fun onCreate() {
        super.onCreate()
        //음악을 준비= 음악객체 생성
        mediaPlayer = MediaPlayer()

    }

    //2.2 iBinder을 전달
    // 전달이 되어야하는 부분이라 중요함
    override fun onBind(intent: Intent): IBinder {
        receiveMessenger = Messenger(IncommingHandler(this))
        return onBind(intent)

    }
    //
    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
    // 3. 음악 객체 해제
    override fun onDestroy() {
        super.onDestroy()
        //음악 객체 해제
        mediaPlayer.release()
    }
    //2.1 receiveMessenger Handler

    inner class IncommingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(
        Looper.getMainLooper()
    ) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {

                // 송신자에게 전송할 메신저를 받음, 노래의 총 시간을 보내주고, 노래를 재생시키는 것이 업무
                10 -> {
                    // 메신저를 받음 (중요)
                    // t송신자에게 응답해줄 메신저를 받음
                    replyMessenger = msg.replyTo
                    // 노래가 작동되지 않으면 새롭게 틀어준다.
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer = MediaPlayer.create(this@MyMessengerService, R.raw.deep_free)

                    }
                    val message = Message()
                    message.what = 10
                    val bundle = Bundle()
                    bundle.putInt("duration", mediaPlayer.duration)
                    message.obj = bundle
                    replyMessenger.send(message)
                    // 노래를 재생한다.
                    mediaPlayer.start()
                }
                // 노래를 멈추고 종료하는 것이 업무
                20 -> {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.stop()

                    }
                }
                // 약속하지 않은 값을 주었을 때 경고를 해준다.
                else -> {
                    Log.e("MyMessengerService", "약속된 프로토콜이 아닙니다.")
                }
            }
        }
    }
}