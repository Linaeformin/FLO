package com.example.flo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flo.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.Serializable

class BottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetBinding
    lateinit var songDB: SongDatabase
    private val songs = ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun deleteLikedSong() {

        val lockerRVAdapter = LockerRVAdapter()
        songDB = SongDatabase.getInstance(requireContext())!!

        val likedsongs = songDB.songDao().getLikedSongs(isLike = true)

        Log.d("likedsongs", likedsongs.toString())
        for (i in 0 until likedsongs.size) {
            songDB.songDao().updateIsLikeById(false, likedsongs[i].id)
            Log.d("likedsongs",songDB.songDao().getLikedSongs(isLike = false).toString())
            dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        binding.bottomSheetDeleteIv.setOnClickListener {
            deleteLikedSong()
        }
    }
}