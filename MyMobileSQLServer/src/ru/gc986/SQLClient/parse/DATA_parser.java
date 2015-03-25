package ru.gc986.SQLClient.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**Парсинг данных с сервера
 * @author Язовцев Игорь
 * @version 1
 * @since 2015
 * */

public class DATA_parser {

	/**Количество записей*/
	public int recordsCount = 0;
	
	/**Структура записи, перечисление полей*/
	public String [] structure;
	
	/**Список данных*/
	public ArrayList<String[]> listData;
	
	/**Исходный JSON*/
	public String dataJSON = "";
	
	/**Сообщения об ошибке*/
	public String error = "";
	
	/**Статус запроса*/
	public String status = "";
	
	public DATA_parser(String dataJSON){
		this.dataJSON = dataJSON;
		parse(this.dataJSON);
	}
	
	/**Парсинг*/
	private void parse(String data){
		try {
			// корень
			JSONObject main = new JSONObject(data);
			// Статус запроса
			status = main.getString("status");
			// количество записей
			recordsCount = Integer.parseInt(main.getString("recordsCount"));
			// массив структуры полей
			structure = new String[Integer.parseInt(main.getString("fieldsCount"))];
			JSONArray structureArr = main.getJSONArray("fileds");
			for(int i = 0 ; i < structure.length ; i++)
				structure[i] = structureArr.getString(i);
			// Данные
			listData = new ArrayList<String[]>();
			JSONArray dataArray = main.getJSONArray("data");
			for(int i = 0 ; i < dataArray.length() ; i++){
				JSONArray inDataArray = dataArray.getJSONArray(i);
				String[] mass = new String[inDataArray.length()];
				for(int i2 = 0 ; i2 < mass.length ; i2++)
				{
					mass[i2] = inDataArray.getString(i2);
				}
				// Добавляем данные в список
				listData.add(mass);
			}
		} catch (JSONException e) {
			// Обработка ошибки парсига
			e.printStackTrace();
			error = "#ERR:JSONException:"+e.getMessage();
			structure = null;
			recordsCount = 0;
			listData = null;
		} catch (Exception e){
			// Обработка остальных ошибок
			e.printStackTrace();
			error = "#ERR:Exception:"+e.getMessage();
			structure = null;
			recordsCount = 0;
			listData = null;
		}
	}
	
}
