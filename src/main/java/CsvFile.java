/*
* Created by Max Maximoff on 11/07/2020.
* класс для работы с csv файлами
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.bean.StatefulBeanToCsv;

public class CsvFile {
	
// поля класса CsvFile    
	private char delimiter; // разделитель в csv файле
    private String path;    // путь и имя csv файла
    private File csvFile;   // абстрактное представление csv файла
	private FileInputStream inputStream; // поток для чтения csv файла
    private FileOutputStream outputStream; // поток для записи в csv файла
    
// конструктор класса CsvFile
    public CsvFile(String path, char delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }	
// метод для запуска конструктора класса File и для создания csv файла    
   public void initCsv() throws IOException, NullPointerException {
	   try { 
		   csvFile = new File(path);
		}
	    catch (NullPointerException e) { 
	        System.err.println("The pathname argument is null. ");
	    	e.printStackTrace();
	    } 		
	   try { 
			if(!csvFile.exists())
				csvFile.createNewFile();
			System.out.println("A new CSV file created.");
		}
	    catch (IOException e) { 
	        System.err.println("CSV file did not created.");
	    	e.printStackTrace();
	    } 
    }

// метод для чтения данных из csv файла в bean
	public List<Entry> readDataFromCSV() throws Exception 
	{ 
		
        List<Entry> entries = new ArrayList<Entry>();
	    try { 
	    	
	    	inputStream = new FileInputStream(csvFile);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	  
	        // создание объекта csvParser с указанием разделителя delimiter
	        CSVParser parser = new CSVParserBuilder().withSeparator(delimiter).build(); 
	  
	        // создание объекта csvReaderс параметрами filereader и parser
	        CSVReader csvReader = new CSVReaderBuilder(reader) 
	                                  .withCSVParser(parser) 
	                                  .build(); 
	  
	        // Читаем всю инфу из csv файла при помощи метода readAll()
	        List<String[]> allData = csvReader.readAll(); 

	        Entry.Builder entry = null;
	  
		        // Добавляем данные из List в bean. 
		        for (String[] row : allData) { 
		            for (int i=0;i<row.length;++i) { 
		            	entry = new Entry.Builder()
		            			.tvchId(row[0])
		            			.channelName(row[1])
		            			.groupTitle(row[2])
		            			.channelUri(row[3])
		            			.providerName(row[3]);
		            	
		            } 
		            entries.add(entry.build());
		        } 
		     reader.close();
	    } 
	    catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	    return entries;
	}
	
//  Метод для перезаписи csv файла целиком	
	public void writePlistToCSV (List<Entry> entries) throws Exception {
		
		try
		{
			// cоздание потока для записи данных plist в csv фаил
			outputStream = new FileOutputStream(csvFile);
			// создание необходимого буфера для записи в csv
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));  
    		
            // создаем Mapping Strategy чтобы расположить столбцы согласно их имени
            ColumnPositionMappingStrategy<Entry> mappingStrategy= new ColumnPositionMappingStrategy<Entry>(); 
            mappingStrategy.setType(Entry.class);  
            
            // Расположение имен столбцов так, как указано в приведенном ниже массиве.
            String[] columns = new String[]  
                    { "tvchId", "channelName", "groupTitle", "channelUri", "providerName"}; 
            mappingStrategy.setColumnMapping(columns);
        
            
            // Создание объекта StatefulBeanToCsv
            StatefulBeanToCsvBuilder<Entry> builder= 
                        new StatefulBeanToCsvBuilder<Entry>(writer); 
            StatefulBeanToCsv<Entry> beanWriter =  
          builder.withSeparator(delimiter).withMappingStrategy(mappingStrategy).build(); 
  
            // Запись list в объект StatefulBeanToCsv
            beanWriter.write(entries); 
  
            // закрываем writer и outputStream
            writer.close(); 
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


	//  Метод для добавления plist в csv файл
	public void addPlistToCSV (List<Entry> entries) throws Exception {
			
			try
			{
				// cоздание потока для добавления данных plist в csv фаил
				outputStream = new FileOutputStream(csvFile, true);
				// создание необходимого буфера для записи в csv
	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));  

	    		
	            // создаем Mapping Strategy чтобы расположить столбцы согласно их имени
	            ColumnPositionMappingStrategy<Entry> mappingStrategy= new ColumnPositionMappingStrategy<Entry>(); 
	            mappingStrategy.setType(Entry.class);  
	            
	            // Расположение имен столбцов так, как указано в приведенном ниже массиве.
	            String[] columns = new String[]  
	                    { "tvchId", "channelName", "groupTitle", "channelUri", "providerName"}; 
	            mappingStrategy.setColumnMapping(columns);
	        
	            
	            // Создание объекта StatefulBeanToCsv
	            StatefulBeanToCsvBuilder<Entry> builder= 
	                        new StatefulBeanToCsvBuilder<Entry>(writer); 
	            StatefulBeanToCsv<Entry> beanWriter =  
	          builder.withSeparator(delimiter).withMappingStrategy(mappingStrategy).build(); 
	  
	            // Запись list в объект StatefulBeanToCsv
	            beanWriter.write(entries); 
	  
	            // закрываем writer и outputStream
	            writer.close(); 
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

}
