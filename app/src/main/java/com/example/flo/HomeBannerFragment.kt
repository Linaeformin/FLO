package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentHomebannerBinding

class HomeBannerFragment(val imgRes:Int) : Fragment(){
    lateinit var binding: FragmentHomebannerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomebannerBinding.inflate(inflater,container,false)
        binding.homePannelBackgroundIv.setImageResource(imgRes)
        return binding.root
    }
}