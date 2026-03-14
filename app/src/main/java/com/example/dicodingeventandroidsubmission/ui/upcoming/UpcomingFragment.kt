package com.example.dicodingeventandroidsubmission.ui.upcoming

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
import com.example.dicodingeventandroidsubmission.databinding.FragmentUpcomingBinding
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModel
import com.example.dicodingeventandroidsubmission.ui.common.ViewModelFactory

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory(1)
    }
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter()

        binding.rvEventList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)

                    outRect.bottom = 32
                    if (position == 0) {
                        outRect.top = 16
                    }
                }
            })

            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        eventViewModel.eventList.observe(viewLifecycleOwner) { eventsItems ->
            eventAdapter.submitList(eventsItems)
            binding.rvEventList.requestLayout()
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
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