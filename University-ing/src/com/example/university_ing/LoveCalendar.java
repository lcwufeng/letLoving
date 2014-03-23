package com.example.university_ing;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class LoveCalendar extends Activity {

	private ListView listViewLeft;
	private ListView listViewRight;
	private ItemsAdapter leftAdapter;
	private ItemsAdapter rightAdapter;
	SQLiteDatabase lovedb;
	int pictureNum = 0;// 记录要显示图片的总数量
	String[] filePath;// 记录左边的照片地址
	String[] fileName;// 记录右边的照片地址

	int[] leftViewsHeights;
	int[] rightViewsHeights;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_love_calendar);

		// 打开数据库
		lovedb = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir()
				.toString() + "/lovedata.db3", null);
		Cursor cursor = lovedb.rawQuery("select * from love_table", null);
		pictureNum = cursor.getCount();
		int piccount = 0;// 帮助记录数据用变量
		filePath = new String[pictureNum];
		fileName = new String[pictureNum];
		// 将数据分为左右两边分别取出名字和地址
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameCulumnIndex = cursor.getColumnIndex("filename");
			int pathCulumnIndex = cursor.getColumnIndex("filepath");
			filePath[piccount] = cursor.getString(pathCulumnIndex);
			fileName[piccount] = cursor.getString(nameCulumnIndex);
			piccount++;
		}

		listViewLeft = (ListView) findViewById(R.id.list_view_left);
		listViewRight = (ListView) findViewById(R.id.list_view_right);

		loadItems(fileName);

		listViewLeft.setOnTouchListener(touchListener);
		listViewRight.setOnTouchListener(touchListener);
		listViewLeft.setOnScrollListener(scrollListener);
		listViewRight.setOnScrollListener(scrollListener);
	}

	// Passing the touch event to the opposite list
	OnTouchListener touchListener = new OnTouchListener() {
		boolean dispatched = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.equals(listViewLeft) && !dispatched) {
				dispatched = true;
				listViewRight.dispatchTouchEvent(event);
			} else if (v.equals(listViewRight) && !dispatched) {
				dispatched = true;
				listViewLeft.dispatchTouchEvent(event);
			}

			dispatched = false;
			return false;
		}
	};

	OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView v, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			if (view.getChildAt(0) != null) {
				if (view.equals(listViewLeft)) {
					leftViewsHeights[view.getFirstVisiblePosition()] = view
							.getChildAt(0).getHeight();

					int h = 0;
					for (int i = 0; i < listViewRight.getFirstVisiblePosition(); i++) {
						h += rightViewsHeights[i];
					}

					int hi = 0;
					for (int i = 0; i < listViewLeft.getFirstVisiblePosition(); i++) {
						hi += leftViewsHeights[i];
					}

					int top = h - hi + view.getChildAt(0).getTop();
					listViewRight.setSelectionFromTop(
							listViewRight.getFirstVisiblePosition(), top);
				} else if (view.equals(listViewRight)) {
					rightViewsHeights[view.getFirstVisiblePosition()] = view
							.getChildAt(0).getHeight();

					int h = 0;
					for (int i = 0; i < listViewLeft.getFirstVisiblePosition(); i++) {
						h += leftViewsHeights[i];
					}

					int hi = 0;
					for (int i = 0; i < listViewRight.getFirstVisiblePosition(); i++) {
						hi += rightViewsHeights[i];
					}

					int top = h - hi + view.getChildAt(0).getTop();
					listViewLeft.setSelectionFromTop(
							listViewLeft.getFirstVisiblePosition(), top);
				}

			}

		}
	};

	private void loadItems(String[] fileName) {
		
		Bitmap[] leftItems; 
		Bitmap[] rightItems;
		String leftpath = null;
		String rightpath = null;
		//int tempsize = 0;
		int templeft=0,tempright=0;
		int leftCount=0,rightCount=0;
		
		if(pictureNum%2==0)//判断是否为单复数，从而定义数组
		{
			leftCount=pictureNum/2;
			rightCount=pictureNum/2;
		}else
		{
			leftCount=pictureNum/2+1;
			rightCount=pictureNum/2;
		}
		leftItems=new Bitmap[leftCount];
		rightItems=new Bitmap[rightCount];
		
		for (int i = 0; i < pictureNum; i++) {
//			if (tempsize == pictureNum)
//				tempsize = 0;
			if(templeft==leftCount)
				templeft=0;
			if(tempright==rightCount)
				tempright=0;
//			try {
//				leftpath = Environment.getExternalStorageDirectory()
//						.getCanonicalFile()
//						+ "/universiting/Camera/"
//						+ "l"
//						+ tempsize + ".png";
//				rightpath = Environment.getExternalStorageDirectory()
//						.getCanonicalFile()
//						+ "/universiting/Camera/"
//						+ "r"
//						+ tempsize + ".png";
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if(i%2==0)
			{
				leftpath=filePath[i];
				leftItems[templeft] = convertToBitmap(leftpath, 192,256);
				templeft++;
			}
			else
			{
				rightpath=filePath[i];
				rightItems[tempright] = convertToBitmap(rightpath, 192,256);
				tempright++;
			}
//			leftItems[i] = convertToBitmap(leftpath, 150,
//					getleftheight(tempsize));
//			rightItems[i] = convertToBitmap(rightpath, 150,
//					getrightheight(tempsize));
			//tempsize++;
		}

		leftAdapter = new ItemsAdapter(this, R.layout.left_item, leftItems,fileName,0);
		rightAdapter = new ItemsAdapter(this, R.layout.right_item, rightItems,fileName,1);
		listViewLeft.setAdapter(leftAdapter);
		listViewRight.setAdapter(rightAdapter);

		leftViewsHeights = new int[leftItems.length];
		rightViewsHeights = new int[rightItems.length];
	}

	// 获取SD卡的路径转换为Bitmap文件
	public Bitmap convertToBitmap(String path, int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	// 设置个性化参数,已经停止使用
	public int getleftheight(int temp) {
		int templeftheight = 0;
		switch (temp) {
		case 0:
			templeftheight = 300;
			break;
		case 1:
			templeftheight = 300;
			break;
		case 2:
			templeftheight = 120;
			break;
		case 3:
			templeftheight = 200;
			break;
		case 4:
			templeftheight = 120;
			break;
		}
		return templeftheight;
	}

	public int getrightheight(int temp) {
		int temprightheight = 0;
		switch (temp) {
		case 0:
			temprightheight = 120;
			break;
		case 1:
			temprightheight = 300;
			break;
		case 2:
			temprightheight = 300;
			break;
		case 3:
			temprightheight = 160;
			break;
		case 4:
			temprightheight = 160;
			break;
		}
		return temprightheight;
	}
}
