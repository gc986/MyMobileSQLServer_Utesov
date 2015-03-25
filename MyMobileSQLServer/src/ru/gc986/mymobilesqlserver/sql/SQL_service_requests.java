package ru.gc986.mymobilesqlserver.sql;

import java.util.ArrayList;

import ru.gc986.mymobilesqlserver.adapters.Data_log;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**Сервисные запросы к базе данных
 * @author Язовцев Игорь
 * @version 1
 * @since 2015*/

public class SQL_service_requests {

	Context context;
	
	public SQL_service_requests(Context context){
		this.context = context;
		
	}
	
	/**Получить список имеющихся логов
	 * @return лист логов*/
	public ArrayList<Data_log> getLogData(){
		ArrayList<Data_log> list = new ArrayList<Data_log>();
		
		SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
		SQLiteDatabase db = sql_device.getWritableDatabase();
		String request = "select * from "+SQL_helper.table_log;		
		Cursor cursor = db.rawQuery(request, new String[] {});
		cursor.moveToLast();
		for(int i = cursor.getCount() ; i >0  ; i--)
		{
			Data_log data_log = new Data_log();
			data_log.date = cursor.getString(cursor.getColumnIndex(SQL_helper.log_time)); // Дата создания
			data_log.text = cursor.getString(cursor.getColumnIndex(SQL_helper.log_request)); // Текст
			data_log.user = cursor.getString(cursor.getColumnIndex(SQL_helper.log_user)); // Создатель
			list.add(data_log);
			cursor.moveToPrevious();
		}
		
		cursor.close();
		db.close();
		sql_device.close();
		return list;
	}
	
	/**Поместить строку в логи*/
	public void addLogData(String user, String date, String data){
		if(data==null) return; // если пустое сообщение то выходим из подпрограммы
		Long time = System.currentTimeMillis();
		if(date.equals("0")) date = String.valueOf(time); // Устанавливаем текущее время
		if(user.length()==0) user = "-"; // Ставим заглушку
		
		SQL_helper sql_device = new SQL_helper(context, SQL_helper.DB_service_name);
		SQLiteDatabase db = sql_device.getWritableDatabase();
		String request = "insert into "+SQL_helper.table_log+"("+SQL_helper.log_time+","+SQL_helper.log_user+","+SQL_helper.log_request+") values (\""+date+"\",\""+user+"\",\""+data.replace("\"", "'")+"\");";		
		Cursor cursor = db.rawQuery(request, new String[] {});
		cursor.moveToFirst();
		
		cursor.close();
		db.close();
		sql_device.close();
	}

		
}
