package hello.world.datagostation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ArrayList<String> stationNameArray;
    private ArrayList<String> stationLineArray;
    private ArrayList<String> stationID;
    LinearLayout searchLayout, mainLayout;
    ImageView searchImage;
    InputMethodManager imm;
    XmlPullParser xpp;
    StationInfoListAdapter listAdapter;
    ListView listView;
    String tag;
    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한을 허가해야 정상적으로 이용 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        editText = (EditText) findViewById(R.id.et);
        listView = (ListView) findViewById(R.id.list_item);
        searchLayout = (LinearLayout)findViewById(R.id.search_layout);
        mainLayout = (LinearLayout)findViewById(R.id.main_layout);
        searchImage = (ImageView)findViewById(R.id.search);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        stationNameArray = new ArrayList<>();
        stationLineArray = new ArrayList<>();
        stationID = new ArrayList<>();
        listAdapter = new StationInfoListAdapter();

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchStation();
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    return true;
                }
                return false;
            }
        });

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), stationID.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AroundStation.class);
                // 대박;; ArrayList 보내기
//                intent.putStringArrayListExtra("station", stationNameArray);

                /////// station id 보내야 함. 수정 //////
                intent.putExtra("station_name", stationNameArray.get(position));
                intent.putExtra("station_id", stationID.get(position));
                intent.putExtra("key_value", key);
                startActivity(intent);

            }
        });
    }

    public void searchStation(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    // parsing 시작
                    getXmlDataStation();
                    // getXmlDataStation()에서 리스트 크기만큼 반복해서 항목 추가
                    for (int i = 0; i < stationNameArray.size(); i++) {
                        listAdapter.addItem(stationLineArray.get(i), stationNameArray.get(i));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 리스트 갱신
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
        // 리스트 연속 출력 시 아래 문구를 사용해야 중복되지 않음
        resetListItem();
        stationLineArray.clear();
        stationNameArray.clear();
        stationID.clear();
    }

    // ListAdapter에 있는 data 삭제
    private void resetListItem() {
        listAdapter.resetList();
    }

    // XmlPullParser를 사용해서 공공 데이터에 있는 xml 파일 parsing
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getXmlDataStation() throws UnsupportedEncodingException {
        String str = editText.getText().toString();
        // 한글은 인식을 못하기 때문에 UTF-8로 인코딩을 해줘야 함
        // Least Android API: KITKAT
        String location = URLEncoder.encode(str, java.nio.charset.StandardCharsets.UTF_8.toString());
        // 웹 사이트 uri 제작
        String queryUrl = "http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList?serviceKey=" + key + "&subwayStationName=" + location;

        try {
            // 문자열인 url을 URL 객체로 생성
            URL url = new URL(queryUrl);
            // url 위치로 입력 스트림 연결
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
            // InputStream()으로 xml 입력받음
            xpp.setInput(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8.toString()));

            // 항목이동
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // parsing 시작
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        // tag 이름
                        tag = xpp.getName();
                        if (tag.equals("item")) ;
                        else if (tag.equals("subwayRouteName")) {
                            xpp.next();
                            stationLineArray.add(xpp.getText());
                        } else if (tag.equals("subwayStationName")) {
                            xpp.next();
                            stationNameArray.add(xpp.getText());
                        } else if(tag.equals("subwayStationId")){
                            xpp.next();
                            stationID.add(xpp.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}