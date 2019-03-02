package mg.studio.weatherappdesign;

import com.google.gson.Gson;

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

    public static WeatherBean.Weather getWeather() {
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

        return weather.results.get(0);
    }
}
