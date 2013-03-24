package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.LocationDao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//private static final String CREATE_TABLE_LOCATION = "create table if not exists locations ( " +
//	"id integer primary key autoincrement, " +
//	"timestamp not null, " +
//	"latitude not null, " +
//	"longitude not null, " +
//	"altitude " +
//	")";
//

@DatabaseTable(tableName="locations", daoClass=LocationDao.class)
public class LocationData {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private double latitude;
	@DatabaseField(canBeNull=false)
	private double longitude;
	@DatabaseField(canBeNull=false)
	private double altitude;

	public LocationData(){}

	public LocationData(long timestamp, double latitude, double longitude,
			double altitude) {
		super();
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public int getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return String
				.format("LocationData [id=%s, timestamp=%s, latitude=%s, longitude=%s, altitude=%s]",
						id, timestamp, latitude, longitude, altitude);
	}
	
	
}
