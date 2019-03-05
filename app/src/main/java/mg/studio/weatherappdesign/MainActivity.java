package mg.studio.weatherappdesign;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private View bg;
    private Button btn;
    private ImageView mWeatherCondition;
    private TextView mLocation;
    private TextView mDate;
    private TextView mTemperature;
    private ImageView mDay1Weather, mDay2Weather, mDay3Weather, mDay4Weather;

    private boolean ignoreToast;
    private WeatherBean currentWeather;

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
        btn = findViewById(R.id.button);
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
        int iconRes = WeatherUtils.getWeatherIconByCode(weatherCode);
        target.setImageResource(iconRes);
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
        setWeatherCondition(weather.daily.get(2).code_day, mDay2Weather);
        setWeatherCondition(weather.daily.get(3).code_day, mDay3Weather);
        setWeatherCondition(weather.daily.get(4).code_day, mDay4Weather);

        updateDates();
        updateCenterPanel(0);
    }

    String[] map = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
    protected void updateDates() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        ((TextView)findViewById(R.id.tv_weekday)).setText(map[dayOfWeek]);
        for(int i = 1; i <= 4; i++) {
            int tvId = WeatherUtils.getResId("tv_day" + i, R.id.class);
            String day = map[(dayOfWeek + i) % 7].substring(0, 3);
            if(currentWeather != null) {
                day += "\n" + currentWeather.daily.get(i).low + "~" + currentWeather.daily.get(i).high;
            }
            ((TextView)findViewById(tvId)).setText(day);
        }
    }

    // Variables for animation
    private ValueAnimator animator;
    private int currentBgColor;
    private int currentTemperatureLow;
    private int currentTemperatureHigh;

    private void updateCenterPanel(int i) {
        if(currentWeather == null || currentWeather.daily.size()-1 < i) {
            return;
        }
        WeatherBean.Daily daily = currentWeather.daily.get(i);

        // setup information of date i
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 + i;
        ((TextView)findViewById(R.id.tv_weekday)).setText(map[dayOfWeek]);

        String temperature = daily.low + "~" + daily.high;
        mTemperature.setText(temperature);

        if(i == 1) setWeatherCondition(0, mWeatherCondition);
        else setWeatherCondition(daily.code_day, mWeatherCondition);
        mLocation.setText(currentWeather.location);
        mDate.setText(daily.date);

        // transform animation
        final int srcColor = currentBgColor;
        final int dstColor = WeatherUtils.getColorOfWeather(daily.code_day);
        final int srcLow = currentTemperatureLow;
        final int dstLow = Integer.parseInt(daily.low);
        final int srcHigh = currentTemperatureHigh;
        final int dstHigh = Integer.parseInt(daily.high);
        if(animator != null && animator.isRunning()) {
            animator.end();
        }
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            float k = animation.getAnimatedFraction();
            int a = (int)(((dstColor >> 24) & 0xff) * k + ((srcColor >> 24) & 0xff) * (1 - k));
            int r = (int)(((dstColor >> 16) & 0xff) * k + ((srcColor >> 16) & 0xff) * (1 - k));
            int g = (int)(((dstColor >> 8) & 0xff) * k + ((srcColor >> 8) & 0xff) * (1 - k));
            int b = (int)(((dstColor) & 0xff) * k + ((srcColor) & 0xff) * (1 - k));
            int color = (a << 24) | (r << 16) | (g << 8) | b;
            bg.setBackgroundColor(currentBgColor = color);
            btn.setBackgroundColor(color - 0x101010);

            currentTemperatureLow = (int)(srcLow + (dstLow - srcLow) * k);
            currentTemperatureHigh = (int)(srcHigh + (dstHigh - srcHigh) * k);
            mTemperature.setText(currentTemperatureLow + "~" + currentTemperatureHigh);
        });
        animator.start();
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
