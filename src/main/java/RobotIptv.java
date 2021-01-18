/*
 * Created by Max Maximoff on 11/07/2020.
 * �������� ��������� � ��������� � ���������� ������������� � ������ �������
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
	
// ���� ������ RobotIptv    
	private String nameFile = "./src/main/resources/plist.m3u"; // ���� � ��� m3u �� ���������
	private int timeOut = 3*1000;    // timeout
	private int lenData = 8192;      // ����� ������ ��� �������� ������� ������
	private int maxAttemt = 3;       // �-�� ������� ��� ��������

// ����������� ������ RobotIptv � ����������� �� ���������
	public RobotIptv() {
	}	
	
// ����������� ������ RobotIptv � ��������� ������ ����� �����
	public RobotIptv(String nameFile) {
	    this.nameFile = nameFile;
	}
	
// ����������� ������ RobotIptv � �������� ����������
    public RobotIptv(String nameFile, int timeOut, int lenData, int maxAttemt) {
        this.nameFile = nameFile;
        this.timeOut = timeOut;
        this.lenData = lenData;
        this.maxAttemt = maxAttemt;
    }
    
// ����� ��� �������� ��������� �� bean � ��������� �� proxytv.ru    
	public void createM3uCheck (List<Entry> entries) throws Exception{

		try {
			// ��������� ����� ��� ������ � ���� m3u
				FileOutputStream createM3U = new FileOutputStream(nameFile); 
			// ��������� ��������� ������ ����� m3u	
				String extm3u = "#EXTM3U\n";
				createM3U.write(extm3u.getBytes());
			// ��������� ������ ��� ������� ������ �� bean	
				for (int i=0; i < entries.size(); ++i) {
					String extinf = String.format("#EXTINF:-1 group-title=\"%s\",%s\n", entries.get(i).getGroupTitle(), entries.get(i).getChannelName());		
					createM3U.write(extinf.getBytes("UTF-8"));
			// �������� ������� ��� �������� ������ ������� ������
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
	
	// ����� ��� �������� ��������� �� bean ��� �������� �� proxytv.ru    
		public void createM3uNoCheck (List<Entry> entries) throws Exception{

			try {
				// ��������� ����� ��� ������ � ���� m3u
					FileOutputStream createM3U = new FileOutputStream(nameFile); 
				// ��������� ��������� ������ ����� m3u	
					String extm3u = "#EXTM3U\n";
					createM3U.write(extm3u.getBytes());
				// ��������� ������ ��� ������� ������ �� bean	
					for (int i=0; i < entries.size(); ++i) {
						String extinf = String.format("#EXTINF:-1 group-title=\"%s\",%s\n", entries.get(i).getGroupTitle(), entries.get(i).getChannelName());		
						createM3U.write(extinf.getBytes("UTF-8"));
				// �������� ������� ��� �������� ������ ������� ������
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

// ����� ��� �������� ������ iptv. ������ ������������� URI �� �������
	public String vrfStream (String idchann, String stream) throws Exception{
		
		try {
	
			int attempt = 0;                 // ���������� �������
			boolean servconnect = true;      // ������������ ��� stream �������
			int  buffer_fullness = 0;        // ���������� �������� � ����������� ������
			String unf = "udpxy not found";  // ������� ������ �� proxytv.ru
			// ���� �������� stream
			do {
				try{
					//�������������� � UDPXY � ������������� TIMEOUT
					URL udpxy = new URL(stream);
					HttpURLConnection udpxyConnection = (HttpURLConnection) udpxy.openConnection();
					udpxyConnection.setConnectTimeout(timeOut);
//					udpxyConnection.connect();
					BufferedReader bufStream = new BufferedReader(new InputStreamReader(udpxyConnection.getInputStream()));
					//������� ����������� ����� �������� lenData
					char[] cbuf = new char[lenData];
					//���������� ����� ������� � ����� cbuf �������� 
					buffer_fullness = bufStream.read(cbuf, 0, lenData);
					servconnect = true;
		
				}
				// ������ ���� �� ������� �������������� � ������� UDPXY
				catch(IOException ioe){
					servconnect = false;
					attempt +=1; 
					System.out.printf("ch #%s - NO connection\n", idchann);
				}
				// �������� �������� ������
				if(servconnect) 
				{
	                // ���� ����� �������
					if (buffer_fullness==lenData) {
						System.out.printf("ch #%s - GOOD stream\n", idchann);	
						// ���������� �����
						return stream + "\n";
						
					}
	             // ���� ����� ������ ������ ��������� ��������� ��������
					else {
						attempt +=1;
					    System.out.printf("ch #%s - BAD stream\n", idchann);
					}
				}
				// ��������� ������ �� ��������� ������ �� proxytv.ru
				if(stream == unf) {
					System.out.printf("ch #%s - %s\n", idchann, unf);
				}
				// �������� ����� URI � proxytv.ru � ������ ������������������� �������
				String serverQueryString = String.format("http://proxytv.ru/iptv/php/allchan.php?id=%s", idchann);
				URL connect = new URL(serverQueryString);
				
				if (connect.openStream()!=null) {
					BufferedReader inputLine = new BufferedReader(new InputStreamReader(connect.openStream()));
					stream = inputLine.readLine();
				}
				// � ��������� ���� ����� maxAttemt ���
		    }while(attempt < maxAttemt);
			
			stream = "http://proxytv.ru/iptv/img/notv.mp4";

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stream + "\n";
	}

// ����� ��� �������� ����� http://proxytv.ru � ������� Selenium � ����� ��������� ������ ����������(��)
	public List<Entry> getProviderPlist (String providerList, String driverName) throws Exception {
    	
    	// ������ bean ���� ����� ��������� ������ � �������
    	List<Entry> entries = new ArrayList<Entry>();
    	
    	try {
	    	// ��������� �������		
			System.setProperty("webdriver.chrome.driver", driverName);
	        // ������� � ��������� ��������� � ChromeOptions
	        ChromeOptions options = new ChromeOptions();
		        options.addArguments("--window-size=1920,1080");
		        options.addArguments("--disable-gpu");
		        options.addArguments("--disable-extensions");
		        options.addArguments("--proxy-server='direct://'");
		        options.addArguments("--proxy-bypass-list=*");
		        options.addArguments("--start-maximized");
		     // ��������� Chrome � ������ headless (������� �� ������ �� ����������)
		        options.addArguments("--headless");
		     // �������� CHrome � �������� � ������� ����������� ���������� ModeHeaders
				options.addArguments("user-data-dir=C:\\Selenium\\BrowserProfile"); 
             //	������� ��������� "Chrome is being controlled by automated test software java"
				options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"}); 
			// ����������� 	WebDriver � ChromeOptions
	        WebDriver driver = new ChromeDriver(options);	
	        // ��������� �������� ��� ��������		
			  String baseUrl = "http://proxytv.ru"; 
			  driver.get(baseUrl);
			  
			  // ������ ������ � ��������� ���������� � ������
			  WebElement input = driver.findElement(By.name("udpxyaddr")); 
			  WebElement button = driver.findElement(By.id("btnsearch"));
			  // ��������������� ������-������ ����������� � ������
			  String[] provider = providerList.split(",");
			  // ������ ������� �� proxytv.ru ��� ������� ���������� �� ������
			  for (int i=0; i < provider.length; ++i) {
				  
				  StringBuilder content = new StringBuilder();
			      // ��������� ������
				  String QueryString = String.format("pl:%s", provider[i]); 
				  input.clear(); // ������� ���� �����
				  input.sendKeys(QueryString); // ������ ������
				  button.click(); // ������� �� ������ "�����"
				  
				  // �������������� ��������� �� ������ � ���������� �������
				  System.out.println("Query made for " + provider[i] + " provider");
				  Thread.sleep(3000); // ����� 3 ������� ��� ��������� ��������� ������� � �����
				  // ���������� � ������ ���������� � ������
				  String results = driver.findElement(By.id("results")).getText(); 
				  
				  // �������������� ������ ����������� � ���� �������� ��� ��������
				  if(results != null) {
					    String line;
				    	// �������� ��������
				  	    String cut_results = results.replace("\n------------ ProxyTV-5 - ���������� IPTV ��� ����������� Kodi!!! ------------", "");
						BufferedReader reader = new BufferedReader(new StringReader(cut_results));
						// ��������� � content ������ ������ ��������� � ��������� ����������
						String extm3u = String.format("#EXTM3U provider-name=\"%s\"\n", provider[i]);
						content.append(extm3u);
							  
						// ���������� ������ ��� ������ � cut_results. ��������� ���������� � content
						for (int j=0; (line = reader.readLine()) != null; ++j) {
							if ((j!=0) && (j!=1)) {
							    content.append(line);
							    content.append(System.lineSeparator());
							}
						}

				  	    String plistM3u = content.toString();
//				  	    System.out.print(plistM3u);

			            // ������ ��������, ������� ��� � ���� stream
						InputStream stream = new ByteArrayInputStream(plistM3u.getBytes(StandardCharsets.UTF_8));
						entries.addAll(new Parser().parse(stream));
						

				  }
				  else {
					  System.out.println("Response from " + provider + " provider is empty");
				  }
			  }
			// �������������� ��������� �� ���������� ����� � ��������� �� ������ �����������  
			System.out.println();  
			System.out.println("All requests to the database are made.");
			// ��������� WebDriver
			driver.quit();
 
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}

    	// ���������� ���������
    return entries;
  	
  }
    

}


