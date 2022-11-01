package ir.malihemoradi.mapapplication.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.malihemoradi.mapapplication.data.Place
import ir.malihemoradi.mapapplication.databinding.ItemPlaceBinding
import ir.malihemoradi.mapapplication.helper.TaskDiffCallback

class PlacesAdapter(private val list: List<Place>) :
    ListAdapter<Place,PlacesAdapter.PlaceViewHolder>(TaskDiffCallback<Place>()) {

    private lateinit var binding:ItemPlaceBinding

    class PlaceViewHolder(binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {

        val textNumber=binding.txtNumber

        fun bind(place: Place,position: Int) {
            textNumber.text= (position+1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        binding.place=getItem(position)
        holder.bind(getItem(position),position)
    }
}