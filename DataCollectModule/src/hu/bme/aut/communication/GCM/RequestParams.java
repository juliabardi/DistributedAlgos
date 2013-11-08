package hu.bme.aut.communication.GCM;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.datacollect.utils.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Storing the request parameters (that come from GCM MessageHandler).
 * This class is responsible for parsing and returning the data that is needed for a request to be completed.
 * RequestParams is Serializable, because we need to pass it between Activities.
 * It is much easier to pass an object than every param one by one.
 * @author Juli
 *
 */
public class RequestParams implements Serializable {

	private static final long serialVersionUID = -4482909114008327224L;
	
	private static final String TAG = "DataCollect:RequestParams";
	
	private String time;
	private String date;
	private String recurrence;
	private JSONArray columns;
	
	private String reqId;
	private String port;
	private String protocol;
	private String ip;
	private String dataType;
	
	private String script;
	
	private String width;
	private String height;
	private String times;	

	private int idRequestLog;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}

	public JSONArray getColumns() {
		return columns;
	}

	public void setColumns(JSONArray columns) {
		this.columns = columns;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public int getIdRequestLog() {
		return idRequestLog;
	}

	public void setIdRequestLog(int idRequestLog) {
		this.idRequestLog = idRequestLog;
	}
	
	public String getAddress() {
		return protocol + "://"+ip+":"+port+"/"+ Constants.OFFER_REPLY;
	}
	
	public List<String> getParams() {
		if (columns != null){
			return Utils.convertJSONArrayToList(columns);
		}
		return null;
	}
	
	public String getJoinedCols() {
		if (getParams() != null){
			return Utils.convertListToCsv(getParams());
		}
		return null;
	}
	
	public void setJoinedCols(String joinedCols) {
		if (joinedCols != null){
			this.columns = Utils.convertListToJSONArray(Utils.convertCsvToList(joinedCols));
		}
	}
	
	public int getRecurrenceInt() {
		return parseInt(recurrence);
	}
	
	public int getTimesInt() {
		return parseInt(times);
	}
	
	public int getWidthInt() {
		return parseInt(width);
	}
	
	public int getHeightInt() {
		return parseInt(height);
	}
	
	public int parseInt(String input) {
		
		if (input != null) {
			try {
				return Integer.parseInt(input);
			} catch (NumberFormatException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return -1;
	}
	
	public Date getQueryDate() {
		
		Date queryDate = null;
		if (date != null){
			queryDate = this.parseDate(date);
		}
		if (time != null){
			queryDate = this.subtractSeconds(time);
		}
		return queryDate;
	}
	
	//trying to parse param date in yyyy-MM-dd format
	@SuppressLint("SimpleDateFormat") 
	private Date parseDate(String d){
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(d);
		} catch (ParseException e) {
			Log.e(TAG, "Error parsing date in (yyyy-MM-dd) format: " + d);
			return null;
		}
	}
	
	private Date subtractSeconds(String t){
		
		try {
			int timeInt = Integer.parseInt(t);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, timeInt*(-1));
			return cal.getTime();
		} catch (NumberFormatException e) {
			Log.e(TAG, "Error parsing time to int: " + t);
			return null;
		}
	}

	@Override
	public String toString() {
		return String
				.format("RequestParams [time=%s, date=%s, recurrence=%s, columns=%s, reqId=%s, port=%s, protocol=%s, ip=%s, dataType=%s, script=%s, width=%s, height=%s, times=%s, idRequestLog=%s]",
						time, date, recurrence, columns, reqId, port, protocol,
						ip, dataType, script, width, height, times,
						idRequestLog);
	}
	
}
