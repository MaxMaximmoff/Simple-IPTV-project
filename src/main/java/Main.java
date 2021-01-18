/*
 * ������ ��� �������� ���������� m3u
 * �� ������ ������ ���������� � ����� http://proxytv.ru/
 * ������������ �������� ����� ���������� ����� ������ ������ �� csv �����
 * � ���� mySQL
 * 
 * ������ ������ ����������� �� proxytv.ru :
 * Optik Redkom Novotel Lada Zapsib Aist Tauer Elite RostNSK Bes Utelekom Post Lanta Perspektiv Corbina Citilink Yarnet Skaynet Tenet
*/


import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception{

		final String M3UNAMEFILE1 = "./src/main/resources/plist_Novotel.m3u";//��� ����� ���������
		final String M3UNAMEFILE2 = "./src/main/resources/plist_sport.m3u";//��� ����� ���������
		final String CSVNAMEFILE = "./src/main/resources/Novotel.csv";//��� ����� ���������
		final String DRIVERNAME = "./src/main/resources/BrowserDrivers"
				+ "/Chrome/ChromeDriver 86.0.4240.22/chromedriver.exe";//���� � ChromeDriver
		final char DELIMITER = ';'; // ����������� � csv �����
		final int TIMEOUT = 3*1000;// 3 �������
		final int LENDATA = 8192; // ����� ������ ��� �������� ������� ������
		final int MAXATTEMPT = 3; // �-�� ������� ��� ��������
		

        // ������ �� ������� ���� �����������		
		final String allProviders = "Aist,Bes,Elite,Lada,Optik,Lanta,Novotel,"
				+ "Perspektiv,Post,Redkom,RostNSK,Skaynet,Citilink,Tenet,Tauer,Zapsib,Utelekom,Corbina,Yarnet";
		
		
		  try {
		  
			  // ������ bean ���� ����� ��������� ������ � �������  
			   List<Entry> entries =  new ArrayList<Entry>();
			  
			  // ����������� ��� ������ RobotIptv
			   RobotIptv robotIptv1 = new RobotIptv();
			  
			  // ������ ���� http://proxytv.ru/ � ���������� ������ � ������� � ������ bean
			  entries = robotIptv1.getProviderPlist(allProviders, DRIVERNAME);
			  
			  
			  // ����������� ������  SqlBase
			  SqlBase newSqlBase = new SqlBase();		  
			  
			  // ������� ��� ������ � ������� ���� // 
			  newSqlBase.deleteAllPlistsInBase();
			  
			  // ��������� ������ � ������� � ���� 
			  newSqlBase.addPlistToBase(entries);
			  
			  // SQL ������ �� ���� ��� ���������� Novotel
			  String query1 = "SELECT * FROM plists WHERE providerName = 'Novotel' ";
			  
			  // �������� ������ �� SQL �������
			  entries = newSqlBase.readDataFromBase(query1);
			  
			  // ����������� ��� ������� CsvFile
			  CsvFile newCsvFile = new CsvFile(CSVNAMEFILE, DELIMITER);
			  
			  // �������� ������� ����� csv. ������� ���� ��� ���
			   newCsvFile.initCsv();
			  
			  // ����� ������ � csv
			   newCsvFile.writePlistToCSV(entries);
			   
			  // ����������� ��� ������ RobotIptv � ��������� ����� �����
			   RobotIptv robotIptv2 = new RobotIptv(M3UNAMEFILE1);	
			   
			  // ������ ������ �� csv
			   entries = newCsvFile.readDataFromCSV();
			   
			  // ������� m3u �� ������ ������ �� csv
			   robotIptv2.createM3uNoCheck(entries);

			  // ����������� ��� ������ RobotIptv � ��������� ����� �����, ��������, ����� ������������ ������
			  // � ������������� �-�� ������� 
			   RobotIptv robotIptv3 = new RobotIptv(M3UNAMEFILE2, TIMEOUT, LENDATA, MAXATTEMPT);	
			   
			   //   SQL ������ �� ���� ��� ������ ������� ����������
			   String query2 = "SELECT * FROM plists WHERE (providerName = 'Novotel' OR providerName = 'Lanta') AND groupTitle = '����������'"; 
			   
			   // �������� ������ �� SQL �������
			   entries = newSqlBase.readDataFromBase(query2);
			   
			   // ������� m3u �� ������ ������ �� ���� � ��������� � ������� ��� ������������� ������
			   robotIptv3.createM3uCheck(entries);
			   

		  
		  } catch(Exception e) {
			  	e.printStackTrace(); 
		  }
		 
	}

}
