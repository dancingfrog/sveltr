package com.ericsson.Methods;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ericsson.obj.voProduct;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class POIStats04072017 {
	String _message="";
	java.sql.Date _date;
	boolean _flag = false;

	public static void main(String[] args) throws Exception, IOException, SQLException {
		//new LoadGeocodedFireDepartmentData().getFilesFromFolder();
		//new POIStats().getPoiStats("risk_hail");
		new POIStats04072017().getCountryPoiStats("wppoi_country_category", "ALBANIA");
		//new POIStats().getCountries("wppoi_country_category");
	}

	
	public JSONArray getPoiStats(String tablename)
	{
		//ArrayList<voProduct> poiproduct = new ArrayList<voProduct>();
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/dbstatsdynamic", "dev", "dev");

			sql = "Select * from "+ tablename;
			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			     
			      Map<String, Object> obj = new LinkedHashMap<String, Object>();
			      
			      for (int i=1; i<numColumns+1; i++) {
			          String column_name = rsmd.getColumnName(i);
			          //System.out.println(column_name);

			          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
			              obj.put(column_name, rs.getArray(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
			              obj.put(column_name, rs.getBoolean(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
			              obj.put(column_name, rs.getBlob(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
			              obj.put(column_name, rs.getDouble(column_name)); 
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
			              obj.put(column_name, rs.getFloat(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
			              obj.put(column_name, rs.getNString(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
			              obj.put(column_name, rs.getString(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
			              obj.put(column_name, rs.getDate(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
			             obj.put(column_name, rs.getTimestamp(column_name));   
			             }
			             else{
			              obj.put(column_name, rs.getObject(column_name));
			             }
			      }
			      
			        
			      Iterator<Entry<String, Object>> itr= obj.entrySet().iterator();
			      JsonObject jsonobj = new JsonObject();
			      while(itr.hasNext()){
			    	  Entry<String, Object> en = itr.next();			    	 
			    	  jsonobj.addProperty(en.getKey(), en.getValue().toString());
			      }
			      
			      json.put(jsonobj);
			    }

			} finally {
				conn.close();
			}
		}
		catch(Exception ex)
		{
			_flag =  false;
			ex.printStackTrace();
		}
		
		//System.out.println(json);
		
		return json;
	}
	
//******************************************************************************************************************************	
	public JSONArray getCountryPoiStats(String tablename,String country)
	{
		//ArrayList<voProduct> poiproduct = new ArrayList<voProduct>();
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/dbstatsdynamic", "dev", "dev");

			
			
			if((tablename.equals("wppoi_country_category")) || (tablename.equals("wppoic_country_category")))
				sql = "Select * from "+ tablename + " where countries ='"+country+"'";
			else if((tablename.equals("wppoi_percentageattribute_fill_rates")) || (tablename.equals("wppoic_percentageattribute_fill_rates")))
				sql = "Select * from "+ tablename + " where iso3 ='"+country+"'";


			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			    	
				      int numColumns = rsmd.getColumnCount();					     
				      Map<String, Object> obj = new LinkedHashMap<String, Object>();
			      
			      for (int i=1; i<numColumns+1; i++) {
			          String column_name = rsmd.getColumnName(i);

			          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
			              obj.put(column_name, rs.getArray(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
			              obj.put(column_name, rs.getBoolean(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
			              obj.put(column_name, rs.getBlob(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
			              obj.put(column_name, rs.getDouble(column_name)); 
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
			              obj.put(column_name, rs.getFloat(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
			              obj.put(column_name, rs.getNString(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
			              obj.put(column_name, rs.getString(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
			              obj.put(column_name, rs.getInt(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
			              obj.put(column_name, rs.getDate(column_name));
			             }
			             else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
			             obj.put(column_name, rs.getTimestamp(column_name));   
			             }
			             else{
			              obj.put(column_name, rs.getObject(column_name));
			             }
			      }
			      Iterator<Entry<String, Object>> itr= obj.entrySet().iterator();
			      JsonObject jsonobj = new JsonObject();
			      while(itr.hasNext()){
			    	  Entry<String, Object> en = itr.next();			    	 
			    	  jsonobj.addProperty(en.getKey(), en.getValue().toString());
			      }
			      json.put(obj);
			      
			    }
				 


			} finally {
				conn.close();
			}
		}
		catch(Exception ex)
		{
			_flag =  false;
			ex.printStackTrace();
		}
		System.out.println(json);
		
		return json;
	}
//************************************************************************************************************************
	public ArrayList<voProduct> getCountries(String _tablename)
	{
		ArrayList<voProduct> country = new ArrayList<voProduct>();
		String sql = null;
		boolean _flag = false;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/dbstatsdynamic", "dev", "dev");
			
			if((_tablename.equals("wppoi_country_category")) || (_tablename.equals("wppoic_country_category")))
			{
				sql = "SELECT distinct countries FROM "+_tablename;
			
			}
			else if((_tablename.equals("wppoi_percentageattribute_fill_rates")) || (_tablename.equals("wppoic_percentageattribute_fill_rates")))
			{
				sql = "SELECT distinct iso3 FROM "+_tablename;
				_flag = true;
			}
			

			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				 
				voProduct productobj = null;
	             while (rs.next()) {
	            	 productobj = new voProduct();
	            	 if(_flag)
	            	 {
	            		 productobj.setCountry(rs.getString("iso3"));
	            	 }
	            	 else
	            	 {
	            		 productobj.setCountry(rs.getString("countries"));
	            	 }
	            	 	            	 
	            	 country.add(productobj);
	             }
	             System.out.println("collected Country Data :: " + country.size());

			} finally {
				conn.close();
			}
		}
		catch(Exception ex)
		{
			_flag =  false;
			ex.printStackTrace();
		}
		return country;
	}
//************************************************************************************************************************

}
