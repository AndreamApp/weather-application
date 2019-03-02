package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://39.107.228.154:4000/weather";

    private ImageView mWeatherCondition;
    private TextView mLocation;
    private TextView mDate;
    private TextView mTemperature;

    private ImageView mDay1Weather, mDay2Weather, mDay3Weather, mDay4Weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        updateDates();
        btnClick(null);
    }

    private void initViews() {
        mWeatherCondition = findViewById(R.id.img_weather_condition);
        mLocation = findViewById(R.id.tv_location);
        mDate = findViewById(R.id.tv_date);
        mTemperature = findViewById(R.id.temperature_of_the_day);
        mDay1Weather = findViewById(R.id.weather_day1);
        mDay2Weather = findViewById(R.id.weather_day2);
        mDay3Weather = findViewById(R.id.weather_day3);
        mDay4Weather = findViewById(R.id.weather_day4);
    }

    private void setWeatherCondition(int weatherCode, ImageView target) {

    }

    protected void updateDates() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        String[] map = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        ((TextView)findViewById(R.id.tv_weekday)).setText(map[dayOfWeek]);
        ((TextView)findViewById(R.id.tv_day1)).setText(map[(dayOfWeek + 1) % 7].substring(0, 3));
        ((TextView)findViewById(R.id.tv_day2)).setText(map[(dayOfWeek + 2) % 7].substring(0, 3));
        ((TextView)findViewById(R.id.tv_day3)).setText(map[(dayOfWeek + 3) % 7].substring(0, 3));
        ((TextView)findViewById(R.id.tv_day4)).setText(map[(dayOfWeek + 4) % 7].substring(0, 3));
    }

    protected void updateViews(WeatherBean.Weather weather) {
        if(weather.daily.size() == 5) {
            setWeatherCondition(weather.daily.get(0).code_day, mWeatherCondition);
            setWeatherCondition(weather.daily.get(1).code_day, mDay1Weather);
            setWeatherCondition(weather.daily.get(2).code_day, mDay2Weather);
            setWeatherCondition(weather.daily.get(3).code_day, mDay3Weather);
            setWeatherCondition(weather.daily.get(4).code_day, mDay4Weather);
        }
        mTemperature.setText(weather.daily.get(0).high);
        mLocation.setText(weather.location.name);
        mDate.setText(weather.daily.get(0).date);

        updateDates();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, WeatherBean.Weather> {


        @Override
        protected WeatherBean.Weather doInBackground(String... strings) {
            return WeatherUtils.getWeather();
        }

        @Override
        protected void onPostExecute(WeatherBean.Weather weather) {
            updateViews(weather);
        }
    }
}
