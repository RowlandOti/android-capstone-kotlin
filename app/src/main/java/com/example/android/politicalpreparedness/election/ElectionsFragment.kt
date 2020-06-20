package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
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
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory =
                ElectionsViewModelFactory(ElectionRepository(PoliticalApp.INSTANCE.database))
        viewModel = viewModelFactory.create(ElectionsViewModel::class.java)

        viewModel.fetchUpcomingElections()
        viewModel.loadSavedElections()

        upcomingElectionListAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            this.findNavController()
                    .navigate(ElectionsFragmentDirections.toVoterInfo(it.id, it.division))
        })

        savedElectionListAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            this.findNavController()
                    .navigate(ElectionsFragmentDirections.toVoterInfo(it.id, it.division))
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