package com.phillingham.hikerschallenge

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
    // Very similar to BadgesAdapterHorizontal, but used for the vertical scrolling badges in the home fragment

    // Variables for the adapter
    private val tag = "BadgesAdapter"
    private var allBadges = appViewModel.badgesModel!!.getAllBadges()

    // Standard creation function
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_row, parent, false)
        return BadgeViewHolder(view)
    }

    // Count function but with no limit
    override fun getItemCount(): Int {
        return allBadges.size
    }

    // Bind function, binds the badge data to the view
    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = allBadges[position]
        holder.bind(badge)
    }

    // Filter function, used for the search bar to filter the badges
    // notifyDataSetChanged() is called to redraw the elements in the recycler view
    fun applyFilter(filter: String){
        allBadges = appViewModel.badgesModel!!.getAllBadges().filter { it.name.contains(filter, true) || it.localLocation.contains(filter, true) || it.countryLocation.contains(filter, true) }
        notifyDataSetChanged()
    }

    // BadgeViewHolder, used for binding the badge data to the view
    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get all the correct views
        private val imageView: ImageView = itemView.findViewById(R.id.badge_row_image)
        private val badgeNameTextView: TextView = itemView.findViewById(R.id.badge_row_name)
        private val badgeDescription: TextView = itemView.findViewById(R.id.badge_row_subtitle)
        private val optionsMenu: View = itemView.findViewById(R.id.menu_anchor_view)

        // Bind function, assigns the data to the correct elements in the view
        fun bind(badgeToDisplay: DataBadge) {

            // Check if the badge is in the tracked list
            val inTracked = appViewModel.badgesModel!!.getTracked().any { it.id == badgeToDisplay.id }

            // Get the data
            val badgeID = badgeToDisplay.id
            val displayName = badgeToDisplay.name
            val localLocation = badgeToDisplay.localLocation
            val countryLocation = badgeToDisplay.countryLocation
            val countryflag = badgeToDisplay.countryflag

            // Assign the default data to the views
            badgeNameTextView.text = displayName + if (inTracked) " (Tracked)" else ""
            badgeDescription.text = "${localLocation}, ${countryLocation} $countryflag"

            // Check if the badge is in the tracked list, and change the image accordingly
            if (inTracked){
                imageView.setImageResource(R.drawable.share_location)
            } else {
                imageView.setImageResource(R.drawable.radio_button)
            }

            // Check if the badge has been collected, and if so, get the user data for when it was collected
            if (appViewModel.badgesModel?.isBadgeCollected(badgeToDisplay.id) == true){
                Log.i(tag, "Badge ${badgeToDisplay.name} has been collected, getting user data")
                val userBadge = appViewModel.badgesModel?.userBadges?.find { it.dataID == badgeToDisplay.id }
                if (userBadge != null) {
                    // Set the badge description to the date it was collected. and change the image too
                    Log.i(tag, "User badge found: $userBadge")
                    badgeDescription.text = "${userBadge.localLocation}, Collected ${userBadge.getDisplayDate(true)}"
                    imageView.setImageResource(R.drawable.workspace_premium)
                    // Setup the popup menu for the badge
                    setupPopupMenu(itemView.context, optionsMenu, userBadge)
                }
            } else {
                // Badge is not collected, setup listener to track and untrack it
                imageView.setOnClickListener{

                    var found = false
                    var foundbadgeID: String = null.toString()

                    // See if the badge is already tracked
                    for (badge in appViewModel.badgesModel?.getTracked()!!){
                        Log.i(tag, "Checking badge ${badge.id} against $badgeID")
                        if (badge.id == badgeID){
                            found = true
                            foundbadgeID = badge.id
                        }
                    }

                    // If the badge is already tracked, ask to untrack it using an alert dialog
                    if (found){
                        val alertDialog = AlertDialog(context = itemView.context)
                        // ask to remove badge from want to collect
                        val badgeName = appViewModel.badgesModel!!.getDataBadge(foundbadgeID).name
                        // Call alert dialog
                        alertDialog.showAlertOptions("Untrack $badgeName badge?", "Are you sure you want to untrack the badge '$badgeName'?", "Yes",{
                            appViewModel.badgesModel?.removeTrackedBadge(foundbadgeID)
                            redraw()
                        }, "Cancel", {
                            Log.i(tag, "Badge removal cancelled")
                        })

                    // If the badge is not tracked, track it
                    } else {
                        val badgeName = appViewModel.badgesModel!!.getDataBadge(badgeID).name
                        Log.i(tag, "User wants to track badge $badgeName")

                        // Check if the user is already tracking 5 badges, and if so, show an alert dialog
                        if (appViewModel.badgesModel!!.getTracked().size >= 5){
                            val alertDialog = AlertDialog(context = itemView.context)
                            alertDialog.showAlert("Too many badges!", "You can only track up to 5 badges at a time. Please untrack a badge before adding another.")
                        } else {
                            // Add the badge to the tracked list
                            appViewModel.badgesModel?.addTrackedBadge(badgeID)

                            // Show an alert dialog to confirm the badge has been tracked
                            val alertDialog = AlertDialog(context = itemView.context)
                            alertDialog.showAlert("Badge Tracked!", "You are now tracking the $badgeName badge.")
                        }

                    }

                    // Redraw the recycler view
                    redraw()

                }
                // If the badge is not collected, hide the options menu
                optionsMenu.visibility = View.GONE
            }

        }
    }

    // Setup the popup menu for the badge
    fun setupPopupMenu(context: Context, view: View, userBadge: UserBadge?){
        val popupMenu = PopupMenu(context, view)

        // Inflate the menu
        popupMenu.menuInflater.inflate(R.menu.collected_badge_row_menu, popupMenu.menu)

        // Set the on click listener for the menu items
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_badge -> {
                    // Show an alert dialog to confirm that the badge should be deleted
                    Log.i(tag, "Delete badge clicked for badge $userBadge")
                    val alertDialog = AlertDialog(context)
                    if (userBadge != null) {
                        // Asl for confirmation
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
                    // Share the badge using the share function within the user badge
                    Log.i(tag, "Share badge clicked for badge $userBadge")

                    userBadge?.share(context)

                    true
                }
                else -> false
            }
        }

        // Show the popup menu when the options menu is clicked
        view.setOnClickListener {
            popupMenu.show()
        }
    }

    // Redraw function, used to redraw the recycler view
    fun redraw(){
        notifyDataSetChanged()
    }

    // Sort function, used to sort the badges alphabetically. Not currently used
    fun sort(badgesList : List<DataBadge>): List<DataBadge> {
        return badgesList.sortedBy { it.name }
    }

}
