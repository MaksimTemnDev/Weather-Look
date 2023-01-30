package com.example.weatherlook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherlook.APIwork.MainViewModel
import com.example.weatherlook.APIwork.WeatherModel
import com.example.weatherlook.R
import com.example.weatherlook.adapters.WeatherAdapter
import com.example.weatherlook.databinding.FragmentDaysBinding
import com.example.weatherlook.databinding.FragmentHoursBinding

class days : Fragment() {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        model.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it.subList(1, it.lastIndex))
        }
    }

    private fun initialize() = with(binding){
        adapter = WeatherAdapter()
        rcView.layoutManager = LinearLayoutManager(activity)
        rcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = days()
    }
}