package com.example.dicodingeventandroidsubmission.ui.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingeventandroidsubmission.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    Toast.makeText(this@SearchActivity, searchView.text, Toast.LENGTH_SHORT).show()
                    false
                }

            searchBar.setOnMenuItemClickListener { menuItem ->
                // Handle menuItem click.
                true
            }
        }
    }
}