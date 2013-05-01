package hu.bme.aut.datacollect.entity;

import java.util.Arrays;
import java.util.List;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="sms", daoClass=DaoBase.class)
public class SmsData extends IData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private String direction;
	
	public SmsData(){}

	public SmsData(long timestamp, String direction) {
		super();
		this.timestamp = timestamp;
		this.direction = direction;
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return String.format(
				"CallData [id=%s, timestamp=%s, direction=%s]",
				id, timestamp, direction);
	}
	
	@Override
	public List<String> getParams(){
		return Arrays.asList("id", "timestamp", "direction");
	}
	
	@Override
	public List<String> getValues(){
		return Arrays.asList(String.valueOf(id), String.valueOf(timestamp), 
				direction);
	}
}
