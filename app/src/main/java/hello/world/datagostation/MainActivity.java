package hello.world.datagostation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
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
    FirebaseAnalytics mFirebaseAnalytics;
    SignInButton Google_Login;
    ImageView userImageView;
    TextView userName, userEmail;
    Button LogOutBtn, RemoveIdBtn;
    private FirebaseUser currentUser;

    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

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

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        Google_Login = findViewById(R.id.google_sing_btn);
        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        editText = (EditText) findViewById(R.id.et);
        listView = (ListView) findViewById(R.id.list_item);
        searchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        searchImage = (ImageView) findViewById(R.id.search);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        stationNameArray = new ArrayList<>();
        stationLineArray = new ArrayList<>();
        stationID = new ArrayList<>();
        listAdapter = new StationInfoListAdapter();

        userImageView = (ImageView)findViewById(R.id.user_img);
        userName = (TextView)findViewById(R.id.user_name);
        userEmail = (TextView)findViewById(R.id.user_email);
        LogOutBtn = (Button)findViewById(R.id.log_out_btn);
        RemoveIdBtn = (Button)findViewById(R.id.remove_btn);

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "log out", Toast.LENGTH_SHORT).show();

                userImageView.setVisibility(View.GONE);
                userEmail.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);

                Google_Login.setVisibility(View.VISIBLE);
                LogOutBtn.setVisibility(View.GONE);
            }
        });

        Log.d("LifeCycle_Firebase", "onCreate");

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
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    searchStation();
                    SearchStationTask task = new SearchStationTask();
                    task.execute();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                //구글 로그인 실패
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();

                            currentUser = mAuth.getCurrentUser();

                            if(currentUser!=null)
                            {
                                LoadUserInfo();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadUserInfo();
    }

    // 파베 이미지 못 가져올때
    // => 스레드 써서하면 되지 않을까 생각
    public void LoadUserInfo(){

        // 자동 로그인 시스템 전역 변수로 설정해서 처리
        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null)
        {
            String uName = null, uEmail = null;
            Uri photoUrl = null;
            String photoPath, newString;
            // Variable holding the original String portion of the url that will be replaced
            String originalPieceOfUrl = "s96-c/photo.jpg";

            // Variable holding the new String portion of the url that does the replacing, to improve image quality
            String newPieceOfUrlToAdd = "s400-c/photo.jpg";

            for(UserInfo profile : currentUser.getProviderData()){
                uName = profile.getDisplayName();
                uEmail = profile.getEmail();
                photoUrl = profile.getPhotoUrl();

                if(photoUrl != null) {
                    photoPath = photoUrl.toString();
                    newString = photoPath.replace(originalPieceOfUrl, newPieceOfUrlToAdd);
                    userImageView.setVisibility(View.VISIBLE);

                    Glide.with(MainActivity.this)
                            .load(newString)
                            .into(userImageView);
                }
            }
            userEmail.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);

            Google_Login.setVisibility(View.GONE);
            LogOutBtn.setVisibility(View.VISIBLE);
            RemoveIdBtn.setVisibility(View.VISIBLE);

            userName.setText(uName);
            userEmail.setText(uEmail);
            userImageView.setImageURI(photoUrl);

            Toast.makeText(getApplicationContext(), "log in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void RemoveID(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "remove ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class SearchStationTask extends AsyncTask<Void, Void, Void> {
        private FlipProgressDialog fpd = new FlipProgressDialog();

        @Override
        protected void onPreExecute() {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            resetListItem();
            stationLineArray.clear();
            stationNameArray.clear();
            stationID.clear();

            List<Integer> imageList = new ArrayList<Integer>();
            imageList.add(R.drawable.ic_baseline_subway_24);
            fpd.setImageList(imageList);                              // *Set a imageList* [Have to. Transparent background png recommended]
            fpd.setCanceledOnTouchOutside(true);                      // If true, the dialog will be dismissed when user touch outside of the dialog. If false, the dialog won't be dismissed.
            fpd.setDimAmount(0.0f);                                   // Set a dim (How much dark outside of dialog)

            // About dialog shape, color
            fpd.setBackgroundColor(Color.parseColor("#FFFFFF"));      // Set a background color of dialog
            fpd.setBackgroundAlpha(0.2f);                                        // Set a alpha color of dialog
            fpd.setBorderStroke(0);                                              // Set a width of border stroke of dialog
            fpd.setBorderColor(-1);                                              // Set a border stroke color of dialog
            fpd.setCornerRadius(16);                                             // Set a corner radius

            // About image
            fpd.setImageSize(200);                                    // Set an image size
            fpd.setImageMargin(10);                                   // Set a margin of image

            // About rotation
            fpd.setOrientation("rotationY");                          // Set a flipping rotation
            fpd.setStartAngle(0.0f);                                  // Set an angle when flipping ratation start
            fpd.setEndAngle(180.0f);                                  // Set an angle when flipping ratation end
            fpd.setMinAlpha(0.0f);                                    // Set an alpha when flipping ratation start and end
            fpd.setMaxAlpha(1.0f);                                    // Set an alpha while image is flipping


            fpd.show(getFragmentManager(), "");
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getXmlDataStation();
                for (int i = 0; i < stationNameArray.size(); i++) {
                    listAdapter.addItem(stationLineArray.get(i), stationNameArray.get(i));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listAdapter.notifyDataSetChanged();
            fpd.dismiss();
            super.onPostExecute(aVoid);
        }
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
                        } else if (tag.equals("subwayStationId")) {
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