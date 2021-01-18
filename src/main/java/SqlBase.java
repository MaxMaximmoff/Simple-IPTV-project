/*
* Created by Max Maximoff on 11/07/2020.
* ����� ��� ������ � ����� mySQL
*/
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class SqlBase {
	
                           
	   static final String DATABASE_URL = "jdbc:mysql://localhost/plistsDB";  // ����� ����
	   static final String USER_LOGIN = "user_login";                         // �����
	   static final String USER_PASSWORD = "password";                        // ������
	   
   
	   
// ����� ��� �������� ���� ������� � ����	   
		public void deleteAllPlistsInBase () throws Exception {
		      Connection connection = null; // manages connection
		      PreparedStatement preparedStatement = null;
				try
				{
					
			    	// ������������� ���������� � ����� ������  
			          connection = DriverManager.getConnection( 
			             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
			          
			          // ������� Statement ��� ������ ����
			          String query = "TRUNCATE TABLE plists";
			          preparedStatement = connection.prepareStatement(query);
			          preparedStatement.executeUpdate();
			          
			          // �������������� ���������
			          System.out.println("All entries in the database have been deleted.");

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}	   
	   
	//  ����� ��� ���������� plist � ���� ������
	public void addPlistToBase (List<Entry> entries) throws Exception {
		
	      Connection connection = null; // manages connection
	      PreparedStatement preparedStatement = null;
			try
			{

				
		    	// ������������� ���������� � ����� ������  
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		        // ������� Statement ��� ������ ����
		          String query = "INSERT INTO plists(channelName, groupTitle, channelUri, providerName, tvchId)"
		          		+ "  VALUES (?, ?, ?, ?, ?)";
		          preparedStatement = connection.prepareStatement(query);
                // ��������� ����������� ������� �� bean � Statement
					for (int i=0; i < entries.size(); ++i) {
						String chId = entries.get(i).getTvchId();

						preparedStatement.setString(1, entries.get(i).getChannelName());
						preparedStatement.setString(2, entries.get(i).getGroupTitle());
						preparedStatement.setString(3, entries.get(i).getChannelUri());
						preparedStatement.setString(4, entries.get(i).getProviderName());
						preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
						preparedStatement.executeUpdate();
		

					}
					
					// �������������� ���������
					System.out.println("Data added to the database.");
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

	//  ����� ��� ���������� plist � ���� ������	
	public void apdatePlistToBase (List<Entry> entries) throws Exception {
		
	      Connection connection = null; // manages connection
	      PreparedStatement preparedStatement = null;
			try
			{

		    	// ������������� ���������� � ����� ������  
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		          
		        // ������� Statement ��� ������ ����
		          String query = "UPDATE plists SET channelName=?, groupTitle=?, channelUri=?, providerName=? WHERE tvchId=?";
		          preparedStatement = connection.prepareStatement(query);
		       // ��������� ����������� ������� �� bean � Statement
					for (int i=0; i < entries.size(); ++i) {
						String chId = entries.get(i).getTvchId();

						preparedStatement.setString(1, entries.get(i).getChannelName());
						preparedStatement.setString(2, entries.get(i).getGroupTitle());
						preparedStatement.setString(3, entries.get(i).getChannelUri());
						preparedStatement.setString(4, entries.get(i).getProviderName());
						preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
						preparedStatement.executeUpdate();
		
					}
					
				// �������������� ���������
				System.out.println("Data in the database has been updated.");
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	
	
	
// ����� ������ ������ �� ������� ���� � bean �� SQL �������
	public List<Entry> readDataFromBase (String query) throws Exception {
	      Connection connection = null; // manages connection
	      Statement statement = null; // query statement
	      ResultSet resultSet = null; // manages results

	      
	        List<Entry> entries = new ArrayList<Entry>();
	        
			try
			{

				// ������������� ���������� � ����� ������ 
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		          // create Statement for querying database INSERT INTO

	                statement = connection.createStatement();
	                // �������������� ResultSet ��������� �� query 
	                resultSet = statement.executeQuery(query);
	                Entry.Builder entry = null;
	                // ���������� ������ ������ ������� � entry
	                while(resultSet.next()){
		            	entry = new Entry.Builder()
		            			.tvchId(resultSet.getString(1))
		            			.channelName(resultSet.getString(2))
		            			.groupTitle(resultSet.getString(3))
		            			.channelUri(resultSet.getString(4))
		            			.providerName(resultSet.getString(5));
		            	// ��������� entry � List
		            	entries.add(entry.build());

	                }

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		// ���������� ������	
		  return entries;

		}	
	
	
	
	

}
