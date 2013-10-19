package hu.bme.aut.datacollect.upload;

import hu.bme.aut.datacollect.db.DataProvider;
import hu.bme.aut.datacollect.db.IDataProvider;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class DataUploadTask extends UploadTask {
	
	private static final long serialVersionUID = -4350943122852060571L;

	static {
		TAG = "DataCollect:DataUploadTask";
	}
	
	private String dataType;
	private Date date;
	private List<String> params;
	
	private IDataProvider dataProvider;
	
	public DataUploadTask(Context context, String dataType, String reqId, String address){		
		super(address, reqId);
		this.dataType = dataType;
		this.dataProvider = new DataProvider(context);
	}
	
	public DataUploadTask(Context context, String dataType, String reqId, String address, List<String> params) {
		super(address, reqId);
		this.dataType = dataType;
		this.params = params;
		this.dataProvider = new DataProvider(context);
	}
	
	public DataUploadTask(Context context, String dataType, String reqId, String address, Date date) {
		super(address, reqId);
		this.dataType = dataType;
		this.date = date;
		this.dataProvider = new DataProvider(context);
	}
	
	public DataUploadTask(Context context, String dataType, String reqId, String address, Date date, List<String> params) {
		super(address, reqId);
		this.dataType = dataType;
		this.date = date;
		this.params = params;
		this.dataProvider = new DataProvider(context);
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
					mCallback.onFailure();
					return;
				}
				
				httpManager.sendPostRequest(address, result.toString());				
				
			}}).start();
	}
	
	@Override
	protected void cleanup(){
		try {
			this.dataProvider.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
