package com.example.hikerschallenge

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BadgesAdapterVertical(private val badges: MutableList<Badge>, private val badgesViewModel: BadgesViewModel) : RecyclerView.Adapter<BadgesAdapterVertical.BadgeViewHolder>() {

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
        private val imageView: ImageView = itemView.findViewById(R.id.badge_row_image)
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_row_name)
        private val badgeDescription: TextView = itemView.findViewById(R.id.badge_row_subtitle)
        private val optionsMenu: View = itemView.findViewById(R.id.menu_anchor_view)

        fun bind(badge: Badge) {
            val collected = badgesViewModel.badgesModel!!.badges.any { it.name == badge.name }
            val userBadge: Badge? = badgesViewModel.badgesModel!!.badges.find { it.name == badge.name }
            badgeNameTextView.text = badge.name
            setupPopupMenu(itemView.context, optionsMenu, badge)

            if (collected){
                if (userBadge != null) {
                    badgeDescription.text = "${badge.location}, Collected ${userBadge.getDisplayDate(true)}"
                    imageView.setImageResource(R.drawable.baseline_brightness_high_64)
                }
            } else {
                badgeDescription.text = "${badge.location}, ${badge.location_2}"
            }

        }
    }

    fun setupPopupMenu(context: Context, view: View, badge: Badge){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.badge_row_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_badge -> {
                    Log.i(tag, "Delete badge clicked for badge $badge")

                    val alertDialog = AlertDialog(context)
                    alertDialog.showAlertOptions("Delete badge?", "Are you sure you want to delete the ${badge.name} badge? You will need to scan the badge again to collect it.", "Yes",{
                        badgesViewModel.badgesModel!!.removeBadge(badge)
                    }, "Cancel", {
                        Log.i(tag, "Badge deletion cancelled")
                    })
                    true
                }
                R.id.share_badge -> {
                    Log.i(tag, "Share badge clicked for badge $badge")

                    badge.share(context)

                    true
                }
                else -> false
            }
        }

        view.setOnClickListener {
            popupMenu.show()
        }
    }
}
