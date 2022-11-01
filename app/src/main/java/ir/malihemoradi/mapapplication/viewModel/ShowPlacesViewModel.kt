package ir.malihemoradi.mapapplication.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.malihemoradi.mapapplication.data.Place
import ir.malihemoradi.mapapplication.data.PlaceDao
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ShowPlacesViewModel(private val placeDao:PlaceDao):ViewModel() {

     val allPlaces= MutableLiveData<List<Place>>()

    /**
     * Get all places from database
     */
    fun getAllPlaces(){
        viewModelScope.launch {
            allPlaces.value=placeDao.getAllPlaces()
        }
    }

}
