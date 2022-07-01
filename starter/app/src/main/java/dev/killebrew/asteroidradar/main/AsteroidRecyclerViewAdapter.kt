package dev.killebrew.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.killebrew.asteroidradar.databinding.AsteroidListItemBinding
import dev.killebrew.asteroidradar.models.Asteroid

// based on:
// https://github.com/udacity/andfun-kotlin-mars-real-estate/blob/master/app/src/main/java/com/example/android/marsrealestate/overview/PhotoGridAdapter.kt
class AsteroidRecyclerViewAdapter(private val onClickListener: OnClickListener):
    ListAdapter<Asteroid, AsteroidRecyclerViewAdapter.AsteroidViewHolder>(DiffCallback) {

    class AsteroidViewHolder(private val binding: AsteroidListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

            fun bind(asteroid: Asteroid) {
                Log.d("RecyclerView", "Binding asteroid ${asteroid.codename} ${asteroid.id}")
                binding.asteroid = asteroid
                binding.executePendingBindings()
            }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(AsteroidListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener {
            Log.d("RecyclerView", "hey, that tickles!")
            onClickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}
