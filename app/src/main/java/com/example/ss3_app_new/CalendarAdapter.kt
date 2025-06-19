package com.example.ss3_app_new

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeekCalendarAdapter(
    private val monthYear: String,
    private val days: List<Int>
) : RecyclerView.Adapter<WeekCalendarAdapter.WeekViewHolder>() {

    class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthYearText: TextView = itemView.findViewById(R.id.monthYearText)
        val sunText: TextView = itemView.findViewById(R.id.sunText)
        val monText: TextView = itemView.findViewById(R.id.monText)
        val tueText: TextView = itemView.findViewById(R.id.tueText)
        val wedText: TextView = itemView.findViewById(R.id.wedText)
        val thuText: TextView = itemView.findViewById(R.id.thuText)
        val friText: TextView = itemView.findViewById(R.id.friText)
        val satText: TextView = itemView.findViewById(R.id.satText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_header_week, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.monthYearText.apply {
            text = monthYear
            gravity = Gravity.CENTER
        }
        holder.monthYearText.text = monthYear
        holder.sunText.text = days[0].toString()
        holder.monText.text = days[1].toString()
        holder.tueText.text = days[2].toString()
        holder.wedText.text = days[3].toString()
        holder.thuText.text = days[4].toString()
        holder.friText.text = days[5].toString()
        holder.satText.text = days[6].toString()
    }

    override fun getItemCount(): Int = 1
}