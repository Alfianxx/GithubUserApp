package com.alfian.githubuserapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfian.githubuserapp.R
import com.alfian.githubuserapp.databinding.ItemRowGithubBinding
import com.alfian.githubuserapp.entity.User
import com.bumptech.glide.Glide

class ListUserAdapter :
    RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    private var mData = ArrayList<User>()

    fun setData(items: ArrayList<User>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ListViewHolder {
        val mView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_row_github, viewGroup, false)
        return ListViewHolder(mView)
    }

    override fun onBindViewHolder(listViewHolder: ListViewHolder, position: Int) {
        listViewHolder.bind(mData[position])
        listViewHolder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(mData[listViewHolder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = mData.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRowGithubBinding.bind(itemView)
        fun bind(data: User) {
            binding.tvName.text = data.username
            binding.tvUrl.text = data.url
            Glide.with(itemView.context)
                .load(data.avatar)
                .into(binding.civUser)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}