package ru.gc986.mymobilesqlserver.adapters;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import ru.gc986.mymobilesqlserver.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**Адаптер для вывода данных по логам
 * @author Язовцев Игорь
 * @version 1
 * @since 2015*/

public class Adapter_log_item extends ArrayAdapter<Data_log> {

	/**Контекст*/
	private  Context context;
	
	/**Лист всех объектов*/
	private  List<Data_log> list;

	// Объекты для отображения данных логов
	static class ViewHolder {
		protected TextView tv_time; // Время лога
		protected TextView tv_text; // Текст лога
		protected TextView tv_user; // Пользователь
	}
	
	public Adapter_log_item(Context _context, List<Data_log> _list) {
		super(_context, R.layout.adapter_log_item,_list);
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
				tmpView = inflator.inflate(R.layout.adapter_log_item, null);
			} else {
				tmpView = convertView;
			}
		
		// Инициализируем виджет
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.tv_time = (TextView) tmpView.findViewById(R.id.textView_time);
		viewHolder.tv_text = (TextView) tmpView.findViewById(R.id.textView_request);
		viewHolder.tv_user = (TextView) tmpView.findViewById(R.id.textView_user);
		
		// Заполняем данные адаптера
		// Дата генерации лога
		Timestamp stamp = new Timestamp(Long.parseLong(list.get(position).date));
		Date date = new Date(stamp.getTime());
		String date_request = date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+" - "+date.getDate()+"."+(date.getMonth()+1)+"."+(date.getYear()+1900);
		viewHolder.tv_time.setText(date_request);
		// Содержимое лога
		viewHolder.tv_text.setText(list.get(position).text);
		// Пользователь
		viewHolder.tv_user.setText(list.get(position).user);
		
		return tmpView;
	}
	
}
