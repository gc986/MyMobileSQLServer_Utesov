package ru.gc986.mymobilesqlserver.sql;

import java.util.ArrayList;

/**Клас помощьник упаковки данных извлечённых с сервера
 * @author Язовцев Игорь
 * @version 1
 * @since 2015
 * */

public class DATA_packaging {

	/**Количество записей*/
	int recordsCount = 0;
	
	/**Структура записи, перечисление полей*/
	String [] structure;
	
	public DATA_packaging(int recordsCount){
		this.recordsCount = recordsCount;
	}
	
	/**Установить структуру данных для записей*/
	public void setStructure(String[] structure){
		this.structure = structure;
	}
	
	/**Создание структуры данных в виде JSON*/
	public String makeSQLJSONString(ArrayList<String[]> list){
		String str = "";
		
		// Начало структуры данных
		str = "{"+
				"\"status\":\""+"ok"+"\","+
				"\"recordsCount\":\""+recordsCount+"\","+
				"\"fieldsCount\":\""+structure.length+"\","+
				"\"fileds\":[";
		// Заполняем список полей
		for(int i = 0 ; i < structure.length ; i++){
			str = str + "\""+structure[i]+"\"";
			if(i<structure.length-1) str = str + ",";
		}
		str = str + "],";
		str = str + "\"data\":[";
		// Наполняем структуру данными
		for(int i = 0 ; i < list.size() ; i++){
			str = str + "[";
			String[] mass = list.get(i); // Массив данных
			for (int i2 = 0 ; i2 < mass.length ; i2++)
			{
				str = str + "\""+mass[i2]+"\"";
				if(i2<mass.length-1) str = str + ",";
			}
			str = str + "]";
			if(i<list.size()-1) str = str + ",";
		}
		str = str + "]}";
		return str;
	}
	
}
