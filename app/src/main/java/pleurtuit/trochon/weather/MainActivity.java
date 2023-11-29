package pleurtuit.trochon.weather;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<Bitmap> {
    private ImageView viewer;
    private TextView cityTitle;
    private TextView cityCoord;
    private TextView description;
    private TextView temp;
    private TextView temp_max;
    private TextView temp_min;
    private TextView vit_vent;
    private TextView pression_atm;
    private TextView humidite;
    private TextView direction;
    private TextView snow;
    private TextView clouds;
    private ProgressDialog progress;
    private Weather weather;

    private Button btnRefresh;
    private Button newCoord;
    private Button conditionDisplay;
    private Button allDisplay;
    private Button windDisplay;
    private Button snowDisplay;
    private Button cloudsDisplay;
    private Button tempDisplay;
    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast?id=524901&units=metric&lang=FR&APPID=33e9c431ebbac95a7e83d26b0bff4d83";
    private static String IMG_URL = "https://openweathermap.org/img/w/";

    RequestQueue queue;
    public String fileName = "file";
    public SharedPreferences pos;
    String lat;
    String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Récupération latitude longitude
        pos = getSharedPreferences(fileName, 0);

        SharedPreferences.Editor editor = pos.edit();
        editor.putString("lat", "48.3646");
        editor.putString("lon", "-1.2129");
        editor.commit();

        lat = pos.getString("lat","40.712784");
        lon = pos.getString("lon","-74.005941");

        // Instancie la file de message (cet objet doit être un singleton)
        queue = Volley.newRequestQueue(this);
        MiseAJour("");
        // Finir de cabler les widgets
        this.viewer = (ImageView) findViewById(R.id.imageV);
        this.cityCoord = (TextView) findViewById(R.id.cityCoord);
        this.cityTitle = (TextView) findViewById(R.id.cityTitle);
        this.description = (TextView) findViewById(R.id.desc);
        this.temp = (TextView) findViewById(R.id.temp);
        this.temp_max = (TextView) findViewById(R.id.temp_max);
        this.temp_min = (TextView) findViewById(R.id.temp_min);
        this.vit_vent = (TextView) findViewById(R.id.vitvent);
        this.pression_atm = (TextView) findViewById(R.id.pression);
        this.humidite = (TextView) findViewById(R.id.humidite);
        this.direction = (TextView) findViewById(R.id.direction);
        this.snow = (TextView) findViewById(R.id.snow);
        this.clouds = (TextView) findViewById(R.id.clouds);

        btnRefresh = (Button) findViewById(R.id.refresh);
        newCoord = (Button) findViewById(R.id.newCoord);

        conditionDisplay = (Button) findViewById(R.id.conditionDisplay);
        allDisplay = (Button) findViewById(R.id.allDisplay);
        windDisplay = (Button) findViewById(R.id.windDisplay);
        snowDisplay = (Button) findViewById(R.id.snowDisplay);
        cloudsDisplay = (Button) findViewById(R.id.cloudsDisplay);
        tempDisplay = (Button) findViewById(R.id.tempDisplay);

        btnRefresh.setOnClickListener(actuelView -> MiseAJour("refresh"));
        newCoord.setOnClickListener(actuelView -> changeCoordonates());

        windDisplay.setOnClickListener(actuelView -> display("wind"));
        conditionDisplay.setOnClickListener(actuelView -> display("condition"));
        allDisplay.setOnClickListener(actuelView -> display("all"));
        snowDisplay.setOnClickListener(actuelView -> display("snow"));
        cloudsDisplay.setOnClickListener(actuelView -> display("clouds"));
        tempDisplay.setOnClickListener(actuelView -> display("temp"));
    }

    public void MiseAJour(String refresh) {

        lat = pos.getString("lat","40.712784");
        lon = pos.getString("lon","-74.005941");

        if(refresh == "refresh") display("all");

        this.progress = new ProgressDialog(this);
        this.progress.setTitle("Please wait");
        this.progress.setMessage("Currently loading weather informations.....");
        this.progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progress.show();



        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + "&lat=" + lat + "&lon=" + lon, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("URLOPENWEATHERMAP", BASE_URL + "&lat=" + lat + "&lon=" + lon);

                        weather = new Weather();
                        try {
                            Log.e("tab", response.toString());
                            weather = JSONWeatherParser.getWeather(response.toString());
                            // Affectation des valeurs au widget
                            cityTitle.setText(weather.city.getName());
                            cityCoord.setText("Latitude:" + lat.toString() + " Longitude:" + lon.toString());
                            temp_max.setText("Temperature max: " + weather.temperature.getMaxTemp() + " degrees Celcius");
                            temp_min.setText("Temperature min: " + weather.temperature.getMinTemp() + " degrees Celcius");
                            temp.setText("Temperature : " + weather.temperature.getTemp() + " degrees Celcius");
                            System.out.println(description);
                            description.setText("Description : " + weather.currentCondition.getDescr());
                            pression_atm.setText("Pressure : " + weather.currentCondition.getPressure());
                            vit_vent.setText("Speed of the wind : " + weather.wind.getSpeed());
                            humidite.setText("Humidity : " + weather.currentCondition.getHumidity());
                            direction.setText("Direction of the wind : " + weather.wind.getDeg());
                            clouds.setText("The sky is " + weather.clouds.getPerc() + "% cloudy");
                            if(weather.snow.getTime() != null) {
                                snow.setText("Snowfall in the last 3 hours: " + weather.snow.getAmmount() + "mm");
                            } else snow.setVisibility(View.GONE);
                            if (progress.isShowing()) progress.dismiss();
                            downloadImage(weather.currentCondition.getIcon(), queue);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("error Volley", error.toString());
                    }
                });
        queue.add(jsObjRequest);
    }

    @Override
    public void onResponse(Bitmap response) { //callback en cas de succès
        //fermeture de la boite de dialogue
        if (this.progress.isShowing()) this.progress.dismiss();

        Bitmap bm = Bitmap.createScaledBitmap(response, 400, 400, true);
        // Affectation de l'image dans l'imageview
        this.viewer.setImageBitmap(bm);
    }

    public void downloadImage(String pathImg, RequestQueue queue) {
        // Requête d'une image à l'URL demandée
        Log.i("Image down path:", pathImg);
        ImageRequest picRequest = new ImageRequest(IMG_URL + pathImg + ".png?APPID=c0bbff4a824cce23670fa594dfa7e8b1", this, 0, 0, null, null);
        // Insère la requête dans la file
        queue.add(picRequest);
    }

    public void display(String filter) {
        switch (filter) {
            case "wind":
                this.description.setVisibility(View.GONE);
                this.temp.setVisibility(View.GONE);
                this.temp_max.setVisibility(View.GONE);
                this.temp_min.setVisibility(View.GONE);
                this.pression_atm.setVisibility(View.GONE);
                this.humidite.setVisibility(View.GONE);
                this.direction.setVisibility(View.VISIBLE);
                this.vit_vent.setVisibility(View.VISIBLE);
                this.clouds.setVisibility(View.GONE);
                this.snow.setVisibility(View.GONE);
                break;
            case "condition":
                this.description.setVisibility(View.VISIBLE);
                this.temp.setVisibility(View.GONE);
                this.temp_max.setVisibility(View.GONE);
                this.temp_min.setVisibility(View.GONE);
                this.pression_atm.setVisibility(View.VISIBLE);
                this.humidite.setVisibility(View.VISIBLE);
                this.direction.setVisibility(View.GONE);
                this.vit_vent.setVisibility(View.GONE);
                this.clouds.setVisibility(View.GONE);
                this.snow.setVisibility(View.GONE);
                break;
            case "clouds":
                this.clouds.setVisibility(View.VISIBLE);
                this.description.setVisibility(View.GONE);
                this.temp.setVisibility(View.GONE);
                this.temp_max.setVisibility(View.GONE);
                this.temp_min.setVisibility(View.GONE);
                this.pression_atm.setVisibility(View.GONE);
                this.humidite.setVisibility(View.GONE);
                this.direction.setVisibility(View.GONE);
                this.vit_vent.setVisibility(View.GONE);
                this.snow.setVisibility(View.GONE);
                break;
            case "snow":
                if(weather.snow.getTime() != null) {
                    snow.setVisibility(View.VISIBLE);
                } else {
                    snow.setVisibility(View.VISIBLE);
                    snow.setText("There is no snowfalls");
                }
                this.description.setVisibility(View.GONE);
                this.temp.setVisibility(View.GONE);
                this.temp_max.setVisibility(View.GONE);
                this.temp_min.setVisibility(View.GONE);
                this.pression_atm.setVisibility(View.GONE);
                this.humidite.setVisibility(View.GONE);
                this.direction.setVisibility(View.GONE);
                this.vit_vent.setVisibility(View.GONE);
                this.clouds.setVisibility(View.GONE);
                break;
            case "temp":
                this.description.setVisibility(View.GONE);
                this.temp.setVisibility(View.VISIBLE);
                this.temp_max.setVisibility(View.VISIBLE);
                this.temp_min.setVisibility(View.VISIBLE);
                this.pression_atm.setVisibility(View.GONE);
                this.humidite.setVisibility(View.GONE);
                this.direction.setVisibility(View.GONE);
                this.vit_vent.setVisibility(View.GONE);
                this.snow.setVisibility(View.GONE);
                this.clouds.setVisibility(View.GONE);
                break;
            case "all":
                this.description.setVisibility(View.VISIBLE);
                this.temp.setVisibility(View.VISIBLE);
                this.temp_max.setVisibility(View.VISIBLE);
                this.temp_min.setVisibility(View.VISIBLE);
                this.pression_atm.setVisibility(View.VISIBLE);
                this.humidite.setVisibility(View.VISIBLE);
                this.direction.setVisibility(View.VISIBLE);
                this.vit_vent.setVisibility(View.VISIBLE);
                if(weather.snow.getTime() != null) {
                    snow.setVisibility(View.VISIBLE);
                } else this.snow.setVisibility(View.GONE);
                this.clouds.setVisibility(View.VISIBLE);
                break;
        }
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Add same code that you want to add in onActivityResult method
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        assert intent != null;
                        Log.e("NEWCOORDLAT", String.valueOf(intent.getStringExtra("lat")));
                        Log.e("NEWCOORDLON", String.valueOf(intent.getStringExtra("lon")));

                        SharedPreferences.Editor editor = pos.edit();
                        editor.remove("lat");
                        editor.putString("lat", String.valueOf(intent.getStringExtra("lat")));
                        editor.remove("lon");
                        editor.putString("lon", String.valueOf(intent.getStringExtra("lon")));
                        editor.apply();

                        MiseAJour("refresh");
                    }


                }
            });

    public void changeCoordonates(){
        Intent chooserIntent = new Intent(MainActivity.this, Popup.class);
        startActivityIntent.launch(chooserIntent);
    }
}