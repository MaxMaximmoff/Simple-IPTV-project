/*
* Created by Max Maximoff on 11/07/2020.
* Класс для работы с базой mySQL
*/
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class SqlBase {
	
                           
	   static final String DATABASE_URL = "jdbc:mysql://localhost/plistsDB";  // Адрес базы
	   static final String USER_LOGIN = "user_login";                         // Логин
	   static final String USER_PASSWORD = "password";                        // Пароль
	   
   
	   
// Метод для удаления всех записей в базе	   
		public void deleteAllPlistsInBase () throws Exception {
		      Connection connection = null; // manages connection
		      PreparedStatement preparedStatement = null;
				try
				{
					
			    	// установливаем соединение с базой данных  
			          connection = DriverManager.getConnection( 
			             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
			          
			          // создаем Statement для опроса базы
			          String query = "TRUNCATE TABLE plists";
			          preparedStatement = connection.prepareStatement(query);
			          preparedStatement.executeUpdate();
			          
			          // Информационное сообщение
			          System.out.println("All entries in the database have been deleted.");

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}	   
	   
	//  Метод для добавления plist в базу данных
	public void addPlistToBase (List<Entry> entries) throws Exception {
		
	      Connection connection = null; // manages connection
	      PreparedStatement preparedStatement = null;
			try
			{

				
		    	// установливаем соединение с базой данных  
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		        // создаем Statement для опроса базы
		          String query = "INSERT INTO plists(channelName, groupTitle, channelUri, providerName, tvchId)"
		          		+ "  VALUES (?, ?, ?, ?, ?)";
		          preparedStatement = connection.prepareStatement(query);
                // добавляем поэлементно строоки из bean в Statement
					for (int i=0; i < entries.size(); ++i) {
						String chId = entries.get(i).getTvchId();

						preparedStatement.setString(1, entries.get(i).getChannelName());
						preparedStatement.setString(2, entries.get(i).getGroupTitle());
						preparedStatement.setString(3, entries.get(i).getChannelUri());
						preparedStatement.setString(4, entries.get(i).getProviderName());
						preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
						preparedStatement.executeUpdate();
		

					}
					
					// Информационное сообщение
					System.out.println("Data added to the database.");
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

	//  Метод для обновления plist в базе данных	
	public void apdatePlistToBase (List<Entry> entries) throws Exception {
		
	      Connection connection = null; // manages connection
	      PreparedStatement preparedStatement = null;
			try
			{

		    	// установливаем соединение с базой данных  
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		          
		        // создаем Statement для опроса базы
		          String query = "UPDATE plists SET channelName=?, groupTitle=?, channelUri=?, providerName=? WHERE tvchId=?";
		          preparedStatement = connection.prepareStatement(query);
		       // добавляем поэлементно строоки из bean в Statement
					for (int i=0; i < entries.size(); ++i) {
						String chId = entries.get(i).getTvchId();

						preparedStatement.setString(1, entries.get(i).getChannelName());
						preparedStatement.setString(2, entries.get(i).getGroupTitle());
						preparedStatement.setString(3, entries.get(i).getChannelUri());
						preparedStatement.setString(4, entries.get(i).getProviderName());
						preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
						preparedStatement.executeUpdate();
		
					}
					
				// Информационное сообщение
				System.out.println("Data in the database has been updated.");
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	
	
	
// Метод чтения данных из таблицы базы в bean по SQL запросу
	public List<Entry> readDataFromBase (String query) throws Exception {
	      Connection connection = null; // manages connection
	      Statement statement = null; // query statement
	      ResultSet resultSet = null; // manages results

	      
	        List<Entry> entries = new ArrayList<Entry>();
	        
			try
			{

				// установливаем соединение с базой данных 
		          connection = DriverManager.getConnection( 
		             DATABASE_URL, USER_LOGIN, USER_PASSWORD );
		          // create Statement for querying database INSERT INTO

	                statement = connection.createStatement();
	                // Подготавливаем ResultSet зависящий от query 
	                resultSet = statement.executeQuery(query);
	                Entry.Builder entry = null;
	                // Записываем каждую строку таблицы в entry
	                while(resultSet.next()){
		            	entry = new Entry.Builder()
		            			.tvchId(resultSet.getString(1))
		            			.channelName(resultSet.getString(2))
		            			.groupTitle(resultSet.getString(3))
		            			.channelUri(resultSet.getString(4))
		            			.providerName(resultSet.getString(5));
		            	// добавляем entry в List
		            	entries.add(entry.build());

	                }

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		// возвращаем список	
		  return entries;

		}	
	
	
	
	

}
