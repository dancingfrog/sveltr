package com.ericsson.include;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

public class MSSQLDBConnection 
{
	private String url;
	private static MSSQLDBConnection instance;
	private static String driver;
	private static String user;
	private static String password;
	private static String db;
	private static String host;
	private static String port;
	private static String _dbtype=null;
	
	private MSSQLDBConnection()
	{
		try 
		{
			Hashtable<String, String> htConf = null;
			
				htConf =new AppConf().readConfig("mssql_" + _dbtype.toLowerCase(),"host,port,dbname,uid,pwd");
				
				driver		= "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				port		= htConf.get("port");		
				db			= htConf.get("dbname");	
				host		= htConf.get("host");		
				user 		= htConf.get("uid");		
				url 		= "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + db + ";selectMethod=cursor";
				//System.out.println(url);
				password	= htConf.get("pwd");
				Class.forName(driver);
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	

	public static Connection getConnection(String dbtype) throws SQLException 
	{
		if ((_dbtype == null) || (_dbtype != dbtype))
		{
			instance = null;
		}
		
		if (instance == null) 
		{
			_dbtype = dbtype;
			instance = new MSSQLDBConnection();
		}
		
		try 
		{
			return DriverManager.getConnection(instance.url,user,password);
		} 
		catch (SQLException e) 
		{
			System.out.println(e.toString());
			throw e;
		}
	}
	

	public static void close(Connection connection)
	{
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
