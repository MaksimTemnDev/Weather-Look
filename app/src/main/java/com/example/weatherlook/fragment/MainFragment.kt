package com.example.weatherlook.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.media.audiofx.Equalizer.Settings
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.weatherlook.APIwork.MainViewModel
import com.example.weatherlook.APIwork.ApiManager
import com.example.weatherlook.APIwork.WeatherModel
import com.example.weatherlook.DialogManager
import com.example.weatherlook.DialogManager.enterNewPalce
import com.example.weatherlook.R
import com.example.weatherlook.adapters.VpAdapter
import com.example.weatherlook.databinding.FragmentMainBinding
import com.example.weatherlook.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MainFragment : Fragment() {
    private val tabslist = listOf(
        "hours", "days"
    )
    private var geoLocationClient = "Madrid"
    private val fragmentsList = listOf(
        hours.newInstance(),
        days.newInstance()
    )
    private val model: MainViewModel by activityViewModels()
    private val apiManager = ApiManager()
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMainBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkpermission()
        initialization()
        updateCurrentCard()
    }

    override fun onResume() {
        super.onResume()
        checkGPS()
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialization() = with(binding){
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fragmentsList)
        vp.adapter= adapter
        TabLayoutMediator(tabLayout, vp){
            tab, pos -> tab.text = tabslist[pos]
        }.attach()
        ibSearch.setOnClickListener {
            checkGPS()
        }
        ibSync.setOnClickListener {
            enterNewPalce(requireContext(), object : DialogManager.NewPlaceEntered{
                override fun placeSetter(newPlace: String) {
                    geoLocationClient = newPlace
                    apiManager.getWeatherData(geoLocationClient, requireContext(), model)
                }
            })
        }
    }

    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner){
            var buf = "${it.currentTemp}"
            var value = buf.toFloat().toInt()
            val maxMin = "${it.minTemp.toFloat().toInt()}°C/${it.maxTemp.toFloat().toInt()}°C"
            tvDate.text = it.time
            tvTempNow.text = "$value°C"
            tvPlace.text = geoLocationClient
            minmax.text = maxMin
            setConditionTv(tvConditionNow, it.imageUrl)
            //backGroundView.setImageResource(R.drawable.sunny)
            if(it.condition.contains("clear", true) || it.condition.contains("sunny", true)){
                //backGroundView.setBackgroundResource(R.drawable.sunny)
                setClear()
            }else if(it.condition.contains("rain", true)){
                setRain()
                //backGroundView.setBackgroundResource(R.drawable.rain)
            }else if(it.condition.contains("snow", true)){
                //backGroundView.setBackgroundResource(R.drawable.winter)
                setSnow()
            }
            else if(it.condition.contains("overcast", true)){
                //backGroundView.setBackgroundResource(R.drawable.overcast)
                setOvercst()
            }else if(it.condition.contains("fog", true)){
                //backGroundView.setBackgroundResource(R.drawable.fog)
                setFog()
            }
            else if(it.condition.contains("cloud", true)){
                //backGroundView.setBackgroundResource(R.drawable.sky)
                setClouds()
            }
            //setBackGround(it.condition, backGroundView as ImageView)
        }
    }

    fun setRain(){
        this.binding.backGroundView.setImageResource(R.drawable.rain)
    }
    fun setClouds(){
        this.binding.backGroundView.setImageResource(R.drawable.sky)
    }
    fun setOvercst(){
        this.binding.backGroundView.setImageResource(R.drawable.overcast)
    }
    fun setSnow(){
        this.binding.backGroundView.setImageResource(R.drawable.winter)
    }
    fun setClear(){
        this.binding.backGroundView.setImageResource(R.drawable.sunny)
    }
    fun setFog(){
        this.binding.backGroundView.setImageResource(R.drawable.fog)
    }

    private fun getLocation(){
        if(!isLocationEnabled()){
            return
        }
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                val latitude = it.result.latitude
                val longitude = it.result.longitude
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val adreses = geocoder.getFromLocation(latitude, longitude, 1)
                geoLocationClient = adreses?.get(0)?.locality.toString()
                apiManager.getWeatherData("${latitude}, ${longitude}",requireContext(), model)
            }
    }

    private fun isLocationEnabled(): Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkGPS(){
        if(isLocationEnabled()){
            getLocation()
        }else{
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.GPSListener{
                override fun onClick() {
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun setConditionTv(view: ImageView, condition: String){
        if(condition.contains("clear", true)){
            view.setImageResource(R.drawable.icon_sun)
        }else if(condition.contains("rain", true)){
            view.setImageResource(R.drawable.icon_rain)
        }else if(condition.contains("snow", true)){
            view.setImageResource(R.drawable.icon_snowy)
        }
        else if(condition.contains("overcast", true)){
            view.setImageResource(R.drawable.icon_overcast)
        }
        else if(condition.contains("cloud", true)){
            view.setImageResource(R.drawable.icon_cloud)
        }else if(condition.contains("fog", true)){
            view.setImageResource(R.drawable.icon_fog)
        }
    }

    private fun checkpermission(){
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}