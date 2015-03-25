package ru.gc986.SQLClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import ru.gc986.SQLClient.parse.DATA_request;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**Главный класс подключения к серверу
 * @author Язовцев Игорь
 * @serial 1
 * @since 2015
 * */

public class MainMobileSQLClient {

	/**Порт для подключения к серверу. По умолчанию значение 6000*/
	private static int SERVERPORT = 6000;
	/**IP адрес для подключения к серверу. По умолчанию localhost*/
	private static String SERVER_IP = "localhost";
	
	/**Главный Handler для обновления состояния подключения*/
	private Handler messageHandler;
	
	/** Показатель что с сервера пришёл null*/
	private int nullCount = 0;
	
	/** Главный сокет*/
	private Socket socket;
	
	/**Генератор идентификаторов запросов*/
	Random random;
	
	/**Идентификатор запроса авторизации*/
	String authId = "";
	
	/**Главный конструктор
	 * @param ip - ip адрес сервера, port - порт подключения к серверу*/
	public MainMobileSQLClient(String ip, int port){
		this.SERVER_IP = ip; // Сообщаем адрес
		this.SERVERPORT = port; // Сообщаем порт

		random = new Random(); // Генератор идентификаторов запросов
		
		// Отображение полученных сообщений с сервера
		messageHandler = new Handler() {
		      public void handleMessage(android.os.Message msg) {
		    	  if(msg.what == 1){ // Проверяем что это сообщение с сервера
		    		  if(onCheckAuthentication((String)msg.obj)) return; // если данная строка является ре
		    		  
		    		  String[] massStr = splitServerMessage((String)msg.obj); // выделяем идентификатор и сообщение
		    		  DATA_request dATA_parser = new DATA_request(massStr[1]);
		    		  onMessage(massStr[0],new DATA_request(massStr[1])); // Сообщаем о сообщении и попутно производим парсинг
		    	  }
		    	  if(msg.what == 100){ // Подключились к серверу
		    		  onConnect();
		    	  }
		    	  if(msg.what == 101){ // Подключаюсь к серверу
		    		  onConnection();
		    	  }
		    	  if(msg.what == 102){ // Отключились от сервера
		    		  onDisconnect();
		    	  }	    	  
		      }
		 };
	}
	
	/**Запускаем клиента*/
	public boolean startClient(){
		Message msg = new Message();
		msg.what = 101;
		messageHandler.sendMessage(msg);
		
		try{
			// Подключаемся к серверу
			if((socket==null)||(socket.isClosed())) // Проверяем доступность сокета
				new Thread(new ClientThread()).start();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**Останавливаем клиента*/
	public boolean stopClient(){
		// Закрываем сокет
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	} 

	/**Отправка нового сообщения на сервер. Формат не определён. Небезопасный метод!
	 * @param msg - запрос отправляемый на сервер
	 * @return результат выполнения запроса*/
	public boolean sendMessage(String msg){
		try{
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Запрос удался
		}
		return true; // Запрос не удался
	}
	
	/**Отправка нового SQL запроса. Безопасный метод.
	 * @param sqlRequest - sql запрос отправляемый на сервер
	 * @return идентификатор запроса. Если 0, то запрос не выполнен*/
	public String sendSQLRequest(String sqlRequest){
		String idRequest = String.valueOf(random.nextInt(9999999));
		
		boolean result = sendMessage(idRequest+":::sql:::"+sqlRequest); // Отправляем запрос на сервер
		
		if(!result) idRequest = "0";
		
		return idRequest;
	};
	
	/**Пришло новое сообщение с сервера с сервера*/
	public void onMessage(String id, DATA_request data){}
	
	/**Сообщаем что подключились к серверу*/
	public void onConnect(){}
	
	/**Сообщаем что отключились от сервера*/
	public void onDisconnect(){
		try {
			if(socket!=null){ // Окончательно закрываем клиент
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Регистрация на сервере*/
	public void onAuthentication(String login, String pass){
		authId = String.valueOf(random.nextInt(999999));
		sendMessage(authId+":::auth:::"+login+":::"+pass);
	}
	
	/**ошибка регистрации на сервере*/
	public void onErrAuthentication(){}
	
	/**регитрация прошла успешно*/
	public void onCompleteAuthentication(){}

	/**проверка сообщения что это результат регистрации на сервере
	 * @param str - строка для проверки регистрации
	 * @return результат регистрации*/
	private boolean onCheckAuthentication(String str){
		try{
			String [] massStr = str.split(":::");
			if(massStr[0].equals(authId)){ // Проверка что это результат аутентификации
				authId = ""; // обнуляем идентификатор регистрации на сервере
				if(massStr[1].equals("userOk")){
					onCompleteAuthentication(); // регистрация пользователя закончена
				} else onErrAuthentication(); // ошибка регистрации пользователя
			} else return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**Начинается процесс подключения к серверу*/
	public void onConnection(){}
	
	/**Реализация работы клиента*/
	class ClientThread implements Runnable {

		@Override
		public void run() {

			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

				socket = new Socket(serverAddr, SERVERPORT);

				BufferedReader input;
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Сообщаем что подключились к серверу
				Message msg = new Message();
				msg.what = 100;
				messageHandler.sendMessage(msg);
				
				while (!Thread.currentThread().isInterrupted()) {
					try {
						if(socket.isClosed()) {break;} // Если сокет закрыт, выходим из цикла
						
						String read = input.readLine(); // Читаем очередную строку из потока
						
						if((read==null)||(read.equals("null"))) nullCount++;
						else nullCount = 0;
						
						// Отключаем сокеты
						if(nullCount>10) {
							// Закрываем сокет
							socket.close();
							// Выходим из цикла
							Log.i("MyApp", "!DISCONNECT!");							
							break;
						}
						
						// Сообщаем о тексте с сервера
						if(read!=null)
						{
							msg = new Message();
							msg.what = 1;
							msg.obj = new String(read);
							messageHandler.sendMessage(msg);
						}
						
//						// Разбираемся с запросом и если это данные от SQL то парсим их
//						if((read!=null)&&(read.split(":::").length>2))
//						{
//							if(read.split(":::")[1].equals("sql")) // Если это всё таки ответ на SQL запрос
//								{
//									DATA_parser dATA_parser = new DATA_parser(read.split(":::")[2]);
//									if(dATA_parser.error.length()==0)
//										for(int i = 0 ; i < dATA_parser.recordsCount ; i++)
//										{
//											for(int i2 = 0 ; i2 < dATA_parser.structure.length ; i2++){
//												Log.i("MyApp", dATA_parser.structure[i2]+" - "+dATA_parser.listData.get(i)[i2]);
//											}
//										}
//								}
//						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Сообщаем об отключении от сервера
			Message msg = new Message();
			msg.what = 102;
			messageHandler.sendMessage(msg);
		}

	}

	/**Разделение сообщения с сервера на идентификатор и сообщение*/
	private String[] splitServerMessage(String str){
		String[] mass = new String[2];
		
		// идентификатор
		mass[0] = str.substring(0, str.indexOf(":::"));
		// сообщение
		mass[1] = str.substring(mass[0].length()+3, str.length());
		
		return mass;
	}
	
}
