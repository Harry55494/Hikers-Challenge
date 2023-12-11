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

class BadgesAdapterVertical(private val appViewModel: AppViewModel) : RecyclerView.Adapter<BadgesAdapterVertical.BadgeViewHolder>() {

    private val tag = "BadgesAdapter"
    private val allBadges = appViewModel.badgesModel!!.getAllBadges()
    private val userBadges = appViewModel.badgesModel!!.userBadges

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_row, parent, false)
        return BadgeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allBadges.size
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = allBadges[position]
        holder.bind(badge)
        Log.i(tag, "onBindViewHolder()2 run")
    }

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.badge_row_image)
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_row_name)
        private val badgeDescription: TextView = itemView.findViewById(R.id.badge_row_subtitle)
        private val optionsMenu: View = itemView.findViewById(R.id.menu_anchor_view)

        fun bind(badgeToDisplay: DataBadge) {

            val badgeID = badgeToDisplay.id
            val displayName = badgeToDisplay.name
            val localLocation = badgeToDisplay.localLocation
            val countryLocation = badgeToDisplay.countryLocation

            badgeNameTextView.text = displayName
            badgeDescription.text = "${localLocation}, ${countryLocation}"

            if (appViewModel.badgesModel?.isBadgeCollected(badgeToDisplay.id) == true){
                Log.i(tag, "Badge ${badgeToDisplay.name} has been collected, getting user data")
                val userBadge = appViewModel.badgesModel?.userBadges?.find { it.dataID == badgeToDisplay.id }
                if (userBadge != null) {
                    Log.i(tag, "User badge found: $userBadge")
                    badgeDescription.text = "${userBadge.localLocation}, Collected ${userBadge.getDisplayDate(true)}"
                    imageView.setImageResource(R.drawable.baseline_album_64)
                    setupPopupMenu(itemView.context, optionsMenu, true, userBadge)
                }
            } else {
                setupPopupMenu(itemView.context, optionsMenu, false, null, badgeID)
            }

        }
    }

    fun setupPopupMenu(context: Context, view: View, collectedBage: Boolean, userBadge: UserBadge?, badgeID: String? = null){
        val popupMenu = PopupMenu(context, view)

        if (collectedBage){

            popupMenu.menuInflater.inflate(R.menu.collected_badge_row_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_badge -> {
                        Log.i(tag, "Delete badge clicked for badge $userBadge")

                        val alertDialog = AlertDialog(context)
                        if (userBadge != null) {
                            alertDialog.showAlertOptions("Delete badge?", "Are you sure you want to delete the ${userBadge.name} badge? You will need to scan the badge again to collect it.", "Yes",{
                                appViewModel.badgesModel?.removeUserBadge(userBadge.dataID)
                            }, "Cancel", {
                                Log.i(tag, "Badge deletion cancelled")
                            })
                        }
                        true
                    }
                    R.id.share_badge -> {
                        Log.i(tag, "Share badge clicked for badge $userBadge")

                        userBadge?.share(context)

                        true
                    }
                    else -> false
                }
            }
        } else {
            popupMenu.menuInflater.inflate(R.menu.uncollected_badge_row_menu, popupMenu.menu)

            var found = false
            var foundbadgeID: String = null.toString()

            for (badge in appViewModel.badgesModel?.getWantToCollect()!!){
                if (badge.id == badgeID){
                    found = true
                    foundbadgeID = badge.id
                    popupMenu.menu.findItem(R.id.track_badge).title = "Untrack Badge"}
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.track_badge -> {

                        if (found){
                            val alertDialog = AlertDialog(context)
                            // ask to remove badge from want to collect
                            val badgeName = appViewModel.badgesModel!!.getDataBadge(foundbadgeID).name
                            alertDialog.showAlertOptions("Remove badge from 'Want to Collect'?", "Are you sure you want to remove the ${badgeName} badge from your 'Want to Collect' list?", "Yes",{
                                appViewModel.badgesModel?.removeWantToCollect(foundbadgeID)
                            }, "Cancel", {
                                Log.i(tag, "Badge removal cancelled")
                            })

                        } else {

                            Log.i(tag, "User wants to track badge $userBadge")
                            appViewModel.badgesModel?.addWantToCollect(badgeID!!)
                        }



                        true
                    }
                    else -> false
                }
            }
        }


        view.setOnClickListener {
            popupMenu.show()
        }
    }
}
