package ru.gc986.mymobilesqlserver.adapters;

import java.util.List;

import ru.gc986.mymobilesqlserver.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**Адаптер для отображения информации о пользователе
 * @author Язовцев Игорь
 * @version 1
 * @since 2015
 * */

public class Adapter_user extends ArrayAdapter<Data_user> {

	/**Контекст*/
	private  Context context;
	
	/**Лист всех объектов*/
	private  List<Data_user> list;

	// Объекты для отображения данных логов
	static class ViewHolder {
		protected TextView tv_name; // Имя пользователя
		protected TextView tv_description; // Описание пользователя
	}
	
	public Adapter_user(Context _context, List<Data_user> _list) {
		super(_context, R.layout.adapter_user,_list);
		// Сохраняем все данные приходящие для адаптера
		this.list = _list;		
		this.context = _context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		// Готовимся к выводу производителей на экран
		View tmpView = null;
		if (convertView == null) {
				// Создание элементов для вывода данных
				LayoutInflater inflator = ((Activity) context).getLayoutInflater();
				tmpView = inflator.inflate(R.layout.adapter_user, null);
			} else {
				tmpView = convertView;
			}
		
		// Инициализируем виджет
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.tv_name = (TextView) tmpView.findViewById(R.id.textView_name);
		viewHolder.tv_description = (TextView) tmpView.findViewById(R.id.textView_description);
		
		// Заполняем данными
		viewHolder.tv_name.setText(list.get(position).name);
		viewHolder.tv_description.setText(list.get(position).description);
		
		return tmpView;
	}
}
