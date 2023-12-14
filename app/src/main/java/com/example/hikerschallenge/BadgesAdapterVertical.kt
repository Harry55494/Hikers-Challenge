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
    private var allBadges = appViewModel.badgesModel!!.getAllBadges()

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
    }

    fun applyFilter(filter: String){
        allBadges = appViewModel.badgesModel!!.getAllBadges().filter { it.name.contains(filter, true) || it.localLocation.contains(filter, true) || it.countryLocation.contains(filter, true) }
        notifyDataSetChanged()
    }

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.badge_row_image)
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_row_name)
        private val badgeDescription: TextView = itemView.findViewById(R.id.badge_row_subtitle)
        private val optionsMenu: View = itemView.findViewById(R.id.menu_anchor_view)

        fun bind(badgeToDisplay: DataBadge) {

            val inTracked = appViewModel.badgesModel!!.getTracked().any { it.id == badgeToDisplay.id }

            val badgeID = badgeToDisplay.id
            val displayName = badgeToDisplay.name
            val localLocation = badgeToDisplay.localLocation
            val countryLocation = badgeToDisplay.countryLocation
            val countryflag = badgeToDisplay.countryflag

            badgeNameTextView.text = displayName + if (inTracked) " (Tracked)" else ""
            badgeDescription.text = "${localLocation}, ${countryLocation} $countryflag"

            if (inTracked){
                imageView.setImageResource(R.drawable.share_location)
            } else {
                imageView.setImageResource(R.drawable.radio_button)
            }

            if (appViewModel.badgesModel?.isBadgeCollected(badgeToDisplay.id) == true){
                Log.i(tag, "Badge ${badgeToDisplay.name} has been collected, getting user data")
                val userBadge = appViewModel.badgesModel?.userBadges?.find { it.dataID == badgeToDisplay.id }
                if (userBadge != null) {
                    Log.i(tag, "User badge found: $userBadge")
                    badgeDescription.text = "${userBadge.localLocation}, Collected ${userBadge.getDisplayDate(true)}"
                    imageView.setImageResource(R.drawable.workspace_premium)
                    setupPopupMenu(itemView.context, optionsMenu, true, userBadge)
                }
            } else {
                imageView.setOnClickListener{

                    var found = false
                    var foundbadgeID: String = null.toString()

                    for (badge in appViewModel.badgesModel?.getTracked()!!){
                        Log.i(tag, "Checking badge ${badge.id} against $badgeID")
                        if (badge.id == badgeID){
                            found = true
                            foundbadgeID = badge.id
                        }
                    }


                    if (found){
                        val alertDialog = AlertDialog(context = itemView.context)
                        // ask to remove badge from want to collect
                        val badgeName = appViewModel.badgesModel!!.getDataBadge(foundbadgeID).name
                        alertDialog.showAlertOptions("Untrack $badgeName badge?", "Are you sure you want to untrack the badge '$badgeName'?", "Yes",{
                            appViewModel.badgesModel?.removeTrackedBadge(foundbadgeID)
                            redraw()
                        }, "Cancel", {
                            Log.i(tag, "Badge removal cancelled")
                        })

                    } else {
                        val badgeName = appViewModel.badgesModel!!.getDataBadge(badgeID).name
                        Log.i(tag, "User wants to track badge $badgeName")

                        if (appViewModel.badgesModel!!.getTracked().size >= 5){
                            val alertDialog = AlertDialog(context = itemView.context)
                            alertDialog.showAlert("Too many badges!", "You can only track up to 5 badges at a time. Please untrack a badge before adding another.")
                        } else {
                            appViewModel.badgesModel?.addTrackedBadge(badgeID)
                        }

                        val alertDialog = AlertDialog(context = itemView.context)
                        alertDialog.showAlert("Badge Tracked!", "You are now tracking the $badgeName badge.")


                    }

                    redraw()

                }
                //setupPopupMenu(itemView.context, optionsMenu, false, null, badgeID)
                optionsMenu.visibility = View.GONE
            }

        }
    }

    fun setupPopupMenu(context: Context, view: View, collectedBadge: Boolean, userBadge: UserBadge?){
        val popupMenu = PopupMenu(context, view)

        if (collectedBadge){

            popupMenu.menuInflater.inflate(R.menu.collected_badge_row_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_badge -> {
                        Log.i(tag, "Delete badge clicked for badge $userBadge")

                        val alertDialog = AlertDialog(context)
                        if (userBadge != null) {
                            alertDialog.showAlertOptions("Delete badge?", "Are you sure you want to delete the ${userBadge.name} badge? You will need to scan the badge again to collect it.", "Yes",{
                                appViewModel.badgesModel?.removeUserBadge(userBadge.dataID)
                                redraw()
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
        }


        view.setOnClickListener {
            popupMenu.show()
        }
    }

    fun redraw(){
        notifyDataSetChanged()
    }

    fun sort(badgesList : List<DataBadge>): List<DataBadge> {
        return badgesList.sortedBy { it.name }
    }

}
