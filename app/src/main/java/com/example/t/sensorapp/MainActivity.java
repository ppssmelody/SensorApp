package com.example.t.sensorapp;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView startLabel;
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private boolean action_flg = false;
    private boolean start_flg = false;
    private int frameHeight;
    private int frameWidth;
    private int boxSize;
    private int boxWidthSize;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageView mDrawable;
    private boolean right,left,up,down;
    public static int x = 0;
    public static int y = 0;
    public static int oldX = 0;
    public static int oldY = 0;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLabel = (TextView) findViewById(R.id.startLabel);
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        Point size = new Point();
        d.getSize(size);
        mDrawable = (ImageView) findViewById(R.id.box);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    public void changePos(){
        if (action_flg){
            y-=10;
        }
        else y+=10;
        if (y<0) y=0;
        if (y>frameHeight-boxSize) y = frameHeight - boxSize;
        mDrawable.setY(y);
    }
    public boolean onTouchEvent(MotionEvent event){
         if (start_flg == false){
                start_flg = true;
             FrameLayout frameLayout = findViewById(R.id.frame);
             frameHeight = frameLayout.getHeight();
             frameWidth = frameLayout.getWidth();
            // y =(int)mDrawable.getY();
             boxSize = mDrawable.getHeight();
             boxWidthSize = mDrawable.getWidth();

                startLabel.setVisibility(View.GONE);
             timer.schedule(new TimerTask() {
                 @Override
                 public void run() {
                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             changePos();
                         }
                     });
                 }
             },0,20);
         }else {
             if (event.getAction() == MotionEvent.ACTION_DOWN){
                 action_flg = true;
             }
             else if (event.getAction() == MotionEvent.ACTION_UP){
                 action_flg = false;
             }
         }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            if (x>frameWidth-boxWidthSize) x = frameWidth - boxWidthSize;
            if (x<0) {x=0; }
            if (y<0) y=0;
            if (y>frameHeight-boxSize) y = frameHeight - boxSize;
            oldX = x;
            oldY = y;
            x -= (int) event.values[0];
            y += (int) event.values[1];
            if(oldX - x > 0){
                right = false;
                left = true;
                Animation an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
                mDrawable.startAnimation(an);
            }
            if(oldX - x < 0){
                right = true;
                left = false;
            }
            if(oldY - y > 0){
                up = true;
                down = false;
                Animation an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.jump);
                mDrawable.startAnimation(an);
            }
            if(oldY - y < 0){
                up = false;
                down = true;

                Animation an= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
                mDrawable.startAnimation(an);
            }
            mDrawable.setY(y);
            mDrawable.setX(x);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
