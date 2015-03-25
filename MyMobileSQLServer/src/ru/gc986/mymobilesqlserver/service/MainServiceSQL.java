package ru.gc986.mymobilesqlserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import ru.gc986.mymobilesqlserver.MainActivity;
import ru.gc986.mymobilesqlserver.sql.SQL_exec;
import ru.gc986.mymobilesqlserver.sql.SQL_service_requests;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**Сервис для осуществления доступа к данным
 * @author Язовцев Игорь
 * @version 1
 * @since 2015 
 * */

public class MainServiceSQL extends Service {

	private ServerSocket serverSocket;

	Handler updateConversationHandler;

	Thread serverThread = null;

	public static int SERVERPORT = 6000;
	
	/**ip адресс*/
	String ip_server = "";
	
	ArrayList<CommunicationThread> tMass = new ArrayList<CommunicationThread>();
	
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	/**Запуск сервиса*/
    @Override
    public void onCreate() {
    	updateConversationHandler = new Handler();
    	// Запуск сервера
    	this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
		
		Log.i("MyApp", "Server start");
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			// Закрываем основное соединение
			serverSocket.close();
			// Закрываем все открытые индивидуальные соединения
			for(int i = 0 ; i < tMass.size() ; i++)
			{
				if(tMass.get(i)!=null)
					tMass.get(i).clientSocket.close();
			}
			// Удаляем весь список
			tMass.clear();
			tMass = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverSocket = null;
		
		Log.i("MyApp", "Service STOP!");
	}
    
	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				SERVERPORT = Integer.parseInt(MainActivity.port);
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Цикл работает только при прерывании, при подключении нового клиента
			while (!Thread.currentThread().isInterrupted()) {

				try {
					// Проверяем что объект существует
					if(serverSocket==null){
						stopSelf(); // Останавливаем сервис
						return;
					}
					// Проверяем сервер на закрытость
					if(serverSocket.isClosed()){
						stopSelf(); // Останавливаем сервис
						return;
					}
					
					socket = serverSocket.accept();
					
					// Создаём отдельный поток для очередного клиента
					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();
					tMass.add(commThread);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {
		private Socket clientSocket;
		private BufferedReader input;
		
		// показатель произошедшего дисконекта
		private int nullMessage = 0;
		
		// Доступ к сервисным запросам
		SQL_service_requests sQL_service_requests;
		
		/**Главный движок поведения*/
		MainUserEngine mainUserEngine;
		
		public CommunicationThread(Socket clientSocket) {
			Log.i("MyApp", "client IP - "+clientSocket.getInetAddress());
			mainUserEngine = new MainUserEngine(MainServiceSQL.this);
			
			sQL_service_requests = new SQL_service_requests(MainServiceSQL.this);
			this.clientSocket = clientSocket;
			try {
				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {
				try {
					
					// Проверяем сокет на закрытость
					if(this.clientSocket.isClosed()) return;
					
					String read = input.readLine();
					
					// Сохраняем лог - сообщение от пользователя
					sQL_service_requests.addLogData(mainUserEngine.userName, "0", read);
					
					// Проверка на закрытие
					if(read==null) nullMessage++;
					if(nullMessage>9) break;

					// Если сообщение не NULL то обрабатываем его и производим запрашиваемые действия
					if(read!=null)
					{
						// Создаём объект для отправки данных клиенту обратно
						PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream())),true);
						
						// Объект отработки сценариев поведения
						String[] result =  mainUserEngine.mainParserActions(read);
						
						// Сообщаем о результатах выполнения запроса
						out.println(result[0]+":::"+result[1]);
						
						// Сохраняем лог - сообщение с сервера
						sQL_service_requests.addLogData("server", "0", result[0]+":::"+result[1]);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// Печатаем текст
	class updateUIThread implements Runnable {
		private String msg;

		public updateUIThread(String str) {
			this.msg = str;
		}

		@Override
		public void run() {
			Log.i("MySQLServer","Client Says: "+ msg);
			
		}
	}	
   
}
