package hello.world.datagostation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GoogleMapClass extends AppCompatActivity implements OnMapReadyCallback {
    MarkerAroundInfo markerAroundInfo;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    Geocoder geocoder;
    String buildingName;
    String stationName;
    String []splitStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        buildingName = intent.getStringExtra("building_name");
        stationName = intent.getStringExtra("station_name");

        markerAroundInfo = new MarkerAroundInfo();
        markerAroundInfo.setBuilding_name("hello");
        markerAroundInfo.setExit_no("world");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 마커 여러개 생성
//        for(int i=0; i<10; i++) {
//            markerOptions = new MarkerOptions();
//            markerOptions.position(new LatLng(37.52487 + i, 126.92723));
//            markerOptions.title("서울");
//            markerOptions.snippet("한국의 수도");
//            googleMap.addMarker(markerOptions);
//        }
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.52487, 126.92723)));

        //마커 커스텀 레이아웃
//        LatLng seoul = new LatLng(37.494870, 126.960763);
//        markerOptions = new MarkerOptions();
//        markerOptions.position(seoul);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        googleMap.addMarker(markerOptions);
//
//        @SuppressLint("InflateParams") View infoWindow = getLayoutInflater().inflate(R.layout.marker_layout, null);
//        MarkerAroundAdapter markerAroundAdapter = new MarkerAroundAdapter(infoWindow, markerAroundInfo);
//        googleMap.setInfoWindowAdapter(markerAroundAdapter);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        // more info about GeoCoder...
        // 데이터 전처리 시 공백 제거
        geocoder = new Geocoder(this);
        String str = buildingName;
        List<Address> addressList = null;
        Log.d("buildnag_name", str);
        try{
            addressList = geocoder.getFromLocationName(str, 10);
//            Log.d("12345678", addressList.get(0).toString());
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            assert addressList != null;
            splitStr = addressList.get(0).toString().split(",");
            Log.d("12345678", addressList.get(0).toString());
            String address = splitStr[0].substring(splitStr[0].indexOf("\"") +1, splitStr[0].length() -2);
            Log.d("12345678", address);

            String latitude = splitStr[10].substring(splitStr[10].indexOf("=") +1);
            String longitude = splitStr[12].substring(splitStr[12].indexOf("=") +1);

            LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            markerOptions = new MarkerOptions();
            markerOptions.title(buildingName)
                    .snippet(address)
                    .position(point);
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(getApplicationContext(), "준비 중...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}