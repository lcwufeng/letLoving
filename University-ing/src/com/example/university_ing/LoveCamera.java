package com.example.university_ing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class LoveCamera extends Activity {

	SurfaceView sView;
	SurfaceHolder surfaceHolder;
	int screenWidth, screenHeight;
	ImageButton Takephoto;
	Camera camera;
	boolean isPreview = false;
	SQLiteDatabase lovedb;// 数据库操作

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_love_camera);

		// 打开数据库
		lovedb = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir()
				.toString() + "/lovedata.db3", null);

		Takephoto = (ImageButton) findViewById(R.id.takephoto);
		Takephoto.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Takephoto.setBackgroundResource(R.drawable.takephototouch);
				return false;
			}
		});

		// 获取窗口管理器
		WindowManager wm = getWindowManager();
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		// 获取屏幕的宽和高
		display.getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		// 获取界面中SurfaceView组件
		sView = (SurfaceView) findViewById(R.id.surfaceview);
		// 设置该Surface不需要自己维护缓冲区
		sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 获得SurfaceView的SurfaceHolder
		surfaceHolder = sView.getHolder();
		// 为surfaceHolder添加一个回调监听器
		surfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 打开摄像头
				initCamera();
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// 如果camera不为null ,释放摄像头
				if (camera != null) {
					if (isPreview)
						camera.stopPreview();
					camera.release();
					camera = null;
				}
			}
		});
	}

	@TargetApi(9)
	private void initCamera() {
		if (!isPreview) {
			// 此处默认打开后置摄像头。
			// 通过传入参数可以打开前置摄像头
			camera = Camera.open(0); // ①
			camera.setDisplayOrientation(90);
		}
		if (camera != null && !isPreview) {
			try {
				Camera.Parameters parameters = camera.getParameters();
				// 设置预览照片的大小
				parameters.setPreviewSize(screenWidth, screenHeight);
				// 设置预览照片时每秒显示多少帧的最小值和最大值
				parameters.setPreviewFpsRange(4, 10);
				// 设置图片格式
				parameters.setPictureFormat(ImageFormat.JPEG);
				// 设置JPG照片的质量
				parameters.set("jpeg-quality", 85);
				parameters.set("orientation", "portrait");
				parameters.set("rotation", 180);
				// 设置照片的大小
				parameters.setPictureSize(screenWidth, screenHeight);
				// 通过SurfaceView显示取景画面
				camera.setPreviewDisplay(surfaceHolder); // ②
				// 开始预览
				camera.startPreview(); // ③
			} catch (Exception e) {
				e.printStackTrace();
			}
			isPreview = true;
		}
	}

	public void capture(View source) {
		if (camera != null) {
			// 控制摄像头自动对焦后才拍照
			camera.autoFocus(autoFocusCallback); // ④
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		// 当自动对焦时激发该方法
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				// takePicture()方法需要传入3个监听器参数
				// 第1个监听器：当用户按下快门时激发该监听器
				// 第2个监听器：当相机获取原始照片时激发该监听器
				// 第3个监听器：当相机获取JPG照片时激发该监听器
				camera.takePicture(new ShutterCallback() {
					public void onShutter() {
						// 按下快门瞬间会执行此处代码
					}
				}, new PictureCallback() {
					public void onPictureTaken(byte[] data, Camera c) {
						// 此处代码可以决定是否需要保存原始照片信息
					}
				}, myJpegCallback); // ⑤
			}
		}
	};

	PictureCallback myJpegCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 根据拍照所得的数据创建位图
			final Bitmap bmTemp = BitmapFactory.decodeByteArray(data, 0,
					data.length);
	        
			//判断图片是横屏还是竖屏拍出来的，如果是横屏旋转90，如果是竖屏旋转180，因为现在强制竖屏，所以无法使用。
			Bitmap bMapRotate;
			Configuration config=getResources().getConfiguration();
			if(config.orientation==1)
			{//竖拍
				Matrix matrix=new Matrix();
				matrix.reset();
				matrix.postRotate(90);
				bMapRotate=Bitmap.createBitmap(bmTemp,0,0,bmTemp.getWidth(),bmTemp.getHeight(),matrix,true);
			}else
			{//横拍
				Matrix matrix=new Matrix();
				matrix.reset();
				matrix.postRotate(180);
				bMapRotate=Bitmap.createBitmap(bmTemp,0,0,bmTemp.getWidth(),bmTemp.getHeight(),matrix,true);
			}
			final Bitmap bm=bMapRotate;//翻转以后赋值
			
			// 加载/layout/Photosave.xml文件对应的布局资源
			View saveDialog = getLayoutInflater().inflate(R.layout.photo_save,
					null);
			final EditText photoName = (EditText) saveDialog
					.findViewById(R.id.phone_name);
			// 获取saveDialog对话框上的ImageView组件
			ImageView show = (ImageView) saveDialog.findViewById(R.id.show);
			// 显示刚刚拍得的照片
			show.setImageBitmap(bm);
			// 使用对话框显示saveDialog组件
			new AlertDialog.Builder(LoveCamera.this).setView(saveDialog)
					.setPositiveButton("保存", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 创建一个位于SD卡上的文件
							String path = null;
							if (photoName.getText().toString() == ""
									|| photoName.getText().toString() == "未命名图片") {
								Toast.makeText(LoveCamera.this, "请输入图片名称！",
										Toast.LENGTH_LONG).show();
							} else {
								try {
									path = Environment
											.getExternalStorageDirectory()
											.getCanonicalFile()
											+ "/universiting/Camera/"
											+ photoName.getText().toString()
											+ ".jpg";
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								File file = new File(path);
								FileOutputStream outStream = null;
								String insertData = "insert into love_table (filename,filepath) values ('"
										+ photoName.getText().toString() + "','" + path + "')";
								lovedb.execSQL(insertData);
								
								try {
									// 打开指定文件对应的输出流
									outStream = new FileOutputStream(file);
									// 把位图输出到指定文件中
									bm.compress(CompressFormat.JPEG, 100,
											outStream);
									bm.recycle();
									outStream.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Toast.makeText(LoveCamera.this,
										"已保存在SD卡了哦~亲……", Toast.LENGTH_LONG)
										.show();
							}
						}
					}).setNegativeButton("取消", null).show();
			// 重新浏览
			camera.stopPreview();
			camera.startPreview();
			isPreview = true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_love_take_photo, menu);
		return true;
	}
}
