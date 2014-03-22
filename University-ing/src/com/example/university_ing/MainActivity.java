package com.example.university_ing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	ImageButton LoveVoicebtn;
	ImageButton LoveCamerabtn;
	ImageButton LoveCalendarbtn;
	ImageButton LoveAboutusbtn;
	MyDataBaseHelper lovedbHelper;
	SQLiteDatabase lovedb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 创建数据库
		lovedb = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir()
				.toString() + "/lovedata.db3", null);
		try {
			Cursor cursor = lovedb.rawQuery("select * from love_table", null);
		} catch (SQLiteException se) {
			lovedb.execSQL("create table love_table(id integer primary key autoincrement,"
					+ "filename varchar(25)," + "filepath varchar(25))");
		}

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_main);

		/*------启动LoveVoiceActivity------*/
		LoveVoicebtn = (ImageButton) findViewById(R.id.lovevoice);
		LoveCamerabtn = (ImageButton) findViewById(R.id.lovecamera);
		LoveCalendarbtn = (ImageButton) findViewById(R.id.lovecalendar);
		LoveAboutusbtn = (ImageButton) findViewById(R.id.loveus);
		LoveVoicebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this,
						LoveVoiceActivity.class);
				MainActivity.this.startActivity(i);

				// MainActivity.this.finish();
			}
		});
		// 设置接触事件，触碰时改变图片颜色
		LoveVoicebtn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LoveVoicebtn.setBackgroundResource(R.drawable.lovevoicetouch);
				return false;
			}
		});

		LoveCamerabtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//调用系统相机
				openImageCamera();
			}
		});
		LoveCamerabtn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LoveCamerabtn.setBackgroundResource(R.drawable.cameratouch);
				return false;
			}
		});

		LoveCalendarbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, LoveCalendar.class);
				MainActivity.this.startActivity(i);
			}
		});
		LoveCalendarbtn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LoveCalendarbtn
						.setBackgroundResource(R.drawable.lovecalendartouch);
				return false;
			}
		});
		LoveAboutusbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, AboutusActivity.class);
				MainActivity.this.startActivity(i);
			}
		});
		LoveAboutusbtn.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LoveAboutusbtn.setBackgroundResource(R.drawable.aboutustouch);
				return false;
			}
		});
	}

	// 释放内存函数
	public void releaseBitmap(ImageButton imgButton) {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private File mImageFile;
	private Uri mImageUri;

	private void openImageCamera() {
		try {
			String filename = new SimpleDateFormat("yyyyMMddKms")
					.format(new Date()) + ".jpg";
			mImageFile = new File(getPicFile(), filename);
			mImageUri = Uri.fromFile(mImageFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(intent, 10000);
		} catch (Exception e) {
			Toast.makeText(this, "储存失败！", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10000 && resultCode == RESULT_OK) {

			getPic(mImageUri);

		}
	}

	private File mFile;

	public void getPic(Uri uri) {
		mFile = null;
		mImageUri = uri;
		mFile = new File(mImageUri.getPath());
		try {
			mFile = compressAndTransfer(mFile, getPicFile());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		if (mFile == null) {
			Toast.makeText(MainActivity.this, "获取图片失败，请重新获得！ ",
					Toast.LENGTH_SHORT).show();
			return;
		}
		final Bitmap bm = BitmapFactory.decodeFile(mFile.getPath());
		// 加载/layout/Photosave.xml文件对应的布局资源
		View saveDialog = getLayoutInflater()
				.inflate(R.layout.photo_save, null);
		final EditText photoName = (EditText) saveDialog
				.findViewById(R.id.phone_name);
		// 获取saveDialog对话框上的ImageView组件
		ImageView show = (ImageView) saveDialog.findViewById(R.id.show);
		// 显示刚刚拍得的照片
		show.setImageBitmap(bm);
		// 使用对话框显示saveDialog组件
		new AlertDialog.Builder(MainActivity.this).setView(saveDialog)
				.setPositiveButton("保存", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 创建一个位于SD卡上的文件
						/*if (photoName.getText().toString() == ""
								|| photoName.getText().toString() == "未命名图片") {
							Toast.makeText(MainActivity.this, "请输入图片名称！",
									Toast.LENGTH_LONG).show();
						} else {
							mFile.renameTo(new File(getPicFile()
									.getAbsolutePath()
									+ "/"
									+ photoName.getText().toString().trim()
									+ ".jpg"));
							String insertData = "insert into love_table (filename,filepath) values ('"
									+ photoName.getText().toString()
									+ "','"
									+ mFile.getAbsolutePath() + "')";
							lovedb.execSQL(insertData);*/
						// 创建一个位于SD卡上的文件
						String path = null;
						if (photoName.getText().toString() == ""
								|| photoName.getText().toString() == "未命名图片") {
							Toast.makeText(MainActivity.this, "请输入图片名称！",
									Toast.LENGTH_LONG).show();
						} else {
							try {
								mFile.renameTo(new File(getPicFile()
										.getAbsolutePath()
										+ "/"
										+ photoName.getText().toString().trim()
										+ ".jpg"));
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
							String insertData = "insert into love_table (filename,filepath) values ('"
									+ photoName.getText().toString() + "','" + path + "')";
							lovedb.execSQL(insertData);

							Toast.makeText(MainActivity.this, "已保存在SD卡了哦~亲……",
									Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton("取消", null).show();
	}

	private File getPicFile() {
		File picFile = new File(Environment.getExternalStorageDirectory()
				.toString() + "/universiting/Camera/");

		if (!picFile.exists()) {
			picFile.mkdirs();
		}
		return picFile;
	}

	private File compressAndTransfer(File sourceFile, File tempFile)
			throws FileNotFoundException {

		int desiredWidth = 480;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), options);
		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;
		if (desiredWidth > srcWidth)
			desiredWidth = srcWidth;
		int inSampleSize = 1;
		while (srcWidth / 2 > desiredWidth) {
			srcWidth /= 2;
			srcHeight /= 2;
			inSampleSize *= 2;
		}

		float desiredScale = (float) desiredWidth / srcWidth;

		// Decode with inSampleSize
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = inSampleSize;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(
				sourceFile.getAbsolutePath(), options);

		/**
		 * 获取图片的旋转角度
		 */
		int degree = readPictureDegree(sourceFile.getAbsolutePath());

		// 旋转图片
		sampledSrcBitmap = rotaingImageView(degree, sampledSrcBitmap);

		// Resize
		Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
				sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
				matrix, true);
		sampledSrcBitmap = null;

		// Save

		File retFile = new File(tempFile, sourceFile.getName());
		FileOutputStream out = new FileOutputStream(retFile);
		scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		scaledBitmap.recycle();
		scaledBitmap = null;
		return retFile;

	}

	/**
	 * 旋转图片
	 * 
	 * @param degree
	 * @param bitmap
	 * @return Bitmap
	 */
	private Bitmap rotaingImageView(int degree, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree 旋转的角度
	 */
	private int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
