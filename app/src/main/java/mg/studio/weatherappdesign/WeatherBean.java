package mg.studio.weatherappdesign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andream on 2019/3/1.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class WeatherBean {

    public List<Weather> results;

    public class Location {
        public String id;
        public String name;
        public String country;
        public String path;
        public String timezone;
        public String timezone_offset;
    }

    public class Daily {
        public String date;
        public String text_day;
        public int code_day;
        public String text_night;
        public int code_night;
        public String high;
        public String low;
        public String precip;
        public String wind_direction;
        public String wind_direction_degree;
        public String wind_speed;
        public String wind_scale;
    }

    public class Weather {
        public Location location;
        public List<Daily> daily;
        public String last_update;
    }
}
