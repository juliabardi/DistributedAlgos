package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.communication.NodeCommunicationIntentService;
import hu.bme.aut.communication.utils.HttpParamsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * User login, only HTTPS.
 * @author Eva Pataji
 *
 */
public class LoginActivity extends BaseUserDataActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		theReceiver = new UserResultReceiver(new Handler());
	}
	
	public void onLogin(View v){
		if(isWifiAvaiable()){
			if((EditText)findViewById(R.id.editTextEMail)== null || 
				((EditText)findViewById(R.id.editTextEMail)).getText().toString().equals("") ||
				(EditText)findViewById(R.id.editTextPassword)== null || 
				((EditText)findViewById(R.id.editTextPassword)).getText().toString().trim().equals("")){
				Toast.makeText(this, "Nem adtál meg minden adatot!", Toast.LENGTH_SHORT).show();			
				return;
			}
			
			id =((EditText)findViewById(R.id.editTextEMail)).getText().toString();
			passwd = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
				
			sendMessage(Constants.LOGIN_USER);
		}
	}

	public void onRegistration(View v){
		this.startActivity(new Intent(this, RegisterActivity.class));					
	}
	
	@Override
	protected void failure(){
		Toast.makeText(this, "A belépés sikertelen.", Toast.LENGTH_SHORT).show();
	}
	

}
