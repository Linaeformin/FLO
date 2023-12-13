package com.example.flo

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.flo.databinding.SongLikeBinding
import com.google.android.material.snackbar.Snackbar

class SnackBar (view: View, private val message: String) {

    companion object {

        fun make(view: View, message: String) = SnackBar(view, message)
    }

    private val context = view.context
    private val snackbar = Snackbar.make(view, "", 3000)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

    private val inflater = LayoutInflater.from(context)
    private val snackbarBinding: SongLikeBinding
            = DataBindingUtil.inflate(inflater, R.layout.song_like, null, false)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackbarLayout) {
            removeAllViews()
            setPadding(0, 0, 0, 0)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackbarBinding.root, 0)
        }
    }

    private fun initData() {
        snackbarBinding.customSnackbarTv.text = message
    }

    fun show() {
        snackbar.show()
    }
}