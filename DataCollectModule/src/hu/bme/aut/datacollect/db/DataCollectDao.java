package hu.bme.aut.datacollect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataCollectDao extends SQLiteOpenHelper{

	 public static final int DATABASE_VERSION = 1;
	 public static final String DATABASE_NAME = "DataCollectDb.db";
	 
	 private static final String CREATE_TABLE_LOCATION = "create table if not exists locations ( " +
	 		"id integer primary key autoincrement, " +
	 		"timestamp not null, " +
	 		"latitude not null, " +
	 		"longitude not null, " +
	 		"altitude " +
	 		")";
	 
	 private static final String CREATE_TABLE_CALLS = "create table if not exists calls ( " +
			 "id integer primary key autoincrement, " + 
			 "timestamp not null, " +
			 "direction not null, " +
			 "cell_location, " +
			 "phone_number " +
			 ")";
	 
	 private static final String CREATE_TABLE_LIGHTS = "create table if not exists lights ( " +
	 		"id integer primary key autoincrement, " +
	 		"timestamp not null, " +
	 		"lx not null)";
	 
	 private static final String CREATE_TABLE_TEMPERATURES = "create table if not exists temperatures ( " +
		 		"id integer primary key autoincrement, " +
		 		"timestamp not null, " +
		 		"celsius not null)";
	 
	 private static final String CREATE_TABLE_ACCELERATIONS = "create table if not exists accelerations ( " +
		 		"id integer primary key autoincrement, " +
		 		"timestamp not null, " +
		 		"accX not null, " +
		 		"accY not null, " +
		 		"accZ not null)";
			
	private static DataCollectDao instance = null;
	 
	private DataCollectDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DataCollectDao getInstance(Context context){
		if (instance == null){
			instance = new DataCollectDao(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_TABLE_LOCATION);
		db.execSQL(CREATE_TABLE_CALLS);
		db.execSQL(CREATE_TABLE_LIGHTS);
		db.execSQL(CREATE_TABLE_TEMPERATURES);
		db.execSQL(CREATE_TABLE_ACCELERATIONS);
		
		//checking the number of rows
//		Cursor result = db.query("locations", null, null, null, null, null, null);
//		Log.d("sqlite", "Number of rows in locations table: " + result.getCount());
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	//TODO: new thread?
	public long insertCall(long timestamp, String direction, int lac, String phone){
		
		long rowId = -1;
		SQLiteDatabase db = this.getWritableDatabase();
				
		if (db != null){
			ContentValues cv = new ContentValues();
			cv.put("timestamp", timestamp);
			cv.put("direction", direction);
			cv.put("cell_location", lac);
			cv.put("phone_number", phone);
			rowId = db.insert("calls", null, cv);
			Log.d("sqlite", "Inserted row into calls, id: " + rowId);
		}
		return rowId;
	}
	
	public long insertFineLocation(long timestamp, double latitude, 
			double longitude, double altitude){
		
		long rowId = -1;
		SQLiteDatabase db = this.getWritableDatabase();		
		
		if (db != null){
			ContentValues cv = new ContentValues();
			cv.put("timestamp", timestamp);
			cv.put("latitude", latitude);
			cv.put("longitude", longitude);
			cv.put("altitude", altitude);
			rowId = db.insert("locations", null, cv);
			Log.d("sqlite", "Inserted row into locations, id: " + rowId);
		}
		return rowId;
	}
	
	public long insertLight(long timestamp, float lx){
		
		long rowId = -1;
		SQLiteDatabase db = this.getWritableDatabase();		
		
		if (db != null){
			ContentValues cv = new ContentValues();
			cv.put("timestamp", timestamp);
			cv.put("lx", lx);
			rowId = db.insert("lights", null, cv);
			Log.d("sqlite", "Inserted row into lights, id: " + rowId);
		}
		return rowId;
	}
	
	public long insertAmbientTemperature(long timestamp, float celsius){
		
		long rowId = -1;
		SQLiteDatabase db = this.getWritableDatabase();		
		
		if (db != null){
			ContentValues cv = new ContentValues();
			cv.put("timestamp", timestamp);
			cv.put("celsius", celsius);
			rowId = db.insert("temperatures", null, cv);
			Log.d("sqlite", "Inserted row into temperatures, id: " + rowId);
		}
		return rowId;
	}
	
	public long insertAcceleration(long timestamp, float accX, float accY, float accZ){
		
		long rowId = -1;
		SQLiteDatabase db = this.getWritableDatabase();		
		
		if (db != null){
			ContentValues cv = new ContentValues();
			cv.put("timestamp", timestamp);
			cv.put("accX", accX);
			cv.put("accY", accY);
			cv.put("accZ", accZ);
			rowId = db.insert("accelerations", null, cv);
			Log.d("sqlite", "Inserted row into accelerations, id: " + rowId);
		}
		return rowId;
	}

}
