package com.meMaxMaximmoff.executabe;
/*
 * Скрипт для создания плейлистов m3u
 * на основе данных полученных с сайта http://proxytv.ru/
 * Демонстрация создания таких плейлистов путем чтения данных из csv файла
 * и базы mySQL
 * 
 * Полный список провайдеров на proxytv.ru :
 * Optik Redkom Novotel Lada Zapsib Aist Tauer Elite RostNSK Bes Utelekom Post Lanta Perspektiv Corbina Citilink Yarnet Skaynet Tenet
*/


import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception{

		final String M3UNAMEFILE1 = "./src/main/resources/plist_Novotel.m3u";//имя файла плейлиста
		final String M3UNAMEFILE2 = "./src/main/resources/plist_sport.m3u";//имя файла плейлиста
		final String CSVNAMEFILE = "./src/main/resources/Novotel.csv";//имя csv файла 
		final String DRIVERNAME = "./src/main/resources/BrowserDrivers"
				+ "/Chrome/ChromeDriver 88.0.4324.96/chromedriver.exe";//путь к ChromeDriver
		final char DELIMITER = ';'; // разделитель в csv файле
		final int TIMEOUT = 3*1000;// 3 секунды
		final int LENDATA = 8192; // длина буфера для проверки наличия потока
		final int MAXATTEMPT = 3; //  к-во попыток для проверки
		final String tableName = "plists";
		

        // строка со списком всех провайдеров		
		final String allProviders = "Aist,Elite,Lada,Optik,Lanta,Novotel,"
				+ "Perspektiv,Post,Redkom,RostNSK,Skaynet,Citilink,Tenet,Tauer,Zapsib,Utelekom,Corbina,Yarnet";
		
		
		  try {
		  
			  // список bean куда будем сохранять данные о каналах  
			   List<Entry> entries =  new ArrayList<Entry>();
			  
			  // конструктор для класса RobotIptv
			   RobotIptv robotIptv1 = new RobotIptv();
			  
			  // парсим сайт http://proxytv.ru/ и записываем данные о каналах в список bean
			  entries = robotIptv1.getProviderPlist(allProviders, DRIVERNAME);
			  
			  
			  // конструктор класса  SqlBase
			  SqlBase newSqlBase = new SqlBase();
			  
			  newSqlBase.createConnection();
			  
			  // удаляем все записи в таблице базы // 
			  newSqlBase.deleteAllPlistsInBase(tableName);
			  
			  // добавляем данные о каналах в базу 
			  newSqlBase.addPlistToBase(entries, tableName);
			  
			  // SQL запрос из базы для провайдера Novotel
			  String query1 = "SELECT * FROM plists WHERE providerName = 'Novotel' ";
			  
			  // получаем данные по SQL запросу
			  entries = newSqlBase.readDataFromBase(query1);
			  
			  // конструктор для класска CsvFile
			  CsvFile newCsvFile = new CsvFile(CSVNAMEFILE, DELIMITER);
			  
			  // пишем данные в csv
			   newCsvFile.writePlistToCSV(entries);
			   
			  // конструктор для класса RobotIptv с указанием имени файла
			   RobotIptv robotIptv2 = new RobotIptv(M3UNAMEFILE1);	
			   
			  // читаем данные из csv
			   entries = newCsvFile.readDataFromCSV();
			   
			  // создаем m3u на основе данных из csv
			   robotIptv2.createM3uNoCheck(entries);

			  // конструктор для класса RobotIptv с указанием имени файла, таймаута, длины проверочного буфера
			  // и максимального к-ва попыток 
			   RobotIptv robotIptv3 = new RobotIptv(M3UNAMEFILE2, TIMEOUT, LENDATA, MAXATTEMPT);	
			   
			   //   SQL запрос из базы для выбора каналов СПОРТИВНЫЕ
			   String query2 = "SELECT * FROM plists WHERE (providerName = 'Novotel' OR providerName = 'Lanta') AND groupTitle = 'СПОРТИВНЫЕ'"; 
			   
			   // получаем данные по SQL запросу
			   entries = newSqlBase.readDataFromBase(query2);
			   
			   // создаем m3u на основе данных из базы с проверкой и заменой при необходимости потока
			   robotIptv3.createM3uCheck(entries);
			   

		  
		  } catch(Exception e) {
			  	e.printStackTrace(); 
		  }
		 
	}

}
