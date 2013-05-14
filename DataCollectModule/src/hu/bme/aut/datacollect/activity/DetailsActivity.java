package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.AccelerationData;
import hu.bme.aut.datacollect.entity.CallData;
import hu.bme.aut.datacollect.entity.IData;
import hu.bme.aut.datacollect.entity.LightData;
import hu.bme.aut.datacollect.entity.LocationData;
import hu.bme.aut.datacollect.entity.TemperatureData;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
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
		List<? extends IData> list = null;
		
		switch (id) {
		case R.id.buttonAcceleration:
			text.setText("Acceleration");		
			list = getHelper().getDaoBase(AccelerationData.class).queryForAll();
			break;
		case R.id.buttonLight:
			text.setText("Light");		
			list = getHelper().getDaoBase(LightData.class).queryForAll();
			break;
		case R.id.buttonTemperature:
			text.setText("Temperature");		
			list = getHelper().getDaoBase(TemperatureData.class).queryForAll();
			break;
		case R.id.buttonFineLocation:
			text.setText("FineLocation");		
			list = getHelper().getDaoBase(LocationData.class).queryForAll();
			break;
		case R.id.buttonCalls:
			text.setText("Calls");		
			list = getHelper().getDaoBase(CallData.class).queryForAll();
			break;
		default:
			list = new ArrayList<IData>();
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
