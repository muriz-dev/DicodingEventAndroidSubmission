package com.example.dicodingeventandroidsubmission.ui.finished

import android.content.Intent
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
import com.example.dicodingeventandroidsubmission.data.response.ListEventsItem
import com.example.dicodingeventandroidsubmission.databinding.FragmentFinishedBinding
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModel
import com.example.dicodingeventandroidsubmission.ui.common.ViewModelFactory
import com.example.dicodingeventandroidsubmission.ui.detail.DetailActivity

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory(0)
    }
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
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

            eventAdapter.setOnItemClickCallback(object : EventListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ListEventsItem) {
                    onClickedItem(data)
                }
            })
        }
    }

    private fun observeViewModel() {
        eventViewModel.eventList.observe(viewLifecycleOwner) { eventsItems ->
            eventAdapter.submitList(eventsItems)
            binding.rvEventList.requestLayout()
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }

    private fun onClickedItem(event: ListEventsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        startActivity(intent)
    }
}