package com.example.university_ing;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class ItemsAdapter extends ArrayAdapter<Bitmap>{
	Context context; 
    LayoutInflater inflater;
    int layoutResourceId;
    float imageWidth;
    String[] fileName;
    int nameCount=0;
    int Version;
    
    //原来标记为List<Bitmap>的，后来修改了一下,暂时加了一个dataTime ,version是判断左边的还是右边的，左边是0右边是1.
    public ItemsAdapter(Context context, int layoutResourceId, Bitmap[] items,String[] filename,int version) {
        super(context, layoutResourceId,items);      
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        fileName=filename;
        Version=version;
        if(Version==0)
        	nameCount=0;
        else
        	nameCount=1;
        
        float width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        float margin = (int)convertDpToPixel(10f, (Activity)context);
        // two images, three margins of 10dips
		imageWidth = ((width - (3 * margin)) / 2);
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout row = (RelativeLayout) convertView;
        ItemHolder holder;
        Bitmap item = this.getItem(position);
        //Integer item = getItem(position);
        
		if (row == null) {
			holder = new ItemHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (RelativeLayout) inflater.inflate(layoutResourceId, parent, false);
            ImageView itemImage = (ImageView)row.findViewById(R.id.item_image);//建立Bitmap然后进行获取
            TextView textDate=(TextView)row.findViewById(R.id.item_text);//新建文本，进行获取和修改
            //从数据库中读取照片信息
            textDate.setText(fileName[nameCount]);
            nameCount=nameCount+2;
//            if(Version==0)
//            {//左边
//            	if(nameCount>fileName.length)
//            		nameCount=0;
//            	textDate.setText(fileName[nameCount]);
//            }else
//            {
//            	if(nameCount>fileName.length)
//            		nameCount=1;
//            	textDate.setText(fileName[nameCount]);
//            }
            
            
            
//            if(true)
//            {
//            	textDate.setText("3333");
//            }else
//            {
//            	textDate.setText("5555");
//            }
			holder.itemImage = itemImage;
			holder.textView=textDate;
		} else {
			holder = (ItemHolder) row.getTag();
		}
		
		row.setTag(holder);
		setImageBitmap(item, holder.itemImage);
        return row;
    }

    public static class ItemHolder
    {
    	ImageView itemImage;
    	TextView textView;
    }
	
    // resize the image proportionately so it fits the entire space
	private void setImageBitmap(Bitmap item, ImageView imageView){
		//Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), item);
		float i = ((float) imageWidth) / ((float) item.getWidth());
		float imageHeight = i * (item.getHeight());
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
		//params.height = (int) imageHeight;
		//params.width = (int) imageWidth;
		//imageView.setLayoutParams(params);
		imageView.setImageBitmap(item);
		//imageView.setImageResource(item);
	}
	
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi/160f);
	    return px;
	}

}
