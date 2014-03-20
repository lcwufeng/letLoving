package com.example.university_ing;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoveVoiceRecordActivity extends Activity {
	
	MediaRecorder audioRecorder;
	ImageButton Recordbtn;
	ImageButton Recordcancel;
	ImageView ImageVoice;
	EditText RecordNameedt;
	String RecordName;
	TextView RecordState;
	private boolean RecordJudge;//记录是否在录音
	SQLiteDatabase Lovedb;//数据库操作
	//private final static int CWJ_HEAP_SIZE = 6* 1024* 1024 ; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        //VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE); //设置最小heap内存为6MB大小。
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_love_voice_record);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        RecordJudge=false; 
        
        Recordbtn=(ImageButton)findViewById(R.id.record);
        Recordcancel=(ImageButton)findViewById(R.id.cancelactivity);
        ImageVoice=(ImageView)findViewById(R.id.recordimage);
        RecordNameedt=(EditText)findViewById(R.id.recordname);
        int a=R.drawable.recordstart;
        Recordbtn.setBackgroundResource(R.drawable.recordstart);
        RecordState=(TextView)findViewById(R.id.recordstate);
        RecordState.setText("");
        
		/*-----------设置文字动画----------*/
    	
    	final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x111 &&RecordJudge) {
					if(RecordState.getText().toString()==""){
						RecordState.setText("正在录音中.");
						ImageVoice.setBackgroundResource(R.drawable.voice1);
					}
					else if(RecordState.getText().toString()=="正在录音中."){
						RecordState.setText("正在录音中..");
						ImageVoice.setBackgroundResource(R.drawable.voice2);
					}
					else if(RecordState.getText().toString()=="正在录音中.."){
						RecordState.setText("正在录音中...");
						ImageVoice.setBackgroundResource(R.drawable.voice3);
					}
					else if(RecordState.getText().toString()=="正在录音中..."){
						RecordState.setText("正在录音中.");	
						ImageVoice.setBackgroundResource(R.drawable.voice1);
					}
				}else
					RecordState.setText("");
			}
		};
    	class MyTimerTask extends TimerTask {
			public void run() {
				// 发送消息到Handler
				Message m = new Message();
				m.what = 0x111;
				// 发送消息
				mHandler.sendMessage(m);
			}
		}
    	Timer t=new Timer();
        MyTimerTask myTimeTask=new MyTimerTask();
        t.schedule(myTimeTask,0,1000);
        //设置按钮事件
        Recordbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getRecordState(RecordJudge))
				{
					stop();
				}
				else
				{
					try {
						record();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
        Recordcancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LoveVoiceRecordActivity.this.finish();
			}
		});
    }
    //读取录音状态
    public boolean getRecordState(boolean RecordState)
    {
    	return RecordState;
    }
    private void record() throws Exception
    {
    	Recordbtn.setBackgroundResource(R.drawable.recording);//改变图标的图片
    	//读取录制文件名称
    	if(getRecordName(RecordNameedt).equals(""))
        {
    		RecordName="未命名录音";
        }
        else
        {
        	RecordName=RecordNameedt.getText().toString();
        }
    	RecordJudge=true;
        //开始录制音频
    	if(audioRecorder==null)
    	{
    		audioRecorder=new MediaRecorder();
    	}
    	
    	String path=Environment.getExternalStorageDirectory().getCanonicalFile()+"/universiting/Loving/"+RecordName+".amr";
    	//String path=this.getFilesDir().getPath().toString() + "abc.mp4";
    	File file=new File(path);
    	if(file.exists())
    		file.delete();	
    	try
    	{
    		file.createNewFile();
    		audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置输出设备为麦克风
    		audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//设置输出格式为AMR
    		audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置编码格式
    		audioRecorder.setOutputFile(file.getAbsolutePath());//设置输出文件路径
    		audioRecorder.prepare();//设置为准备状态
    		audioRecorder.start();//设置状态为开始
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();//捕获IO异常
    		throw e;
    	}
    	catch(IllegalStateException e)
    	{
    		e.printStackTrace();//捕获非法状态异常
    	}
    	//对数据库进行相应的操作
//    	MyDataBaseHelper DataBaseHelper=new MyDataBaseHelper(getBaseContext());
//    	Lovedb=DataBaseHelper.getWritableDatabase();
//    	ContentValues values=new ContentValues();
//    	values.put(DataBaseHelper.FILENAME, RecordName+".amr");
//    	values.put(DataBaseHelper.FILEPATH, path);
//    	values.put(DataBaseHelper.TYPE, "AMR");
//    	SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd    hh:mm:ss");//获得当前系统时间          
//    	values.put(DataBaseHelper.TIME, sDateFormat.format(new java.util.Date()));
    
    }
    private void stop()
    {
    	Recordbtn.setBackgroundResource(R.drawable.recordstart);
    	if(audioRecorder!=null)
    	{
    		audioRecorder.stop();
    		audioRecorder.release();
    		audioRecorder=null;
    	}
    	RecordJudge=false;
    	Toast.makeText(this, "已保存在SD卡了哦~亲……", Toast.LENGTH_LONG).show();
    }
    protected String getRecordName(EditText RecordNameedt)
    {
    	return RecordNameedt.getText().toString();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_love_voice_record, menu);
        return true;
    }
}
