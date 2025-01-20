package com.dicoding.dicodingevent.ui.detailEvent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.dicodingevent.R
import com.dicoding.dicodingevent.data.Result
import com.dicoding.dicodingevent.data.local.entity.DetailEventEntity
import com.dicoding.dicodingevent.databinding.ActivityDetailEventBinding
import com.dicoding.dicodingevent.di.Injection

class DetailEventActivity : AppCompatActivity() {
    private lateinit var activityDetailEventBinding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels {
        DetailEventViewModelFactory(Injection.provideRepository(this))
    }

    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        activityDetailEventBinding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(activityDetailEventBinding.root)

        setSupportActionBar(activityDetailEventBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Event"

        activityDetailEventBinding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val eventId = intent.getIntExtra("EVENT_ID", -1)
        isFavorite = intent.getBooleanExtra("IS_FAVORITE", false)

        if (eventId != -1) {
            viewModel.fetchEventDetail(eventId.toString())
        }

        viewModel.eventDetail.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    updateUI(result.data)
                    updateFavoriteIcon()
                }

                is Result.Error -> {
                    Log.e("DetailEventActivity", "Error fetching event details: ${result.error}")
                    showError(result.error)
                }

                is Result.Loading -> showLoading(true)
            }
        }

        viewModel.isFavorite.observe(this) { favorite ->
            isFavorite = favorite
            updateFavoriteIcon()
        }

        activityDetailEventBinding.ivFavorite.setOnClickListener {
            val eventDetail = (viewModel.eventDetail.value as? Result.Success)?.data
            if (eventDetail != null) {
                viewModel.toggleFavorite(eventDetail)
                val status = if (!isFavorite) "added to favorites" else "removed from favorites"
                showToast("event $status")
            } else {
                showError("Unable to toggle favorite. Event detail is not available")
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun updateFavoriteIcon() {
        activityDetailEventBinding.ivFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isFavorite) R.drawable.ic_favorited_black else R.drawable.ic_favorite_black
            )
        )
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(eventDetail: DetailEventEntity) {
        activityDetailEventBinding.apply {
            eventName.text = eventDetail.name
            eventOwner.text = eventDetail.ownerName
            eventTime.text = eventDetail.beginTime
            val sisaQuota = eventDetail.quota - eventDetail.registrants
            eventSisaQuota.text = buildString {
                append(getString(R.string.sisa_quota, sisaQuota))
            }
            eventDescription.text = HtmlCompat.fromHtml(
                eventDetail.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            Glide.with(this@DetailEventActivity)
                .load(eventDetail.mediaCover)
                .into(eventImage)

            registerButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(eventDetail.link)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        activityDetailEventBinding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
}
