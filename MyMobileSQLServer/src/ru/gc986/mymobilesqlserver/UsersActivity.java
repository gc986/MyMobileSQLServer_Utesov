package ru.gc986.mymobilesqlserver;

import java.util.ArrayList;

import ru.gc986.mymobilesqlserver.adapters.Adapter_user;
import ru.gc986.mymobilesqlserver.adapters.Data_user;
import ru.gc986.mymobilesqlserver.popupwindow.AddUserPopupWindow;
import ru.gc986.mymobilesqlserver.sql.SQL_users;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

public class UsersActivity extends ActionBarActivity {

	/**Список пользователей*/
	ArrayList<Data_user> list_users;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		
		// Добавление нового пользователя
		Button bt_adduser = (Button) findViewById(R.id.button_adduser);
		bt_adduser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Окно создания нового пользователя
				AddUserPopupWindow popupWIndow_groups = new AddUserPopupWindow(v){
					
					@Override
					public void onAddNewUser(){
						// Показать пользователей
		            	showAllUsers();
					}
				};
			}
		});
		
		// Вывод пользователей
		showAllUsers(); // Показать пользователей
		ListView lv_users = (ListView) findViewById(R.id.listView_users);
		lv_users.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
            	// Спрашиваем у пользователя о возможности удаления пользователя
            	dialogDeleteUser(list_users.get(pos).name,list_users.get(pos).id);
                return true;
            }
        }); 
	}
	
	/**Вопрос удаления пользователя*/
	public void dialogDeleteUser(String name, final String id){
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage(this.getResources().getString(R.string.user_delete_user)+" - "+name); // сообщение
        ad.setPositiveButton(this.getResources().getString(R.string.yes), new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            	// Удаляем пользвоателя из БД
            	SQL_users sQL_users = new SQL_users(UsersActivity.this);
            	sQL_users.deleteUser(id);
            	// Показать пользователей
            	showAllUsers();
            }
        });
        ad.setNegativeButton(this.getResources().getString(R.string.no), new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                
            }
        });
        ad.show();
	}
	
	/**Залить данные в список*/
	public void showAllUsers(){
		ListView lv_users = (ListView) findViewById(R.id.listView_users);
		SQL_users sQL_users = new SQL_users(this);
		list_users = sQL_users.getUsers();
		Adapter_user adapter_users = new Adapter_user(this,list_users);
		lv_users.setAdapter(adapter_users);
		adapter_users.notifyDataSetChanged();
	}
	
}
