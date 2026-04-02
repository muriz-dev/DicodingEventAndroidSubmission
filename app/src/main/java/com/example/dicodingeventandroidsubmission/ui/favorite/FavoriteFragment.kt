package com.example.dicodingeventandroidsubmission.ui.favorite

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingeventandroidsubmission.EventListAdapter
import com.example.dicodingeventandroidsubmission.data.handle
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity
import com.example.dicodingeventandroidsubmission.databinding.FragmentFavoriteBinding
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModel
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModelFactory
import com.example.dicodingeventandroidsubmission.ui.detail.DetailActivity
import com.example.dicodingeventandroidsubmission.utils.showLoading
import kotlin.getValue

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
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
                override fun onItemClicked(data: EventsEntity) {
                    onClickedItem(data)
                }
            })
        }
    }

    private fun observeViewModel() {
        eventViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { result ->
            result?.handle(
                onLoading = {
                    binding.progressBar.showLoading(true)
                },
                onSuccess = { data ->
                    eventAdapter.submitList(data)
                    binding.rvEventList.requestLayout()

                    binding.progressBar.showLoading(false)
                },
                onError = {
                    binding.progressBar.showLoading(false)

                    if (eventAdapter.itemCount == 0) {
                        Toast.makeText(context, "Gagal memuat data: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leak
    }

    private fun onClickedItem(event: EventsEntity) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        startActivity(intent)
    }
}