package com.example.weatherlook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherlook.APIwork.MainViewModel
import com.example.weatherlook.APIwork.WeatherModel
import com.example.weatherlook.R
import com.example.weatherlook.adapters.VpAdapter
import com.example.weatherlook.adapters.WeatherAdapter
import com.example.weatherlook.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class hours : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerViewObject()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(updateHoursList(it[0], it[1]))
        }
    }

    private fun updateHoursList(weatherDayModel: WeatherModel, weatherNextDayModel: WeatherModel): List<WeatherModel>{
        val hoursArray = JSONArray(weatherDayModel.hours)
        val nHoursArray = JSONArray(weatherNextDayModel.hours)
        val timeNow = Calendar.getInstance().time.hours
        var list = ArrayList<WeatherModel>()
        for (i in timeNow until hoursArray.length()) {
                val model = WeatherModel(
                    "",
                    (hoursArray[i] as JSONObject).getString("datetime"),
                    (hoursArray[i] as JSONObject).getString("conditions"),
                    (hoursArray[i] as JSONObject).getString("temp"),
                    "",
                    "",
                    (hoursArray[i] as JSONObject).getString("icon"),
                    ""
                )
                list.add(model)
        }
        for(i in 0 until timeNow){
                val model = WeatherModel(
                    "",
                    (nHoursArray[i] as JSONObject).getString("datetime"),
                    (nHoursArray[i] as JSONObject).getString("conditions"),
                    (nHoursArray[i] as JSONObject).getString("temp"),
                    "",
                    "",
                    (nHoursArray[i] as JSONObject).getString("icon"),
                    ""
                )
                list.add(model)
            }
        return list
    }

    private fun initializeRecyclerViewObject() = with(binding){
        val list = listOf(null)
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter= adapter
        adapter.submitList(null)
    }

    companion object {
        @JvmStatic
        fun newInstance() = hours()
    }
}