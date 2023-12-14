package com.example.hikerschallenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WalletFragment : Fragment() {
    // Wallet fragment for displaying the user's badges

    private val appViewModel by activityViewModels<AppViewModel>()
    private val tag = "WalletFragment"

    // On create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(tag, "onCreate() run")
    }

    // On create view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)

        // check if there are no badges, and display a message if so
        if (appViewModel.badgesModel!!.userBadges.size == 0){
            val noBadgesText = view.findViewById<TextView>(R.id.no_badges_text)
            noBadgesText.visibility = View.VISIBLE
        }

        // create the recycler view
        val badgesRecyclerView = view.findViewById<RecyclerView>(R.id.wallet_recycler)
        badgesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // create the observer for the badges
        val badgesObserver = androidx.lifecycle.Observer<BadgesModel> { badgesModel ->
            badgesModel.let {
                badgesRecyclerView.adapter = BadgesAdapterVertical(appViewModel)
            }
        }

        // add the observer to the view model
        badgesObserver.onChanged(appViewModel.badgesModel!!)

        // add the search bar including the functions for searching
        val searchBar = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.search_bar)
        searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i(tag, "onQueryTextSubmit() run with query: $query")
                // creates a new adapter with the filtered data
                val adapter = badgesRecyclerView.adapter as BadgesAdapterVertical
                adapter.applyFilter(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(tag, "onQueryTextChange() run with newText: $newText")
                onQueryTextSubmit(newText)
                return false
            }
        })

        Log.i(tag, "onCreateView() run")
        return view
    }

}