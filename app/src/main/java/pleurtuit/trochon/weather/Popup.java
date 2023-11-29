package pleurtuit.trochon.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

public class Popup extends Activity {

    private EditText lat;
    private EditText lon;
    private Button validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        lat = findViewById(R.id.latCoord);
        lon = (EditText) findViewById(R.id.lonCoord);
        validate = findViewById(R.id.validateCoord);

        validate.setOnClickListener(actuelView -> SetNewCoordonates(lat, lon));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.3));
    }


    public void SetNewCoordonates(EditText lat, EditText lon){
        Intent intent = new Intent();
        intent.putExtra("lat",lat.getText().toString());
        intent.putExtra("lon",lon.getText().toString());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
