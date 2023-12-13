package com.example.flo

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding

    lateinit var timer : Timer
    private var mediaPlayer:MediaPlayer?=null
    private var gson: Gson = Gson()

    val songs= arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
    }

    fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying=isPlaying
        timer.isPlaying=isPlaying

        if (isPlaying) {
            binding.songPlayIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()

        } else {
            binding.songPlayIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying==true){
                mediaPlayer?.pause()
            }

        }
    }

    fun setRepeaterStatus(isRepeating: Boolean) {
        if (isRepeating) {
            binding.songRepeatIv.visibility = View.VISIBLE
            binding.songRepeatyesIv.visibility = View.INVISIBLE
        } else {
            binding.songRepeatIv.visibility = View.INVISIBLE
            binding.songRepeatyesIv.visibility = View.VISIBLE
        }
    }

    fun setRandomStatus(isRandom: Boolean) {
        if (isRandom) {
            binding.songRandomInactiveIv.visibility = View.VISIBLE
            binding.songRandomActiveIv.visibility = View.INVISIBLE
        } else {
            binding.songRandomInactiveIv.visibility = View.INVISIBLE
            binding.songRandomActiveIv.visibility = View.VISIBLE
        }
    }
    inner class Timer(private val playTime: Int, var isPlaying: Boolean=true):Thread(){

        private var second : Int= songs[nowPos].second
        private var mills: Float=(songs[nowPos].second*1000).toFloat()

        override fun run() {
            super.run()
            binding.songProgressSb.progress=(mills/playTime*100).toInt()
            try{
                while (true){

                    if (second>=playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills+=50

                        runOnUiThread{
                            binding.songProgressSb.progress=((mills/playTime)*100).toInt()
                        }
                        if (mills%1000==0f){
                            runOnUiThread {
                                binding.songStartTimeTv.text=String.format("%02d:%02d", second/60,second%60)
                            }
                            second++
                        }
                    }
                }
            } catch (e:InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다 ${e.message}")
            }

        }
    }

    override fun onPause(){
        super.onPause()
        setPlayerStatus(false)

        songs[nowPos].second=((binding.songProgressSb.progress*songs[nowPos].playTime)/100)/1000
        songs[nowPos].isPlaying=false
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() //에디터

        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("second",songs[nowPos].second)
        editor.apply()

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() //미디어 플레이어가 갖고 있던 리소스 해제
        mediaPlayer=null
    }

    private fun initPlayList(){
        songDB=SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initSong(){
        val spf=getSharedPreferences("song", MODE_PRIVATE)
        val songId=spf.getInt("songId",0)

        nowPos=getPlayingSongPosition(songId)

        Log.d("now Song",songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id==songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song:Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text=String.format("%02d:%02d", song.second/60,song.second%60)
        binding.songEndTimeTv.text=String.format("%02d:%02d", song.playTime/60,song.playTime%60)
        binding.songProgressSb.progress=(song.second*1000/song.playTime)
        binding.songAlbumIv.setImageResource(song.coverImg!!)

        val music=resources.getIdentifier(song.music,"raw", this.packageName)
        mediaPlayer=MediaPlayer.create(this,music)

        if (song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)
    }

    private fun startTimer(){
        timer=Timer(songs[nowPos].playTime,songs[nowPos].isPlaying)
        timer.start()
    }
    private fun startService() {
        Toast.makeText(this, "Foreground Service Started", Toast.LENGTH_SHORT).show()
        startService(Intent(this, ForegroundService::class.java))
    }
    private fun stopService(){
        Toast.makeText(this, "Foreground Service Stopped", Toast.LENGTH_SHORT).show()
        stopService(Intent(this, ForegroundService::class.java))
    }

    private fun isServiceRunning(inputClass : Class<ForegroundService>) : Boolean {
        val manager : ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager

        for (service : ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (inputClass.name.equals(service.service.className)) {
                return true
            }

        }
        return false
    }
    private fun setLike(isLike: Boolean){
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)
        songs[nowPos].isLike=!isLike
        Log.d("isLike", songs[nowPos].isLike.toString())
        if (!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            SnackBar.make(binding.root,"좋아요 한 곡에 담겼습니다.").show()
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            SnackBar.make(binding.root, "좋아요 한 곡이 취소되었습니다.").show()
        }
    }
    private fun moveSong(direct: Int){

        if ((nowPos*direct==0)&&(direct==-1)){
            nowPos=0
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
        }
        if ((nowPos*direct==songs.size-1)&&(direct==1)){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
        }
        if (((nowPos*direct>=0&&(direct!=-1))&&(nowPos!=songs.size-1))){
            nowPos+=direct
        }
        if ((nowPos!=0)&&(direct==-1)){
            nowPos+=direct
        }

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer=null

        setPlayer(songs[nowPos])
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songPlayIv.setOnClickListener {
            setPlayerStatus(true)
            startService()
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
            stopService()
        }
        binding.songSkipNextIv.setOnClickListener {
            moveSong(1)
        }
        binding.songSkipPreIv.setOnClickListener {
            moveSong(-1)
        }
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songPlayIv.setOnClickListener {
            setPlayerStatus(true)
            startService()
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
            stopService()
        }

        binding.songRepeatIv.setOnClickListener {
            setRepeaterStatus(false)
            val repbtn = findViewById<TextView>(R.id.song_start_time_tv)
            repbtn.bringToFront()
        }
        binding.songRepeatyesIv.setOnClickListener {
            setRepeaterStatus(true)
        }
        binding.songRandomInactiveIv.setOnClickListener {
            setRandomStatus(false)
            val ranbtn = findViewById<TextView>(R.id.song_end_time_tv)
            ranbtn.bringToFront()
        }
        binding.songRandomActiveIv.setOnClickListener {
            setRandomStatus(true)
        }
        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

}
