package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.PoliticalApp
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter


class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var upcomingElectionListAdapter: ElectionListAdapter
    private lateinit var savedElectionListAdapter: ElectionListAdapter
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentElectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory =
                ElectionsViewModelFactory(ElectionRepository(PoliticalApp.INSTANCE.database))
        viewModel = viewModelFactory.create(ElectionsViewModel::class.java)

        //TODO: Refresh adapters when fragment loads
        viewModel.fetchUpcomingElections()
        viewModel.loadSavedElections()


        //TODO: Link elections to voter info
        upcomingElectionListAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {

        })

        savedElectionListAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {

        })

        binding.rvUpcomingElection.adapter = upcomingElectionListAdapter
        binding.rvSavedElection.adapter = savedElectionListAdapter

        viewModel.getUpcomingElections().observe(viewLifecycleOwner, Observer {
            upcomingElectionListAdapter.submitList(it)
        })

        viewModel.getSavedElections().observe(viewLifecycleOwner, Observer {
            upcomingElectionListAdapter.submitList(it)
        })
    }
}