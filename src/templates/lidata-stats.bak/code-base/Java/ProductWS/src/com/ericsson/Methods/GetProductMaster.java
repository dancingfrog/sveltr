package com.ericsson.Methods;
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

import com.ericsson.obj.voCountry;

public class GetProductMaster {
	String _message="";
	java.sql.Date _date;
	boolean _flag = false;

	public static void main(String[] args) throws Exception, IOException, SQLException {
		//new LoadGeocodedFireDepartmentData().getFilesFromFolder();
		//new GetProductMaster().getProductData();
		//new GetProductMaster().getProductline();
		//new GetProductMaster().getProducts("GEM");
		new GetProductMaster().getFirstFilter("Premium POI");
		//new GetProductMaster().getSecondFilterData("PropertyAttributeParcelPlus");
		//new GetProductMaster().getSecondFilter("AK", "All");
		//new GetProductMaster().getProductTree();
		//new GetProductMaster().getCountryList();
		//new GetProductMaster().getFips("PropertyAttributeParcelPlus", "CT");

	}
	

	
	public ArrayList<voCountry> getCountryList()
	{
		ArrayList<voCountry> lstCountry = new ArrayList<voCountry>();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");
			
			sql = "Select opensource as 'key', concat(wbo,';',mbi,';',gfk,';',poi,';',`RateCentreInfi/ExchangeInfoPlus`,';',EarthQuakeBundle,';',BaseDemographics,';',Cameo,';',StreetsRouting) as 'value' FROM `country_list`;";
			//System.out.println(sql);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			voCountry obj = new voCountry();
			
		      while (rs.next()) {
		          obj = new voCountry();
		          obj.setKey(rs.getString("key").toLowerCase());
		          obj.setValue(rs.getString("value").toLowerCase());
		          
		          lstCountry.add(obj);
		        }
		}
		catch(Exception ex)
		{
			_flag =  false;
			ex.printStackTrace();
		}
	    System.out.println("Size " + lstCountry.size());
	    return lstCountry;
	}
	
//***************************************************************************************************************************************************	

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
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

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
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

			sql = "Select distinct product_line from filters";
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
	public JSONArray getProducts(String productline)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

			sql = "SELECT distinct product_line,products FROM `filters` where product_line = '"+productline+"'";
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
	
	public JSONArray getFirstFilter(String product)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		String tablename = null;
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
				(product.toLowerCase().equals("propertyattributeparcelpremium")))
			tablename = "pap_metrics";
		else if(product.toLowerCase().equals("propertyattributefabric"))
			tablename = "paf_metrics";
		else if(product.toLowerCase().equals("propertyattributegem"))
			tablename = "pag_metrics";		
		
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");
			
			if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) || (product.toLowerCase().equals("propertyattributeparcelpremium")) ||
					(product.toLowerCase().equals("propertyattributefabric")) || (product.toLowerCase().equals("propertyattributegem")))
			{
				sql = "SELECT distinct stabb as 'filter2' FROM "+tablename+" order by stabb";
			}
			else
			{
				sql = "SELECT distinct filter2display,coveragemap FROM `filters` where products = '"+product+"'";
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
		System.out.println(json);
		
		return json;
	}	
	
//************************************************************************************************************************	
	public JSONArray getFips(String product,String state)
	{
		String tablename = "";
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
				(product.toLowerCase().equals("propertyattributeparcelpremium")))
			tablename = "pap_metrics";
		else if(product.toLowerCase().equals("propertyattributefabric"))
			tablename = "paf_metrics";
		else if(product.toLowerCase().equals("propertyattributegem"))
			tablename = "pag_metrics";
		
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

			sql = "select distinct fips FROM "+tablename+" where stabb = '"+state+"';";
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
		System.out.println(json);
		
		return json;
	}
//************************************************************************************************************************
//************************************************************************************************************************
	
public JSONArray getSecondFilterData(String product)
{
	JSONArray json = new JSONArray();
	String sql = null;
	String tablename = null;
	
	if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) ||
			(product.toLowerCase().equals("propertyattributeparcelpremium")))
		tablename = "pap_metrics";
	else if(product.toLowerCase().equals("propertyattributefabric"))
		tablename = "paf_metrics";
	else if(product.toLowerCase().equals("propertyattributegem"))
		tablename = "pag_metrics";
	
	try
	{
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");
		
		if((product.toLowerCase().equals("propertyattributeparcelplus")) || (product.toLowerCase().equals("propertyattributeparcelclassic")) || (product.toLowerCase().equals("propertyattributeparcelpremium")) ||
				(product.toLowerCase().equals("propertyattributefabric")) || (product.toLowerCase().equals("propertyattributegem")))
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
	System.out.println(json);
	
	return json;
}	
		
	//************************************************************************************************************************	
	public JSONArray getSecondFilter(String product,String filter1)
	{
		JSONArray json = new JSONArray();
		String sql = null;
		try
		{
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

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
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test", "dev", "dev");

			sql = "SELECT * from filters";
			
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
	
}
//************************************************************************************************************************

