package com.example.hikerschallenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgesAdapterHorizontal(appViewModel: AppViewModel, val type: String, order: String) : RecyclerView.Adapter<BadgesAdapterHorizontal.BadgeViewHolder>() {

    private var badgesList = when (type) {
        "all" -> appViewModel.badgesModel!!.getAllBadges()
        "user" -> appViewModel.badgesModel!!.userBadges
        "wanted" -> appViewModel.badgesModel!!.getTracked()
        else -> appViewModel.badgesModel!!.getAllBadges()
    }

    init {
        if (order == "reverse") {
            badgesList = badgesList.reversed()
        }
    }

    private val tag = "BadgesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_scroller_view, parent, false)
        return BadgeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return minOf(badgesList.size, 5)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgesList[position]
        holder.bind(badge)
        Log.i(tag, "onBindViewHolder() run")
    }

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_scroller_name)
        private val badgeSecondaryText: TextView = itemView.findViewById(R.id.badge_scroller_secondary_text)
        private val badgeImage: ImageView = itemView.findViewById(R.id.badge_scroller_image)

        fun bind(userBadge: Any) {
            if (type == "wanted") {
                badgeNameTextView.text = (userBadge as DataBadge).name
                badgeSecondaryText.text = userBadge.localLocation
                badgeImage.setImageResource(R.drawable.share_location)
            } else if (type == "user") {
                userBadge as UserBadge
                badgeNameTextView.text = userBadge.name
                badgeSecondaryText.text = userBadge.getDisplayDate()
            }

        }
    }
}
