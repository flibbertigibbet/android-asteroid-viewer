package dev.killebrew.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import dev.killebrew.asteroidradar.R
import dev.killebrew.asteroidradar.databinding.FragmentMainBinding
import dev.killebrew.asteroidradar.models.Asteroid

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroidsForWeek.observe(viewLifecycleOwner) {
            it?.apply {
                val adapter = AsteroidRecyclerViewAdapter(
                    AsteroidRecyclerViewAdapter.OnClickListener { asteroid ->
                        val action = MainFragmentDirections.actionShowDetail(asteroid)
                        findNavController().navigate(action)
                    }
                )

                binding.statusLoadingWheel.visibility = View.GONE
                binding.asteroidRecycler.visibility = View.VISIBLE

                binding.asteroidRecycler.adapter = adapter
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.pictureOfDay.observe(viewLifecycleOwner) {
            it?.apply {
                Picasso.Builder(requireContext())
                    .build()
                    .load(it.url)
                    .into(binding.activityMainImageOfTheDay)
                binding.activityMainImageOfTheDay.contentDescription = it.title
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> reloadAsteroids(viewModel.asteroidsForWeek)
            R.id.show_today_menu -> reloadAsteroids(viewModel.asteroidsForDay)
            R.id.show_saved_menu -> reloadAsteroids(viewModel.savedAsteroids)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun reloadAsteroids(liveData: LiveData<List<Asteroid>>) {
        binding.statusLoadingWheel.visibility = View.VISIBLE
        binding.asteroidRecycler.visibility = View.GONE

        liveData.observe(viewLifecycleOwner) {
            it?.apply {
                binding.statusLoadingWheel.visibility = View.GONE
                binding.asteroidRecycler.visibility = View.VISIBLE

                val adapter = binding.asteroidRecycler.adapter as AsteroidRecyclerViewAdapter
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
