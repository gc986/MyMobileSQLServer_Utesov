package com.example.test2mymobilesqlclient;

import ru.gc986.SQLClient.MainMobileSQLClient;
import ru.gc986.SQLClient.parse.DATA_request;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	MainMobileSQLClient mainMobileSQLClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Подключение к серверу		
		Button bt_connect = (Button) findViewById(R.id.button_connect);
		bt_connect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et_ip = (EditText) findViewById(R.id.editText_ip);
				EditText et_port = (EditText) findViewById(R.id.editText_port);
				mainMobileSQLClient = new MainMobileSQLClient(et_ip.getText().toString(),Integer.parseInt(et_port.getText().toString())){
					
					/**Пришло новое сообщение с сервера*/
					@Override
					public void onMessage(String id, DATA_request data){
						super.onMessage(id, data);
						TextView tv_log = (TextView) findViewById(R.id.textView_log);
						
						String message = "";
						for(int i = 0 ; i < data.recordsCount ; i ++){
							for(int i1 = 0 ; i1 < data.structure.length ; i1++)
							{
								message = message + "\n" + data.structure[i1] + " : " + data.listData.get(i)[i1];
							}
							message = message + "\n" + "-----";
						}
						message = "Status - " + data.status + "\n" + message + "\n" + "======" + "\n";
						tv_log.setText(message + tv_log.getText().toString());
					}
					
					/**Начинается процесс подключения к серверу*/
					@Override
					public void onConnection(){
						super.onConnection();
						
					}
					
					/**Подключились к серверу*/
					@Override
					public void onConnect(){
						super.onConnect();
						// Скрываем объекты для подключения
						TextView tv_ip = (TextView) findViewById(R.id.textView1);
						tv_ip.setVisibility(View.GONE);
						TextView tv_port = (TextView) findViewById(R.id.textView2);
						tv_port.setVisibility(View.GONE);
						EditText et_ip = (EditText) findViewById(R.id.editText_ip);
						et_ip.setVisibility(View.GONE);
						EditText et_port = (EditText) findViewById(R.id.editText_port);
						et_port.setVisibility(View.GONE);
						
						// Аутентификация
						EditText et_login = (EditText) findViewById(R.id.editText_login);
						EditText et_pass = (EditText) findViewById(R.id.editText_pass);
						onAuthentication(et_login.getText().toString(), et_pass.getText().toString());
					}
					
					/**Сообщаем что отключились от сервера*/
					@Override
					public void onDisconnect(){
						showAllView();
					}
					
					/**Ошибка регистрации на сервере*/
					@Override
					public void onErrAuthentication(){
						showAllView();
					}
					
					/**Регитрация прошла успешно*/
					@Override
					public void onCompleteAuthentication(){
						// Скрываем объекты для аутентификации
						TextView tv_login = (TextView) findViewById(R.id.textView3);
						tv_login.setVisibility(View.GONE);
						TextView tv_pass = (TextView) findViewById(R.id.textView4);
						tv_pass.setVisibility(View.GONE);
						EditText et_login = (EditText) findViewById(R.id.editText_login);
						et_login.setVisibility(View.GONE);
						EditText et_pass = (EditText) findViewById(R.id.editText_pass);
						et_pass.setVisibility(View.GONE);
						Button bt_connect = (Button) findViewById(R.id.button_connect);
						bt_connect.setVisibility(View.GONE);
					}
					
					void showAllView(){
						// Показываем все объекты
						TextView tv_ip = (TextView) findViewById(R.id.textView1);
						tv_ip.setVisibility(View.VISIBLE);
						TextView tv_port = (TextView) findViewById(R.id.textView2);
						tv_port.setVisibility(View.VISIBLE);
						EditText et_ip = (EditText) findViewById(R.id.editText_ip);
						et_ip.setVisibility(View.VISIBLE);
						EditText et_port = (EditText) findViewById(R.id.editText_port);
						et_port.setVisibility(View.VISIBLE);
						TextView tv_login = (TextView) findViewById(R.id.textView3);
						tv_login.setVisibility(View.VISIBLE);
						TextView tv_pass = (TextView) findViewById(R.id.textView4);
						tv_pass.setVisibility(View.VISIBLE);
						EditText et_login = (EditText) findViewById(R.id.editText_login);
						et_login.setVisibility(View.VISIBLE);
						EditText et_pass = (EditText) findViewById(R.id.editText_pass);
						et_pass.setVisibility(View.VISIBLE);
						Button bt_connect = (Button) findViewById(R.id.button_connect);
						bt_connect.setVisibility(View.VISIBLE);
					}
				};
				mainMobileSQLClient.startClient();
			}
		});
		
		// Отключение от сервера
		Button bt_disconnect = (Button) findViewById(R.id.button_disconnect);
		bt_disconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mainMobileSQLClient!=null)
					mainMobileSQLClient.stopClient();
			}
		});
		
		// Отправить сообщение
		Button bt_request = (Button) findViewById(R.id.button_request);
		bt_request.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_request = (EditText) findViewById(R.id.editText_sql_request);
				
				if(mainMobileSQLClient!=null)
					mainMobileSQLClient.sendSQLRequest(et_request.getText().toString());
			}
		});
	}

}