package ru.gc986.mymobilesqlserver.sql;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**Класс для осуществления SQL запросов к БД*/

public class SQL_exec {

	Context context;
	
	/**Имя БД с которой происходит работа*/
	String nameDB = "";
	
	public SQL_exec(Context context, String nameDB){
		this.context = context;
		this.nameDB = nameDB;
	}
	
	/**Выполнение SQL запроса к БД*/
	public String sqlExec(String request){
		String result = "";
		
		// Начинаем работать с бд
		SQL_helper sql_device = new SQL_helper(context, nameDB);
		SQLiteDatabase db = sql_device.getWritableDatabase();
		Cursor cursor = null;
		
		try{
			cursor = db.rawQuery(request, new String[] {});
			cursor.moveToFirst();
			
			// Собираемвыборку воедино
			if(cursor.getCount()>0)
			{
				DATA_packaging dATA_packaging = new DATA_packaging(cursor.getCount());
				// Формируем массив структуры
				String[] structure = new String[cursor.getColumnCount()];
				for(int i = 0 ; i < cursor.getColumnCount() ; i++)
					structure[i] = cursor.getColumnName(i);
				// Устанавливаем структуру данных
				dATA_packaging.setStructure(structure);
				// Формируем список данных
				ArrayList<String[]> listData = new ArrayList<String[]>();
				cursor.moveToFirst(); // Передвигаем курсор в начало списка
				for(int i = 0 ; i < cursor.getCount() ; i++){
					String[] massData = new String[structure.length];
					for(int i2 = 0 ; i2 < structure.length ; i2++){
						massData[i2] = cursor.getString(cursor.getColumnIndex(structure[i2]));
					}
					listData.add(massData);
					cursor.moveToNext();
				}
				result = dATA_packaging.makeSQLJSONString(listData);
//				Log.i("MyApp", "RESULT JSON - "+result);
			}
				
			// Закрываем всё
			cursor.close();
			db.close();
			sql_device.close();
			
		} catch (Exception e){
			e.printStackTrace();
			// Закрываем всё
			if((cursor!=null)&&(!cursor.isClosed())) cursor.close();
			if(db.isOpen()) db.close();
			sql_device.close();
			
			return "{\"status\":\"err\",\"recordsCount\":\"1\",\"fieldsCount\":\"1\",\"fileds\":[\"message\"],\"data\":[[\""+e.getMessage()+"\"]]}";
		}
		
		return result;
	}
	
}
