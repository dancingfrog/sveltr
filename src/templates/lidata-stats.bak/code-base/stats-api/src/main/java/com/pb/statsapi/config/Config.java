package com.pb.statsapi.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
	private static Config instance;
	private Properties properties;
	private static String propertiesFileName;

	private Config() {
		URL _url = Thread.currentThread().getContextClassLoader().getResource("application.properties");
		String path="";
        if (_url != null) {
            path = _url.getPath();
            path = path.replaceFirst("^/(.:/)", "$1");
            System.out.println("    absolute resource path found :\n    " + path);
            //String s = new String(Files.readAllBytes(Paths.get(path)));
            //System.out.println("    file content: "+s);
            //input = Thread.currentThread().getContextClassLoader().getResourceAsStream(s);
        } else {
            System.out.println("    No Resource Found: application.properties" );
        }
        
		try {
			if((propertiesFileName == null) || (propertiesFileName.equals("")))
				propertiesFileName = path;
			
			
			InputStream resourceAsStream = new FileInputStream(new File(propertiesFileName));
			this.properties = new Properties();
			properties.load(resourceAsStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setPropertiesFile(String propertiesFileName) {
		Config.propertiesFileName = propertiesFileName;
	}

	public String getDriver() {
		return properties.getProperty("jdbc.driver");
	}

	public String getUrl() {
		return properties.getProperty("jdbc.url");
	}

	public String getUserName() {
		return properties.getProperty("jdbc.username");
	}

	public String getPassword() {
		return properties.getProperty("jdbc.password");
	}

	public String getParcelPremiumColumns() {
		return properties.getProperty("parcelpremium");
	}

	public String getParcelPlusColumns() {
		return properties.getProperty("parcelplus");
	}

	public String getParcelClassicColumns() {
		return properties.getProperty("parcelclassic");
	}

	public String getParcelPremiumHistoricColumns() {
		return properties.getProperty("parcelpremiumhistoric");
	}

	public String getParcelPremiumStateColumns() {
		return properties.getProperty("stateparcelpremium");
	}

	public String getParcelPlusStateColumns() {
		return properties.getProperty("stateparcelplus");
	}

	public String getParcelClassicStateColumns() {
		return properties.getProperty("stateparcelclassic");
	}

	public String getParcelPremiumFipsColumns() {
		return properties.getProperty("fipsparcelpremium");
	}

	public String getParcelPlusFipsColumns() {
		return properties.getProperty("fipsparcelplus");
	}

	public String getParcelClassicFipsColumns() {
		return properties.getProperty("fipsparcelclassic");
	}

	public String getPropertyAttributesUltimateColumns() {
		return properties.getProperty("completepropertyattributesultimate");
	}

	public String getPropertyAttributesUltimateStateColumns() {
		return properties.getProperty("statepropertyattributesultimate");
	}

	public String getPropertyAttributesUltimateFipsColumns() {
		return properties.getProperty("fipspropertyattributesultimate");
	}

	public static Config getInstance() {
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					Config.instance = new Config();
				}
			}
		}
		return instance;
	}
}
