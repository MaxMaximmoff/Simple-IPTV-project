/*
* Created by Max Maximoff on 11/07/2020.
* ����� ��� ������ � csv �������
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
	
// ���� ������ CsvFile    
	private char delimiter; // ����������� � csv �����
    private String path;    // ���� � ��� csv �����
    private File csvFile;   // ����������� ������������� csv �����
	private FileInputStream inputStream; // ����� ��� ������ csv �����
    private FileOutputStream outputStream; // ����� ��� ������ � csv �����
    
// ����������� ������ CsvFile
    public CsvFile(String path, char delimiter) {
        this.path = path;
        this.delimiter = delimiter;
    }	
// ����� ��� ������� ������������ ������ File � ��� �������� csv �����    
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

// ����� ��� ������ ������ �� csv ����� � bean
	public List<Entry> readDataFromCSV() throws Exception 
	{ 
		
        List<Entry> entries = new ArrayList<Entry>();
	    try { 
	    	
	    	inputStream = new FileInputStream(csvFile);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	  
	        // �������� ������� csvParser � ��������� ����������� delimiter
	        CSVParser parser = new CSVParserBuilder().withSeparator(delimiter).build(); 
	  
	        // �������� ������� csvReader� ����������� filereader � parser
	        CSVReader csvReader = new CSVReaderBuilder(reader) 
	                                  .withCSVParser(parser) 
	                                  .build(); 
	  
	        // ������ ��� ���� �� csv ����� ��� ������ ������ readAll()
	        List<String[]> allData = csvReader.readAll(); 

	        Entry.Builder entry = null;
	  
		        // ��������� ������ �� List � bean. 
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
	
//  ����� ��� ���������� csv ����� �������	
	public void writePlistToCSV (List<Entry> entries) throws Exception {
		
		try
		{
			// c������� ������ ��� ������ ������ plist � csv ����
			outputStream = new FileOutputStream(csvFile);
			// �������� ������������ ������ ��� ������ � csv
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));  
    		
            // ������� Mapping Strategy ����� ����������� ������� �������� �� �����
            ColumnPositionMappingStrategy<Entry> mappingStrategy= new ColumnPositionMappingStrategy<Entry>(); 
            mappingStrategy.setType(Entry.class);  
            
            // ������������ ���� �������� ���, ��� ������� � ����������� ���� �������.
            String[] columns = new String[]  
                    { "tvchId", "channelName", "groupTitle", "channelUri", "providerName"}; 
            mappingStrategy.setColumnMapping(columns);
        
            
            // �������� ������� StatefulBeanToCsv
            StatefulBeanToCsvBuilder<Entry> builder= 
                        new StatefulBeanToCsvBuilder<Entry>(writer); 
            StatefulBeanToCsv<Entry> beanWriter =  
          builder.withSeparator(delimiter).withMappingStrategy(mappingStrategy).build(); 
  
            // ������ list � ������ StatefulBeanToCsv
            beanWriter.write(entries); 
  
            // ��������� writer � outputStream
            writer.close(); 
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}


	//  ����� ��� ���������� plist � csv ����
	public void addPlistToCSV (List<Entry> entries) throws Exception {
			
			try
			{
				// c������� ������ ��� ���������� ������ plist � csv ����
				outputStream = new FileOutputStream(csvFile, true);
				// �������� ������������ ������ ��� ������ � csv
	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));  

	    		
	            // ������� Mapping Strategy ����� ����������� ������� �������� �� �����
	            ColumnPositionMappingStrategy<Entry> mappingStrategy= new ColumnPositionMappingStrategy<Entry>(); 
	            mappingStrategy.setType(Entry.class);  
	            
	            // ������������ ���� �������� ���, ��� ������� � ����������� ���� �������.
	            String[] columns = new String[]  
	                    { "tvchId", "channelName", "groupTitle", "channelUri", "providerName"}; 
	            mappingStrategy.setColumnMapping(columns);
	        
	            
	            // �������� ������� StatefulBeanToCsv
	            StatefulBeanToCsvBuilder<Entry> builder= 
	                        new StatefulBeanToCsvBuilder<Entry>(writer); 
	            StatefulBeanToCsv<Entry> beanWriter =  
	          builder.withSeparator(delimiter).withMappingStrategy(mappingStrategy).build(); 
	  
	            // ������ list � ������ StatefulBeanToCsv
	            beanWriter.write(entries); 
	  
	            // ��������� writer � outputStream
	            writer.close(); 
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

}
