package ru.gc986.mymobilesqlserver.popupwindow;

import ru.gc986.mymobilesqlserver.R;
import ru.gc986.mymobilesqlserver.sql.SQL_users;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

/**Добавление нового пользователя
 * @author Язовцев Игорь
 * @serial 1
 * @since 2015*/

public class AddUserPopupWindow {

	private PopupWindow pwindo;
	
	public Context context;
	
	public AddUserPopupWindow(View v){
		 LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 final View layout = inflater.inflate(R.layout.popupwindow_newuser, (ViewGroup) v.findViewById(R.id.main_popup));
		 
		 context = layout.getContext();
		 
		 // Всплывающее окно
		 pwindo = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		 pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
		 
		 // Проверка паролей
		 Button bt_createuser = (Button) layout.findViewById(R.id.button_create_user);
		 bt_createuser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Сравнение паролей
				EditText et_pass1 = (EditText) layout.findViewById(R.id.editText_pass1);
				EditText et_pass2 = (EditText) layout.findViewById(R.id.editText_pass2);
				if(!et_pass1.getText().toString().equals(et_pass2.getText().toString())){
					// пароли не совпадают
					Toast toast = Toast.makeText(context, layout.getResources().getText(R.string.user_pass_wrong_message), Toast.LENGTH_SHORT); 
					toast.show();
					return;
				}
				// Проверка правильности ввода имени (на ноль, на служебную комбинацию :::, на перенос строки)
				EditText et_name = (EditText) layout.findViewById(R.id.editText_name);
				if((et_name.getText().toString().length()==0)||(et_name.getText().toString().indexOf(":::")>=0)||(et_name.getText().toString().indexOf("\n")>=0))
				{
					// имя введено с нарушением 
					Toast toast = Toast.makeText(context, layout.getResources().getText(R.string.user_bad_name), Toast.LENGTH_SHORT); 
					toast.show();
					return;
				}
				
				EditText et_description = (EditText) layout.findViewById(R.id.editText_description);
				// Регистрация нового пользователя
				SQL_users sQL_users = new SQL_users(context);
				sQL_users.regNewUser(et_name.getText().toString(), et_pass1.getText().toString(), et_description.getText().toString());
				// Скрываем окно
				pwindo.dismiss();
				// Сообщаем что добавили нового пользователя
				onAddNewUser();
			}
		});
		 
		 // Скрываем окно
		 Button bt_cancel = (Button) layout.findViewById(R.id.button_cancel);
		 bt_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pwindo.dismiss();
			}
		});
	}
	
	/**Сообщаем что добавили нового пользователя*/
	public void onAddNewUser(){
		
	}
	
}
