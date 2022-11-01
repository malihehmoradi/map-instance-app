package ir.malihemoradi.mapapplication.viewModel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ir.malihemoradi.mapapplication.data.Place
import ir.malihemoradi.mapapplication.data.PlaceDao
import kotlinx.coroutines.launch

class MapViewModel(private val placeDao: PlaceDao) : ViewModel() {


    private val currentLocation = MutableLiveData<Location>()

    fun saveCurrentLocation(location: Location) {
        currentLocation.value = location
    }

    fun getCurrentLocation(): Location? {
        return currentLocation.value;
    }

}

class PlaceViewModelFactory(private val placeDao:PlaceDao):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(placeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}