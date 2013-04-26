package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.DaoBase;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="packages", daoClass=DaoBase.class)
public class PackageData {

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private String action;
	@DatabaseField(canBeNull=true)
	private int uid;
	
	public PackageData(){}
		
	public PackageData(long timestamp, String action, int uid) {
		super();
		this.timestamp = timestamp;
		this.action = action;
		this.uid = uid;
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return String.format(
				"PackageData [id=%s, timestamp=%s, action=%s, uid=%s]", id,
				timestamp, action, uid);
	}
		
}
