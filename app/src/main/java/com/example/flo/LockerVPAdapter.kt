package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flo.databinding.FragmentSavesongBinding

class LockerVPAdapter(fragment: Fragment):FragmentStateAdapter(fragment){
    override fun getItemCount(): Int=3
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->SaveSongFragment()
            1->MusicFileFragment()
            else->SaveAlbumFragment()
        }
    }
}