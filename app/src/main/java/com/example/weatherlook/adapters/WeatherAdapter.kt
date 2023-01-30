package com.example.weatherlook.adapters

import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.weatherlook.APIwork.WeatherModel
import com.example.weatherlook.R
import com.example.weatherlook.databinding.ListItemBinding
import java.util.zip.Inflater
import kotlin.math.round
import kotlin.math.roundToInt

class WeatherAdapter : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {
    class Holder(view: View): RecyclerView.ViewHolder(view){
        val binding =  ListItemBinding.bind(view)

        fun bind(item: WeatherModel) = with(binding){
            var str = StringBuilder(item.time)
            if(str[2] == ':') {
                str.delete(4, 7)
            }else{
                str = StringBuilder("${item.time.substring(8,10)}.${item.time.substring(5,7)}")
            }
            if(!item.currentTemp.isEmpty()) {
                var temper = item.currentTemp.toFloat()
                temp.text = "${round(temper).toInt()}°C"
            }else{
                temp.text = "${round(item.minTemp.toFloat()).toInt()}°C/${round(item.maxTemp.toFloat()).toInt()}°C"
            }
            labelTD.text = str.toString()
            val condi = item.condition
            condition.text = condi.replace('-',' ')
            setConditionTv(imageCondition, item.condition)
        }

        private fun setConditionTv(view: ImageView, condition: String){
            if(condition.contains("clear", true) || condition.contains("sunny", true)){
                view.setImageResource(R.drawable.icon_sun)
            }else if(condition.contains("rain", true)){
                view.setImageResource(R.drawable.icon_rain)
            }else if(condition.contains("snow", true)){
                view.setImageResource(R.drawable.icon_snowy)
            }
            else if(condition.contains("overcast", true)){
                view.setImageResource(R.drawable.icon_overcast)
            }
            else if(condition.contains("cloud", true) || condition.contains("overcast", true)){
                view.setImageResource(R.drawable.icon_cloud)
            }
            else if(condition.contains("fog", true)){
                view.setImageResource(R.drawable.icon_fog)
            }
        }
    }

    class Comparator: DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}