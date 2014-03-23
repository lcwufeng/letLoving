package com.example.university_ing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class LoveVoiceGroup extends ListActivity {

	//ListView audioList;// 显示手机SD卡中所有可用音频资源
	ContentResolver resolver;// 用于查询SD卡内的音频文件
	Uri nowPlaying;//当前播放音频的地址
	private File mRecAudioPath;        // 录制的音频文件路徑
	private List<String> mMusicList = new ArrayList<String>();// 录音文件列表
	private MediaPlayer MusicPlay;
	private ImageButton[] ibMusic;
	private File[] CurrentPlay;
	private boolean[] CurrentNumber;
	private int Current;//记录当前的音乐位置
	private ImageButton PlayAudio,SaveAudio;
	private String GroupFileName=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        setContentView(R.layout.activity_love_voice_group);
        PlayAudio=(ImageButton)findViewById(R.id.playaudio);
        SaveAudio=(ImageButton)findViewById(R.id.saveaudio);
        //audioList=(ListView)findViewById(R.id.musiclist);
        
        CurrentPlay=new File[7];
        CurrentNumber=new boolean[7];
        Current=0;
        for(int i=0;i<7;i++)
        {
        	CurrentNumber[i]=false;
        }
        
        //初始化ImageButton数组
        ibMusic=new ImageButton[7];
        ibMusic[0]=(ImageButton)findViewById(R.id.music0101);
    	ibMusic[1]=(ImageButton)findViewById(R.id.music0102);
    	ibMusic[2]=(ImageButton)findViewById(R.id.music0103);
    	ibMusic[3]=(ImageButton)findViewById(R.id.music0201);
    	ibMusic[4]=(ImageButton)findViewById(R.id.music0202);
    	ibMusic[5]=(ImageButton)findViewById(R.id.music0203);
    	ibMusic[6]=(ImageButton)findViewById(R.id.music0204);
    	
        MusicPlay=new MediaPlayer();
        RefreshList();
		
		/*--------设置按钮的监听事件---------*/
		PlayAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(CurrentNumber[0]==true)
				{
					for(int i=0;i<=Current;i++)
					{
						MusicPlay=new MediaPlayer();
						try
						{
							try {
								MusicPlay.setDataSource(CurrentPlay[i].getAbsolutePath());
							} catch (SecurityException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								MusicPlay.prepare();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							MusicPlay.start();
							MusicPlay.setOnCompletionListener(new OnCompletionListener()
							{
								public void onCompletion(MediaPlayer arg0)
								{
									if(MusicPlay!=null)
									{
										MusicPlay.stop();
										MusicPlay.release();
										MusicPlay=null;
									}
								}
							});
						}
						catch(IllegalArgumentException e)
						{
							e.printStackTrace();
						}
						catch(IllegalStateException e)
						{
							e.printStackTrace();
						}
					}
				}else
				{
					Toast.makeText(LoveVoiceGroup.this, "还没有装载录音哦亲~O(∩_∩)O~", Toast.LENGTH_LONG).show();
				}		
			}
			
		});
		SaveAudio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(
						R.layout.dialog_voice_group,
						(ViewGroup) findViewById(R.id.dialog));
				new AlertDialog.Builder(LoveVoiceGroup.this)
						.setTitle("输入合成音频的名字").setView(layout)
						.setPositiveButton("确定", new OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								EditText InputName = (EditText) layout
										.findViewById(R.id.etname);
								if (InputName.getText().toString() != "") {
									GroupFileName = InputName.getText()
											.toString();
								} else {
									GroupFileName = "未命名合成录音";
								}
								String OutputPath = null;// 为了函数需要得设一个空值
								try {
									OutputPath = Environment
											.getExternalStorageDirectory()
											.getCanonicalFile()
											+ "/universiting/Loving/"
											+ GroupFileName + ".amr";
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String[] AudioPath = new String[Current + 1];
								// 创建一个组合音频的路径集合
								for (int i = 0; i <= Current; i++)
									AudioPath[i] = CurrentPlay[i]
											.getAbsolutePath();
								UniteAMRFile(AudioPath, OutputPath);
								RefreshList();
							}
						}).setNegativeButton("取消", null).show();
			}
		});
        
    }
    /*---------------设置ListActivity的监听事件-----------------*/
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File ClickFile=new File(mRecAudioPath.getAbsolutePath()+File.separator+mMusicList.get(position));
		for(int i=0;i<7;i++)
    	{
    		if(CurrentNumber[i]==false)
    		{
    			Current=i;
    			CurrentNumber[i]=true;
    			break;
    		}
    	}
		CurrentPlay[Current]=ClickFile;
    	setImageButtonBackground(ibMusic[Current]);
		super.onListItemClick(l, v, position, id);
	}
	/* 播放列表 */
	public void musicList() {
		// 取得指定位置的文件设置显示到播放列表
		File home = mRecAudioPath;
		if (home.listFiles(new MusicFilter()).length > 0) {
			for (File file : home.listFiles(new MusicFilter())) {
				mMusicList.add(file.getName());
			}
			ArrayAdapter<String> musicList = new ArrayAdapter<String>(
					LoveVoiceGroup.this, android.R.layout.simple_list_item_1, mMusicList);
			setListAdapter(musicList);
			Toast.makeText(this, "更新列表已完成O(∩_∩)O~", Toast.LENGTH_LONG).show(); 
		}
	}
	
	/* 过滤文件类型 */
	class MusicFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".amr"));
		}
	}
	public void setImageButtonBackground(ImageButton ib)
	{
		Random random=new Random();
		switch(random.nextInt(6))
		{
		case 0:ib.setBackgroundResource(R.drawable.music1);break;
		case 1:ib.setBackgroundResource(R.drawable.music2);break;
		case 2:ib.setBackgroundResource(R.drawable.music3);break;
		case 3:ib.setBackgroundResource(R.drawable.music4);break;
		case 4:ib.setBackgroundResource(R.drawable.music5);break;
		case 5:ib.setBackgroundResource(R.drawable.music6);break;
		case 6:ib.setBackgroundResource(R.drawable.music7);break;
		}
	}


	/*
	 * 将两个amr格式音频文件合并为1个
	 * 注意:amr格式的头文件为6个字节的长度
	 * @param partsPaths各部分路径
	 * @param unitedFilePath合并后路径
	 */ 
	public void UniteAMRFile(String[] partsPaths, String unitedFilePath) {
		try {
			File unitedFile = new File(unitedFilePath);
			FileOutputStream fos = new FileOutputStream(unitedFile);
			RandomAccessFile ra = null;
			for (int i = 0; i < partsPaths.length; i++) {
				ra = new RandomAccessFile(partsPaths[i], "r");
				if (i != 0) {
					ra.seek(6);
				}
				byte[] buffer = new byte[1024 * 8];
				int len = 0;
				while ((len = ra.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
			}
			ra.close();
			fos.close();
			Toast.makeText(LoveVoiceGroup.this, "文件已经创建", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
		}
	}
	
	/*------------更新录制的音频到音频列表-------------*/
	public void RefreshList()
	{
		/* 检测是否存在SD卡 */
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			//mRecAudioPath = Environment.getExternalStorageDirectory();// 得到SD卡得路径
			mRecAudioPath=new File(Environment.getExternalStorageDirectory()+"/universiting/Loving/");
			musicList();// 更新所有录音文件到List中
		} else {
			Toast.makeText(LoveVoiceGroup.this, "没有SD卡", Toast.LENGTH_LONG).show();
		}
	}

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_love_voice_group, menu);
        return true;
    }
}
