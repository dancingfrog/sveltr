package com.ericsson.include;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class AppConf {
  String host = "";
  String port = "";
  String dbname = "";
  String uid = "";
  String pwd = "";
  String path = "";

  // class constructor
  public AppConf() {

    try {

      // path = "C:\\apache-tomcat-7.0.53\\webapps\\VFNetherlandWS\\WEB-INF\\config.properties";

      path = "C:\\blazeds\\tomcat\\webapps\\netran.properties";

      // path = "C:\\blazeds\\tomcat\\app_config\\config_airtel_bangladesh.properties";

    } catch (Exception ex) {
      path = "";
      System.out.println("Config file not found " + ex.getMessage());
    }
  }

  public Hashtable<String, String> readConfig(String keytag, String parm) {
    Hashtable<String, String> htConf = new Hashtable<String, String>();
    if (!path.equals("")) {
      // create an instance of properties class
      Properties props = new Properties();

      // try retrieve data from file
      try {
        // System.out.println("Path:" + path);
        props.load(new FileInputStream(path));
        String[] listParm = parm.split(",");
        for (int i = 0; i < listParm.length; i++) {
          htConf.put(listParm[i].toString(),
              props.getProperty(keytag + "_" + listParm[i].toString()));
        }
      }
      // catch exception in case properties file does not exist
      catch (IOException e) {
        // e.printStackTrace();
        System.out.println("IOException;\t user.authenticate(" + keytag + "," + parm + ") \t "
            + e.getMessage() + "\t" + e.toString());
      }
    }
    return htConf;
  }

  public String getTagValue(String keytag) {
    String tagValue = "";
    if (!path.equals("")) {
      // create an instance of properties class
      Properties props = new Properties();

      // try retrieve data from file
      try {
        props.load(new FileInputStream(path));
        tagValue = props.getProperty(keytag);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return tagValue;
  }
}
