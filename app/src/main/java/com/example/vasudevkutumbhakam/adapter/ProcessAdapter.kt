package com.example.vasudevkutumbhakam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vasudevkutumbhakam.databinding.ItemProcessLayoutBinding
import com.example.vasudevkutumbhakam.model.Process

class ProcessAdapter(
    private val items: List<Process>
) : RecyclerView.Adapter<ProcessAdapter.ProcessViewHolder>() {

    inner class ProcessViewHolder(val binding: ItemProcessLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessViewHolder {
        val binding = ItemProcessLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProcessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProcessViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            titleTv.text = item.title
            descTv.text = item.desc
            iconIv.setImageResource(item.icon)
        }
    }

    override fun getItemCount(): Int = items.size
}
