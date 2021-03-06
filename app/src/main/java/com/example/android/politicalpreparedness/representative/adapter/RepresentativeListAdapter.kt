package com.example.android.politicalpreparedness.representative.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeListAdapter(private val clickListener: RepresentativeListener) :
        ListAdapter<Representative, RepresentativeListAdapter.RepresentativeViewHolder>(
                RepresentativeDiffCallback()
        ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return inflate(parent)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        fun inflate(parent: ViewGroup): RepresentativeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRepresentativeBinding.inflate(layoutInflater, parent, false)
            return RepresentativeViewHolder(binding)
        }
    }


    class RepresentativeViewHolder(val binding: ItemRepresentativeBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Representative) {
            binding.representative = item

            if (!item.official.photoUrl.isNullOrEmpty()) {
                Glide.with(binding.representativePhoto.context)
                        .load(item.official.photoUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.representativePhoto)
            }

            item.official.channels?.let {
                showSocialLinks(it)
            }

            item.official.urls?.let {
                showWWWLinks(it)
            }

            binding.executePendingBindings()
        }


        private fun showSocialLinks(channels: List<Channel>) {
            val facebookUrl = getFacebookUrl(channels)
            if (!facebookUrl.isNullOrBlank()) {
                enableLink(binding.facebookIcon, facebookUrl)
            }

            val twitterUrl = getTwitterUrl(channels)
            if (!twitterUrl.isNullOrBlank()) {
                enableLink(binding.twitterIcon, twitterUrl)
            }
        }

        private fun showWWWLinks(urls: List<String>) {
            enableLink(binding.wwwIcon, urls.first())
        }

        private fun getFacebookUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Facebook" }
                    .map { channel -> "https://www.facebook.com/${channel.id}" }
                    .firstOrNull()
        }

        private fun getTwitterUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Twitter" }
                    .map { channel -> "https://www.twitter.com/${channel.id}" }
                    .firstOrNull()
        }

        private fun enableLink(view: ImageView, url: String) {
            view.visibility = View.VISIBLE
            view.setOnClickListener { setIntent(url) }
        }

        private fun setIntent(url: String) {
            try {
                val uri = Uri.parse(url)
                val intent = Intent(ACTION_VIEW, uri)
                itemView.context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                        itemView.context,
                        "No app found that could open the link. Please install browser",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.official.name == newItem.official.name
        }

        override fun areContentsTheSame(
                oldItem: Representative,
                newItem: Representative
        ): Boolean {
            return oldItem == newItem
        }
    }

    class RepresentativeListener(val clickListener: (name: String) -> Unit) {
        //fun onClick(item: Representative) = clickListener(item.name)
    }
}