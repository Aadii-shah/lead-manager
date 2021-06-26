package sales_crm.customers.leads.crm.leadmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import sales_crm.customers.leads.crm.leadmanager.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();

        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        /** Making this activity, full screen */
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_splash);

        /*ImageView imageView=(ImageView)findViewById(R.id.imageView); // Declare an imageView to show the animation.
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade); // Create the animation.

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        imageView.startAnimation(animation);*/


        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);*/

    }

}
