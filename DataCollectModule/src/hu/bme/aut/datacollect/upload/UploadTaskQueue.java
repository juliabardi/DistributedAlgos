package hu.bme.aut.datacollect.upload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.tape.InMemoryObjectQueue;
import com.squareup.tape.TaskQueue;

public class UploadTaskQueue extends TaskQueue<UploadTask> {
	
	private static final String TAG = "DataCollect:UploadTaskQueue";
	
	private static UploadTaskQueue instance;	
	private Context mContext;
	
	public static UploadTaskQueue instance(Context context){
		if (instance == null){
			instance = new UploadTaskQueue(context);
		}
		return instance;
	}
	
	private UploadTaskQueue(Context context) {
		super(new InMemoryObjectQueue<UploadTask>());
		this.mContext = context;
	}
	
	public void startService() {
		mContext.startService(new Intent(mContext, UploadTaskService.class));
	}

	@Override
	public void add(UploadTask entry) {
		if (entry instanceof ImageUploadTask){
			Log.e(TAG, "Don't put ImageUploadTask to UploadTaskQueue, because it slows down other Tasks. Use ImageUploadTaskQueue!");
			return;
		}
		super.add(entry);
		this.startService();
	}

	@Override
	public void remove() {
		super.remove();
	}

}
