package com.example.flo

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.FragmentSavesongBinding
import com.example.flo.databinding.ItemLockerBinding

class LockerRVAdapter():RecyclerView.Adapter<LockerRVAdapter.ViewHolder>() {

    lateinit var songDB: SongDatabase
    private val switchStatus = SparseBooleanArray()
    private val songs = ArrayList<Song>()
    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }
    private lateinit var mItemClickListener:MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener=itemClickListener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LockerRVAdapter.ViewHolder {
        val binding: ItemLockerBinding = ItemLockerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LockerRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])

        holder.binding.songSettingIb.setOnClickListener {
            itemClickListener.onRemoveAlbum(songs[position].id) // 좋아요 취소로 업데이트하는 메서드
            removeSong(position) // 현재 화면에서 아이템을 제거
        }

        val switch =  holder.binding.itemSwitchSw
        switch.isChecked = switchStatus[position]
        Log.d("SparseBooleanArray", switch.isChecked.toString())
        switch.setOnClickListener {
            if (switch.isChecked) {
                switchStatus.put(position, true)
            }
            else {
                switchStatus.put(position, false)
            }

            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding: ItemLockerBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(song : Song){
            binding.itemAlbumTitleTv.text = song.title
            binding.itemAlbumSingerTv.text = song.singer
            binding.itemLockerCoverImgIv.setImageResource(song.coverImg!!)
        }
    }

    interface OnItemClickListener {
        fun onRemoveAlbum(songId: Int)
    }

    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    @SuppressLint("NotifyDataSetChanged") // 경고 무시 어노테이션
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)
        Log.d("addsongs",songs.toString())

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSong(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }
}