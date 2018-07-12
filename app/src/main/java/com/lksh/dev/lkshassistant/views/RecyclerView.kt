package com.lksh.dev.lkshassistant.views

import android.content.Context
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
import com.lksh.dev.lkshassistant.activities.TAG
import com.lksh.dev.lkshassistant.sqlite_helper.UserData
import org.jetbrains.anko.backgroundColor


/* Example */
class UserCardAdapter(private val mContext: Context, private val dataset: ArrayList<UserData>) :
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
data class SearchResult(val type: Type, val title: String) {
    enum class Type {
        USER, HOUSE
    }
}

class SearchResultAdapter(private val mContext: Context, private val dataset: Array<SearchResult>) :
        RecyclerView.Adapter<SearchResultAdapter.ViewHolder>(), Filterable {

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
        holder.title.text = data.title
        holder.image.setImageResource(when (data.type) {
            SearchResult.Type.USER -> android.R.drawable.ic_media_pause
            SearchResult.Type.HOUSE -> android.R.drawable.ic_media_ff
        })
    }

    override fun getItemCount() = currentData.size

    override fun getFilter() = resultFilter

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = Filter.FilterResults()
            val filterList = ArrayList<SearchResult>()

            if (constraint != null && constraint.isNotEmpty()) {
                for (i in 0 until dataset.size) {
                    if (dataset[i].title.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filterList.add(dataset[i])
                    }
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