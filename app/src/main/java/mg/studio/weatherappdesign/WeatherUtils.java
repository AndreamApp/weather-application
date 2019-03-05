package mg.studio.weatherappdesign;

import com.google.gson.Gson;

import java.lang.reflect.Field;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Andream on 2019/3/1.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class WeatherUtils {

    private static final String URL = "http://39.107.228.154:4000/weather";

    /**
     * get weather data from api, and parse it into bean Object
     * @return parsed {@link WeatherBean}, or null if network failed
     */
    public static WeatherBean getWeather() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();

        WeatherBean weather = null;
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            weather = new Gson().fromJson(json, WeatherBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(weather == null) return null;

        return weather;
    }

    /**
     * get background color by weather code
     * @param code {@see:https://www.seniverse.com/doc}
     * @return the color of weather
     */
    public static int getColorOfWeather(int code) {
        if(code <= 3) // sunny -> yellow
        {
            return 0xfff9a901;
        }
        else if(code <= 9) { // cloudy -> gray
            return 0xff7b7b7b;
        }
        else { // rainy -> blue
            return 0xff2495d1;
        }
    }


    /**
     * get resource id by string name
     * @param resName resource name
     * @param c class of resource, eg. R.drawable.class
     * @return res id
     */
    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * get weather icon resource id by weather code
     * @param weatherCode {@see:https://www.seniverse.com/doc}
     * @return icon res id
     */
    public static int getWeatherIconByCode(int weatherCode) {
        return getResId("weather_" + weatherCode, R.drawable.class);
    }
}
