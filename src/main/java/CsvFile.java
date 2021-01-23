/*
* Created by Max Maximoff on 11/07/2020.
* A class for working with csv files
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
	private char delimiter;                   // A delimiter in a csv file
    private File csvFile;                     // An abstract representation of a csv file
	private FileInputStream inputStream;      // A stream for reading a csv file
    private FileOutputStream outputStream;    // A stream to write to a csv file
    
//  CsvFile class constructor
   public CsvFile(String path, char delimiter) throws IOException, NullPointerException {

       this.delimiter = delimiter;
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

// method for reading data from a csv file to List<bean>
	public List<Entry> readDataFromCSV() throws Exception 
	{ 
		
        List<Entry> entries = new ArrayList<Entry>();
	    try { 
	    	
	    	inputStream = new FileInputStream(csvFile);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	  
	        // creating a CSVParser object with a delimiter
	        CSVParser parser = new CSVParserBuilder().withSeparator(delimiter).build(); 
	  
	        // creating a CSVReader object with the filereader and parser parameters
	        CSVReader csvReader = new CSVReaderBuilder(reader) 
	                                  .withCSVParser(parser) 
	                                  .build(); 
	  
	        // read all the information from the csv file using the ReadAll() method
	        List<String[]> allData = csvReader.readAll(); 

	        Entry.Builder entry = null;
	  
		        // Добавляем данные в bean. 
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
