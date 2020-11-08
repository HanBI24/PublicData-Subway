package hello.world.datagostation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    ImageView subImage;
    TextView subText;
    Animation subAnim, textAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        subImage = (ImageView)findViewById(R.id.subway_image);
        subText = (TextView)findViewById(R.id.title);

        subAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_subway_image);
        textAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_splash_text);

        subAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        subImage.startAnimation(subAnim);

        textAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        subText.startAnimation(textAnim);
    }
}