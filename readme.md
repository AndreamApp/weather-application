# Weather application

## Portrait

<img src="display/demo_weather_portrait.gif" width="320" />

## Landscape

<img src="display/demo_weather_landscape.gif" height="320" />

## Simplified API

This weather api is a simplified version from [seniverse.com/doc](https://www.seniverse.com/doc)

See: [https://cqu.andream.app/weather](https://cqu.andream.app/weather)

For example:

```json
{
    "location": "chongqing",
    "daily": [{
        "date": "2019-03-04",
        "text_day": "小雨",
        "code_day": "13",
        "high": "18",
        "low": "11"
    },
    {
        "date": "2019-03-05",
        "text_day": "小雨",
        "code_day": "13",
        "high": "13",
        "low": "10"
    },
    {
        "date": "2019-03-06",
        "text_day": "阴",
        "code_day": "9",
        "high": "14",
        "low": "10"
    },
    {
        "date": "2019-03-07",
        "text_day": "阴",
        "code_day": "9",
        "high": "14",
        "low": "10"
    },
    {
        "date": "2019-03-08",
        "text_day": "阴",
        "code_day": "9",
        "high": "13",
        "low": "10"
    }],
    "temperature": "11"
}
```

## Dependencies

OKHttp for http requests

Gson for json parse

