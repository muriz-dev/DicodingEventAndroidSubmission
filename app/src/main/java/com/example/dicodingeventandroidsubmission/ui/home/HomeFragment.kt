package com.example.dicodingeventandroidsubmission.ui.home

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
import com.example.dicodingeventandroidsubmission.databinding.FragmentHomeBinding
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModel
import com.example.dicodingeventandroidsubmission.ui.common.EventViewModelFactory
import com.example.dicodingeventandroidsubmission.ui.detail.DetailActivity
import com.example.dicodingeventandroidsubmission.utils.showLoading

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory.getInstance(requireActivity())
    }
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
            adapter = adapterUpcoming
            isNestedScrollingEnabled = false

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    val itemCount = state.itemCount

                    val params = view.layoutParams
                    if (itemCount > 1) {
                        val displayMetrics = resources.displayMetrics
                        val cardWidth = (displayMetrics.widthPixels * 0.85).toInt()
                        params.width = cardWidth

                        if (position < itemCount - 1) {
                            outRect.right = 32
                        } else {
                            outRect.right = 0
                        }
                    } else {
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT
                        outRect.right = 0
                    }

                    view.layoutParams = params
                }
            })
        }

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

        adapterUpcoming.setOnItemClickCallback(object : EventListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: EventsEntity) {
                onClickedItem(data)
            }
        })

        adapterFinished.setOnItemClickCallback(object : EventListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: EventsEntity) {
                onClickedItem(data)
            }
        })
    }

    private fun observeViewModel() {
        eventViewModel.getEvents(1).observe(viewLifecycleOwner) { result ->
            result?.handle(
                onLoading = {
                    binding.progressBar.showLoading(true)
                },
                onSuccess = { data ->
                    adapterUpcoming.submitList(data)
                    binding.rvEventUpcomingList.requestLayout()

                    binding.progressBar.showLoading(false)
                },
                onError = {
                    binding.progressBar.showLoading(false)

                    if (adapterUpcoming.itemCount == 0) {
                        Toast.makeText(context, "Gagal memuat data: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        eventViewModel.getEvents(0).observe(viewLifecycleOwner) { result ->
            result?.handle(
                onLoading = {
                    binding.progressBar.showLoading(true)
                },
                onSuccess = { data ->
                    adapterFinished.submitList(data)
                    binding.rvEventFinishedList.requestLayout()

                    binding.progressBar.showLoading(false)
                },
                onError = {
                    binding.progressBar.showLoading(false)

                    if (adapterFinished.itemCount == 0) {
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