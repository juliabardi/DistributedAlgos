package hu.bme.aut.datacollect.upload;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;

public class DataUploadTask extends UploadTask {
	
	private static final long serialVersionUID = -4350943122852060571L;

	static {
		TAG = "DataCollect:DataUploadTask";
	}
	
	private String dataType;
	private Date date;
	private List<String> params;
	
	public DataUploadTask(Context context, int idRequestLog, String dataType, String reqId, String address, String port, Date date, List<String> params) {
		super(context, address, reqId, port, idRequestLog);
		this.dataType = dataType;
		this.date = date;
		this.params = params;		
	}

	@Override
	public void execute(final Callback callback) {
		super.execute(callback);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				JSONObject result;
				
				if (date == null){
					result = dataProvider.getAllDataParams(dataType, reqId, params);
				}
				else {
					result = dataProvider.getDataAfterDate(dataType, reqId, date, params);
				}
				
				if (result == null){
					errorOccured("No data available.");
					return;
				}
				responseLog.setResponseSent(System.currentTimeMillis());
				httpManager.sendPostRequest(address, result.toString(),port);				
				
			}}).start();
	}

}
