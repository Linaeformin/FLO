package com.example.flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    val albumList= ArrayList<Album>()
    val songs= arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos=0
    lateinit var binding: ActivityMainBinding
    private var song: Song=Song()
    private var gson: Gson=Gson()
    private var mediaPlayer: MediaPlayer?=null
    lateinit var timer: MainActivity.Timer
    companion object {const val STRING_INTENT_KEY="my_string_key"}

    private val getResultText= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->
        if(result.resultCode==Activity.RESULT_OK){
            val returnString=result.data?.getStringExtra("title")
            Toast.makeText(this, result.data?.getStringExtra("title"),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        startTimer()
        inputDummySongs()
        initBottomNavigation()
        initPlayList()

//        setPlayer(song)

        binding.mainPlayerCl.setOnClickListener {
            val editor= getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent=Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())

        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayStatus(false)
            Log.d("test123", songs[nowPos].toString())
        }
        binding.mainMiniplayerPauseBtn.setOnClickListener {
            setPlayStatus(true)
            mediaPlayer?.stop()
            timer.interrupt()
        }
        binding.mainMiniplayernextBtn.setOnClickListener {
            moveSong(1)
        }
        binding.mainMiniplayerpreBtn.setOnClickListener {
            moveSong(-1)
        }
    }
    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    private fun moveSong(direct: Int){

        timer.interrupt()

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

        mediaPlayer?.release()
        mediaPlayer=null

        setMiniPlayer(songs[nowPos])
        setPlayStatus(false)
    }

    fun setPlayStatus(isPlaying:Boolean){
        if(isPlaying){
            binding.mainMiniplayerBtn.visibility=View.VISIBLE
            binding.mainMiniplayerPauseBtn.visibility=View.GONE
            setPlayerStatus(false)
        }
        else{
            binding.mainMiniplayerBtn.visibility=View.GONE
            binding.mainMiniplayerPauseBtn.visibility=View.VISIBLE
            setPlayer(songs[nowPos])
        }
    }

    private fun setMiniPlayer(song: Song){
        val spf=getSharedPreferences("song", MODE_PRIVATE)
        val second=spf.getInt("second",0)
        binding.mainMiniplayerTitleTv.text=song.title
        binding.mainMiniplayerSingerTv.text=song.singer
        binding.mainMiniplayerProgressSb.progress= (second*100000)/song.playTime
    }

    override fun onResume() {
        super.onResume()
        val spf=getSharedPreferences("song", MODE_PRIVATE)
        val songId=spf.getInt("songId",0)

        nowPos=getPlayingSongPosition(songId)
        val songDB=SongDatabase.getInstance(this)!!

        song=if(songId==0){
            songDB.songDao().getSong(1)
        } else {
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", song.id.toString())
        setMiniPlayer(song)
    }
    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun getJwt():String?{
        val spf=this.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf!!.getString("jwt","")
    }

    fun updateMainPlayerCl(album: Album){

        var nowPlaying=false
        val album_songs=songDB.songDao().getSongsInAlbum(album.id)
        for (i in 0 until album_songs.size){
            if(nowPlaying){
                Handler(Looper.getMainLooper()).postDelayed({
                    timer.interrupt()
                    binding.mainMiniplayerProgressSb.progress=0
                    binding.mainMiniplayerTitleTv.text=album_songs[i].title
                    binding.mainMiniplayerSingerTv.text=album_songs[i].singer
                    songs[nowPos]=album_songs[i]
                    setPlayer(album_songs[i])
                },songs[nowPos].playTime*1000.toLong())
            }
            else{
                    binding.mainMiniplayerTitleTv.text=album_songs[i].title
                    binding.mainMiniplayerSingerTv.text=album_songs[i].singer
                    binding.mainMiniplayerProgressSb.progress=0
                    songs[nowPos]=album_songs[i]
                    setPlayer(album_songs[i])
                    nowPlaying=true
                }
            }
        binding.mainMiniplayerBtn.visibility = View.GONE
        binding.mainMiniplayerPauseBtn.visibility = View.VISIBLE
    }
    fun setPlayerStatus(isPlaying: Boolean){

        song.isPlaying=isPlaying
        timer.isPlaying=isPlaying

        if(isPlaying){
            mediaPlayer?.start()
            startTimer()
        }
        else{
            if(mediaPlayer?.isPlaying==true){
                mediaPlayer?.pause()
            }
        }
    }
    private fun setPlayer(song: Song){

        binding.mainMiniplayerProgressSb.progress=(song.second*1000/song.playTime)
        val music=resources.getIdentifier(songs[nowPos].music,"raw", this.packageName)
        mediaPlayer=MediaPlayer.create(this, music)
        setPlayerStatus(true)
    }
    private fun startTimer(){
        timer=Timer(song.playTime, song.isPlaying)
        timer.start()
    }
    inner class Timer(private val playTime: Int, var isPlaying: Boolean=true):
        Thread(){
        var second: Int=0
        var mills: Float=0f

            override fun run() {
                super.run()
                try{
                    while(true){
                        if(second>=playTime){
                            break
                        }
                        if (isPlaying){
                            sleep(50)
                            mills+=50
                            runOnUiThread { binding.mainMiniplayerProgressSb.progress=((mills/playTime)*100).toInt() }
                            if(mills%1000==0f){
                                second++
                            }

                        }
                    }
                } catch (e: InterruptedException){
                    Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
                }
            }
        }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second=((binding.mainMiniplayerProgressSb.progress*song.playTime)/100)/1000
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharedPreferences.getString("songData", null)

//        song = if(songJson == null){
//            Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
//        } else {
//            gson.fromJson(songJson, Song::class.java)
//        }

        song = Song(songs[nowPos].title, songs[nowPos].singer, songs[nowPos].second, songs[nowPos].playTime, false, songs[nowPos].music)

        setMiniPlayer(song)
    }
    private fun inputDummySongs(){
        val songDB=SongDatabase.getInstance(this)!!
        val songs=songDB.songDao().getSongs()

        if (songs.isNotEmpty()){
            return
        }

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                214,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )

        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )

        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )

        val _songs=songDB.songDao().getSongs()
    }

}