package ir.malihemoradi.mapapplication.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ir.malihemoradi.mapapplication.App
import ir.malihemoradi.mapapplication.databinding.FragmentShowRecordsBinding
import ir.malihemoradi.mapapplication.viewModel.ShowPlacesViewModel

class ShowRecordsFragment : Fragment() {

    private lateinit var binding:FragmentShowRecordsBinding
    private var adapter=PlacesAdapter(arrayListOf())
    private val viewModel:ShowPlacesViewModel by lazy {
        ShowPlacesViewModel(
            (activity?.application as App).database.getPlaceDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentShowRecordsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.recyclerPlaces.adapter=adapter
        viewModel.allPlaces.observe(viewLifecycleOwner,androidx.lifecycle.Observer{
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.getAllPlaces()
    }

}