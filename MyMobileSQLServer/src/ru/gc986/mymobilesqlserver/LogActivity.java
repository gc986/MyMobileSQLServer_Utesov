package ru.gc986.mymobilesqlserver;

import java.util.List;

import ru.gc986.mymobilesqlserver.adapters.Adapter_log_item;
import ru.gc986.mymobilesqlserver.adapters.Data_log;
import ru.gc986.mymobilesqlserver.sql.SQL_service_requests;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ListView;

public class LogActivity extends ActionBarActivity {

	/**ListView для вывода логов*/
	ListView lv_log;
	
	/**Адаптер для вывода логов*/
	Adapter_log_item adapter_log;
	
	/**Список логов*/
	List<Data_log> list_log;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		// Вывод логов
		lv_log = (ListView) findViewById(R.id.listView_log);
		SQL_service_requests sQL_service_requests = new SQL_service_requests(this);
		list_log = sQL_service_requests.getLogData();
		adapter_log = new Adapter_log_item(this,list_log);
		lv_log.setAdapter(adapter_log);
	}

}
