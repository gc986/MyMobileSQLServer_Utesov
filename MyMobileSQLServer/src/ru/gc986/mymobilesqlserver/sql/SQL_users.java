package ru.gc986.mymobilesqlserver.sql;

import java.util.ArrayList;

import ru.gc986.mymobilesqlserver.adapters.Data_user;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**Работа с пользователями системы
 * @author Язовцев Игорь
 * @serial 1
 * @since 2015
 * */

public class SQL_users {

	Context context;
	
	public SQL_users(Context context){
		this.context = context;
	}
	
	/**Добавить пользователя
	 * @param name - имя
	 * @param pass - пароль
	 * @param description - описание учётной записи
	 * @return результат регистрации*/
	public boolean regNewUser(String name, String pass, String description){
		try{
			SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
			SQLiteDatabase db = sql_device.getWritableDatabase();
			Cursor cursor = db.rawQuery("insert into "+SQL_helper.table_users+"("+SQL_helper.user_name+","+SQL_helper.user_description+","+SQL_helper.user_pass+") values (\""+name+"\",\""+description+"\",\""+pass+"\");", new String[] {});
			cursor.moveToFirst();
			
			cursor.close();
			db.close();
			sql_device.close();
			
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**Удалить пользователя
	 * @param id - идентификатор удаляемого пользователя
	 * @return результат удаления*/
	public boolean deleteUser(String id){
		
		try{
			SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
			SQLiteDatabase db = sql_device.getWritableDatabase();
			Cursor cursor = db.rawQuery("delete from "+SQL_helper.table_users+" where "+SQL_helper.user_id+" = "+id+";", new String[] {});
			cursor.moveToFirst();
			
			cursor.close();
			db.close();
			sql_device.close();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**Проверить пользователя на существование
	 * @param login - логин пользователя
	 * @param pass - пароль пользователя
	 * @return результат проверки пользователя
	 * */
	public boolean checkUser(String login, String pass){
		// Показатель существования пользователя
		boolean userExists = false;
		
		try{
			SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
			SQLiteDatabase db = sql_device.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from "+SQL_helper.table_users+" where "+SQL_helper.user_name+" like \""+login+"\" and "+SQL_helper.user_pass+" like \""+pass+"\";", new String[] {});
			cursor.moveToFirst();
			if(cursor.getCount()>0) userExists = true;
			
			cursor.close();
			db.close();
			sql_device.close();
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return userExists;
	}
	
	/**Получение всех пользователей зарегистрированных в системе
	 * @return список найденых пользователей*/
	public ArrayList<Data_user> getUsers(){
		ArrayList<Data_user> list = new ArrayList<Data_user>();
		
		try{
			SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
			SQLiteDatabase db = sql_device.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from "+SQL_helper.table_users+";", new String[] {});
			cursor.moveToFirst();
			
			for(int i = 0 ; i < cursor.getCount() ; i++){
				Data_user data_user = new Data_user();
				data_user.name = cursor.getString(cursor.getColumnIndex(SQL_helper.user_name));
				data_user.description = cursor.getString(cursor.getColumnIndex(SQL_helper.user_description));
				data_user.pass = cursor.getString(cursor.getColumnIndex(SQL_helper.user_pass));
				data_user.id = cursor.getString(cursor.getColumnIndex(SQL_helper.user_id));
				list.add(data_user);
				cursor.moveToNext();
			}
			
			cursor.close();
			db.close();
			sql_device.close();
		} catch (Exception e){
			e.printStackTrace();
			return list;
		}
		
		return list;
	}
	
}
