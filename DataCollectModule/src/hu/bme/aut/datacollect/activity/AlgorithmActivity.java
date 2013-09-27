package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DataProvider;
import hu.bme.aut.datacollect.utils.FileUtils;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AlgorithmActivity extends Activity {
	
	private static final String TAG ="DataCollect:AlgorithmActivity";
	
	private WebView webView;
	
	private DataProvider dataProvider = new DataProvider(this);

	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.algorithm_activity);
		
		this.webView = (WebView)this.findViewById(R.id.webView);
		
		//this is ok too, not necessary to show the webview
		//this.webView = new WebView(this);
		
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.addJavascriptInterface(dataProvider, "dataProvider");
		
		this.webView.setWebViewClient(new WebViewClient(){
		    @Override  
		    public void onPageFinished(WebView view, String url)  
		    {  
//		    	webView.loadUrl("javascript:(function() { " +  
//		    			//calling jsinterface, result is string
//		                "var result = dataProvider.getAllDataParamsString('AccelerationData', 1, 'timestamp,id'); " +
//		    			//parsing string to json
//		                "var json = JSON.parse(result); " +
//		                "document.write('json.id: ' + json.id + '<br/>'); " +  
//			            "document.write('json.name: ' + json.name + '<br/>'); " +  
//		                //iterating through json
//			        	"for (var i in json.params){ " +			        		
//			        	"	document.write('json.params['+i+']: ' + json.params[i] + ' ');	" +
//			        	"}" +
//			        	"var sum = 0;" +
//			        	//summing up values
//			        	"for (var j in json.values){" +
//			        	"	sum += parseInt(json.values[j][1]);" +
//			        	"}" +
//			        	"document.write('values.id sum:' + sum);" +
//			        	//calling jsinterface to set the sum
//			        	"dataProvider.setSum(sum);" +
//			        	//calling jsinterface to set the data in json (string)
//			        	"dataProvider.setJSON(JSON.stringify(json));" +
//		                "})()"); 		
		    	
		    	String js = FileUtils.getStringFromStream(
		    			getResources().openRawResource(R.raw.script));
		    	webView.loadUrl("javascript:(" + js + ")()");
		    } 
		});
		
		//this.webView.loadUrl("https://www.google.com/");
		this.webView.loadUrl("file:///android_asset/sensors.html");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			this.dataProvider.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	

	
}
