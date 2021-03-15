package com.pb.ddd.delivery.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public enum PropertyUtil {

	INSTANCE;
	private Properties properties;
	private HashMap<String, String> map = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	private PropertyUtil() {
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/application.properties");
			properties = new Properties();
			properties.load(inputStream);
			Set<String> keys = ((Map) properties).keySet();
			for (String key : keys) {
				map.put(key, properties.getProperty(key));
			}
		} catch (IOException ex) {
			System.out.println(ex);
		}

	}

	public String getValue(String key) {
		String value = map.get(key);
		return value;
	}

}
