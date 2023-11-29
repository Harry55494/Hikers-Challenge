package com.example.hikerschallenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgesAdapter(private val badges: MutableList<Badge>) : RecyclerView.Adapter<BadgesAdapter.BadgeViewHolder>() {

    private val tag = "BadgesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_scroller_view, parent, false)
        return BadgeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badges.size
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.bind(badge)
        Log.i(tag, "onBindViewHolder() run")
    }

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_scroller_name)

        fun bind(badge: Badge) {
            badgeNameTextView.text = badge.name
            // Add any other binding logic if needed
        }
    }
}
