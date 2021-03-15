package com.ericsson.include;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;


public class DBConnection extends InitConf {
  private static final Logger log = Logger.getLogger(DBConnection.class);
  private String url;
  private static DBConnection instance;
  private static String driver;
  private static String user;
  private static String password;

  private DBConnection() {
    AppConf objAPPCONF;
    try {
      objAPPCONF = new AppConf();

      driver = objAPPCONF.getTagValue("db_driver");
      url = objAPPCONF.getTagValue("db_url");
      user = objAPPCONF.getTagValue("db_user");
      password = objAPPCONF.getTagValue("db_pwd");

      Class.forName(driver);
    } catch (Exception e) {
      log.warn(e);
    }
  }


  public static Connection getConnection() throws SQLException {
	 
	 // MYSQLDB.instance = null;
	  DBConnection.instance = null;
    if (instance == null) {
      instance = new DBConnection();
    }
    try {
      return DriverManager.getConnection(instance.url, user, password);
    } catch (SQLException e) {
      log.warn(e);
      throw e;
    }
  }
  
  //*****************************For Netran***************************************************
  public static Connection getConnectionMW(int circle_id) throws SQLException {
	  //MYSQLDB.instance = null;
	  //System.out.println(instance);
	  DBConnection.instance = null;
	    if (instance == null) {
	      instance = new DBConnection(circle_id);
	    }
	    try {
	      return DriverManager.getConnection(instance.url, user, password);
	    } catch (SQLException e) {
	      log.warn(e);
	      throw e;
	    }
	  }
  
  
  private DBConnection(int circle_id) {
	    AppConf objAPPCONF;
	    try {
	      objAPPCONF = new AppConf();

	      driver = objAPPCONF.getTagValue("db_"+circle_id+"_driver");
	      url = objAPPCONF.getTagValue("db_"+circle_id+"_url");
	      user = objAPPCONF.getTagValue("db_"+circle_id+"_user");
	      password = objAPPCONF.getTagValue("db_"+circle_id+"_pwd");
	      
	      System.out.println(url);

	      Class.forName(driver);
	    } catch (Exception e) {
	      log.warn(e);
	    }
	  }
  
 //***************************************************************************************** 

  public static void close(Connection connection) {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      log.warn(e);
    }
  }
}
