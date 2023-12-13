package com.example.flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson

class HomeFragment : Fragment(), AlbumRVAdapter.CommunicationInterface {
    var currentPage=0

    lateinit var binding: FragmentHomeBinding
    private var albumDatas=ArrayList<Album>()
    private lateinit var songDB: SongDatabase

    override fun sendData(album: Album) {
        if (activity is MainActivity){
            val activity=activity as MainActivity
            activity.updateMainPlayerCl(album)
            Log.d("sendData", albumDatas.toString())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        songDB=SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())
        inputDummyAlbums()

        val thread=Thread(PagerRunnable())
        thread.start()
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val albumRVAdapter=AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter=albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager=LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayAlbum(album: Album){
                sendData(album)
            }
        })

        val bannerAdapter=BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter=bannerAdapter
        binding.homeBannerVp.orientation= ViewPager2.ORIENTATION_HORIZONTAL

        val HomeBannerAdapter=HomeBannerVPAdapter(this)
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        HomeBannerAdapter.addFragment(HomeBannerFragment(R.drawable.img_first_album_default))
        binding.homeMainpannelVp.adapter=HomeBannerAdapter
        binding.homeMainpannelVp.orientation=ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeMainpannelIndicator3.setViewPager(binding.homeMainpannelVp)

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    inner class PagerRunnable:Runnable{
        override fun run() {
            while(true){
                try{
                    Thread.sleep(2000)
                    handler.sendEmptyMessage(0)
                } catch (e:InterruptedException){

                }
            }
        }
    }
    val handler=Handler(Looper.getMainLooper()){
        setPage()
        true
    }
    fun setPage(){
        if(currentPage==2)
            currentPage=0
        binding.homeBannerVp.setCurrentItem(currentPage, true)
        currentPage+=1
    }
    private fun inputDummyAlbums(){
        val songDB=SongDatabase.getInstance(requireActivity())!!
        val songs=songDB.albumDao().getAlbums()

        if (songs.isNotEmpty()) {
            return
        }

        songDB.albumDao().insert(
            Album(
                "IU 5th Album 'LILAC'",
                "아이유 (IU)",
                R.drawable.img_album_exp2,
                1
            )
        )

        songDB.albumDao().insert(
            Album(
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp,
                2
            )
        )

        songDB.albumDao().insert(
            Album(
                "iScreaM Vol.10: Next \n Level Remixes",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3,
                3
            )
        )

        songDB.albumDao().insert(
            Album(
                "Map of the Soul \n Persona",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4,
                4
            )
        )


        songDB.albumDao().insert(
            Album(
                "Great!",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5,
                5
            )
        )
    }
}
