package hu.bme.aut.datacollect.upload;

import android.content.Context;
import android.content.Intent;

import com.squareup.tape.InMemoryObjectQueue;
import com.squareup.tape.TaskQueue;

public class ImageUploadTaskQueue extends TaskQueue<ImageUploadTask> {
	
	private static ImageUploadTaskQueue instance;	
	private Context mContext;
	
	public static ImageUploadTaskQueue instance(Context context){
		if (instance == null){
			instance = new ImageUploadTaskQueue(context);
		}
		return instance;
	}
	
	private ImageUploadTaskQueue(Context context) {
		super(new InMemoryObjectQueue<ImageUploadTask>());
		this.mContext = context;
	}
	
	public void startService() {
		mContext.startService(new Intent(mContext, ImageUploadTaskService.class));
	}

	@Override
	public void add(ImageUploadTask entry) {
		super.add(entry);
		this.startService();
	}

	@Override
	public void remove() {
		super.remove();
	}

}
