package com.example.weatherlook

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.MutableLiveData
import com.example.weatherlook.APIwork.MainViewModel
import com.example.weatherlook.APIwork.WeatherModel
import com.example.weatherlook.databinding.ActivityMainBinding
import com.example.weatherlook.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    //private val model: MainViewModel by activityViewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        supportFragmentManager.beginTransaction().replace(R.id.holder, MainFragment.newInstance()).commit()
    }
}