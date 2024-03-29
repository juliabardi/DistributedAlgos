package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="locations", daoClass=DaoBase.class)
public class LocationData extends IData {
	
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
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "latitude", "longitude", "altitude");
	}
	
	@Override
	public Map<String,String> getValues(){
		
		Map<String,String> values = new HashMap<String,String>();
		values.put("id", String.valueOf(id));
		values.put("timestamp", String.valueOf(timestamp));
		values.put("latitude", String.valueOf(latitude));
		values.put("longitude", String.valueOf(longitude));
		values.put("altitude", String.valueOf(altitude));
		return values;
	}
}
