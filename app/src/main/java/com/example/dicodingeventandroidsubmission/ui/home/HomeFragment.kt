package com.example.dicodingeventandroidsubmission.ui.home

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingeventandroidsubmission.EventListAdapter
import com.example.dicodingeventandroidsubmission.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapterUpcoming: EventListAdapter
    private lateinit var adapterFinished: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        adapterUpcoming = EventListAdapter()
        adapterFinished = EventListAdapter()

        binding.rvEventUpcomingList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterFinished
            isNestedScrollingEnabled = false

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val itemCount = state.itemCount

                    if (itemCount > 1) {
                        if (position < itemCount - 1) {
                            outRect.right = 32
                        }
                    }
                }
            })
        }

        // 2. Setup RecyclerView Finished (Vertical)
        binding.rvEventFinishedList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterFinished
            isNestedScrollingEnabled = false


            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = 32
                }
            })
        }
    }

    private fun observeViewModel() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            adapterUpcoming.submitList(events.take(5))
            binding.rvEventUpcomingList.requestLayout()
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            adapterFinished.submitList(events.take(5))
            binding.rvEventFinishedList.requestLayout()
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}