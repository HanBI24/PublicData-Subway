package hello.world.datagostation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ArrayList<String> stationNameArray;
    private ArrayList<String> stationLineArray;
    ListAdapter listAdapter;
    ListView listView;
    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.et);
        listView = (ListView) findViewById(R.id.list_item);
        stationNameArray = new ArrayList<>();
        stationLineArray = new ArrayList<>();
        listAdapter = new ListAdapter();

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), stationNameArray.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void searchStation(View view) {
        if (view.getId() == R.id.btn_station) {
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    try {
                        getXmlDataStation();
                        for (int i = 0; i < stationNameArray.size(); i++) {
                            listAdapter.addItem(stationLineArray.get(i), stationNameArray.get(i));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
            resetListItem();
            stationLineArray.clear();
            stationNameArray.clear();
        }
    }

    private void resetListItem() {
        listAdapter.resetList();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getXmlDataStation() throws UnsupportedEncodingException {
        String str = editText.getText().toString();
        String location = URLEncoder.encode(str, java.nio.charset.StandardCharsets.UTF_8.toString());
        String queryUrl = "http://openapi.tago.go.kr/openapi/service/SubwayInfoService/getKwrdFndSubwaySttnList?serviceKey=" + key + "&subwayStationName=" + location;

        try {
            URL url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8.toString()));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) ;
                        else if (tag.equals("subwayRouteName")) {
                            xpp.next();
                            stationLineArray.add(xpp.getText());
                        } else if (tag.equals("subwayStationName")) {
                            xpp.next();
                            stationNameArray.add(xpp.getText());
                            Log.d("1234", xpp.getText());
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