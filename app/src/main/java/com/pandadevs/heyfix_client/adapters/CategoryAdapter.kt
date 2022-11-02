package com.pandadevs.heyfix_client.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.databinding.ItemContentCardBinding

class CategoryAdapter(private val myEvents: MyEvents, context: Context) :
    ListAdapter<CategoryModel, CategoryAdapter.ViewHolder>(DiffUtilCallback) {

    val globalContext = context

    interface MyEvents {
        fun onClick(category: CategoryModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item =
            ItemContentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(private val binding: ItemContentCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(element: CategoryModel, position: Int) = with(binding) {
            Glide.with(globalContext).load(element.icon).into(binding.imIcon)
            binding.tvName.text = element.name
            binding.cvCategory.setOnClickListener {
                this@CategoryAdapter.myEvents.onClick(element)
            }
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(
            oldItem: CategoryModel,
            newItem: CategoryModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: CategoryModel,
            newItem: CategoryModel
        ): Boolean = newItem == oldItem
    }
}
