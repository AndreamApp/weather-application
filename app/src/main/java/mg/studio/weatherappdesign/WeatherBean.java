package mg.studio.weatherappdesign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andream on 2019/3/1.
 * Email: andreamapp@qq.com
 * Website: http://andreamapp.com
 */
public class WeatherBean {

    public String location;
    public String temperature;
    public List<Daily> daily;

    public class Daily {
        public String date;
        public String text_day; // weather description of this day, in Chinese
        public int code_day; // weather code
        public String high;
        public String low;
    }

}
