package com.example.flo

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.databinding.ItemAlbumBinding
import com.google.gson.Gson
import java.lang.reflect.Array

class AlbumRVAdapter(private val albumList: ArrayList<Album>): RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {

    interface CommunicationInterface {
        fun sendData(album: Album)
    }
    interface MyItemClickListener{
        fun onItemClick(album: Album)
        fun onPlayAlbum(album: Album)
        fun onRemoveAlbum(position: Int)
    }

    private var albumDatas=ArrayList<Album>()

    private lateinit var mItemClickListener: MyItemClickListener

    val songs= arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos=0
    lateinit var song:Song
    private var gson: Gson = Gson()

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener=itemClickListener
    }

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumRVAdapter.ViewHolder {
        val binding: ItemAlbumBinding=ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(albumList[position])
        }
        holder.binding.itemAlbumPlayImg.setOnClickListener {
            mItemClickListener.onPlayAlbum(albumList[position])
        }
    }

    override fun getItemCount(): Int=albumList.size

    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemAlbumTitleTv.text=album.title
            binding.itemAlbumSingerTv.text=album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
        }
    }
}