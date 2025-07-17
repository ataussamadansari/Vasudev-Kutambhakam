package com.example.vasudevkutumbhakam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ItemReviewLayoutBinding
import com.example.vasudevkutumbhakam.model.Review

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(val binding: ItemReviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = reviews[position]
        holder.binding.apply {
            Glide.with(holder.itemView.context)
                .load(item.imageResId)
                .override(300, 300) // Resize to prevent memory crash
                .centerCrop()
                .into(reviewImg)

//            reviewImg.setImageResource(item.imageResId)
            playerBtn.setOnClickListener { /* Add player logic */ }
            ratingBar.rating = item.rating
            nameTv.text = item.name
            addressTv.text = item.address
        }
    }

    override fun getItemCount(): Int = reviews.size
}
