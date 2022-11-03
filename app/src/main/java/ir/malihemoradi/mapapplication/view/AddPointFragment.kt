package ir.malihemoradi.mapapplication.view

import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ir.malihemoradi.mapapplication.App
import ir.malihemoradi.mapapplication.R
import ir.malihemoradi.mapapplication.databinding.FragmentAddPointBinding
import ir.malihemoradi.mapapplication.viewModel.AddPlaceViewModel

class AppPointFragment : Fragment() {

    private lateinit var currentLocation: Location
    private lateinit var binding: FragmentAddPointBinding
    private val viewModel: AddPlaceViewModel by lazy {
        AddPlaceViewModel(
            (activity?.application as App).database.getPlaceDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddPointBinding.inflate(inflater, container, false)

         currentLocation = arguments?.getParcelable("CurrentLocation")!!
        binding.location = currentLocation

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAdd.setOnClickListener {


            if (binding.edtPointName.text.toString().isEmpty() ){
                binding.inputLayoutName.error=getString(R.string.error_null_name)
                return@setOnClickListener
            }else if (binding.edtCode.text.toString().isEmpty()){
                binding.inputLayoutCode.error=getString(R.string.error_null_code)
                return@setOnClickListener
            }

            viewModel.addNewPlace(
                name = binding.edtPointName.text.toString(),
                code = binding.edtCode.text.toString(),
                east = currentLocation.longitude.toString(),
                north = currentLocation.latitude.toString(),
                elevation = currentLocation.altitude.toString()
            )
        }

        viewModel.message.observe(viewLifecycleOwner, Observer {
            it?.let {

                Toast.makeText(context,it,Toast.LENGTH_LONG).show()

                findNavController().popBackStack()
            }
        })
    }

}