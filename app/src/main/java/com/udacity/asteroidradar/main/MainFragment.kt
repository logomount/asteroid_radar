package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidFilter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidAdapter {
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        }

        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            binding.activityMainImageOfTheDay.contentDescription = getString(R.string.nasa_picture_of_day_content_description_format, it.title)
        })

        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
                when (item.itemId){
                    R.id.show_next_week_menu -> AsteroidFilter.WEEK_ASTEROIDS
                    R.id.show_today_menu -> AsteroidFilter.TODAY_ASTEROIDS
                    else -> AsteroidFilter.SAVED_ASTEROIDS
                }
        )
        return true
    }
}
