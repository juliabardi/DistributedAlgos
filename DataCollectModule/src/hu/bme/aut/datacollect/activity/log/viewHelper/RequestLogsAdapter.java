package hu.bme.aut.datacollect.activity.log.viewHelper;

import hu.bme.aut.communication.entity.RequestLogData;
import hu.bme.aut.datacollect.activity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

/**
 * 
 * @author Eva Pataji
 *
 */
public class RequestLogsAdapter extends ArrayAdapter<RequestLogData> {
	private Context mContext;
	private List<RequestLogData> mRequests;

	public RequestLogsAdapter(Context context, List<RequestLogData> objects) {
		super(context, R.layout.log_requestrow, objects);
		
		mContext =context;
		mRequests = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RequestLogData request = mRequests.get(position);
		 LayoutInflater inflater = (LayoutInflater) mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.log_requestrow, parent, false);
	    ((TextView) rowView.findViewById(R.id.textViewName)).setText(request.getOfferName());
	    Calendar.getInstance().getTimeInMillis();
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(request.getRequestReceived());
	    String display = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	    ((TextView) rowView.findViewById(R.id.textViewDate)).setText(display);
	    ImageView image = (ImageView)rowView.findViewById(R.id.imageViewRec);
	    if(request.getPeriodic()){
	    	image.setVisibility(View.VISIBLE);
	    }
	    if(request.getStatusCode()!=null && !request.getStatusCode().trim().equals("")){
	    	image.setVisibility(View.VISIBLE);
	    	if(request.getStatusCode().equals("200")){
	    		image.setImageResource(R.drawable.ok);
	    	}else{
	    		image.setImageResource(R.drawable.error);
	    	}
	    }
	    return rowView;
		
	}
	
	


}
