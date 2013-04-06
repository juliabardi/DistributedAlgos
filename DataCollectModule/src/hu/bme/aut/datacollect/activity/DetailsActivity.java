package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class DetailsActivity extends OrmLiteBaseActivity<DatabaseHelper>  {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.detail_view);
		
		int i = 0;
		if (getIntent()!=null){
			i = getIntent().getIntExtra("id",0);
		}		
		this.loadTableData(i);
	}

	protected void loadTableData(int id){
		
		TextView text = (TextView)findViewById(R.id.tableName);
		TableLayout table = (TableLayout)findViewById(R.id.table);
		List<?> list = null;
		
		switch (id) {
		case R.id.buttonAcceleration:
			text.setText("Acceleration");		
			list = getHelper().getAccelerationDao().queryForAll();
			break;
		case R.id.buttonLight:
			text.setText("Light");		
			list = getHelper().getLightDao().queryForAll();
			break;
		case R.id.buttonTemperature:
			text.setText("Temperature");		
			list = getHelper().getTemperatureDao().queryForAll();
			break;
		case R.id.buttonFineLocation:
			text.setText("FineLocation");		
			list = getHelper().getLocationDao().queryForAll();
			break;
		case R.id.buttonCalls:
			text.setText("Calls");		
			list = getHelper().getCallDao().queryForAll();
			break;
		default:
			list = new ArrayList<String>();
			break;
		}
		
		this.constructRows(table, list);
		
	}
	
	private <T> void constructRows(TableLayout table, List<T> list){
		
		for (T item : list) {
			TableRow row = new TableRow(this);
			TextView tv = new TextView(this);
			tv.setText(item.toString());
			row.addView(tv);
			table.addView(row);
		}
	}
}
