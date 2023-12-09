package com.plants.plantopia;

import org.json.JSONArray;
import org.json.JSONException;

public class weatherData {

    // Instance variables to store weather data
    private String mTemperature, micon, mcity, mWeatherType;
    private int mCondition;

    /**
     * Parses a JSON array containing weather data and returns a WeatherData object.
     * @param jsonObject A JSON array containing weather data.
     * @return A WeatherData object containing the parsed data, or null if parsing fails.
     */
    public static weatherData fromJson(JSONArray jsonObject) {
        try
        {
            weatherData weatherD=new weatherData();
            weatherD.mcity=jsonObject.getString(Integer.parseInt("name"));
            weatherD.mCondition=jsonObject.getJSONArray(Integer.parseInt("weather")).getJSONObject(0).getInt("id");
            weatherD.mWeatherType=jsonObject.getJSONArray(Integer.parseInt("weather")).getJSONObject(0).getString("main");
            weatherD.micon=updateWeatherIcon(weatherD.mCondition);
            double tempResult=jsonObject.getJSONObject(Integer.parseInt("main")).getDouble("temp")-273.15;
            int roundedValue=(int)Math.rint(tempResult);
            weatherD.mTemperature=Integer.toString(roundedValue);
            return weatherD;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
    }
    /**
     * Returns the appropriate icon for the given weather condition code.
     * @param condition The weather condition code.
     * @return The name of the corresponding weather icon.
     */
    private static String updateWeatherIcon(int condition)
    {
        if(condition>=0 && condition<=300)
        {
            return "thunderstorm1";
        }
        else if(condition>=300 && condition<=500)
        {
            return "lightrain";
        }
        else if(condition>=500 && condition<=600)
        {
            return "shower";
        }
        else  if(condition>=600 && condition<=700)
        {
            return "snow2";
        }
        else if(condition>=701 && condition<=771)
        {
            return "fog";
        }

        else if(condition>=772 && condition<=800)
        {
            return "overcast";
        }
        else if(condition==800)
        {
            return "sunny";
        }
        else if(condition>=801 && condition<=804)
        {
            return "cloudy";
        }
        else  if(condition>=900 && condition<=902)
        {
            return "thunderstorm1";
        }
        if(condition==903)
        {
            return "snow1";
        }
        if(condition==904)
        {
            return "sunny";
        }
        if(condition>=905 && condition<=1000)
        {
            return "thunderstorm2";
        }
        return "dunno";
    }
    // Getters for weather data
    public String getmTemperature() {
        return mTemperature+"°C";
    }
    public String getMicon() {
        return micon;
    }
    public String getMcity() {
        return mcity;
    }
    public String getmWeatherType() {
        return mWeatherType;
    }
}


