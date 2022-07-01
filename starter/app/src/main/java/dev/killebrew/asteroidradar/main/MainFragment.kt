package dev.killebrew.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import dev.killebrew.asteroidradar.R
import dev.killebrew.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroids.observe(viewLifecycleOwner) {
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
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
