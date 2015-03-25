package ru.gc986.mymobilesqlserver;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ServerSocket serverSocket;

	Handler updateConversationHandler;

	Thread serverThread = null;

	public static final int SERVERPORT = 6000;

	/**ip адресс*/
	String ip_server = "";
	
	/**Порт*/
	public static String port = "6000";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		updateConversationHandler = new Handler();

		// Отключить сервер
		RadioButton rb_off = (RadioButton) findViewById(R.id.radio_off);
		rb_off.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Остановка главного сервиса
				stopService(new Intent(MainActivity.this, ru.gc986.mymobilesqlserver.service.MainServiceSQL.class));
			}
		});
		
		// Включить сервер
		RadioButton rb_on = (RadioButton) findViewById(R.id.radio_on);
		rb_on.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Порт
				EditText et_port = (EditText) findViewById(R.id.editText_port);
				port = et_port.getText().toString();
				if(port.length()==0) 
					{
						Toast toast = Toast.makeText(getApplicationContext(),MainActivity.this.getResources().getText(R.string.err_port), Toast.LENGTH_SHORT); 
							toast.show(); 
						return;
					}
				
				Log.i("MyApp","Start...");
				
				// Запуск главного сервиса
				startService(new Intent(MainActivity.this, ru.gc986.mymobilesqlserver.service.MainServiceSQL.class));
			}
		});
		
		// Переход к активности логов
		Button bt_log = (Button) findViewById(R.id.button_log);
		bt_log.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, LogActivity.class);
			    startActivity(intent);
			}
		});
		
		// Переход к активности пользователей
		Button bt_users = (Button) findViewById(R.id.button_edit_users);
		bt_users.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UsersActivity.class);
			    startActivity(intent);
			}
		});
		
		// Переход к активности отработки SQL запроса
		Button bt_exec_sql = (Button) findViewById(R.id.button_exec_sql);
		bt_exec_sql.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ExecSQLActivity.class);
			    startActivity(intent);
			}
		});
		
		/**Показываем IP адрес*/
		TextView tv_address = (TextView) findViewById(R.id.textViewIP);
		tv_address.setText(getIpAddress());
	
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	public static String getIpAddress() { 
        try {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address) {
                        String ipAddress=inetAddress.getHostAddress().toString();
                        Log.e("IP address",""+ipAddress);
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
        }
        return null; 
	}
	
}
