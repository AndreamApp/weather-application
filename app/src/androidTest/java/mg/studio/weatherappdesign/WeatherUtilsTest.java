package mg.studio.weatherappdesign;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;

import static org.junit.Assert.*;

/**
 * Created by Andream on 2019/3/1.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class WeatherUtilsTest {

    public static void generateGetDiaryWeatherURL() throws UnsupportedEncodingException, SignatureException {
        String weatherJson = WeatherUtils.generateGetDiaryWeatherURL(
                "chongqing",
                "zh-Hans",
                "c",
                "1",
                "1"
        );
        System.out.println(weatherJson);
    }


    public static void getWeather() {
        WeatherBean weather = WeatherUtils.getWeather("chongqing");
    }

    public static void main() {

    }
}