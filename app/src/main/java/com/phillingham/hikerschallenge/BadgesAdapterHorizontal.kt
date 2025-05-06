package com.phillingham.hikerschallenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgesAdapterHorizontal(appViewModel: AppViewModel, val type: String, order: String) : RecyclerView.Adapter<BadgesAdapterHorizontal.BadgeViewHolder>() {
    // BadgesAdapterHorizontal, used for the horizontal scrolling badges in the home fragment

    private val tag = "BadgesAdapter"

    // Type filter, allowing this to be reused for different scrollers
    private var badgesList = when (type) {
        "all" -> appViewModel.badgesModel!!.getAllBadges()
        "user" -> appViewModel.badgesModel!!.userBadges
        "tracked" -> appViewModel.badgesModel!!.getTracked()
        else -> appViewModel.badgesModel!!.getAllBadges()
    }

    // Order filter, allowing the badges to be reversed if needed
    init {
        if (order == "reverse") {
            badgesList = badgesList.reversed()
        }
    }

    // Standard creation function
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_scroller_view, parent, false)
        return BadgeViewHolder(view)
    }

    // Count function, with a limit of 5 badges. Can increase or decrease this if wanted, but this should match the max number of tracked badges
    override fun getItemCount(): Int {
        return minOf(badgesList.size, 5)
    }

    // Bind function, binds the badge data to the view
    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgesList[position]
        holder.bind(badge)
        Log.i(tag, "onBindViewHolder() run")
    }

    // BadgeViewHolder, used for binding the badge data to the view
    // Assigns the data to the correct elements in the view
    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_scroller_name)
        private val badgeSecondaryText: TextView = itemView.findViewById(R.id.badge_scroller_secondary_text)
        private val badgeImage: ImageView = itemView.findViewById(R.id.badge_scroller_image)

        fun bind(userBadge: Any) {
            if (type == "tracked") {
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
