package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemAsteroidBinding

class AsteroidAdapter constructor(private var onClick: (Asteroid) -> Unit): ListAdapter<Asteroid, AsteroidViewHolder>(DIFF_CALLBACK) {

    companion object  {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Asteroid> = object: DiffUtil.ItemCallback<Asteroid>(){
            override fun areItemsTheSame(oldAsteroid: Asteroid, newAsteroid: Asteroid): Boolean {
                return oldAsteroid.id == newAsteroid.id
            }

            override fun areContentsTheSame(oldAsteroid: Asteroid, newAsteroid: Asteroid): Boolean {
                return oldAsteroid == newAsteroid
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(ItemAsteroidBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener {
            onClick(asteroid)
        }
        holder.bind(getItem(position))
    }

}

class AsteroidViewHolder(private var binding: ItemAsteroidBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(asteroid: Asteroid){
        binding.asteroid = asteroid
        binding.executePendingBindings()
    }

}
