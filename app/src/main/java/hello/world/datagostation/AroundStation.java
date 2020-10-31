package hello.world.datagostation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AroundStation extends Activity {
    ArrayList<String> StationBuilding;
    ArrayList<String> StationExit;
    StationAroundInfoListAdapter listAdapter;
    ListView listView;
    XmlPullParser xpp;
    String stationID;
    String key;
    String tag;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_around_station);

        Intent intent = getIntent();
        stationID = intent.getStringExtra("station_id");
        key = intent.getStringExtra("key_value");

        StationBuilding = new ArrayList<>();
        StationExit = new ArrayList<>();

        Log.d("key_1234", key);
        listView = (ListView)findViewById(R.id.list_item);
        listAdapter = new StationAroundInfoListAdapter();
        listView.setAdapter(listAdapter);

        searchAround();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void searchAround() {
        // Android 4.0 이상부터 네트워크 처리는 반드시 Thread 사용.
        // 작업 스레드: 네트워크
        // UI 스레드: 그 외 나머지 (UI)
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    // parsing 시작
                    getXmlDataStation();
                    // getXmlDataStation()에서 리스트 크기만큼 반복해서 항목 추가
                    for (int i = 0; i < StationBuilding.size(); i++) {
                        listAdapter.addItem(StationBuilding.get(i), StationExit.get(i));
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
        //resetListItem();
//        StationBuilding.clear();
//        StationExit.clear();
    }

    // ListAdapter에 있는 data 삭제
    private void resetListItem() {
        listAdapter.resetList();
    }

    // XmlPullParser를 사용해서 공공 데이터에 있는 xml 파일 parsing
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getXmlDataStation() throws UnsupportedEncodingException {
        String str = stationID;
        Log.d("1234", str);
        // 한글은 인식을 못하기 때문에 UTF-8로 인코딩을 해줘야 함
        // Least Android API: KITKAT
//        String location = URLEncoder.encode(str, java.nio.charset.StandardCharsets.UTF_8.toString());
        // 웹 사이트 uri 제작
        String queryUrl = "http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getSubwaySttnExitAcctoCfrFcltyList?serviceKey=" + key + "&subwayStationId=" + str;

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
                        else if (tag.equals("dirDesc")) {
                            xpp.next();
                            Log.d("123456", xpp.getText());
                            StationBuilding.add(xpp.getText());
                        } else if(tag.equals("exitNo")){
                            xpp.next();
                            StationExit.add(xpp.getText());
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