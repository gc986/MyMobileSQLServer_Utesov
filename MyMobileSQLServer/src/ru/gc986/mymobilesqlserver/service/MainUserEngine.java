package ru.gc986.mymobilesqlserver.service;

import ru.gc986.mymobilesqlserver.sql.SQL_exec;
import ru.gc986.mymobilesqlserver.sql.SQL_users;
import android.content.Context;
import android.util.Log;

/**Главный пользовательский движок, управляющий поведением приложения на пользовательские сообщения*/

public class MainUserEngine {

	Context context;
	
	/**Имя пользователя*/
	public String userName = "";
	
	/**Показатель что пользователь зарегистрировался в системе*/
	boolean userAuth = false;
	
	/**Основной конструктор*/
	public MainUserEngine(Context context){
		this.context = context;
	}
	
	/**Главный парсер действий*/
	public String[] mainParserActions(String msg){
		// Возвращаемые сообщения
		String[] returnMsg = new String[2];
		
		try{
			// Выделение идентификатора и заголовка
			// идентификатор
			returnMsg[0] = msg.substring(0, msg.indexOf(":::"));
			msg = msg.substring(returnMsg[0].length()+3, msg.length());
			// заголовок
			String title = msg.substring(0, msg.indexOf(":::"));
			msg = msg.substring(title.length()+3, msg.length());
			
			//--------------------------------------
			// аутентификация пользователя в системе
			if(title.equals("auth")){
				String[] user = msg.split(":::");
				userAuth = loginUser(user[0],user[1]); // аутентификация пользователя
				if(userAuth) {
					returnMsg[1] = "userOk"; // сообщаем что аутентификация прошла удачно
					userName = user[0]; // Сохраняем логин
				}
			}
			// если к данному моменту пользователь не зарегистрировался, то сообщаем об ошибке
			if (!userAuth) {
				returnMsg[1] = "err";
			}
			//--------------------------------------
			// Выполнение SQL кода
			if((userAuth)&&(title.equals("sql"))){
				// Класс для запроса к SQL базе данных
				SQL_exec sQL_exec = new SQL_exec(context, userName);
				returnMsg[1] = sQL_exec.sqlExec(msg);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			returnMsg[1] = "err";
		}
		
		return returnMsg;
	}
	
	/**Аутентификация пользователя
	 * @param login - имя пользователя
	 * @param pass - пароль
	 * @return результат аутентификации*/
	protected boolean loginUser(String login, String pass){
		SQL_users sQL_users = new SQL_users(context);
		return sQL_users.checkUser(login, pass);
	}
	
}
