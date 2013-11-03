package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.upload.JavascriptCallback;
import hu.bme.aut.datacollect.utils.StringUtils;

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
	
	private JavascriptCallback callback;

	@SuppressLint("SetJavaScriptEnabled") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.algorithm_activity);
		
		String script = null;
		script = StringUtils.trimToNull(getIntent().getStringExtra("script"));
		final String s = script;
		Log.d(TAG, "Algorithm activity started with script: " + s);
		
		callback = new JavascriptCallback(this, getIntent().getStringExtra("address"), 
			getIntent().getStringExtra("reqId"), getIntent().getStringExtra("port"));
		
		//this.webView = (WebView)this.findViewById(R.id.webView);
		
		//this is ok too, not necessary to show the webview
		this.webView = new WebView(this);
		
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.addJavascriptInterface(callback, "callback");
		
		this.webView.setWebViewClient(new WebViewClient(){
		    @Override  
		    public void onPageFinished(WebView view, String url)  
		    {  		    	
//		    	String js = FileUtils.getStringFromStream(
//		    			getResources().openRawResource(R.raw.script));
		    	webView.loadUrl("javascript:(" + s + ")()");
		    	AlgorithmActivity.this.finish();
		    } 
		});
		
		this.webView.loadUrl("https://www.google.com/");
		//this.webView.loadUrl("file:///android_asset/sensors.html");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try {
			this.callback.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
}
