package com.example.weatherlook.APIwork

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject


class ApiManager constructor(){
    public fun getRes(name: String, context:Context, model: MainViewModel){
        var url = "https://api.weatherapi.com/v1/forecast.json?key=${KEY}&q=${name}&days=10&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, url, {response->
            parseWeatherData(response, model)
        },
            { Log.e("Result", "Volley error ${it.toString()}") })
        queue.add(stringRequest)
    }

    private fun parseWeatherData(result:String, model: MainViewModel){
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        model.liveDataList.value = list
        parseCurrentData(mainObject, list[0], model)
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name =  mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        return list
    }

    private fun parseCurrentData(mainObject:JSONObject, weatheritem: WeatherModel, model: MainViewModel): WeatherModel{
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatheritem.maxTemp,
            weatheritem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatheritem.hours
        )
        model.liveDataCurrent.value = item

        Log.d("Result", "city: ${item.city}")
        Log.d("Result", "condition: ${item.condition}")
        Log.d("Result", "temp: ${item.currentTemp}")
        Log.d("Result", "maxtemp: ${item.maxTemp}")
        Log.d("Result", "mintemp: ${item.minTemp}")
        Log.d("Result", "time: ${item.time}")
        Log.d("Result", "hours: ${item.hours}")
        return item
    }

    public fun getWeatherData(name: String, context:Context, model: MainViewModel){
        var url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$name/next14days?unitGroup=metric&key=$M_Key&contentType=json"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(Request.Method.GET, url, {response->
            parseWeatherDataStream(response, model)
        },
            { Log.e("Result", "Volley error ${it.toString()}") })
        queue.add(stringRequest)
    }

    private fun parseWeatherDataStream(result:String, model: MainViewModel){
        val mainObject = JSONObject(result)
        val list = parseDaysStream(mainObject)
        model.liveDataList.value = list
        parseCurrentDataStream(mainObject, list[0], model)
    }

    private fun parseDaysStream(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONArray("days")
        val name =  mainObject.getString("address")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("datetime"),
                day.getString("icon"),
                "",
                day.getString("tempmax"),
                day.getString("tempmin"),
                day.getString("icon"),//day.getJSONObject("day").getJSONObject("condition") .getString("icon"),
                day.getJSONArray("hours").toString()
            )
            list.add(item)
        }
        return list
    }

    private fun parseCurrentDataStream(mainObject:JSONObject, weatheritem: WeatherModel, model: MainViewModel): WeatherModel{
        val currentDay = mainObject.getJSONArray("days")[0] as JSONObject
        val item = WeatherModel(
            weatheritem.city,
            weatheritem.time,
            weatheritem.condition,
            currentDay.getString("temp"),
            weatheritem.maxTemp,
            weatheritem.minTemp,
            weatheritem.imageUrl,
            weatheritem.hours
        )
        model.liveDataCurrent.value = item

        Log.d("Result", "city: ${item.city}")
        Log.d("Result", "condition: ${item.condition}")
        Log.d("Result", "temp: ${item.currentTemp}")
        Log.d("Result", "maxtemp: ${item.maxTemp}")
        Log.d("Result", "mintemp: ${item.minTemp}")
        Log.d("Result", "time: ${item.time}")
        Log.d("Result", "hours: ${item.hours}")
        return item
    }
}