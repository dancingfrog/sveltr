package com.pb.statsapi.app;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.pb.statsapi.config.Config;
import com.pb.statsapi.util.Stats;


public class UIFilters {
	String _message="";
	java.sql.Date _date;
	boolean _flag = false;
	
	private String driver = "";
	private String url = "";
	private String username = "";
	private String password = "";
	//Connection conn;

	public UIFilters() throws ClassNotFoundException {
		driver = "com.mysql.jdbc.Driver";
		if (driver != null) {
			Class.forName(driver);
		}

		url = "jdbc:mysql://152.144.227.109/dbstats";
		username = "dev";
		password = "dev";
	}

	public static void main(String[] args) throws Exception, IOException, SQLException {
		//new LoadGeocodedFireDepartmentData().getFilesFromFolder();
		new UIFilters().getProductline();
		//new UIResources().getProductline();
		//new GetProductMaster().getProducts("GEM");
		//new UIFilters().getSecondFilterData("GEPropertyAttributesUltimate");
		//new UIFilters().getFips("GEPropertyAttributesUltimate", "CA");
		//new UIFilters().test();
		//new GetProductMaster().getSecondFilterData("PropertyAttributeGEM");
		//new GetProductMaster().getSecondFilter("PropertyAttributes", "PropertyAttributeParcelClassic");
		//new GetProductMaster().getProductTree();
		//new GetProductMaster().getCountryList();
		//new GetProductMaster().getFips("PropertyAttributeParcelPlus", "CT");

	}

/**
 * Getting main product stats page
 * @return
 */
	public JSONArray getProductData()
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "Select * from productmaster";
			//System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);		
		return json;
	}
	
//***************************************************************************************************************************************************
	public JSONArray getProductline()
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "Select distinct product_line from filters";
			//System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);
		
		return json;
	}
//************************************************************************************************************************
	public JSONArray getProducts(String productline)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "SELECT distinct product_line,products FROM `filters` where product_line = '"+productline+"'";
			//System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);
		
		return json;
	}	
//************************************************************************************************************************	
	public JSONArray getFirstFilter(String product)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		String tablename = null;
		String column_name = null;
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
				(product.toLowerCase().equals("propertyattributeparcelpremium")))
			tablename = "pap_metrics";
		else if(product.toLowerCase().equals("gepropertyattributesultimate"))
			tablename = "pag_metrics";
	
		
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);
			
			if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) || (product.toLowerCase().equals("propertyattributeparcelpremium")) ||
					(product.toLowerCase().equals("gepropertyattributesultimate")) || (product.toLowerCase().equals("propertyattributegem")))
			{
				sql = "SELECT distinct stabb as 'filter2' FROM "+tablename+" order by stabb";
			}
			else if(product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
			{
				sql = "SELECT distinct filter2display,coveragemap FROM filters where products = 'PropertAttributeParcelPremiumHistoric' ";
			}
			else
			{
				sql = "SELECT distinct filter2display,coveragemap FROM filters where products = '"+product+"'";
			}
			
			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
			      for (int i=1; i<numColumns+1; i++) {
			          column_name = rsmd.getColumnName(i);

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
		//System.out.println(json);
		
		return json;
	}	
	
//************************************************************************************************************************	
	public JSONArray getFips(String product,String state)
	{
		String tablename = "";
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
				(product.toLowerCase().equals("propertyattributeparcelpremium")))
			tablename = "pap_metrics";
		else if(product.toLowerCase().equals("gepropertyattributesultimate"))
			tablename = "pag_metrics";

		
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "select distinct fips FROM "+tablename+" where stabb = '"+state+"' order by fips ASC;";
			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);
		
		return json;
	}
//************************************************************************************************************************
//************************************************************************************************************************
	
public JSONArray getSecondFilterData(String product)
{
	JSONArray json = new JSONArray();
	String sql = null;
	String tablename = null;
	String column_name = null;
	
	if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
			(product.toLowerCase().equals("propertyattributeparcelpremium")))
		tablename = "pap_metrics";
	else if(product.toLowerCase().equals("gepropertyattributesultimate"))
		tablename = "pag_metrics";
	
	try
	{
		Connection conn = DriverManager.getConnection(url, username, password);
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) || (product.toLowerCase().equals("propertyattributeparcelpremium")) ||
				(product.toLowerCase().equals("gepropertyattributesultimate")) || (product.toLowerCase().equals("propertyattributegem")))
		{
			sql = "SELECT distinct stabb as 'filter1' FROM "+tablename+" order by stabb";
		}
		else			
			sql = "SELECT distinct filter1 FROM `filters` where products = '"+product+"'";
		
		
		System.out.println(sql);
		Statement stmt = null;
		try {

			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			json = new JSONArray();
		    ResultSetMetaData rsmd = rs.getMetaData();

		    while(rs.next()) {
		      int numColumns = rsmd.getColumnCount();
		      JSONObject obj = new JSONObject();
		      
		      for (int i=1; i<numColumns+1; i++) {
		          column_name = rsmd.getColumnLabel(i);

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
	//System.out.println(json);
	
	return json;
}	
		
//************************************************************************************************************************	
	public JSONArray getSecondFilter(String product,String filter1)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			if(filter1 == null)
				sql = "SELECT distinct filter2 FROM `filters` where products = '"+product+"'";
			else
				sql = "SELECT distinct filter2,coveragemap FROM `filters` where products = '"+product+"' and filter1='"+filter1+"'";
			
			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);
		
		return json;
	}	
	
//************************************************************************************************************************	
	public JSONArray getProductTree()
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "SELECT * from filters";
			
			//System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				json = new JSONArray();
			    ResultSetMetaData rsmd = rs.getMetaData();

			    while(rs.next()) {
			      int numColumns = rsmd.getColumnCount();
			      JSONObject obj = new JSONObject();
			      
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
		//System.out.println(json);
		
		return json;
	}		
	
}
//************************************************************************************************************************

