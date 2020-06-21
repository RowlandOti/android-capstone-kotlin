package com.example.android.politicalpreparedness.election

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.PoliticalApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionRepository
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelFactory =
                VoterInfoViewModelFactory(ElectionRepository(PoliticalApp.INSTANCE.database))
        viewModel = viewModelFactory.create(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel

        arguments?.let {
            val electionId = VoterInfoFragmentArgs.fromBundle(it).argElectionId
            val division = VoterInfoFragmentArgs.fromBundle(it).argDivision

            viewModel.setDataFromArgs(electionId, division)
        }

        viewModel.getVoterInfo().observe(viewLifecycleOwner, Observer {
            binding.voterResponse = it
        })

        viewModel.getLoadVotingLocation().observe(viewLifecycleOwner, Observer {
            setIntent(it)
        })

        viewModel.getLoadBallotInformation().observe(viewLifecycleOwner, Observer {
            setIntent(it)
        })

        viewModel.getErrorMessage().observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setIntent(url: String) {
        try {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context?.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                    context,
                    getString(R.string.no_browser_app),
                    Toast.LENGTH_SHORT
            ).show()
        }
    }
}