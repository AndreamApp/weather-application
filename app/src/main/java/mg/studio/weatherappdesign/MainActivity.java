package mg.studio.weatherappdesign;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private View bg;

    private ImageView mWeatherCondition;
    private TextView mLocation;
    private TextView mDate;
    private TextView mTemperature;

    private ImageView mDay1Weather, mDay2Weather, mDay3Weather, mDay4Weather;

    private boolean ignoreToast;
    private WeatherBean currentWeather;
    private ValueAnimator bgAnim;
    private ValueAnimator tempAnim;
    private int currentTemperature;
    private int currentBgColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateDates();
        ignoreToast = true;
        btnClick(null);
    }

    private void initViews() {
        bg = findViewById(R.id.bg);
        mWeatherCondition = findViewById(R.id.img_weather_condition);
        mLocation = findViewById(R.id.tv_location);
        mDate = findViewById(R.id.tv_date);
        mTemperature = findViewById(R.id.temperature_of_the_day);
        mDay1Weather = findViewById(R.id.weather_day1);
        mDay2Weather = findViewById(R.id.weather_day2);
        mDay3Weather = findViewById(R.id.weather_day3);
        mDay4Weather = findViewById(R.id.weather_day4);
    }

    /**
     * get resource id by string name
     * @param resName resource name
     * @param c class of resource, eg. R.drawable.class
     * @return res id
     */
    private int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void setWeatherCondition(int weatherCode, ImageView target) {
        int iconRes = getResId("weather_" + weatherCode, R.drawable.class);
        target.setImageResource(iconRes);
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

    protected void updateViews(WeatherBean weather) {
        if(weather == null || weather.daily.size() != 5) {
            Toast.makeText(this, "Please check network!", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            if(!ignoreToast) {
                Toast.makeText(this, "Current temperature:" + weather.daily.get(0).high, Toast.LENGTH_SHORT).show();
            }
            ignoreToast = false;
        }

        currentWeather = weather;

        setWeatherCondition(weather.daily.get(1).code_day, mDay1Weather);
        setWeatherCondition(0, mDay1Weather);
        setWeatherCondition(weather.daily.get(2).code_day, mDay2Weather);
        setWeatherCondition(weather.daily.get(3).code_day, mDay3Weather);
        setWeatherCondition(weather.daily.get(4).code_day, mDay4Weather);

        updateDates();
        updateCenterPanel(0);
    }

    private void updateCenterPanel(int i) {
        if(currentWeather == null || currentWeather.daily.size()-1 < i) {
            return;
        }
        // setup information of date i
        String temperature = i > 0 ? currentWeather.daily.get(i).high : currentWeather.temperature;
        mTemperature.setText(temperature);
        if(i == 1) setWeatherCondition(0, mWeatherCondition);
        else setWeatherCondition(currentWeather.daily.get(i).code_day, mWeatherCondition);
        mLocation.setText(currentWeather.location);
        mDate.setText(currentWeather.daily.get(i).date);

        // background animation
        final int srcColor = currentBgColor;
        final int dstColor = WeatherUtils.getColorOfWeather(i == 1 ? 0 : currentWeather.daily.get(i).code_day);
        if(bgAnim != null && bgAnim.isRunning()) {
            bgAnim.end();
            bgAnim = null;
        }
        bgAnim = ValueAnimator.ofFloat(0, 1);
        bgAnim.setDuration(1000);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float k = animation.getAnimatedFraction();
                int a = (int)(((dstColor >> 24) & 0xff) * k + ((srcColor >> 24) & 0xff) * (1 - k));
                int r = (int)(((dstColor >> 16) & 0xff) * k + ((srcColor >> 16) & 0xff) * (1 - k));
                int g = (int)(((dstColor >> 8) & 0xff) * k + ((srcColor >> 8) & 0xff) * (1 - k));
                int b = (int)(((dstColor) & 0xff) * k + ((srcColor) & 0xff) * (1 - k));
                int color = (a << 24) | (r << 16) | (g << 8) | b;
                bg.setBackgroundColor(currentBgColor = color);
            }
        });
        bgAnim.start();

        // temperature animation
        final int srcTemp = currentTemperature;
        final int dstTemp = Integer.parseInt(currentWeather.daily.get(i).high);
        if(tempAnim != null && tempAnim.isRunning()) {
            tempAnim.end();
            tempAnim = null;
        }
        tempAnim = ValueAnimator.ofInt(srcTemp, dstTemp);
        tempAnim.setDuration(500);
        tempAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentTemperature = (int)animation.getAnimatedValue();
                mTemperature.setText(String.valueOf(currentTemperature));
            }
        });
        tempAnim.start();
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }

    public void day1Click(View view) {
        updateCenterPanel(1);
    }

    public void day2Click(View view) {
        updateCenterPanel(2);
    }

    public void day3Click(View view) {
        updateCenterPanel(3);
    }

    public void day4Click(View view) {
        updateCenterPanel(4);
    }


    private class DownloadUpdate extends AsyncTask<String, Void, WeatherBean> {


        @Override
        protected WeatherBean doInBackground(String... strings) {
            return WeatherUtils.getWeather();
        }

        @Override
        protected void onPostExecute(WeatherBean weather) {
            updateViews(weather);
        }
    }
}
