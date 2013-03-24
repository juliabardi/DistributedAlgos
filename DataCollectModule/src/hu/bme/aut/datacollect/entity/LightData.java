package hu.bme.aut.datacollect.entity;

import hu.bme.aut.datacollect.db.LightDao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//private static final String CREATE_TABLE_LIGHTS = "create table if not exists lights ( " +
//	"id integer primary key autoincrement, " +
//	"timestamp not null, " +
//	"lx not null)";
//
@DatabaseTable(tableName="lights", daoClass=LightDao.class)
public class LightData {
	
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField(canBeNull=false)
	private long timestamp;
	@DatabaseField(canBeNull=false)
	private float lx;
	
	public LightData(){}

	public LightData(long timestamp, float lx) {
		super();
		this.timestamp = timestamp;
		this.lx = lx;
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

	public float getLx() {
		return lx;
	}

	public void setLx(float lx) {
		this.lx = lx;
	}

	@Override
	public String toString() {
		return String.format("LightData [id=%s, timestamp=%s, lx=%s]", id,
				timestamp, lx);
	}
	
	

}
