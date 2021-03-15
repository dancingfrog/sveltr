package com.ericsson.include;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceLoader {

    private static ResourceLoader loader = null;
    private ResourceBundle resourceBundle;
    private final String PROPERTY_FILE_NAME = "config_path_routing_v3";

    static {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-mm-dd");
        System.setProperty("current.date", formatter.format(new Date()));
    }

    private ResourceLoader() throws MissingResourceException {
        load();
    }

    private void load() throws MissingResourceException {
        resourceBundle = ResourceBundle.getBundle(PROPERTY_FILE_NAME);
    }

    public String get(String key) {
        try {
            String foundString = resourceBundle.getString(key);
            return convertToUTF8(foundString);
        } catch (MissingResourceException e) {
            return "";
        }
    }

    private String convertToUTF8(String str) {
        try {
            return new String(str.getBytes("ISO-8859-1"), Charset.forName("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return str; // not real case
        }
    }

    public static ResourceLoader getInstance() {
        synchronized (ResourceLoader.class) {
            if (loader == null) {
                loader = new ResourceLoader();
            }
        }
        return loader;
    }

    public static void main(String[] args) {
        try {
            System.out.println(ResourceLoader.getInstance().get("test1.string1"));
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }
}
