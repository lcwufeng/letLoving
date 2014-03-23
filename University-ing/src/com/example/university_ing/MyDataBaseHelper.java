package com.example.university_ing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper{

	final static String DATABASENAME="love_database.db";
	final static int VERSION=1;
	final String TABLENAME="love_table";
	final String ID="id";
	final String FILENAME="filename";
	final String FILEPATH="filepath";
	final String CREATE_TABLE_SQL="create table love_table(id string primary key autoincrement," +
			"filename varchar(25)," +
			"filepath varchar(25))";
	
	public MyDataBaseHelper(Context context,String name,int version) {
		super(context, name,null,version);
	}

	//创建表lovedata,Id是主键，FileName是文件名称,FilePath是文件的路径...实在无语，onCreat无法调用= =。。。
	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL(CREATE_TABLE_SQL);
	}

	//数据更新时的操作
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("更新从"+oldVersion+"版本到"+newVersion+"版本.");
		//版本不同时再进行编写
	}

}
