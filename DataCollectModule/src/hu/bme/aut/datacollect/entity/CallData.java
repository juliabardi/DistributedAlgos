package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.CallDao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="calls", daoClass=CallDao.class)
public class CallData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private String direction;
	
	public CallData(){}

	public CallData(long timestamp, String direction) {
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
	
	
}
