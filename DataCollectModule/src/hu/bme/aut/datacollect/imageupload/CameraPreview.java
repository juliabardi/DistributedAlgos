package hu.bme.aut.datacollect.imageupload;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.ShutterCallback {
	
	private static final String TAG = "DataCollect:Camera";
	
    private SurfaceHolder mHolder;
    private Camera mCamera;
    
    private boolean stopped = true;
    
    public CameraPreview(Context context, Camera camera){
    	super(context);
       	this.mCamera = camera;
    	this.initPreview();
    }
    
    public void initPreview(){ 
    	if (stopped){
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	        stopped = false;
    	}
    }
    
    public void removePreview(){
    	if (!stopped){
    		mHolder.removeCallback(this);
    		stopped = true;
    	}
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
    	if (mCamera != null){
	        try {
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
    	}
    }

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
    	//mCamera.stopPreview();
    }

    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
          return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

	@Override
	public void onShutter() {
		//Log.d(TAG, "Camera shutter.");
	}
	
	public void setCamera(Camera camera){
		this.mCamera = camera;
	}
}