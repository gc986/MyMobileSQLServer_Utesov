package ru.gc986.mymobilesqlserver;

import java.util.ArrayList;

import ru.gc986.SQLClient.parse.DATA_parser;
import ru.gc986.mymobilesqlserver.adapters.Data_user;
import ru.gc986.mymobilesqlserver.sql.SQL_exec;
import ru.gc986.mymobilesqlserver.sql.SQL_users;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ExecSQLActivity extends ActionBarActivity {

	String user = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exec_sql);
		
		// Заполнение списка пользователей
		SQL_users sQL_users = new SQL_users(this);
		ArrayList<Data_user> list_users = sQL_users.getUsers();
		final String[] users = new String[list_users.size()];
		for(int i = 0 ; i < list_users.size() ; i++)
			users[i] = list_users.get(i).name;
		Log.i("MyApp", "count - "+users.length);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sp = (Spinner) findViewById(R.id.spinner_users);
        sp.setAdapter(adapter);
        
        // Выбор пользователя
        sp.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		user = users[position];
            }
            
        	@Override
            public void onNothingSelected(AdapterView<?> arg0) {
            
        	}

        });
        
        /**Отработка скрипта*/
        Button bt_exec = (Button) findViewById(R.id.button_exec_sql_now);
        bt_exec.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_request = (EditText) findViewById(R.id.editText_sql);
				
				TextView tv_log = (TextView) findViewById(R.id.textView_request_log);
				tv_log.setText(et_request.getText()+"\n"+"=========="+"\n"+tv_log.getText());
				
				SQL_exec sQL_exec = new SQL_exec(ExecSQLActivity.this,user);
				String txt = sQL_exec.sqlExec(et_request.getText().toString());
				
				DATA_parser dATA_parser = new DATA_parser(txt);
				
				for(int i = 0 ; i < dATA_parser.recordsCount ; i++){
					for(int i1 = 0 ; i1 < dATA_parser.structure.length ; i1++){
						tv_log.setText(dATA_parser.structure[i1]+" - "+dATA_parser.listData.get(i)[i1]+"\n"+tv_log.getText());
					}
					tv_log.setText("-"+i+"-\n"+tv_log.getText());
				}
				
				tv_log.setText(dATA_parser.status+"\n"+tv_log.getText());
				
			}
		});
        
	}
}
