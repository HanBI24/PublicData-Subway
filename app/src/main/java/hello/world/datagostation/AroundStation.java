package hello.world.datagostation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class AroundStation extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_station);

        tv = (TextView)findViewById(R.id.tv);
        Intent intent = getIntent();
        String station = intent.getStringExtra("station");
        tv.setText(station);
    }
}