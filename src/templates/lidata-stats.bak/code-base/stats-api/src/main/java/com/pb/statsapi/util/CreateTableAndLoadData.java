package com.pb.statsapi.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

import com.csvreader.CsvReader;

/**
 * @author MO009KH
 *
 */
public class CreateTableAndLoadData {
	
	private String[] headerdata = null;
	private String[] arrtable = null;
	private String sql = null;
	private Statement statement = null;
	private Connection conn = null;
	private PreparedStatement ps = null;
	private String _tablename = null;
	private int att_table =0;
	private int _cnt = 0;
	private String filter1 = null;
	
	
    /**
     * 
     * creates the schema in the database for the data
     * 
     * @param schemaprefix ... the prefix for the schema
     * @param _filename ... name of the file for the directory that we are parsing
     * @param filetype ... xls or csv
     * @param _directory ... name of the directory for which we are parsing the files
     * @param _productline ... parent folder of the current processing directory
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws SQLIntegrityConstraintViolationException
     */
    public void createSchema(String schemaprefix,String _filename,String filetype,String _directory,String _productline) throws FileNotFoundException,IOException,SQLException,ClassNotFoundException,SQLIntegrityConstraintViolationException
    {

		try {
			
			CsvReader products = new CsvReader("C:/output/"+_filename+".csv");
			
			products.readHeaders();
			//products = head(products,10);
			headerdata = products.getHeaders();
			
			
			if((_filename.toLowerCase().equals("metrics")) && (schemaprefix != null))
				_tablename = schemaprefix +"_"+_filename;
			else if((_filename.toLowerCase().equals("metrics")) && (schemaprefix.equals(null)))
			{
				_cnt = _cnt + 1;
				_tablename = _cnt +"_"+_filename;
			}

			else
			{
				_tablename = _filename;
			}
			
			if(filetype.equals("xlsx"))
			{
				if(schemaprefix.equals("pag")||schemaprefix.equals("paf")||schemaprefix.equals("pap"))
				{
					String tabpart2 = null;
					if(_filename.contains(" ")){
						tabpart2 = _filename.substring(_filename.indexOf(" "));
						if(tabpart2.indexOf("Fill") > 0||tabpart2.indexOf("fill")>0)
						{
							arrtable = _filename.split(" ");
							
								filter1 = arrtable[arrtable.length - 3];
							
							System.out.println(filter1);
							_tablename = arrtable[arrtable.length - 3] +"_" +arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
							//_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							_tablename = _tablename.toLowerCase();
						}
						else if(tabpart2.indexOf("Metrics") > 0||tabpart2.indexOf("metrics")>0)
						{
							arrtable = _filename.split(" ");
							
								filter1 = arrtable[arrtable.length - 2];
							
							System.out.println("Metrics"+filter1);
							_tablename = arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
							//_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							_tablename = _tablename.toLowerCase();
						}
					}
					
				}
				if(schemaprefix.equals("school_metrics"))
				{
					if(_filename.equals("School Metrics"))
					{
						_tablename = schemaprefix.toLowerCase();
					}
					else if(_filename.equals("School MX Id Changed"))
					{
						_tablename = _filename.replace(" ", "_").toLowerCase();
					}
					else
					{
						//_tablename = schemaprefix.toLowerCase() + "_" + _filename.replace(" ", "_").toLowerCase();
						_tablename = _filename.replace(" ", "_").toLowerCase();
					}
				}else if(schemaprefix.equals("geofence"))
				{
					if(_filename.equals("Geofence Total Metrics"))
					{
						_tablename = schemaprefix.toLowerCase() + "_metrics";
					}
					else
					{
						//_tablename = schemaprefix + "_"+_filename.replace(" ", "_").toLowerCase();
						_tablename = _filename.replace(" ", "_").toLowerCase();
					}
				}
				else if(schemaprefix.equals("gzt"))
				{
					_tablename = schemaprefix + "_metrics";
					
				}
				else
				{
					String tabpart = null;
					if(_filename.contains(" ")){
						tabpart = _filename.substring(_filename.indexOf(" "));
						
					//TODO
						if((tabpart.trim().equals("Object not in Place")) || (tabpart.trim().equals("Object not in CBSA")))
						{
							arrtable = _filename.split(" ");
							_tablename = arrtable[arrtable.length - 3] +"_" +arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
							_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							//_tablename = _tablename.toLowerCase();
						}
						else if(_filename.contains("%")){
							_tablename = _filename.replace("%","percentage");
							_tablename = _tablename.replace(" ","_");
							if(schemaprefix.equals("wppoi")||schemaprefix.equals("wppoic")){
								_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								//_tablename = _tablename.toLowerCase();
								att_table = 1;
							}
							else if(schemaprefix!=null){
								_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								//_tablename = _tablename.toLowerCase();
							}
							_tablename = _tablename.toLowerCase();
						}
						else if(tabpart.indexOf("Fill") > 0)
						{
							arrtable = _filename.split(" ");
							_tablename = arrtable[arrtable.length - 3] +"_" +arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
							//_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							_tablename = _tablename.toLowerCase();
						}
						else if( tabpart.indexOf("&")>0)
						{
							arrtable = _filename.split(" ");
							_tablename = arrtable[arrtable.length - 3] +"_"+arrtable[arrtable.length - 1];
							_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							//_tablename = _tablename.toLowerCase();
						}
						
						else if( tabpart.indexOf("by")>0 || tabpart.indexOf("By")>0)
						{
							/*arrtable = _filename.split(" ");
							_tablename = arrtable[arrtable.length - 3] +"_" +arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
							_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();*/
							_tablename = _tablename.trim();
							_tablename = _filename.replace(" ","_");
							if(schemaprefix!=null){
								if((_directory.toLowerCase().equals("social places")) || (_directory.toLowerCase().equals("world premium plus points of interest")) || (_directory.toLowerCase().equals("world premium plus points of interest csmr"))
										|| (_directory.toLowerCase().equals("world premium points of interest")) || (_directory.toLowerCase().equals("world premium points of interest csmr")))
									_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								else
									_tablename =  _tablename.toLowerCase();
							}
							_tablename = _tablename.trim().toLowerCase();
							_tablename = _tablename.replace(".", "_");
							_tablename = _tablename.replace("+", "plus_");

						}
						else
						{	
							
							arrtable = _filename.split(" ");
							if(_filename.contains("Metro")||_filename.contains("Base")){
								_tablename = _filename.replace(" ","_");
								_tablename = _tablename.replace(".", "_");
								if(schemaprefix!=null){
									if(_directory.toLowerCase().equals("social places")) 
										_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
									else
										_tablename =  _tablename.toLowerCase();
								}
								_tablename = _tablename.toLowerCase();
								_tablename = _tablename.replace("+", "plus_");

								
							}
							else if(arrtable.length>2){
								_tablename = arrtable[arrtable.length - 3]+"_"+arrtable[arrtable.length - 2]+"_"+arrtable[arrtable.length - 1];
								//_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								_tablename = _tablename.toLowerCase();
								_tablename = _tablename.replace(".", "_");
								_tablename = _tablename.replace("+", "plus_");


							}
							else if(arrtable.length>1){
								_tablename = arrtable[arrtable.length - 2]+"_"+arrtable[arrtable.length - 1];
								if((_directory.toLowerCase().equals("world premium plus points of interest")) || (_directory.toLowerCase().equals("world premium plus points of interest csmr"))
										|| (_directory.toLowerCase().equals("world premium points of interest")) || (_directory.toLowerCase().equals("world premium points of interest csmr")))
									_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								else
									_tablename = _tablename.toLowerCase();
								
								//_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								//_tablename = _tablename.toLowerCase();
								_tablename = _tablename.replace(".", "_");
								_tablename = _tablename.replace("+", "plus_");


							}
							else{
								_tablename = arrtable[arrtable.length - 1];
								if((_directory.toLowerCase().equals("world premium plus points of interest")) || (_directory.toLowerCase().equals("world premium plus points of interest csmr"))
										|| (_directory.toLowerCase().equals("world premium points of interest")) || (_directory.toLowerCase().equals("world premium points of interest csmr")))
									_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
								else
									_tablename = _tablename.toLowerCase();
								
								_tablename = _tablename.replace(".", "_");
								_tablename = _tablename.replace("+", "plus_");


							}
						}
					}
					else{
						_tablename = _filename;
						if((_directory.toLowerCase().equals("world premium plus points of interest")) || (_directory.toLowerCase().equals("world premium plus points of interest csmr"))
								|| (_directory.toLowerCase().equals("world premium points of interest")) || (_directory.toLowerCase().equals("world premium points of interest csmr")))
							_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
						else if(schemaprefix.toLowerCase().equals("gem"))
							_tablename = schemaprefix +"_"+_filename.toLowerCase();
						else if(schemaprefix.toLowerCase().equals("fabric"))
							_tablename = schemaprefix +"_"+_filename.toLowerCase();
						else
							_tablename = _tablename.toLowerCase();
						
						
					}
				}
				
			}
			//TODO
			else
			{
				String tabpart = null;
				if(_filename.contains(" ")){
					tabpart = _filename.substring(_filename.indexOf(" "));
				//TODO
					if((tabpart.trim().equals("Object not in Place")) || (tabpart.trim().equals("Object not in CBSA")))
					{
						arrtable = _filename.split(" ");
						_tablename = arrtable[arrtable.length - 3] +"_" +arrtable[arrtable.length - 2] + "_"+arrtable[arrtable.length - 1];
						if(schemaprefix!=null){
							//_tablename = schemaprefix.toLowerCase() +"_" +_tablename.toLowerCase();
							_tablename = _tablename.toLowerCase();
						}
					}
					else
					{
						_tablename = _filename.replace(" ","_");
						if(schemaprefix!=null){
							//_tablename = schemaprefix.toLowerCase() + "_" + _tablename;
							_tablename = _tablename.toLowerCase();
						}
						_tablename = _tablename.toLowerCase();
					}
				}
				else{
					_tablename = _tablename.toLowerCase();
					
				}
			}
			
			//checking for map coverage
			String coveragemap = null;
			for(int i = 0;i<products.getHeaderCount();i++)
			{
				if((headerdata[i].toLowerCase().equals("country")) || (headerdata[i].toLowerCase().equals("state")))
				{
					coveragemap = "yes";
					break;
				}
				else
				{
					coveragemap = "no";
				}
					
			}
			
//			Class.forName("com.mysql.jdbc.Driver");
//			conn = DriverManager.getConnection(Stats.connUrl);
			
			Connection conn = DriverManager.getConnection(Stats.url, Stats.username, Stats.password);
			statement = conn.createStatement();
			
			sql = "DROP TABLE IF EXISTS `"+_tablename+"`;";
			statement.execute(sql);				
			
			sql = "";
			sql = "CREATE TABLE  `"+_tablename+"` ( ";
			
			if((_tablename.equals("populated_column_count")) || (_tablename.equals("populated_column_percentage")))
			{
				for(int i = 0;i<products.getHeaderCount();i++)
				{
					if(i < 4)
						sql = sql + "`"+headerdata[i]+"` varchar(100) NOT NULL DEFAULT '',";
					else
						sql = sql + "`"+headerdata[i]+"` float NOT NULL DEFAULT '0',";
				}
			}
			else if(_tablename.equals("globaldatacoverage")){
				for(int i = 0;i<products.getHeaderCount();i++)
				{
					 sql = sql + "`"+headerdata[i]+"` varchar(80) NOT NULL DEFAULT '',";
				}
			}
			else if((_tablename.equals("pap_metrics")) || (_tablename.equals("pag_metrics")) || (_tablename.equals("paf_metrics")) || (_tablename.equals("us_propertyattrbutefabricdata"))
					 || (_tablename.equals("us_propertyattrbutegem"))  || (_tablename.equals("us_propertyattrbutegemdata")) ){
				for(int i = 0;i<products.getHeaderCount();i++)
				{
					 sql = sql + "`"+headerdata[i]+"` varchar(45) NOT NULL DEFAULT '',";
				}
			}
			else if((_tablename.equals("risk_earthquake")) || (_tablename.equals("risk_firestations")) || (_tablename.equals("risk_floodriskpro"))
					 || (_tablename.equals("risk_hail")) || (_tablename.equals("risk_hurricane")) || (_tablename.equals("risk_tornado"))
					 || (_tablename.equals("risk_wind")) || (_tablename.equals("risk_world_earthquake")) 
					)
			{
				for(int i = 0;i<products.getHeaderCount();i++)
				{
					 sql = sql + "`'"+headerdata[i]+"'` varchar(250) NOT NULL DEFAULT '',";
				}
			}
			else
			{
				//todo
				String[] dup_column = new String[products.getHeaderCount()];
				for(int i = 0;i<products.getHeaderCount();i++)
				{
					//kunal edit
					String temp = new String(headerdata[i]);
					for(int k=0;k<i;k++){
						if(dup_column[k].equals(temp)){	
							temp = temp + Integer.toString(i);
						}
					}
					dup_column[i] = new String(temp);
					//System.out.println(temp);
					temp = temp.replace(".", "_");
					//System.out.println("temp"+temp);
					temp = temp.replace("(","");
					temp = temp.replace(")","");
					
					 sql = sql + "`"+temp+"` varchar(100) NOT NULL DEFAULT '',";
				}
			}
			
			sql = sql.substring(0, sql.length()-1);
			
			if((_tablename.equals("pap_metrics")) || (_tablename.equals("pag_metrics")) || (_tablename.equals("paf_metrics")) ||(_tablename.equals("us_propertyattrbutefabricdata")) || (_tablename.equals("us_propertyattrbutegemdata")))
			{
				sql = sql +	") ENGINE=MyISAM DEFAULT CHARSET=utf8;";
			}else{
				sql = sql +	") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			}
			
			System.out.println(sql);
			statement.execute(sql);	
			
			if((_tablename.equals("productmaster")) ){
				sql = "ALTER TABLE "+_tablename+" ADD id INT PRIMARY KEY AUTO_INCREMENT;";
				statement.execute(sql);	
			}
			
			sql = "";
			products.close();
			
			loadData(_filename,_tablename,filetype,_directory,_productline,coveragemap);
			
		}
		 catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}catch(SQLSyntaxErrorException e){
			e.printStackTrace();
			//System.exit(1);
		}
		catch(SQLIntegrityConstraintViolationException e){
			e.printStackTrace();
			//System.exit(1);
		}
		finally {
			if (statement != null) {
				statement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		
    	
    }
    
    /**
     * 
     * loads the data from excel or csv to the table created on runtime as per the data and 
     * also inserts the information to the filters table to be used by front-end
     * 
     * @param _filename ... name of the file for the directory that we are parsing
     * @param _tablename ... name of the table created for the file
     * @param filetype ... xls or csv
     * @param _directory ... name of the directory for which we are parsing the files
     * @param _productline ... parent folder of the current processing directory
     * @param _coveragemap
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws SQLIntegrityConstraintViolationException
     */
    private void loadData(String _filename,String _tablename,String filetype,String _directory,String _productline,String _coveragemap) throws FileNotFoundException,IOException,SQLException,ClassNotFoundException,SQLIntegrityConstraintViolationException
    {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = null;
		String _filter2display = null;
		
		if(filetype.equals("xlsx"))
		{
			if((_tablename.contains("nbrhood")) || (_tablename.contains("social_places")) || (_tablename.contains("school")) || (_tablename.contains("auto_dealers")))
			{
				sql = "LOAD DATA LOCAL INFILE 'C://output//"+_filename+".csv ' INTO TABLE `"+_tablename+"` "+
						"FIELDS TERMINATED BY ',' "+
						"ENCLOSED BY '\"' "+
						"LINES TERMINATED BY '\n' "+
						"IGNORE 2 LINES;";
			}else{
				sql = "LOAD DATA LOCAL INFILE 'C://output//"+_filename+".csv ' INTO TABLE `"+_tablename+"` "+
						"FIELDS TERMINATED BY ',' "+
						"ENCLOSED BY '\"' "+
						"LINES TERMINATED BY '\n' "+
						"IGNORE 1 LINES;";
			}
			
		}
		else if (filetype.equals("csv"))
		{
//			if(_tablename.equals("productmaster"))
//			{
//				sql = "LOAD DATA LOCAL INFILE 'C://output//"+_filename+".csv ' INTO TABLE `"+_tablename+"` "+
//						"FIELDS TERMINATED BY ',' "+
//						"ENCLOSED BY '\"' "+
//						"LINES TERMINATED BY '\r\n' "+
//						"IGNORE 1 LINES"
//						+ "(Region, Country, WPPOI_Cycle, WPPOI_Data_Source, WPPOI_Data_Available, WPPOI_Version, WPPOI_Released_Month, WPOI_Cycle, WPOI_Cycle_Month, WPOI_Data_Source, WPOI_Data_Available, WPOI_Vintage, WPOI_Released_Month, WBO_Cycle, WBO_Cycle_Month, WBO_Data_Source, WBO_Data_Available, WBO_Vintage, WBO_Released_Month, Admin_Cycle, Admin_Cycle_Month, Admin_Source, Admin_Available, Admin_Vintage, Admin_Data_Released, StreetPro_Cycle, StreetPro_Data_Source, StreetPro_Available, StreetPro_Vintage, StreetPro_Released, StreetProNav_Cycle, StreetProNav_Data_Source, StreetProNav_Available, StreetProNav_Vintage, StreetProNav_Released, StreetProWrld_Cycle, StreetProWrld_Data_Source, StreetProWrld_Available, StreetProWrld_Vintage, StreetProWrld_Released, RJS_Cycle, RJS_Cycle_Month, RJS_Source, RJS_Available, RJS_Vintage, RJS_Released, ERM_Cycle, ERM_Cycle_Month, ERM_Source, ERM_Available, ERM_Vintage, ERM_Released, ERM_Historic_Data, ERM_CVR, RF_Cycle, RF_Cycle_Month, RF_Source, RF_Available, RF_Version, RF_Data_Vintage, RF_Released, AD_Cycle, AD_Source, AD_Available, AD_Vintage, AD_Released, AD_Updated, Loqate_Cycle, Loqate_Source, Loqate_Available, Loqate_Released_Month, Loqate_Verification_Level, Loqate_Geocoding_Level, Loqate_Power_Search, ADN_Cycle, ADN_Source, ADN_Available, ADN_Version, ADN_Released_Month, ADN_Knowledgebase_Updated, ADN_Validation_Level, ADN_RefData_Updated, ADN_Geocode_Updated, ICP_Cycle, ICP_Source, ICP_Available, ICP_Released, ICP_Updated, ICP_Level, Cameo_Data, Cameo_Level, DetDem_Data, DetDem_Level, BaseDem_Data, BaseDem_Level, `PAD Release Cycle`, `PAD Source`, `PAD Version`, `PAD Availibilty`, `PAD Part Code`, `PAD Format`, `PAD Geography Level`, `PAD Group`, `AED Release Cycle`, `AED Source`, `AED Version`, `AED Availibilty`, `AED Part Code`, `AED Format`, `AED Geography Level`, `AED Group`);";
//			}
//			else{
				sql = "LOAD DATA LOCAL INFILE 'C://output//"+_filename+".csv ' INTO TABLE `"+_tablename+"` "+
						"FIELDS TERMINATED BY ',' "+
						"ENCLOSED BY '\"' "+
						"LINES TERMINATED BY '\r\n' "+
						"IGNORE 1 LINES;";
	//		}
		}
		

		System.out.println(sql);
		try
		{
			conn = DriverManager.getConnection(Stats.url, Stats.username, Stats.password);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		
		if(!recordCheck(_productline,_directory,_tablename)){
		
			sql = "INSERT INTO filters (product_line, products, filter1,filter2,coveragemap,filter2display)" +
			        "VALUES (?,?,?,?,?,?)";		
			
			System.out.println(sql);
			
			if(_tablename.toLowerCase().equals("disttocoast_metrics_usa"))
				_tablename = "disttocoast_metrics_USA";
			else if(_tablename.toLowerCase().equals("demographics_metrics_usa"))
				_tablename = "demographics_metrics_USA";
			else if(_tablename.toLowerCase().equals("floodrisk_metrics_usa"))
				_tablename = "floodrisk_metrics_USA";
			else if(_tablename.toLowerCase().equals("propfireprotection_metrics_usa"))
				_tablename = "propfireprotection_metrics_USA";
			else if(_tablename.toLowerCase().equals("wildfire_metrics_usa"))
				_tablename = "wildfire_metrics_USA";
			
			if(_directory.toLowerCase().equals("world premium plus points of interest"))
			{
				_directory = "Premium Plus POI";
				_filter2display = _tablename.substring(_tablename.indexOf("_")+1).toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if(_directory.toLowerCase().equals("world premium plus points of interest csmr"))
			{
				_directory = "Premium Plus POI CSMR";
				_filter2display = _tablename.substring(_tablename.indexOf("_")+1).toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if(_directory.toLowerCase().equals("world premium points of interest"))
			{
				_directory = "Premium POI";
				_filter2display = _tablename.substring(_tablename.indexOf("_")+1).toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if(_directory.toLowerCase().equals("world premium points of interest csmr"))
			{
				_directory = "Premium POI CSMR";
				_filter2display = _tablename.substring(_tablename.indexOf("_")+1).toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if(_directory.toLowerCase().equals("social places"))
			{
				_filter2display = _tablename.toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if(_tablename.toLowerCase().equals("social places"))
			{
				_filter2display = _tablename.toLowerCase();
				if(_filter2display.contains("counts_by"))
					_filter2display = _filter2display.replace("counts_by_", "");
			}
			else if((_tablename.toLowerCase().equals("gem_demographics_products")) || (_tablename.toLowerCase().equals("gem_risk_products")) || (_tablename.toLowerCase().equals("gem_property_attribute_products"))
					|| (_tablename.toLowerCase().equals("fabric_demographics_products")) || (_tablename.toLowerCase().equals("fabric_risk_products")) || (_tablename.toLowerCase().equals("fabric_property_attribute_products")))
			{
				_filter2display = _tablename.substring(_tablename.indexOf("_") + 1);
			}
			else
			{
				_filter2display = _tablename.toLowerCase();
			}
	
			
			try {
	
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
				preparedStatement.setString(1, _productline);
				preparedStatement.setString(2, _directory);
				preparedStatement.setString(3, filter1);
				preparedStatement.setString(4, _tablename);
				preparedStatement.setString(5, _coveragemap);
				preparedStatement.setString(6, _filter2display);
	
				preparedStatement.executeUpdate(); 
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			} finally {
				//conn.close();
			}
		}
		handleAddressing(_directory, _filter2display, _productline, _coveragemap);
    }
		
	
    /**
     * 
     * checks for the record entry in filter table in database
     * 
     * @param _productline ... parent folder of the current processing directory
     * @param _products ... product name
     * @param _filter2 ... first filter
     * @throws IOException
     * @throws SQLException
     * @throws SQLIntegrityConstraintViolationException
     */    
    private boolean recordCheck(String _productline,String _products,String _filter2)  throws IOException,SQLException,SQLIntegrityConstraintViolationException{
    	boolean recordFound = false;
    	try {
    		Connection conn = DriverManager.getConnection(Stats.url, Stats.username, Stats.password);
			sql = "select product_line from filters where product_line = '"+_productline+"' and products = '"+_products+"' and filter2 = '"+_filter2+"'";	
			System.out.println(sql);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

     	    while(rs.next()){            
    	       /// Do your insertion of new records
    	    	recordFound = true;
    	    }
    	   
    	}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		} finally {
			//conn.close();
		}
    	 return recordFound;
    }
    
    /**
     * 
     * Handling the addressing with customized table creation in the database
     * 
     * @param _directory ... name of the directory for which we are parsing the files
     * @param _filter2display ... filtername to be displayed on front end
     * @param _productline ... parent folder of the current processing directory
     * @param _coveragemap
     * @throws SQLException
     * @throws IOException 
     */  
	private void handleAddressing(String _directory,String _filter2display,String _productline,String _coveragemap ) throws SQLException, IOException{

		String newtablename = null;
		String tablename = null;
		boolean stateflag = false;
		
		Addressing addressing = new Addressing();
		
		if((_directory.toLowerCase().equals("propertyattributeparcelplus")) || (_directory.toLowerCase().equals("propertyattributeparcelpremium")) || (_directory.toLowerCase().equals("propertyattributeparcelclassic")))
		{
			newtablename = "pap_usa_state";
			tablename = "pap_metrics";
			addressing.createParcelClassicPlusPremiumStatesUSAView(tablename,newtablename,stateflag);
		}
//		else if(_directory.toLowerCase().equals("propertyattributefabric"))
//		{
//			newtablename = "paf_usa_state";
//			tablename = "paf_metrics";
//			addressing.createGemFabricStatesUSAView(tablename,newtablename,stateflag);
//		}
		else if(_directory.toLowerCase().equals("gepropertyattributesultimate"))
		{
			newtablename = "pag_usa_state";	
			tablename = "pag_metrics";
			addressing.createGemFabricStatesUSAView(tablename,newtablename,stateflag);
		}
		
		
		newtablename = null;
		tablename = null;
		
		if((_directory.toLowerCase().equals("propertyattributeparcelplus")) || (_directory.toLowerCase().equals("propertyattributeparcelpremium")) || (_directory.toLowerCase().equals("propertyattributeparcelclassic")))
		{
			newtablename = "pap_usa";
			tablename = "pap_metrics";
			addressing.createParcelClassicPlusPremiumUSACombinedView(tablename,newtablename);
		}
//		else if(_directory.toLowerCase().equals("propertyattributefabric"))
//		{
//			newtablename = "paf_usa";
//			tablename = "paf_metrics";
//			addressing.createGemfabricUSACombinedView(tablename,newtablename);
//		}
		else if(_directory.toLowerCase().equals("gepropertyattributesultimate"))
		{
			newtablename = "pag_usa";	
			tablename = "pag_metrics";
			addressing.createGemfabricUSACombinedView(tablename,newtablename);
		}
		else if(_directory.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
		{
			newtablename = "paph_usa";	
			tablename = "paph_metrics";
			addressing.createParcelClassicPlusPremiumUSACombinedView(tablename,newtablename);
		}
		else if(_directory.toLowerCase().equals("gepropertyattributeshistoric"))//changes for GeoEnrichment Historic
		{
			newtablename = "pah_usa";	
			tablename = "pah_metrics";
			addressing.createParcelClassicPlusPremiumUSACombinedView(tablename,newtablename);
		}

		
		if((_directory.toLowerCase().equals("propertattributeparcelpremiumhistoric")) || (_directory.toLowerCase().equals("gepropertyattributeshistoric")))///changes for GeoEnrichment Historic
		{
			if((_directory.toLowerCase().equals("propertattributeparcelpremiumhistoric"))){
				_filter2display = "paph_usa";
				_tablename = "paph_usa";
			}
			else if((_directory.toLowerCase().equals("gepropertyattributeshistoric"))){
				_filter2display = "pah_usa";
				_tablename = "pah_usa";
			}
			
			if(!recordCheck(_productline,_directory,_tablename)){
			
				sql = "INSERT INTO filters (product_line, products, filter1,filter2,coveragemap,filter2display)" +
				        "VALUES (?,?,?,?,?,?)";
			
				try {
	
					PreparedStatement preparedStatement = conn.prepareStatement(sql);
					preparedStatement.setString(1, _productline);
					preparedStatement.setString(2, _directory);
					preparedStatement.setString(3, filter1);
					preparedStatement.setString(4, _tablename);
					preparedStatement.setString(5, _coveragemap);
					preparedStatement.setString(6, _filter2display);
	
					preparedStatement.executeUpdate(); 
				}catch(Exception e){
					e.printStackTrace();
				
				} finally {
					conn.close();
				}
			}
		}
	
		
    }
   

}
