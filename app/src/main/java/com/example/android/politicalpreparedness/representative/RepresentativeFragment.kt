package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*


// Concepts and code borrowed from my own LocationReminder project at: https://github.com/RowlandOti/LocationReminder
class RepresentativeFragment : Fragment() {

    companion object {
        val TAG = RepresentativeFragment::class.java.simpleName
        private const val REQUEST_CODE_BACKGROUND = 102
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 324
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeListAdapter: RepresentativeListAdapter
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val viewModel by viewModels<RepresentativeViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this

        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

        representativeListAdapter =
                RepresentativeListAdapter(RepresentativeListAdapter.RepresentativeListener {

                })

        binding.representativeRecyclerview.adapter = representativeListAdapter

        viewModel.getRepresentatives().observe(viewLifecycleOwner, Observer {
            representativeListAdapter.submitList(it)
        })

        viewModel.getAddress().observe(viewLifecycleOwner, Observer {
            binding.address = it
            viewModel.fetchRepresentatives(it.toFormattedString())
        })

        viewModel.getErrorMessage().observe(viewLifecycleOwner, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            val address = Address(
                    binding.addressLine1.text.toString(),
                    binding.addressLine2.text.toString(),
                    binding.city.text.toString(),
                    binding.state.selectedItem.toString(),
                    binding.zip.text.toString()
            )
            //binding.address = address
            viewModel.setAddress(address)
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BACKGROUND) {
            getLocation()
        }
    }

    @SuppressLint("CheckResult")
    private fun checkLocationPermissions() {
        RxPermissions(this)
                .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { permission ->
                    // will emit 1 Permission object
                    when {
                        permission.granted -> {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                // All permissions are granted !
                                checkDeviceLocationSettings()
                            } else {
                                requestQPermission()
                            }
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            // At least one denied permission without ask never again
                            Toast.makeText(
                                    activity,
                                    getString(R.string.permissions_request),
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                        else -> {
                            // At least one denied permission with ask never again Need to go to the settings
                            Toast.makeText(
                                    activity,
                                    getString(R.string.permissions_request_settings),
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
    }

    private fun getLocation() {
        try {
            val locationResult = fusedLocationProviderClient?.lastLocation

            locationResult?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    location?.let {
                        val address = geoCodeLocation(it)
                        viewModel.setAddress(address)
                    }
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Exception: %s".format(e.message))
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(
                            address?.thoroughfare ?: "",
                            address?.subThoroughfare ?: "",
                            address?.locality ?: "",
                            address?.adminArea ?: "",
                            address?.postalCode ?: ""
                    )
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestQPermission() {
        val hasForegroundPermission = ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasForegroundPermission) {
            val hasBackgroundPermission = ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (hasBackgroundPermission) {
                // All permissions are granted !
                checkDeviceLocationSettings()
            } else {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        REQUEST_CODE_BACKGROUND
                )
            }
        }
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                            requireActivity(),
                            REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                        requireView(),
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    getLocation()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }
}