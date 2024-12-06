package com.jibu.weatherwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.jibu.weatherwise.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

//b05e10f95e83df2932915e892b9a3acc

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Mahendranagar")
        SearchCity()
    }
    private fun SearchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "b05e10f95e83df2932915e892b9a3acc", "metric")
        response.enqueue(object : Callback<WeatherWise>{
            override fun onResponse(call: Call<WeatherWise>, response: Response<WeatherWise>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.presentTemp.text= "$temperature °C"
                    binding.humidity.text = "$humidity %"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.windSpeed.text = "$windspeed m/s"
                    binding.sea.text = "$sealevel hPa"
                    binding.sunRise.text = "${time(sunrise)}"
                    binding.sunSet.text = "${time(sunset)}"

                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = todayDate()
                        binding.cityName.text = "$cityName"

//                    Log.d("TAG", "onResponse: $temperature *C")
//                    Log.d("TAG", "onResponse: $sealevel")
                    
                    changeImageAccordingToCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherWise>, t: Throwable) {
                Toast.makeText(applicationContext, "Check Internet and try Again.", Toast.LENGTH_LONG).show()
            }

        })

    }



    private fun setTextColorForCondition(colorResId: Int) {
        val color = getColor(colorResId)

        binding.textView2.setTextColor(color)
        binding.presentTemp.setTextColor(color)
        binding.humidity.setTextColor(color)
        binding.weather.setTextColor(color)
        binding.maxTemp.setTextColor(color)
        binding.minTemp.setTextColor(color)
        binding.windSpeed.setTextColor(color)
        binding.sea.setTextColor(color)
        binding.sunRise.setTextColor(color)
        binding.sunSet.setTextColor(color)
        binding.condition.setTextColor(color)
        binding.day.setTextColor(color)
        binding.date.setTextColor(color)
        binding.cityName.setTextColor(color)
    }



    private fun changeImageAccordingToCondition(conditions: String) {
        when(conditions){

            "Clear Sky" , "Sunny"  ->{
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieAnimationView.setAnimation(R.raw.sunnylottie)
            }

            "Clouds"->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.lottieAnimationView.setAnimation(R.raw.sunnylottie)

            }

            "Clear"->{
                binding.root.setBackgroundResource(R.drawable.clear)
                binding.lottieAnimationView.setAnimation(R.raw.sunnylottie)
            }

            "Mist" , "Partly Clouds" , "Haze" , "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.haze2)
                binding.lottieAnimationView.setAnimation(R.raw.hazelottie)
                setTextColorForCondition(R.color.rainy_color)
            }

            "Light Rain" , "Drizzle" , "Rain" , "Showers" , "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain2)
                binding.lottieAnimationView.setAnimation(R.raw.rainylottie)
                setTextColorForCondition(R.color.rainy_color)
            }

            "Light Snow" , "Snow" , "Heavy Snow" , "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.winterlottie)
                setTextColorForCondition(R.color.rainy_color)
            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieAnimationView.setAnimation(R.raw.sunnylottie)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun todayDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}