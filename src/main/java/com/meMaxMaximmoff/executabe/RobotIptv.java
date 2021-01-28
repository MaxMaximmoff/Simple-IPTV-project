package com.meMaxMaximmoff.executabe;
/*
 * Created by Max Maximoff on 11/07/2020.
 * Создание плейлиста с проверкой и повторными подключениями в случае неудачи
 */
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;

public class RobotIptv {
	
// Поля класса RobotIptv    
	private String nameFile = "./src/main/resources/plist.m3u"; // путь и имя m3u по умолчанию
	private int timeOut = 3*1000;    // timeout
	private int lenData = 8192;      // Длина буфера для проверки наличия потока
	private int maxAttemt = 3;       // К-во попыток для проверки

// Конструктор класса RobotIptv с параметрами по умолчанию
	public RobotIptv() {
	}	
	
// Конструктор класса RobotIptv с указанием только имени файла
	public RobotIptv(String nameFile) {
	    this.nameFile = nameFile;
	}
	
// Конструктор класса RobotIptv с заданием параметров
    public RobotIptv(String nameFile, int timeOut, int lenData, int maxAttemt) {
        this.nameFile = nameFile;
        this.timeOut = timeOut;
        this.lenData = lenData;
        this.maxAttemt = maxAttemt;
    }
    
// Метод для создания плейлиста из листа bean с проверкой на proxytv.ru    
	public void createM3uCheck (List<Entry> entries) throws Exception{

		try {
			// открываем поток для записи в фаил m3u
				FileOutputStream createM3U = new FileOutputStream(nameFile); 
			// добавляем указатель начала файла m3u	
				String extm3u = "#EXTM3U\n";
				createM3U.write(extm3u.getBytes());
			// формируем строки для каждого канала из bean	
				for (int i=0; i < entries.size(); ++i) {
					String extinf = String.format("#EXTINF:-1 group-title=\"%s\",%s\n", entries.get(i).getGroupTitle(), entries.get(i).getChannelName());		
					createM3U.write(extinf.getBytes("UTF-8"));
			// вызываем функцию для проверки потока каждого канала
					String stream = vrfStream (entries.get(i).getTvchId(), entries.get(i).getChannelUri());
					createM3U.write(stream.getBytes()); 
					
				}
				createM3U.close();
				System.out.println("Playlist formed");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
}
	
	// Метод для создания плейлиста из bean без проверки на proxytv.ru    
		public void createM3uNoCheck (List<Entry> entries) throws Exception{

			try {
				// открываем поток для записи в фаил m3u
					FileOutputStream createM3U = new FileOutputStream(nameFile); 
				// добавляем указатель начала файла m3u	
					String extm3u = "#EXTM3U\n";
					createM3U.write(extm3u.getBytes());
				// формируем строки для каждого канала из bean	
					for (int i=0; i < entries.size(); ++i) {
						String extinf = String.format("#EXTINF:-1 group-title=\"%s\",%s\n", entries.get(i).getGroupTitle(), entries.get(i).getChannelName());		
						createM3U.write(extinf.getBytes("UTF-8"));
				// вызываем функцию для проверки потока каждого канала
						String stream = entries.get(i).getChannelUri() + '\n';
						createM3U.write(stream.getBytes()); 

					}
					createM3U.close();
					System.out.println("Playlist formed");

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

// Метод для проверки потока iptv. замена неработающего URI на рабочий
	public String vrfStream (String idchann, String stream) throws Exception{
		
		try {
	
			int attempt = 0;                 // количество попыток
			boolean servconnect = true;      // предполагаем что stream рабочий
			int  buffer_fullness = 0;        // количество символов в проверочном буфере
			String unf = "udpxy not found";  // вариант ответа от proxytv.ru
			// Цикл проверки stream
			do {
				try{
					//Подсоединяемся к UDPXY с установленным TIMEOUT
					URL udpxy = new URL(stream);
					HttpURLConnection udpxyConnection = (HttpURLConnection) udpxy.openConnection();
					udpxyConnection.setConnectTimeout(timeOut);
//					udpxyConnection.connect();
					BufferedReader bufStream = new BufferedReader(new InputStreamReader(udpxyConnection.getInputStream()));
					//создаем проверочный буфер размером lenData
					char[] cbuf = new char[lenData];
					//возвращаем число попавши в буфер cbuf символов 
					buffer_fullness = bufStream.read(cbuf, 0, lenData);
					servconnect = true;
		
				}
				// случай если не удалось подсоединиться к данному UDPXY
				catch(IOException ioe){
					servconnect = false;
					attempt +=1; 
					System.out.printf("ch #%s - NO connection\n", idchann);
				}
				// проверка качества потока
				if(servconnect) 
				{
	                // если поток хороший
					if (buffer_fullness==lenData) {
						System.out.printf("ch #%s - GOOD stream\n", idchann);	
						// возвращаем поток
						return stream + "\n";
						
					}
	             // Если поток плохой случае запускаем повторную проверку
					else {
						attempt +=1;
					    System.out.printf("ch #%s - BAD stream\n", idchann);
					}
				}
				// обработка одного из вариантов ответа от proxytv.ru
				if(stream == unf) {
					System.out.printf("ch #%s - %s\n", idchann, unf);
				}
				// Получаем новый URI с proxytv.ru в случае неработоспособности старого
				String serverQueryString = String.format("http://proxytv.ru/iptv/php/allchan.php?id=%s", idchann);
				URL connect = new URL(serverQueryString);
				
				if (connect.openStream()!=null) {
					BufferedReader inputLine = new BufferedReader(new InputStreamReader(connect.openStream()));
					stream = inputLine.readLine();
				}
				// и повторяем цикл снова maxAttemt раз
		    }while(attempt < maxAttemt);
			
			stream = "http://proxytv.ru/iptv/img/notv.mp4";

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stream + "\n";
	}

// Метод для парсинга сайта http://proxytv.ru с помощью Selenium с целью получения данных провайдера(ов)
	public List<Entry> getProviderPlist (String providerList, String driverName) throws Exception {
    	
    	// список bean куда будем сохранять данные о каналах
    	List<Entry> entries = new ArrayList<Entry>();
    	
    	try {
	    	// указываем драйвер		
			System.setProperty("webdriver.chrome.driver", driverName);
	        // создаем и добавляем аргументы в ChromeOptions
	        ChromeOptions options = new ChromeOptions();
		        options.addArguments("--window-size=1920,1080");
		        options.addArguments("--disable-gpu");
		        options.addArguments("--disable-extensions");
		        options.addArguments("--proxy-server='direct://'");
		        options.addArguments("--proxy-bypass-list=*");
		        options.addArguments("--start-maximized");
		     // открываем Chrome в режиме headless (браузер на экране не появляется)
		        options.addArguments("--headless");
		     // Загрузка CHrome с профилем в котором установлено расширение ModeHeaders
				options.addArguments("user-data-dir=C:\\Selenium\\BrowserProfile"); 
             //	Убираем сообщение "Chrome is being controlled by automated test software java"
				options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); 
			// конструктор 	WebDriver с ChromeOptions
	        WebDriver driver = new ChromeDriver(options);	
	        // открываем страницу для парсинга		
			  String baseUrl = "http://proxytv.ru"; 
			  driver.get(baseUrl);
			  
			  // Вводим запрос с указанием провайдера и парсим
			  WebElement input = driver.findElement(By.name("udpxyaddr")); 
			  WebElement button = driver.findElement(By.id("btnsearch"));
			  // Преобразовываем строку-список провайдеров в массив
			  String[] provider = providerList.split(",");
			  // делаем запросы на proxytv.ru для каждого провайдера из списка
			  for (int i=0; i < provider.length; ++i) {
				  
				  StringBuilder content = new StringBuilder();
			      // формируем запрос
				  String QueryString = String.format("pl:%s", provider[i]); 
				  input.clear(); // очищаем поле ввода
				  input.sendKeys(QueryString); // вводим данные
				  button.click(); // нажатие на кнопку "Найти"
				  
				  // Информационное сообщение на экране о совершении запроса
				  System.out.println("Query made for " + provider[i] + " provider");
				  Thread.sleep(3000); // пауза 3 секунды для надежного получения отклика с сайта
				  // результаты с экрана записываем в строку
				  String results = driver.findElement(By.id("results")).getText(); 
				  
				  // преобразование строки результатов к виду удобному для парсинга
				  if(results != null) {
					    String line;
				    	// вырезаем ненужное
				  	    String cut_results = results.replace("\n------------ ProxyTV-5 - стабильное IPTV для медиацентра Kodi!!! ------------", "");
						BufferedReader reader = new BufferedReader(new StringReader(cut_results));
						// добавляем в content первую строку плейлиста с указанием провайдера
						String extm3u = String.format("#EXTM3U provider-name=\"%s\"\n", provider[i]);
						content.append(extm3u);
							  
						// игнорируем первые две строки в cut_results. остальное записываем в content
						for (int j=0; (line = reader.readLine()) != null; ++j) {
							if ((j!=0) && (j!=1)) {
							    content.append(line);
							    content.append(System.lineSeparator());
							}
						}

				  	    String plistM3u = content.toString();
//				  	    System.out.print(plistM3u);

			            // парсим плейлист, приведя его к виду stream
						InputStream stream = new ByteArrayInputStream(plistM3u.getBytes(StandardCharsets.UTF_8));
						
						try {
							entries.addAll(new Parser().parse(stream));
						}
						catch(ParsingException e) { e.printStackTrace();}
						

				  }
				  else {
					  System.out.println("Response from " + provider + " provider is empty");
				  }
			  }
			// Информационное сообщение по завершении цикла с запросами по разным провайдерам  
			System.out.println();  
			System.out.println("All requests to the database are made.");
			// закрываем WebDriver
			driver.quit();
 
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}

    	// возвращаем результат
    return entries;
  	
  }
    

}


