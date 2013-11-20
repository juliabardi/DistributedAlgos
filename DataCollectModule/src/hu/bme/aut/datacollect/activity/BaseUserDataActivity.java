package hu.bme.aut.datacollect.activity;

import org.json.JSONException;
import org.json.JSONObject;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.communication.NodeCommunicationIntentService;
import hu.bme.aut.communication.utils.HttpParamsUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public abstract class BaseUserDataActivity extends Activity{
	protected ProgressDialog progressDialog = null;
	protected UserResultReceiver theReceiver = null;
	protected String id;
	protected String passwd;
	public static boolean showProgress = false; 

	protected void sendMessage(String msgType){
		Intent intent = new Intent(this, NodeCommunicationIntentService.class);
		intent.putExtra(NodeCommunicationIntentService.URL, HttpParamsUtils.getFullNodeAddressProtocol(this, Constants.HTTPS) 
				+ msgType);
		intent.putExtra(Constants.MESSAGE_TYPE, msgType);
		intent.putExtra(Constants.ITEM_NAME, msgType);
		intent.putExtra("resReceiver", theReceiver);			
		intent.putExtra(NodeCommunicationIntentService.JSOBJ, createJSON());
		this.startService(intent);
		
		showProgress=true;
		progressDialog = new ProgressDialog(this);
	    progressDialog.setMessage("Bejelentkezés folyamatban...");
	    progressDialog.show();
	}
	
	private void handleResult(Bundle resultData){
     	if(resultData.getBoolean(Constants.ITEM_SYNC_VALUE, false)){
     		success();
     	}else{
     		hideProgress();
     		failure();
     	}

	}
	
	private void hideProgress(){
		if(progressDialog!=null){
			progressDialog.dismiss();
			progressDialog = null;
		}
	}	

	@Override
	protected void onPause() {
		super.onPause();
		if(progressDialog!=null && !showProgress){
			hideProgress();
		}
	}

	protected void success(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(DataCollectService.USER_NAME, id);
		editor.putString(DataCollectService.USER_PASSWORD, 
				passwd);
		
		editor.commit();
		this.startActivity(new Intent(this, MainActivity.class));
		this.finish();
	}
	protected abstract void failure();	
	
	public class UserResultReceiver extends ResultReceiver {
		
	    public UserResultReceiver(Handler handler) {
	        super(handler);
	    }
	    
	    @Override
	    protected void onReceiveResult (int resultCode, Bundle resultData) {
	     	   handleResult(resultData);
	    }
	}
	
	public void navigateToServerData(View v){
    	Intent i = new Intent(this, FirstActivity.class);
    	i.setAction("showData");
    	this.startActivity(i);
    }
	
	private String createJSON(){
		JSONObject userInfo=new JSONObject();
		try {
			userInfo.put("userID", id);
			userInfo.put("password", passwd);
		} catch (JSONException e) {
			Toast.makeText(this, "Az adatok küldése nem sikerült.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return "";
		}
		return userInfo.toString();
	}

}
