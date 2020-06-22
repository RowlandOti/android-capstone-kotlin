package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val representatives = MutableLiveData<List<Representative>>()
    private val address = MutableLiveData<Address>()

    private val errorMessage = MutableLiveData<Int>()

    fun getErrorMessage(): LiveData<Int> {
        return errorMessage
    }

    fun getRepresentatives(): LiveData<List<Representative>> {
        return representatives
    }

    fun getAddress(): LiveData<Address> {
        return address
    }

    fun setAddress(address: Address) {
        this.address.postValue(address)
    }

    fun fetchRepresentatives(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = CivicsApi.retrofitService.getRepresentatives(address)
                if (result.isSuccessful) {
                    result.body()?.let {
                        val offices = it.offices
                        val officials = it.officials

                        representatives.postValue(offices.flatMap { office ->
                            office.getRepresentatives(
                                    officials
                            )
                        })
                    }
                } else {
                    errorMessage.postValue(R.string.msg_network_error)
                }
            } catch (e: Exception) {
                errorMessage.postValue(R.string.msg_network_error)
                Log.e(ElectionsViewModel::class.java.simpleName, e.toString())
            }
        }
    }
}
