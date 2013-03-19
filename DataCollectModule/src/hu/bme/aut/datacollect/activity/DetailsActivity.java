package hu.bme.aut.datacollect.activity;

import hu.bme.aut.datacollect.R;
import hu.bme.aut.datacollect.db.DatabaseHelper;
import hu.bme.aut.datacollect.entity.AccelerationData;

import java.util.List;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class DetailsActivity extends OrmLiteBaseActivity<DatabaseHelper>  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.detail_view);
		this.loadTableData();
	}

	protected void loadTableData(){
		
		TableLayout table = (TableLayout)findViewById(R.id.table);
		
		List<AccelerationData> list = getHelper().getAccelerationDao().queryForAll();
		for (AccelerationData acc : list) {
			TableRow row = new TableRow(this);
			TextView tv = new TextView(this);
			tv.setText(String.format(
					"accX:%f, accY:%f, accZ:%f", acc.getAccX(), acc.getAccY(),
					acc.getAccZ()));
			row.addView(tv);
			table.addView(row);
		}
	}
}
