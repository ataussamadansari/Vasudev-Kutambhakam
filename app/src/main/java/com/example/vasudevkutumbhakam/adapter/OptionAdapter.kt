package com.example.vasudevkutumbhakam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vasudevkutumbhakam.databinding.ItemOptionBtnLayoutBinding
import com.example.vasudevkutumbhakam.model.OptionItem

class OptionAdapter(
    private val items: List<OptionItem>
) : RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    inner class OptionViewHolder(val binding: ItemOptionBtnLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemOptionBtnLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val item = items[position]
        holder.binding.optionIcon.setImageResource(item.iconResId)
        holder.binding.optionTitle.text = item.title
    }

    override fun getItemCount(): Int = items.size
}
