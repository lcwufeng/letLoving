package com.example.university_ing;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoveVoiceActivity extends Activity {
	ImageButton Recordbtn;
	ImageButton PlayVoicebtn;
	ImageButton GroupVoicebtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        setContentView(R.layout.activity_love_voice);
        Recordbtn=(ImageButton)findViewById(R.id.record);
        PlayVoicebtn=(ImageButton)findViewById(R.id.playvoice);
        GroupVoicebtn=(ImageButton)findViewById(R.id.groupvoice);
        createSDCardDir(); 
        
        //判断是否插入SD卡
        if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {  
        } else {  
            Toast.makeText(this, "请先插入SD卡", Toast.LENGTH_LONG).show();  
            return;  
        } 
        
        /*------设置按钮事件------*/
        Recordbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoveVoiceActivity.this,LoveVoiceRecordActivity.class);
				LoveVoiceActivity.this.startActivity(i);
				//LoveVoiceActivity.this.finish();
			}
		});
        Recordbtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Recordbtn.setBackgroundResource(R.drawable.loverecordtouch);
				return false;
			}
		});
        PlayVoicebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoveVoiceActivity.this,LoveVoicePlay.class);
				LoveVoiceActivity.this.startActivity(i);
				//LoveVoiceActivity.this.finish();
			}
		});
        PlayVoicebtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				PlayVoicebtn.setBackgroundResource(R.drawable.playvoicetouch);
				return false;
			}
		});
        GroupVoicebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoveVoiceActivity.this,LoveVoiceGroup.class);
				LoveVoiceActivity.this.startActivity(i);
				//LoveVoiceActivity.this.finish();			
			}
		});
        GroupVoicebtn.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				GroupVoicebtn.setBackgroundResource(R.drawable.groupvoicetouch);
				return false;
			}
		});
    }

	// 在SD卡上创建一个文件夹
	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/universiting/Loving";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
				setTitle("paht ok,path:" + path);
			}
		} else {
			setTitle("false");
			return;
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_love_voice, menu);
        return true;
    }
}
