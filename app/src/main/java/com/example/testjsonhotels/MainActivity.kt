package com.example.testjsonhotels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var executorForTaskInBackgroundThread: ScheduledExecutorService
    private lateinit var executorForTaskInMainThread: Executor
    private lateinit var listOfHotelsDefaultFilter: ArrayList<Hotel>
    private lateinit var appCompatSpinner: AppCompatSpinner
    private var spinnerPosition: Int = 0
    private var isRequestFinished = false
    private lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            spinnerPosition = savedInstanceState.getInt(Constants.SPINNER_POSITION)
        }

        setContentView(R.layout.activity_main)
        initialize()
    }

    private fun initialize() {
        setActionBar()
        setSpinner()
        circularProgressIndicator = findViewById(R.id.circular_progress_indicator)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listOfHotelsDefaultFilter = ArrayList()
        text = getString(R.string.empty)
        makeRequestToServerForJsonInfoAndShowProgressIndicator(getString(R.string.string_url_initial))
    }

    private fun fillRecyclerView(listOfHotels: ArrayList<Hotel>) {
        recyclerView.adapter = HotelsListAdapter(listOfHotels, this)
    }

    private fun setActionBar() {
        this.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_main_activity)
    }

    private fun setSpinner() {
        appCompatSpinner = findViewById(R.id.app_compat_spinner)
        appCompatSpinner.setSelection(spinnerPosition)

        appCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                refreshRecycleViewAccordingToSpinnerPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun refreshRecycleViewAccordingToSpinnerPosition(position: Int) {
        when (position) {
            1 -> {
                fillRecyclerView(createSortedArrayListDistance())
                appCompatSpinner.setSelection(1)
            }
            2 -> {
                fillRecyclerView(createSortedArrayListSuitesAvailable())
                appCompatSpinner.setSelection(2)
            }
            else -> {
                fillRecyclerView(listOfHotelsDefaultFilter)
                appCompatSpinner.setSelection(0)
            }
        }
    }

    private fun createSortedArrayListSuitesAvailable(): ArrayList<Hotel> {
        val listOfHotelsFilterSuitesAvailable: ArrayList<Hotel> = ArrayList()
        val sortedMapSuitesAvailable: SortedMap<Int, Hotel> = sortedMapOf()

        for (i in 0 until listOfHotelsDefaultFilter.size) {
            val numbers =
                ArrayList<String>(listOfHotelsDefaultFilter[i].suitesAvailability.map {
                    it.toString()
                })

            var counter = 0
            repeat(numbers.size) {
                counter++
            }

            val keyMapSuitesAvailable: Int = counter
            val valueMapSuitesAvailable: Hotel = listOfHotelsDefaultFilter[i]

            sortedMapSuitesAvailable[keyMapSuitesAvailable] = valueMapSuitesAvailable
        }
        sortedMapSuitesAvailable.forEach { (_, hotel) ->
            listOfHotelsFilterSuitesAvailable.add(hotel)
        }
        return listOfHotelsFilterSuitesAvailable
    }

    private fun createSortedArrayListDistance(): ArrayList<Hotel> {
        val listOfHotelsFilterDistance: ArrayList<Hotel> = ArrayList()
        val sortedMapDistance: SortedMap<Double, Hotel> = sortedMapOf()

        for (i in 0 until listOfHotelsDefaultFilter.size) {
            val keyMapDistance: Double = listOfHotelsDefaultFilter[i].distance.toDouble()
            val valueMapDistance: Hotel = listOfHotelsDefaultFilter[i]

            sortedMapDistance[keyMapDistance] = valueMapDistance
        }

        sortedMapDistance.forEach { (_, hotel) ->
            listOfHotelsFilterDistance.add(hotel)
        }
        return listOfHotelsFilterDistance
    }

    private fun makeRequestToServerForJsonInfoAndShowProgressIndicator(request: String) {
        executorForTaskInBackgroundThread = Executors.newSingleThreadScheduledExecutor()
        executorForTaskInMainThread = ContextCompat.getMainExecutor(this)

        executorForTaskInBackgroundThread.execute {
            val jsonInfoRequestMaker = JsonInfoRequestMaker(this)
            text = jsonInfoRequestMaker.makeRequestText(request)
            isRequestFinished = true

            executorForTaskInMainThread.execute {
                while (!isRequestFinished) {
                    circularProgressIndicator.visibility = View.VISIBLE
                }
                circularProgressIndicator.visibility = View.GONE
                parseJsonObject(text)

                refreshRecycleViewAccordingToSpinnerPosition(spinnerPosition)
            }
        }
    }

    private fun parseJsonObject(text: String) {
        val jsonArray = JSONArray(text)

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)

            val sequentialNumber: Int = i
            val id: String = jsonObject.getString(getString(R.string.id))
            val name: String = jsonObject.getString(getString(R.string.name))
            val address: String = jsonObject.getString(getString(R.string.address))
            val stars: String = jsonObject.getString(getString(R.string.stars))
            val distance: String = jsonObject.getString(getString(R.string.distance))
            val suitesAvailability: String =
                jsonObject.getString(getString(R.string.suites_availability))

            val hotel = Hotel(
                sequentialNumber = sequentialNumber,
                id = id,
                name = name,
                address = address,
                stars = stars,
                distance = distance,
                suitesAvailability = suitesAvailability
            )
            listOfHotelsDefaultFilter.add(hotel)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.SPINNER_POSITION, appCompatSpinner.selectedItemPosition)
    }
}