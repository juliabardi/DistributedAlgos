package hu.bme.aut.datacollect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataCollectDao extends SQLiteOpenHelper{

	 public static final int DATABASE_VERSION = 1;
	 public static final String DATABASE_NAME = "DataCollectDb.db";
	 
	 public static final String CREATE_TABLE_LOCATION = "create table location ( " +
	 		"id primary key, " +
	 		"timestamp not null, " +
	 		"latitude not null, " +
	 		"longitude not null" +
	 		")";
	 
	public DataCollectDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LOCATION);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
