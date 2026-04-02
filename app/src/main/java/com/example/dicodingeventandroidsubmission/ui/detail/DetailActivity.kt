package com.example.dicodingeventandroidsubmission.ui.detail

import com.example.dicodingeventandroidsubmission.R
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dicodingeventandroidsubmission.data.Result
import com.example.dicodingeventandroidsubmission.data.local.entity.EventsEntity
import com.example.dicodingeventandroidsubmission.databinding.ActivityDetailBinding
import androidx.core.net.toUri
import com.example.dicodingeventandroidsubmission.utils.loadImage

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_EVENT_ID = "extra_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels {
        DetailViewModelFactory.getInstance(this@DetailActivity)
    }

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
            observeViewModel(eventId.toInt())
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeViewModel(eventId: Int) {
        detailViewModel.getEventDetail(eventId).observe(this) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        val eventData = result.value
                        displayEventData(eventData)

                        showLoading(false)
                    }
                    is Result.Error -> {
                        showLoading(true)

                        Toast.makeText(
                            this@DetailActivity,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun displayEventData(event: EventsEntity) {
        val eventQuota = event.quota ?: 0
        val eventRegistrants = event.registrants ?: 0
        val quotaRemain = eventQuota - eventRegistrants

        binding.apply {
            tvTitle.text = event.name
            tvOwner.text = getString(R.string.owner_detail, event.ownerName ?: "Unknown")
            tvLocation.text = event.cityName ?: "No Location"
            tvSchedule.text = getString(
                R.string.schedule_detail,
                event.beginTime ?: "No Schedule",
                event.endTime ?: "No Schedule"
            )
            tvQuota.text = resources.getQuantityString(
                R.plurals.quota_detail,
                quotaRemain,
                quotaRemain,
                eventRegistrants
            )
            tvDescription.text = HtmlCompat.fromHtml(
                event.description ?: "There is no description for this event.",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            tvDescription.movementMethod = LinkMovementMethod.getInstance()

            ivThumbnail.loadImage(event.mediaCover)

            val icon = if (event.isFavorite) R.drawable.round_favorite_24 else R.drawable.round_favorite_border_24
            ivFavorite.setImageResource(icon)

            ivFavorite.setOnClickListener {
                onFavoriteClick(event)
            }

            btnRegister.setOnClickListener {
                val link = event.link ?: ""
                if (link.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DetailActivity, "Link pendaftaran tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun onFavoriteClick(event: EventsEntity) {
        if (event.isFavorite) {
            detailViewModel.deleteFromFavorite(event)
            Toast.makeText(this, getString(R.string.delete_favorite_success), Toast.LENGTH_SHORT).show()
        } else {
            detailViewModel.saveToFavorite(event)
            Toast.makeText(this, getString(R.string.save_favorite_success), Toast.LENGTH_SHORT).show()
        }
    }
}