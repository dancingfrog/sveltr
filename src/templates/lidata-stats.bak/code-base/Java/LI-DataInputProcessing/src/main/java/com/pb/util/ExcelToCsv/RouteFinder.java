/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package com.pb.util.ExcelToCsv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.csvreader.CsvReader;
/**
 * Class to run all the checks for Route Finder
 */
public class RouteFinder {
	

	String _country = null, product_type = null;
	boolean filecount = false,link_file_name = false,driving_spd = false,walking_spd = false,link_spd = false,networktxt_del = false,restriction_del = false;
	boolean premium_schema_matched = false,standard_schema_matched = false,routefinderini_matched = false;
	
	HashMap<String,String> paramval = new HashMap<String,String>();  
	
	/**	
	 * Checks for all the files that should be there before bundling
	 * @param filepath
	 * @param _country
	 * @return
	 */
    public boolean checkFile(String filepath,String _country)
    {
    	boolean links_dat = false,links_id = false,links_ind = false,links_map = false,links_tab = false;
    	boolean restrictions_dat = false,restrictions_id = false,restrictions_map = false,restrictions_tab = false;
    	boolean driving_spd = false,walking_spd = false,link_spd = false;
    	boolean turn_bin = false,spatialindex_bin = false,roadnumber01_bin= false,roadname01_bin = false,node_bin =false,link_bin = false,limit1_bin=false,length_bin=false,index1_bin=false,index2_bin=false;
    	boolean coord3i_bin = false,coord3_bin = false,coord_bin = false,attribute_bin=false,rwnet_config_ini = false,routefinder_ini = false;
    	
    	File dir = new File(filepath);
    	
        for (final File fileEntry : dir.listFiles()) {   	

	    	if(fileEntry.getName().toLowerCase().equals("turn.bin"))
	    		turn_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("spatialindex.bin"))
	    		spatialindex_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("roadnumber01.bin"))
	    		roadnumber01_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("roadname01.bin"))
	    		roadname01_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("node.bin"))
	    		node_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("link.bin"))
	    		link_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("limit1.bin"))
	    		limit1_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("length.bin"))
	    		length_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("index2.bin"))
	    		index2_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("index1.bin"))
	    		index1_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("coord3i.bin"))
	    		coord3i_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("coord3.bin"))
	    		coord3_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("coord.bin"))
	    		coord_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("attribute.bin"))
	    		attribute_bin = true;
	    	else if(fileEntry.getName().toLowerCase().equals("walking.spd"))
	    		walking_spd = true;
	    	else if(fileEntry.getName().toLowerCase().equals("link.spd"))
	    		link_spd = true;
	    	else if(fileEntry.getName().toLowerCase().equals("driving.spd"))
	    		driving_spd = true;
	    	else if(fileEntry.getName().toLowerCase().equals("restrictions.map"))
	    		restrictions_map = true;
	    	else if(fileEntry.getName().toLowerCase().equals("restrictions.id"))
	    		restrictions_id = true;
	    	else if(fileEntry.getName().toLowerCase().equals("restrictions.dat"))
	    		restrictions_dat = true;
	    	else if(fileEntry.getName().toLowerCase().equals("restrictions.tab"))
	    		restrictions_tab = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links_pro.dat"))
	    		links_dat = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links.dat"))
	    		links_dat = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links_pro.id"))
	    		links_id = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links_pro.map"))
	    		links_map = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links_pro.tab"))
	    		links_tab = true;
	    	else if(fileEntry.getName().toLowerCase().equals(_country.toLowerCase()+"_links_pro.ind"))
	    		links_ind = true;
	    	else if(fileEntry.getName().toLowerCase().equals("routefinder.ini"))
	    		routefinder_ini = true;
	    	else if(fileEntry.getName().toLowerCase().equals("rwnet_config.ini"))
	    		rwnet_config_ini = true;

        }
        
        
    	if((links_dat == true) && (links_id == true) && (links_ind == true) && (links_map == true) && (links_tab == true) && (restrictions_dat == true) && (restrictions_id == true)  
    			&& (restrictions_map == true) && (restrictions_tab == true) && (driving_spd == true) && (walking_spd == true) && (link_spd == true) && (turn_bin == true) 
    			&& (spatialindex_bin == true) && (roadnumber01_bin == true) && (roadname01_bin == true) && (node_bin == true) && (link_bin == true) && (limit1_bin == true) && (length_bin == true) 
    			&& (index1_bin == true) && (index2_bin == true) && (coord3i_bin == true) && (coord3_bin == true) && (coord_bin == true) && (attribute_bin == true) && (rwnet_config_ini == true) && (routefinder_ini == true))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
        
    }
    
    /**
     * Compares link.tab schema to that of predefined schema with the data-types and character-set
     * @param received
     * @param base
     * @return
     * @throws IOException
     */
    public boolean compareSchema(String received,String base) throws IOException
    {
    	InputStream in = getClass().getResourceAsStream(base); 
        BufferedReader basereader = new BufferedReader(new InputStreamReader(in));
        
        BufferedReader receivedreader = new BufferedReader(new FileReader(received));
         
        String line1 = basereader.readLine();         
        String line2 = receivedreader.readLine();
         
        boolean areEqual = true;         
        int lineNum = 1;
         
        while (line1 != null || line2 != null)
        {
            if(line1 == null || line2 == null)
            {
                areEqual = false;                 
                break;
            }
            else if(! line1.equalsIgnoreCase(line2))
            {
                areEqual = false;                 
                break;
            }
             
            line1 = basereader.readLine();             
            line2 = receivedreader.readLine();
             
            lineNum++;
            
            if(lineNum > 35)
            	break;

        }
        
       
        if(areEqual)
        {
            basereader.close();            
            receivedreader.close();
            return true;
        }
        else
        {
            System.out.println("Two files have different content. They differ at line "+lineNum);             
            System.out.println("File1 has "+line1+" and File2 has "+line2+" at line "+lineNum);
            basereader.close();            
            receivedreader.close();
            return false;
        }
                 
    }
    
    /**
     * Checks the values of the RouteFinder.ini file 
     * @param inifilepath
     * @param rf
     * @return
     * @throws InvalidFileFormatException
     * @throws IOException
     */
    public boolean checkRouteFinderINI(String inifilepath, RouteFinder rf) throws InvalidFileFormatException, IOException
    {
    	Ini _routefinderini = new Ini(new File(inifilepath));    	

    	if(_routefinderini.get("NETWORK", "TABLE").toLowerCase().equals(rf.paramval.get("TABLE").toLowerCase()) && _routefinderini.get("INFO", "AVOID1DESC").toLowerCase().equals(rf.paramval.get("AVOID1DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID2DESC").toLowerCase().equals(rf.paramval.get("AVOID2DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID3DESC").toLowerCase().equals(rf.paramval.get("AVOID3DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID4DESC").toLowerCase().equals(rf.paramval.get("AVOID4DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID5DESC").toLowerCase().equals(rf.paramval.get("AVOID5DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID6DESC").toLowerCase().equals(rf.paramval.get("AVOID6DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID7DESC").toLowerCase().equals(rf.paramval.get("AVOID7DESC").toLowerCase())
    			&& _routefinderini.get("INFO", "AVOID8DESC").toLowerCase().equals(rf.paramval.get("AVOID8DESC").toLowerCase())
    			)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
 
    
    /**
     * Checks all the .spd file values and returns true or false
     * @param spdfilepath
     * @param rf
     * @return
     * @throws InvalidFileFormatException
     * @throws IOException
     */
    public boolean checkRouteFinderSPD(String spdfilepath, RouteFinder rf) throws InvalidFileFormatException, IOException
    {
    	Ini _routefinderspd = new Ini(new File(spdfilepath));

    	if(_routefinderspd.get("SPEED_VALUE", "Speed26").toLowerCase().equals(rf.paramval.get("Speed26").toLowerCase()) 
    			&& _routefinderspd.get("SPEED_VALUE", "Speed30").toLowerCase().equals(rf.paramval.get("Speed30").toLowerCase())
    			&& _routefinderspd.get("SPEED_NAME", "Name26").toLowerCase().equals(rf.paramval.get("Name26").toLowerCase())
    			&& _routefinderspd.get("SPEED_NAME", "Name30").toLowerCase().equals(rf.paramval.get("Name30").toLowerCase())	)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    } 
    
    
    public static void transform(File source, String srcEncoding, File target, String tgtEncoding) throws IOException {
        try (
          BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source), srcEncoding));
          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), tgtEncoding)); ) {
              char[] buffer = new char[16384];
              int read;
              while ((read = br.read(buffer)) != -1)
                  bw.write(buffer, 0, read);
        } 
    }
    public static void check_errors(Connection conn, String tablename){
    	String check = null;
    	try{
    		
    		
    	CallableStatement proc_stmt = conn.prepareCall("{ call allchecks(?) }");
        proc_stmt.setString(1, tablename);
		 ResultSet rs = proc_stmt.executeQuery();
		 
    	//}
		if (rs.next()) {
		      check = rs.getString(1);
				 System.out.println("Value : " + check);

		      if(check.contains("avoidcheck")){
		    	  System.out.println("Error in avoid1, avoid2, avoid3 ,avoid4, avoid5 ,avoid6, avoid7, avoid8 value");
		      }
		      if(check.contains("avoidsum")){
		    	  System.out.println("Error avoid1 + avoid2 + avoid3 + avoid4 + avoid5 + avoid6 + avoid7 + avoid8 not equal to avoid value");
		      }
		      if(check.contains("characterissues")){
		    	  System.out.println("Error irregular characters used");
		      }
		      if(check.contains("speedvalue")){
		    	  System.out.println("Error speed value not 5 when road class is Z");
		      }
		      if(check.contains("startendallzero")){
		    	  System.out.println("Error startZ or EndZ all zero");
		      }
		      if(check.contains("streetnameloss")){
		    	  System.out.println("Error street name contains loss");
		      }
		      if(check.contains("featureidnotunique")){
		    	  System.out.println("Error Feature id is not unique");
		      }
		     // System.out.println("Value : " + check);
		    } else {
		     System.out.println("Stored procedure couldn't generate result");
		    }
    	}
    	catch(Exception e){
    		System.out.println(e);
    	}

    	
    }
    public static void print_errors(int num){
    	int err = 0;
    	err = num % 10;
    	if (err == 1){
    		 System.out.println("Error Feature id is not unique");
    	}
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		System.out.println("Error street name contains loss");
    	}
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		System.out.println("Error startZ or EndZ all zero");
	      }
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		  System.out.println("Error speed value not 5 when road class is Z");
    	}
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		 System.out.println("Error irregular characters used");
    	}
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		 System.out.println("Error avoid1 + avoid2 + avoid3 + avoid4 + avoid5 + avoid6 + avoid7 + avoid8 not equal to avoid value");
	      
    	}
    	num = num/10;
    	err = num % 10;
    	if (err == 1){
    		 System.out.println("Error in avoid1, avoid2, avoid3 ,avoid4, avoid5 ,avoid6, avoid7, avoid8 value");
	      
    	}  	
    	
    }
    public static void postgrechecks(String tablename){
    	
    	 Connection c = null;
         try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
               .getConnection("jdbc:postgresql://152.144.227.176:5432/dbRouteFinder",
               "postgres","postgres");
         } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
         }
        // System.out.println("Opened database successfully");
         
         
        
      try{
         	 
         	 CallableStatement upperProc = c.prepareCall("{ ? = call qachecks( ? ) }");
         	 upperProc.registerOutParameter(1, java.sql.Types.INTEGER);
              upperProc.setString(2, tablename);
              upperProc.execute();
              int upperCased = upperProc.getInt(1);
              System.out.println(upperCased);
              print_errors(upperCased);
              upperProc.close();
        
              c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         System.exit(0);
      }
      //System.out.println("Function Called");
    	
    }
    
    public static void create_table(String path, String _filename){
    	
    	String[] headerdata = null;
		String[] arrtable = null;
		String sql = null;
		Statement statement = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String _tablename = null;
		InputStream istream = null;
		CsvReader products = null;
    	
    	
    	//FileUtils.copyFile(new File(folder.getAbsolutePath()+"//"+_filename), new File("C://output//"+_filename));
		System.out.println(path+"//"+_filename+".csv");
       	File xlsxFile = new File(path+"//"+_filename+".csv");
        int minColumns = -1;
        Charset cs= Charset.forName("UTF-8");
        try{
        	istream = new FileInputStream(new File(path+"//"+_filename+".csv"));
        	products = new CsvReader(istream,cs);
        	products.readHeaders();
		//products = head(products,10);
        	headerdata = products.getHeaders();
        }
        catch(Exception e){
        	System.out.println(e);
        }
        Connection c = null;
        try {
           Class.forName("org.postgresql.Driver");
           c = DriverManager
              .getConnection("jdbc:postgresql://152.144.227.176:5432/dbRouteFinder",
              "postgres","postgres");
        } catch (Exception e) {
           e.printStackTrace();
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(0);
        }
        
		try{
			statement = c.createStatement();
			
			sql = "DROP TABLE IF EXISTS "+_filename+";";
			statement.execute(sql);	
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		sql = "";
		sql = "CREATE TABLE  "+_filename+" ( ";
		
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
				
				
				
				 sql = sql + temp + " varchar(100) DEFAULT ''::character varying,";
				
			}
		
		
		sql = sql.substring(0, sql.length()-1);
		sql = sql +	");";
		
		
		System.out.println(sql);
		try{
			statement = c.createStatement();
			
			statement.execute(sql);	
		}
		catch(Exception e){
			System.out.println(e);
		}	
		products.close();

        
		sql = "COPY "+_filename+" FROM '"+path+"//"+_filename+".csv' DELIMITER ',' CSV header;";
		
		try
		{
			statement = c.createStatement();
			
			statement.execute(sql);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
    	
    	
    	
    }
    
    public static void drop_table(String _filename){
    	 Connection c = null;
         try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
               .getConnection("jdbc:postgresql://152.144.227.176:5432/dbRouteFinder",
               "postgres","postgres");
         } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
         }
         
 		try{
 			Statement statement = c.createStatement();
 			
 			String sql = "DROP TABLE IF EXISTS "+_filename+";";
 			statement.execute(sql);	
 		}
 		catch(Exception e){
 			System.out.println(e);
 		}
    }
    
    public static void main(String[] args) throws Exception {
    	 Class.forName("com.mysql.cj.jdbc.Driver");
 		Connection conn = DriverManager.getConnection("jdbc:mysql://152.144.227.176/test?useSSL=false", "dev", "dev");
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  <CountryISO3> <csv file path> <producttype>");
            return;
        }
        System.out.println("Country : "+ args[0] + " File Path : "+ args[1] + " Product Type : "+ args[2]);        
        
        RouteFinder rf = new RouteFinder();
        
        
        if(args[2].equals("Premium")){
        	rf.paramval.put("TABLE", args[0]+"_links_Pro.tab");
        	String prefix = args[0].toLowerCase();
        	//check_errors(conn, prefix+"_links_Pro");
        }
        else{
        	rf.paramval.put("TABLE", args[0]+"_links.tab");
        	String prefix = args[0].toLowerCase();
        	//check_errors(conn, prefix+"_links");
        }
        
       
        
    	rf.paramval.put("AVOID1DESC","Pedestrian only");
    	rf.paramval.put("AVOID2DESC","Vehicle Only");
    	rf.paramval.put("AVOID3DESC","Toll Roads");
    	rf.paramval.put("AVOID4DESC","Motorways");
    	rf.paramval.put("AVOID5DESC","Ferries");
    	rf.paramval.put("AVOID6DESC","Tunnels");
    	rf.paramval.put("AVOID7DESC","4WD Tracks");
    	rf.paramval.put("AVOID8DESC","Bridges");    	
    	rf.paramval.put("SPEEDUNITS","KM");
    	rf.paramval.put("Speed26","30");
    	rf.paramval.put("Speed30","5");
    	rf.paramval.put("Name26","VehicleFerry");
    	rf.paramval.put("Name30","Footpath");   
    	
       
    	rf._country = args[0];
    	final File folder = new File(args[1]);
    	rf.product_type = args[2];

    	
        for (final File fileEntry : folder.listFiles()) { 
        	
        	//Check if network.txt and restriction.txt are deleted and also for the three spd files
        	if(fileEntry.getName().equals(rf._country+"_restrictions.txt") || fileEntry.getName().toLowerCase().equals("network_report.txt"))
        	{
        		fileEntry.delete();
        		rf.networktxt_del = true;
        		rf.restriction_del = true;
        	}
        	else if (fileEntry.getName().equals("Driving.spd")) {
        		rf.driving_spd = true;				
			} 
        	else if (fileEntry.getName().equals("link.spd")) {
        		rf.link_spd= true;				
			} 
        	else if (fileEntry.getName().equals("Walking.spd")) {
        		rf.walking_spd = true;				
			} 
        		
        	
        	
        	String csvpath = null;
        	String srccsvpath = null;
        	//checking for Product Type
        	if(args[2].equals("Premium"))
        	{
        		//Check for file name
        		if(fileEntry.getName().indexOf("links_Pro.TAB") > 0)
        		{
        			rf.link_file_name = true; 
        			
        			String copyfile = fileEntry.getAbsolutePath();
        			copyfile = copyfile.substring(0,copyfile.lastIndexOf("."));
        			copyfile = copyfile+"1.TAB";

        			FileUtils.copyFile(fileEntry,new File(copyfile));
        			fileEntry.renameTo(new File(fileEntry.getParentFile(), "links_schema.csv"));
        			
        			File f = new File(copyfile);
        			f.renameTo(fileEntry);        			
        			csvpath = fileEntry.getAbsolutePath();
        			csvpath = csvpath.substring(0, csvpath.lastIndexOf("\\")) + "\\links_schema.csv";

        			
        			//ckeck for schema
        			if(rf.compareSchema(csvpath, "/links_schema_premium.properties"))
        			{
        				rf.premium_schema_matched = true;
        			}
        			else
        			{
        				rf.premium_schema_matched = false;
        			}
        			
        			File file = new File(csvpath);
        			file.delete();
        		}
        		

        	}
        	else
        	{
        		if(fileEntry.getName().indexOf("links.TAB") > 0)
        		{
        			rf.link_file_name = true; 

        			String copyfile = fileEntry.getAbsolutePath();
        			copyfile = copyfile.substring(0,copyfile.lastIndexOf("."));
        			copyfile = copyfile+"1.TAB";

        			FileUtils.copyFile(fileEntry,new File(copyfile));
        			fileEntry.renameTo(new File(fileEntry.getParentFile(), "links_schema.csv"));
        			
        			File f = new File(copyfile);
        			f.renameTo(fileEntry);
        			
        			csvpath = fileEntry.getAbsolutePath();
        			csvpath = csvpath.substring(0, csvpath.lastIndexOf("\\")) + "\\links_schema.csv";

        			//while running through run configurations 
        			//if(rf.compareSchema(csvpath, "resources//links_schema_standard.properties"))
        			//for runnable jar to work
        			if(rf.compareSchema(csvpath, "/links_schema_standard.properties"))
        			{
        				rf.standard_schema_matched = true;
        			}
        			else
        			{
        				rf.standard_schema_matched = false;
        			}
        			
        			File file = new File(csvpath);
        			file.delete();
        		}
        	}
        	
    		//Check for RouteFinder INI file
    		if(fileEntry.getName().equals("RouteFinder.ini"))
    		{
    			if(rf.checkRouteFinderINI(fileEntry.getAbsolutePath(),rf))
    				rf.routefinderini_matched = true;
    			else
    				rf.routefinderini_matched = false;
    		}
    		
    		//Check for driving.spd,walking.spd,link.spd file
    		else if(fileEntry.getName().equals("Driving.spd"))
    		{
    			if(rf.checkRouteFinderSPD(fileEntry.getAbsolutePath(), rf))
    				rf.driving_spd = true;
    			else
    				rf.driving_spd = false;
    		}
    		else if( fileEntry.getName().equals("Walking.spd") )
    		{
    			if(rf.checkRouteFinderSPD(fileEntry.getAbsolutePath(), rf))
    				rf.walking_spd = true;
    			else
    				rf.walking_spd = false;
    		}
    		else if(fileEntry.getName().equals("link.spd"))
    		{
    			if(rf.checkRouteFinderSPD(fileEntry.getAbsolutePath(), rf))
    				rf.link_spd = true;
    			else
    				rf.link_spd = false;
    		}
        	
        }
        
        //Check for file count
        if(rf.checkFile(args[1], rf._country))
        {
        	rf.filecount = true;        	
        }
        else
        {
        	rf.filecount = false;
        }
        
        //Checking if the test Passes !!!
        if(args[2].equals("Premium"))
        {
	        if((rf.driving_spd) && (rf.filecount) && (rf.link_file_name) && (rf.link_spd) && (rf.networktxt_del) && (rf.premium_schema_matched) && (rf.restriction_del) 
	        		&& (rf.routefinderini_matched)  && (rf.walking_spd)) 
	        {
	        	System.out.println("Test Passed with all results...");
	        }
	        else
	        {
	        	if(!rf.driving_spd)
	        		System.out.println("Driving.spd values are not correct, pls check !!!");
	        	else if(!rf.walking_spd)
	        		System.out.println("Walking.spd values are not correct, pls check !!!");
	        	if(!rf.link_spd)
	        		System.out.println("link.spd values are not correct, pls check !!!");
	        	if(!rf.filecount)
	        		System.out.println("Some files are missing, pls check !!!");
	        	if(!rf.link_file_name)
	        		System.out.println("Link file name is not correct, pls check !!!");
	        	if(!rf.networktxt_del)
	        		System.out.println("Network.txt file is not deleted, pls check !!!");
	        	if(!rf.restriction_del)
	        		System.out.println("Restriction.txt file is not deleted, pls check !!!");
	        	if(!rf.routefinderini_matched)
	        		System.out.println("RouteFinder.ini values are not correct, pls check !!!");
	        	if(!rf.premium_schema_matched)
	        		System.out.println(args[0]+"_links_Pro.tab schema does not match, pls check !!!");
	        }
        
        }
        else if(args[2].equals("Standard"))
        {
	        if((rf.driving_spd) && (rf.filecount) && (rf.link_file_name) && (rf.link_spd) && (rf.networktxt_del)  && (rf.restriction_del) 
	        		&& (rf.routefinderini_matched) && (rf.standard_schema_matched) && (rf.walking_spd)) 
	        {
	        	System.out.println("Test Passed with all results...");
	        }
	        else
	        {
	        	if(!rf.driving_spd)
	        		System.out.println("Driving.spd values are not correct, pls check !!!");
	        	else if(!rf.walking_spd)
	        		System.out.println("Walking.spd values are not correct, pls check !!!");
	        	if(!rf.link_spd)
	        		System.out.println("link.spd values are not correct, pls check !!!");
	        	if(!rf.filecount)
	        		System.out.println("Some files are missing, pls check !!!");
	        	if(!rf.link_file_name)
	        		System.out.println("Link file name is not correct, pls check !!!");
	        	if(!rf.networktxt_del)
	        		System.out.println("Network.txt file is not deleted, pls check !!!");
	        	if(!rf.restriction_del)
	        		System.out.println("Restriction.txt file is not deleted, pls check !!!");
	        	if(!rf.routefinderini_matched)
	        		System.out.println("RouteFinder.ini values are not correct, pls check !!!");
	        	if(!rf.premium_schema_matched)
	        		System.out.println(args[0]+"_links_Pro.tab schema does not match, pls check !!!");
	        }
        
        }
        if(args[2].equals("Premium")){
        	create_table(args[1],args[0]+"_Links_Pro");
        	String prefix = args[0].toLowerCase();
        	postgrechecks(prefix+"_links_pro");
        	drop_table(prefix+"_links_pro");
        }
        else{
        	create_table(args[1],args[0]+"_Links");
        	String prefix = args[0].toLowerCase();
        	postgrechecks(prefix+"_links");
        	drop_table(prefix+"_links");
        }

	}
  
    
}