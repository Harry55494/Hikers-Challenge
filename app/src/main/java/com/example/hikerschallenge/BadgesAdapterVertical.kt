package com.example.hikerschallenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgesAdapterVertical(private val badges: MutableList<Badge>) : RecyclerView.Adapter<BadgesAdapterVertical.BadgeViewHolder>() {

    private val tag = "BadgesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_row, parent, false)
        return BadgeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badges.size
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.bind(badge)
        Log.i(tag, "onBindViewHolder()2 run")
    }

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_row_name)

        fun bind(badge: Badge) {
            badgeNameTextView.text = badge.name

        }
    }
}
