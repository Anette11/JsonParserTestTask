package com.example.testjsonhotels

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONObject
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class DetailInfoActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var textViewName: TextView
    private lateinit var textViewId: TextView
    private lateinit var textViewDistance: TextView
    private lateinit var textViewAddress: TextView
    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView
    private lateinit var textViewStarFirst: TextView
    private lateinit var textViewStarSecond: TextView
    private lateinit var textViewStarThird: TextView
    private lateinit var textViewStarForth: TextView
    private lateinit var textViewStarFifth: TextView
    private lateinit var textViewSuitesAvailability: TextView
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var executorForTaskInBackgroundThread: ScheduledExecutorService
    private lateinit var executorForTaskInMainThread: Executor
    private lateinit var text: String
    private lateinit var linearLayout: LinearLayout
    private var isRequestFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutAccordingToScreenOrientation()
        initialize()
        makeRequest()
    }

    private fun makeRequest() {
        val id = intent.getStringExtra(Constants.ID)

        val requestType = resources.getString(R.string.string_url_detailed)
            .replace(getString(R.string.n), "$id", false)

        makeRequestToServerForJsonInfoAndShowProgressIndicator(requestType)
    }

    private fun initialize() {
        linearLayout = findViewById(R.id.linear_layout)
        linearLayout.visibility = View.VISIBLE
        circularProgressIndicator = findViewById(R.id.circular_progress_indicator)
        setActionBar()
        imageView = findViewById(R.id.image_view)
        textViewName = findViewById(R.id.text_view_name)
        textViewId = findViewById(R.id.text_view_id)
        textViewDistance = findViewById(R.id.text_view_distance)
        textViewAddress = findViewById(R.id.text_view_address)
        textViewLatitude = findViewById(R.id.text_view_latitude)
        textViewLongitude = findViewById(R.id.text_view_longitude)
        textViewStarFirst = findViewById(R.id.text_view_star_first)
        textViewStarSecond = findViewById(R.id.text_view_star_second)
        textViewStarThird = findViewById(R.id.text_view_star_third)
        textViewStarForth = findViewById(R.id.text_view_star_forth)
        textViewStarFifth = findViewById(R.id.text_view_star_fifth)
        textViewSuitesAvailability = findViewById(R.id.text_view_suites_availability)
    }

    private fun fillContent(latitude: String, longitude: String) {
        textViewName.text = intent.getStringExtra(Constants.NAME)
        textViewId.text = intent.getStringExtra(Constants.ID)
        textViewDistance.text = intent.getStringExtra(Constants.DISTANCE)
        textViewAddress.text = intent.getStringExtra(Constants.ADDRESS)
        textViewSuitesAvailability.text = intent.getStringExtra(Constants.SUITES_AVAILABILITY)
        textViewLatitude.text = latitude
        textViewLongitude.text = longitude
        setAllStarsColor()
    }

    private fun setAllStarsColor() {
        textViewStarFirst.setTextColor(
            setStarColor(getBooleanStarColor(Constants.STAR_FIRST), applicationContext)
        )

        textViewStarSecond.setTextColor(
            setStarColor(getBooleanStarColor(Constants.STAR_SECOND), applicationContext)
        )

        textViewStarThird.setTextColor(
            setStarColor(getBooleanStarColor(Constants.STAR_THIRD), applicationContext)
        )

        textViewStarForth.setTextColor(
            setStarColor(getBooleanStarColor(Constants.STAR_FORTH), applicationContext)
        )

        textViewStarFifth.setTextColor(
            setStarColor(getBooleanStarColor(Constants.STAR_FIFTH), applicationContext)
        )
    }

    private fun getBooleanStarColor(stringExtra: String): Boolean {
        return intent.getBooleanExtra(stringExtra, false)
    }

    private fun setLayoutAccordingToScreenOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.one_item_layout_detail_info_landscape)
        } else {
            setContentView(R.layout.one_item_layout_detail_info_portrait)
        }
    }

    private fun setActionBar() {
        this.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_detail_info_activity)
        setBackArrowOnClick()
    }

    private fun setBackArrowOnClick() {
        val imageButtonBack: ImageButton = findViewById(R.id.image_button_back)

        imageButtonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setStarColor(boolean: Boolean, context: Context): Int {
        return if (boolean) {
            ContextCompat.getColor(context, R.color.yellow)
        } else {
            ContextCompat.getColor(context, R.color.grey_1)
        }
    }

    private fun makeRequestToServerForJsonInfoAndShowProgressIndicator(requestType: String) {
        executorForTaskInBackgroundThread = Executors.newSingleThreadScheduledExecutor()
        executorForTaskInMainThread = ContextCompat.getMainExecutor(this)

        executorForTaskInBackgroundThread.execute {
            val requestMakerToServerForJsonInfo = JsonInfoRequestMaker(this)
            val textResultFromFirstRequest: String =
                requestMakerToServerForJsonInfo.makeRequestText(requestType)

            val jsonObject = JSONObject(textResultFromFirstRequest)
            val latitude = jsonObject.getString(getString(R.string.lat))
            val longitude = jsonObject.getString(getString(R.string.lon))

            val image = jsonObject.getString(getString(R.string.image))
                .replace(getString(R.string.jpg), getString(R.string.empty))

            val requestTypeSecond: String =
                getString(R.string.string_url_image).replace(
                    getString(R.string.m_jpg),
                    image.plus(getString(R.string.jpg)),
                    false
                )

            val bitmap: Bitmap? =
                requestMakerToServerForJsonInfo.makeRequestImage(requestTypeSecond)

            isRequestFinished = true

            executorForTaskInMainThread.execute {
                while (!isRequestFinished) {
                    circularProgressIndicator.visibility = View.VISIBLE
                }
                circularProgressIndicator.visibility = View.GONE
                linearLayout.visibility = View.GONE

                setPictureInImageView(bitmap)
                fillContent(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }
    }

    private fun setPictureInImageView(bitmap: Bitmap?) {
        if (bitmap != null) {
            imageView.setImageBitmap(
                Bitmap.createBitmap(bitmap, 1, 1, bitmap.width - 2, bitmap.height - 2)
            )
        }
    }
}