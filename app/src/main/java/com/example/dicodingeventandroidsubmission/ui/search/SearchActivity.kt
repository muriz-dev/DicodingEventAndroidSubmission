package com.example.dicodingeventandroidsubmission.ui.search

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicodingeventandroidsubmission.data.Result
import com.example.dicodingeventandroidsubmission.EventListAdapter
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity
import com.example.dicodingeventandroidsubmission.databinding.ActivitySearchBinding
import com.example.dicodingeventandroidsubmission.ui.detail.DetailActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var eventAdapter: EventListAdapter

    private val searchViewModel: SearchViewModel by viewModels {
        SearchViewModelFactory.getInstance(this@SearchActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()

        searchViewModel.getInitialSuggestions()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter()

        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
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

    private fun setupSearchView() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)

            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.text.toString()
                if (query.isNotEmpty()) {
                    searchBar.setText(query)
                    searchViewModel.setSearchQuery(query)
                    // searchView.hide()
                    searchView.clearFocus()
                }
                false
            }

            searchView.editText.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        // Jika teks dihapus sampai kosong, tampilkan suggestion lagi
                        searchViewModel.setSearchQuery("")
                    }
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }

    private fun observeViewModel() {
        searchViewModel.searchResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val data = result.value
                        eventAdapter.submitList(data)

                        // Tampilkan pesan kosong jika tidak ada data
                        binding.tvEmptyMessage.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is Result.Error -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Sembunyikan recycler view saat loading agar fokus ke progress bar
        binding.rvSearch.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun onClickedItem(event: EventsEntity) {
        val intent = Intent(this@SearchActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id.toString())
        startActivity(intent)
    }
}