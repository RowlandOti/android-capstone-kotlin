package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val representatives = MutableLiveData<List<Representative>>()
    private val address = MutableLiveData<Address>()

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
        viewModelScope.launch {
            val result = CivicsApi.retrofitService.getRepresentatives(address)
            if (result.isSuccessful) {
                result.body()?.let {
                    val offices = it.offices
                    val officials = it.officials

                    representatives.value =
                            offices.flatMap { office -> office.getRepresentatives(officials) }
                }
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location
/*    fun getAddress(location: Location): Address {

    }*/

    //TODO: Create function to get address from individual fields

}
