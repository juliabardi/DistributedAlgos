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
	
	private String name;
	private int reqId;
	private Date date;
	private List<String> params;
	private String address;
	
	private IDataProvider dataProvider;
	
	public DataUploadTask(Context context, String name, int reqId, String address){		
		super();
		this.name = name;
		this.dataProvider = new DataProvider(context);
		this.address = address;
	}
	
	public DataUploadTask(Context context, String name, int reqId, String address, List<String> params) {
		super();
		this.name = name;
		this.params = params;
		this.dataProvider = new DataProvider(context);
		this.address = address;
	}
	
	public DataUploadTask(Context context, String name, int reqId, String address, Date date) {
		super();
		this.name = name;
		this.date = date;
		this.dataProvider = new DataProvider(context);
		this.address = address;
	}
	
	public DataUploadTask(Context context, String name, int reqId, String address, Date date, List<String> params) {
		super();
		this.name = name;
		this.date = date;
		this.params = params;
		this.dataProvider = new DataProvider(context);
		this.address = address;
	}

	@Override
	public void execute(final Callback callback) {
		super.execute(callback);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				JSONObject result;
				
				if (date == null){
					result = dataProvider.getAllDataParams(name, reqId, params);
				}
				else {
					result = dataProvider.getDataAfterDate(name, reqId, date, params);
				}
				
				if (result == null){
					mCallback.onFailure();
					return;
				}
				
				//adding http:// and the port number for now
				String fullAddress = String.format("http://%s:3001", address);
				httpManager.sendPostRequest(fullAddress, result.toString());				
				
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
