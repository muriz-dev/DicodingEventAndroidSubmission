package com.example.dicodingeventandroidsubmission.utils

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.dicodingeventandroidsubmission.R

fun View.showLoading(isLoading: Boolean) {
    this.visibility = if (isLoading) View.VISIBLE else View.GONE
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.round_image_24)
        .error(R.drawable.round_broken_image_24)
        .into(this)
}