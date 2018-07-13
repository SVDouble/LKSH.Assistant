package com.lksh.dev.lkshassistant.views

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.activities.ProfileActivity
import com.lksh.dev.lkshassistant.activities.TAG
import com.lksh.dev.lkshassistant.fragments.HouseInfo
import com.lksh.dev.lkshassistant.sqlite_helper.UserData
import org.jetbrains.anko.backgroundColor


/* Example */
class UserCardAdapter(private val mContext: Context,
                      private val dataset: ArrayList<UserData>) :
        RecyclerView.Adapter<UserCardAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val number = v.findViewById<TextView>(R.id.number)!!
        val name = v.findViewById<TextView>(R.id.name)!!
        val parallel = v.findViewById<TextView>(R.id.parallel)!!
        val home = v.findViewById<TextView>(R.id.home)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.part_rv_building, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataset[position]
        holder.number.text = position.toString()
        holder.name.text = data.name
        holder.parallel.text = data.parallel
        holder.home.text = data.house
    }

    override fun getItemCount() = dataset.size
}


/* Timetable */
data class TimetableEvent(
        val time: String,
        val eventInfo: String
) {
    val isCurrentEvent: Boolean
        get() = false
}

class TimetableAdapter(private val mContext: Context, private val dataset: ArrayList<TimetableEvent>) :
        RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val time = v.findViewById<TextView>(R.id.time)!!
        val event = v.findViewById<TextView>(R.id.event)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.part_rv_timetable, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Update recycler")
        val data = dataset[position]
        holder.time.text = data.time
        holder.itemView.backgroundColor = if (data.isCurrentEvent)
            android.R.color.holo_green_light
        else android.R.color.holo_blue_light
        holder.event.text = data.eventInfo
    }

    override fun getItemCount() = dataset.size
}


/* Search */
data class SearchResult(val type: Type, val user: UserData?, val house: HouseInfo?) {
    enum class Type {
        USER, HOUSE
    }
}

class SearchResultAdapter(private val mContext: Context,
                          private val dataset: ArrayList<SearchResult>,
                          private val mHouseListener: OnHouseClickListener) :
        RecyclerView.Adapter<SearchResultAdapter.ViewHolder>(), Filterable {
    interface OnHouseClickListener {
        fun onCLick(houseId: String)
    }

    private var currentData = arrayListOf<SearchResult>()
    private val resultFilter = ValueFilter()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val image = v.findViewById<ImageView>(R.id.type)!!
        val title = v.findViewById<TextView>(R.id.title)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.part_search_result, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = currentData[position]
        var title = ".."
        var icon = 0
        if (data.type == SearchResult.Type.USER
                && data.user != null) {
            title = "${data.user.name} ${data.user.surname}"
            icon = android.R.drawable.ic_media_pause
            holder.itemView.setOnClickListener {
                Log.d(TAG, "Open user profile")
                mContext.startActivity(Intent(mContext, ProfileActivity::class.java).putExtra("USER", arrayOf(data.user.login,
                        data.user.name, data.user.surname, data.user.city, data.user.parallel, data.user.house, data.user.room)))
            }
        } else if (data.type == SearchResult.Type.HOUSE
                && data.house != null) {
            title = data.house.name
            icon = android.R.drawable.ic_media_ff
            holder.itemView.setOnClickListener {
                Log.d(TAG, "Open house page")
                mHouseListener.onCLick(title)
            }
        }

        holder.title.text = title
        holder.image.setImageResource(icon)

    }

    override fun getItemCount() = currentData.size

    override fun getFilter() = resultFilter

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            val filterList = ArrayList<SearchResult>()

            if (constraint != null && constraint.isNotEmpty()) {
                for (i in dataset) if (i.type == SearchResult.Type.HOUSE
                        && i.house != null
                        && i.house.name.toUpperCase().contains(constraint.toString().toUpperCase())) {
                    filterList.add(i)
                }
                for (i in dataset) if (i.type == SearchResult.Type.USER
                        && i.user != null
                        && "${i.user.name} ${i.user.surname}".toUpperCase().contains(constraint.toString().toUpperCase())) {
                    filterList.add(i)
                }
            }
            results.count = filterList.size
            results.values = filterList
            return results
        }

        override fun publishResults(constraint: CharSequence,
                                    results: Filter.FilterResults) {
            currentData = results.values as ArrayList<SearchResult>
            notifyDataSetChanged()
        }

    }
}