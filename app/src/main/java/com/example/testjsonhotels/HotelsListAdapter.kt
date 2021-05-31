package com.example.testjsonhotels

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HotelsListAdapter(
    private val listOfHotels: List<Hotel>,
    private val context: Context
) : RecyclerView.Adapter<HotelsListAdapter.HotelsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelsListViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.one_item_layout_card_view, parent, false)
        return HotelsListViewHolder(itemView)
    }

    private fun clearLastSymbolCommaInSuitesAvailabilityParameter(
        position: Int,
        listOfHotels: List<Hotel>
    ): String {
        val charArray: CharArray = listOfHotels[position]
            .suitesAvailability
            .replace(context.getString(R.string.colon), context.getString(R.string.comma))
            .trim()
            .toCharArray()

        val lastCharSymbol: Char = charArray[charArray.size - 1]

        if (lastCharSymbol == ',') {
            charArray[charArray.size - 1] = ' '
        }
        return charArray.concatToString().trim()
    }

    override fun onBindViewHolder(holder: HotelsListViewHolder, position: Int) {
        val name = listOfHotels[position].name
        val id = listOfHotels[position].id
        val distance = listOfHotels[position].distance
        val address = listOfHotels[position].address

        holder.textViewName.text = name
        holder.textViewId.text = id
        holder.textViewDistance.text = distance
        holder.textViewAddress.text = address

        holder.textViewSuitesAvailability.text = clearLastSymbolCommaInSuitesAvailabilityParameter(
            position = position,
            listOfHotels = listOfHotels
        )

        val listOfTextViewsStars: ArrayList<TextView> = ArrayList()

        listOfTextViewsStars.add(holder.textViewStarFirst)
        listOfTextViewsStars.add(holder.textViewStarSecond)
        listOfTextViewsStars.add(holder.textViewStarThird)
        listOfTextViewsStars.add(holder.textViewStarForth)
        listOfTextViewsStars.add(holder.textViewStarFifth)

        setTextViewsStarsColor(
            stars = listOfHotels[position].stars.toDouble().toInt(),
            listOfTextViewsStars = listOfTextViewsStars
        )

        setButtonOpenDetailInfoActivity(
            holder = holder,
            name = name,
            id = id,
            distance = distance,
            address = address,
            suites_availability = holder.textViewSuitesAvailability.text.toString()
        )
    }

    private fun setButtonOpenDetailInfoActivity(
        holder: HotelsListViewHolder,
        name: String,
        id: String,
        distance: String,
        address: String,
        suites_availability: String
    ) {
        holder.buttonDetails.setOnClickListener {
            val context: Context = holder.buttonDetails.context

            val intent = Intent(context, DetailInfoActivity::class.java)
                .apply {
                    putExtra(Constants.NAME, name)
                    putExtra(Constants.ID, id)
                    putExtra(Constants.DISTANCE, distance)
                    putExtra(Constants.ADDRESS, address)
                    putExtra(Constants.SUITES_AVAILABILITY, suites_availability)

                    putExtra(Constants.STAR_FIRST, checkStarColor(holder.textViewStarFirst))
                    putExtra(Constants.STAR_SECOND, checkStarColor(holder.textViewStarSecond))
                    putExtra(Constants.STAR_THIRD, checkStarColor(holder.textViewStarThird))
                    putExtra(Constants.STAR_FORTH, checkStarColor(holder.textViewStarForth))
                    putExtra(Constants.STAR_FIFTH, checkStarColor(holder.textViewStarFifth))
                }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listOfHotels.size
    }

    class HotelsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        val textViewId: TextView = itemView.findViewById(R.id.text_view_id)
        val textViewDistance: TextView = itemView.findViewById(R.id.text_view_distance)
        val textViewAddress: TextView = itemView.findViewById(R.id.text_view_address)
        val textViewSuitesAvailability: TextView =
            itemView.findViewById(R.id.text_view_suites_availability)
        val textViewStarFirst: TextView = itemView.findViewById(R.id.text_view_star_first)
        val textViewStarSecond: TextView = itemView.findViewById(R.id.text_view_star_second)
        val textViewStarThird: TextView = itemView.findViewById(R.id.text_view_star_third)
        val textViewStarForth: TextView = itemView.findViewById(R.id.text_view_star_forth)
        val textViewStarFifth: TextView = itemView.findViewById(R.id.text_view_star_fifth)
        val buttonDetails: Button = itemView.findViewById(R.id.button_details)
    }

    private fun checkStarColor(textView: TextView): Boolean {
        return textView.textColors.defaultColor == ContextCompat.getColor(
            context,
            R.color.yellow
        )
    }

    private fun setTextViewsStarsColor(
        stars: Int,
        listOfTextViewsStars: ArrayList<TextView>
    ) {
        if (stars > 0) {
            for (j in 1..stars) {
                listOfTextViewsStars[j - 1].setTextColor(
                    ContextCompat.getColor(context, R.color.yellow)
                )
            }
        } else {
            for (k in 0 until listOfTextViewsStars.size) {
                listOfTextViewsStars[k].setTextColor(
                    ContextCompat.getColor(context, R.color.grey_1)
                )
            }
        }
    }
}