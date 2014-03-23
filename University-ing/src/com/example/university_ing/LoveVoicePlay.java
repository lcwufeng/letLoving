package com.example.university_ing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LoveVoicePlay extends ListActivity {
	ContentResolver resolver;// 用于查询SD卡内的音频文件
	Uri nowPlaying;//当前播放音频的地址
	private File mRecAudioPath;        // 录制的音频文件路徑
	private List<String> mMusicList = new ArrayList<String>();// 录音文件列表
	private MediaPlayer MusicPlay;
	ImageButton Musicbtn;
	TextView MusicName;
	File CurrentFile;
	protected boolean isPlaying=false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
        setContentView(R.layout.activity_love_voice_play);
        Musicbtn=(ImageButton)findViewById(R.id.musicbtn);
        Musicbtn.setBackgroundResource(R.drawable.musicplaybtn);
        MusicName=(TextView)findViewById(R.id.musicname);
        RefreshList();
        
        Musicbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CurrentFile != null) {
					if (isPlaying == false) {
						Musicbtn.setBackgroundResource(R.drawable.musicstopbtn);
						isPlaying = true;
						if (MusicPlay == null)
							MusicPlay = new MediaPlayer();
						try {
							try {
								MusicPlay.setDataSource(CurrentFile
										.getAbsolutePath());
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
							MusicPlay
									.setOnCompletionListener(new OnCompletionListener() {
										public void onCompletion(
												MediaPlayer arg0) {
											if (MusicPlay != null) {
												MusicName.setText("~请选定录音~");
												Musicbtn.setBackgroundResource(R.drawable.musicplaybtn);
												MusicPlay.stop();
												isPlaying = false;
												MusicPlay.release();
												MusicPlay = null;
											}
										}
									});
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						}
					} else {
						if (MusicPlay != null) {
							Musicbtn.setBackgroundResource(R.drawable.musicplaybtn);
							MusicName.setText("~请选定录音~");
							MusicPlay.stop();
							isPlaying = false;
							MusicPlay.release();
							MusicPlay = null;
						}
					}
				}else
				{
					Toast.makeText(LoveVoicePlay.this, "需要先点击列表的录音哦，亲~", Toast.LENGTH_LONG).show();
				}
			}
		});
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
			Toast.makeText(LoveVoicePlay.this, "没有SD卡", Toast.LENGTH_LONG).show();
		}
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
					LoveVoicePlay.this, android.R.layout.simple_list_item_1, mMusicList);
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
	/*---------------设置ListActivity的监听事件-----------------*/
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CurrentFile=new File(mRecAudioPath.getAbsolutePath()+File.separator+mMusicList.get(position));	
		MusicName.setText(CurrentFile.getName());
		super.onListItemClick(l, v, position, id);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_love_voice_play, menu);
        return true;
    }
}
