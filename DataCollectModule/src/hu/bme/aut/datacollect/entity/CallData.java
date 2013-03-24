package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.CallDao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//private static final String CREATE_TABLE_CALLS = "create table if not exists calls ( " +
//"id integer primary key autoincrement, " + 
//"timestamp not null, " +
//"direction not null, " +
//"cellLocation " +
//")";
//
@DatabaseTable(tableName="calls", daoClass=CallDao.class)
public class CallData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private String direction;
	@DatabaseField
	private int cellLocation;
	
	public CallData(){}

	public CallData(long timestamp, String direction, int cellLocation) {
		super();
		this.timestamp = timestamp;
		this.direction = direction;
		this.cellLocation = cellLocation;
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

	public int getCellLocation() {
		return cellLocation;
	}

	public void setCellLocation(int cellLocation) {
		this.cellLocation = cellLocation;
	}

	@Override
	public String toString() {
		return String.format(
				"CallData [id=%s, timestamp=%s, direction=%s, cellLocation=%s]",
				id, timestamp, direction, cellLocation);
	}
	
	
}
