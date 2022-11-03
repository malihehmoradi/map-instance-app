package ir.malihemoradi.mapapplication.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import ir.malihemoradi.mapapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding =ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
    }

}

