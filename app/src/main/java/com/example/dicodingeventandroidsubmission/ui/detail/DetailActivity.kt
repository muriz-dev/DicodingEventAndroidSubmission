package com.example.dicodingeventandroidsubmission.ui.detail

import com.example.dicodingeventandroidsubmission.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.dicodingeventandroidsubmission.data.response.Event
import com.example.dicodingeventandroidsubmission.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DetailActivity"
        const val EXTRA_EVENT_ID = "extra_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)

        if (eventId != null) {
            detailViewModel.getEventDetail(eventId)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        detailViewModel.eventDetail.observe(this) { event ->
            if (event != null) {
                displayEventData(event)
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)

            binding.contentGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun displayEventData(event: Event) {
        binding.apply {
            tvTitle.text = event.name
            tvOwner.text = getString(R.string.owner_detail, event.ownerName)
            tvLocation.text = event.cityName
            tvSchedule.text = getString(R.string.schedule_detail, event.beginTime, event.endTime)
            tvQuota.text = getString(R.string.quota_detail, event.quota, event.registrants)

            tvDescription.text = HtmlCompat.fromHtml(
                event.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            tvDescription.movementMethod = LinkMovementMethod.getInstance()

            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(ivThumbnail)

            binding.btnRegister.setOnClickListener {
                val link = event.link
                if (link.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}