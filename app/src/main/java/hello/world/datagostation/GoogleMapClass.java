package hello.world.datagostation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GoogleMapClass extends AppCompatActivity implements OnMapReadyCallback {
    MarkerAroundInfo markerAroundInfo;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    Geocoder geocoder;
    String buildingName;
    String stationName;
    String []splitStr;
    TextView buildingLink, buildingAddress, buildingNumber;
    ListView opinionList;
    Button sendOpinion;
    EditText inputOpinion;
    TextView setBuildingName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String userName;
    ArrayAdapter<String> arrayAdapter;

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


        setBuildingName = (TextView)findViewById(R.id.building_name);
        setBuildingName.setText(buildingName);

        buildingLink = (TextView)findViewById(R.id.link);
        buildingAddress = (TextView)findViewById(R.id.building_address) ;
        buildingNumber = (TextView)findViewById(R.id.tell_number);

        NaverSearchAsync async = new NaverSearchAsync(GoogleMapClass.this);
        async.execute();

        inputOpinion = (EditText)findViewById(R.id.input_opinion);
        sendOpinion = (Button)findViewById(R.id.send_opinion);
        opinionList = (ListView)findViewById(R.id.opinion_list);

        userName = "user" + new Random().nextInt(10000);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        opinionList.setAdapter(arrayAdapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        sendOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpinionData opinionData = new OpinionData(userName, inputOpinion.getText().toString());
                databaseReference.child("opinion").child(buildingName).push().setValue(opinionData);
                inputOpinion.setText("");
            }
        });

        databaseReference.child("opinion").child(buildingName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OpinionData opinionData = snapshot.getValue(OpinionData.class);
                arrayAdapter.add(opinionData.getUserName()+ ": " +opinionData.getOpinion());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                OpinionData opinionData = snapshot.getValue(OpinionData.class);
                arrayAdapter.remove(opinionData.getUserName()+ ": " +opinionData.getOpinion());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        databaseReference.child("message").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    String text = snapshot.getValue(String.class);
//                    arrayAdapter.add(text);
//                }catch (NullPointerException e){
//                    Toast.makeText(getApplicationContext(), "NullPointerException", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
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

    private static class NaverSearchAsync extends AsyncTask<Void, Void, Void>{
        public String clientId = ""; //애플리케이션 클라이언트 아이디값"
        public String clientSecret = ""; //애플리케이션 클라이언트 시크릿값"
        public String title, address, link, number;
        public String buildingText;
        private WeakReference<GoogleMapClass> activityWeakReference;

        public NaverSearchAsync(GoogleMapClass context){
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            GoogleMapClass activity = activityWeakReference.get();
            buildingText = activity.buildingName;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            String enText;
            try {
                enText = URLEncoder.encode(buildingText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패",e);
            }

            String apiURL = "https://openapi.naver.com/v1/search/local?query=" + enText;    // json 결과
            //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);
            String responseBody = get(apiURL,requestHeaders);

            parseData(responseBody);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String get(String apiUrl, Map<String, String> requestHeaders){
            HttpURLConnection con = connect(apiUrl);
            try {
                con.setRequestMethod("GET");
                for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                    con.setRequestProperty(header.getKey(), header.getValue());
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    return readBody(con.getInputStream());
                } else { // 에러 발생
                    return readBody(con.getErrorStream());
                }

            } catch (IOException e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                con.disconnect();
            }
        }

        private HttpURLConnection connect(String apiUrl){
            try {
                URL url = new URL(apiUrl);
                return (HttpURLConnection)url.openConnection();
            } catch (MalformedURLException e) {
                throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
            } catch (IOException e) {
                throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String readBody(InputStream body){
            // InputStreamReader: 바이트 스트림 -> 문자 스트림 (문자 단위로 읽어 들이는 방식으로 변형)
            InputStreamReader streamReader = new InputStreamReader(body);

            // BufferReader: 버퍼라는 배열에 데이터를 저장하고, 버퍼가 가득차면 데이터를 한 번에 보냄
            // 입출력 빠름
            try (BufferedReader lineReader = new BufferedReader(streamReader)) {
                // StringBuilder: 문자열 합칠 때 일반 String 클래스보다 훨씬 빠름
                // 보통 긴 문자열이나 큰 값을 더할 때 사용
                StringBuilder responseBody = new StringBuilder();

                String line;
                while ((line = lineReader.readLine()) != null) {
                    responseBody.append(line);
                }

                return responseBody.toString();
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
            }
        }

        private void parseData(String responseBody) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(responseBody.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    title = item.getString("title");
//                    number = item.getString("number");
                    address = item.getString("address");
                    link = item.getString("link");

                    // number: 왜인진 모르겠지만 정상작동안됨
                    // 아마 null 이기 때문?
//                    Log.d("building_info", number);
                    Log.d("building_info", address);
                    Log.d("building_info", link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            GoogleMapClass activity = activityWeakReference.get();
            activity.buildingLink.setText(link);
            activity.buildingAddress.setText(address);
//            activity.buildingNumber.setText(number);
        }
    }

}