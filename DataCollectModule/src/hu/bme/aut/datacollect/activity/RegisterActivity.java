package hu.bme.aut.datacollect.activity;

import hu.bme.aut.communication.Constants;
import hu.bme.aut.communication.NodeCommunicationIntentService;
import hu.bme.aut.datacollect.activity.BaseUserDataActivity.UserResultReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * User register.
 * @author Eva Pataji
 *
 */
public class RegisterActivity extends BaseUserDataActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.register);
		theReceiver = new UserResultReceiver(new Handler());
	}
	
	public void onRegistration(View v){
		if((EditText)findViewById(R.id.editTextEMail)== null ||
			(EditText)findViewById(R.id.editTextPassword)== null ||
			(EditText)findViewById(R.id.editTextPasswordAgain)== null ||
			((EditText)findViewById(R.id.editTextEMail)).getText().toString().equals("") ||
			((EditText)findViewById(R.id.editTextPassword)).getText().toString().trim().equals("") ||
			((EditText)findViewById(R.id.editTextPasswordAgain)).getText().toString().trim().equals("")){
			Toast.makeText(this, "Nem adtál meg minden adatot!", Toast.LENGTH_SHORT).show();			
			return;
		}else if( ! ((EditText)findViewById(R.id.editTextPassword)).getText().toString().
				equals(((EditText)findViewById(R.id.editTextPasswordAgain)).getText().toString())){
			Toast.makeText(this, "A jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		id =((EditText)findViewById(R.id.editTextEMail)).getText().toString();
		passwd = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
						
		sendMessage(Constants.REGISTER_USER);
	}

	@Override
	protected void failure() {
		Toast.makeText(this, "A regisztráció sikertelen.", Toast.LENGTH_SHORT).show();		
	}

}
