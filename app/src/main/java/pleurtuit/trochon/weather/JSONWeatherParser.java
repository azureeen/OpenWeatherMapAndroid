
package pleurtuit.trochon.weather;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class JSONWeatherParser {

	public static Weather getWeather(String data) throws JSONException {
		Weather weather = new Weather();

		// We create out JSONObject from the data
		JSONObject jObj = new JSONObject(data);


		// We get weather info (This is an array)
		JSONArray jArr = jObj.getJSONArray("list");
		// We use only the first value

		double tmp_max = jArr.getJSONObject(0).getJSONObject("main").getDouble("temp_max");
		double tmp_min = jArr.getJSONObject(0).getJSONObject("main").getDouble("temp_min");

		//JSONObject JSONWeather = jArr.getJSONObject(0);
		for (int i = 0; i < jArr.length(); i++) {
			JSONObject JSONWeather = jArr.getJSONObject(i);

		JSONObject mainObj = JSONWeather.getJSONObject("main");
			if (tmp_max < mainObj.getDouble("temp_max")) {
				weather.temperature.setMaxTemp((float) mainObj.getDouble("temp_max"));
				tmp_max = mainObj.getDouble("temp_max");
			}
			if (tmp_min > mainObj.getDouble("temp_min")) {
				weather.temperature.setMinTemp((float) mainObj.getDouble("temp_min"));
				tmp_min = mainObj.getDouble("temp_min");
			}
			if(i == 0) {
				weather.temperature.setTemp((float) mainObj.getDouble("temp"));
				weather.currentCondition.setHumidity(mainObj.getInt("humidity"));
				weather.currentCondition.setPressure(mainObj.getInt("pressure"));
			}

		
		// Wind
		JSONObject wObj = JSONWeather.getJSONObject("wind");
			weather.wind.setSpeed((float) wObj.getDouble("speed"));
			weather.wind.setDeg((float) wObj.getDouble("deg"));
		
		// Clouds
		JSONObject cObj = JSONWeather.getJSONObject("clouds");
			weather.clouds.setPerc(cObj.getInt("all"));

		//Weather
		JSONArray Wobj = JSONWeather.getJSONArray("weather");
			for (int h = 0; h < Wobj.length(); h++) {
				JSONObject JSONMain = Wobj.getJSONObject(h);
				weather.currentCondition.setDescr(JSONMain.getString("description"));
				weather.currentCondition.setIcon(JSONMain.getString("icon"));
				weather.currentCondition.setWeatherId(JSONMain.getInt("id"));
				weather.currentCondition.setCondition(JSONMain.getString("main"));
			}

		// Snow
			try {
				if(JSONWeather.getJSONObject("snow") != null) {
					JSONObject snowObj = JSONWeather.getJSONObject("snow");
					Iterator<String> iter = snowObj.keys();
					while(iter.hasNext()){
						String key = iter.next();
						weather.snow.setTime(key);
					}
					weather.snow.setAmmount(Float.parseFloat(snowObj.getString("3h")));
				}
			} catch (JSONException e){
				Log.e("snow","Pas de neige");
			}
		}
		// We get weather info (This is an array)
		JSONObject JSONArrCity = jObj.getJSONObject("city");
		weather.city.setName(JSONArrCity.getString("name"));

		return weather;
		}
}
