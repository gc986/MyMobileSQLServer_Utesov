package ru.gc986.mymobilesqlserver.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**Клас помощьник для доступа к данным
 * @author Язовцев Игорь
 * @version 1
 * @since 2015
 * */

public class SQL_helper extends SQLiteOpenHelper{
	// Параметры базы данных
	public static String DB_service_name = "MyMobileSQLServer";
	public static int DB_version = 1;
	
	// Системные объекты
	Context context;
	
	// Таблица хранения логов
	/**Идентификатор таблицы логов*/
	public static String table_log = "table_log";
	/**Идентификатор записи*/
	public static String log_id = "id";
	/**Время создания*/
	public static String log_time = "time";
	/**Пользователь создавший запись*/
	public static String log_user = "user";
	/**Текст запроса*/
	public static String log_request = "request";
	
	// Таблица пользователей
	/**Идентификатор таблицы пользователей*/
	public static String table_users = "table_users";
	/**Идентификатор записи*/
	public static String user_id = "id";
	/**Имя пользователя*/
	public static String user_name = "name";
	/**Описание пользователя*/
	public static String user_description = "description";
	/**Пароль*/
	public static String user_pass = "pass";
	
	/**Конструктор по умолчанию*/
	public SQL_helper (Context context, String dbName){
		super(context, dbName, null, DB_version);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Создание таблицы хранения логов
		db.execSQL("CREATE TABLE "+table_log+" ("+log_id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+log_time+" INTEGER, "+log_user+" TEXT, "+log_request+" TEXT);");
		// Создание таблицы пользователей
		db.execSQL("CREATE TABLE "+table_users+" ("+user_id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+user_name+" TEXT, "+user_description+" TEXT,"+user_pass+" TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
