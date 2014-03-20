package com.example.university_ing;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/*-----------SplashScreen开始界面 ----------*/

public class SplashScreen extends Activity {

    protected static long SplashScreenStartTime = 0;
    protected Timer SplashScreenTimer;
	protected boolean _active;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        _active=true;
        //Splash欢迎界面
        setContentView(R.layout.activity_splashscreen);
        SplashScreenTimer = new Timer(true);
        SplashScreenStartTime = System.currentTimeMillis();
        SplashScreenTimer.schedule(SplashTask, 0, 1);
    }
    
    /*-----Splash欢迎界面-----*/
	private final TimerTask SplashTask = new TimerTask() {
		@Override
		public void run() {
			if (SplashTask.scheduledExecutionTime()-SplashScreenStartTime == 3000||!_active) {
				Message message = new Message();
				message.what = 0;
				timerHandler.sendMessage(message);
				SplashScreenTimer.cancel();
				this.cancel();
			}

		}
	};
	private final Handler timerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Intent i=new Intent(SplashScreen.this,MainActivity.class);
				SplashScreen.this.startActivity(i);//启动新的界面
				SplashScreen.this.finish();//关闭SplashScreen
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		_active = false;
		return true;
	}
	
}
