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

package com.pb.statsapi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
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
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.csvreader.CsvReader;
import com.pb.statsapi.app.StatsData;
import com.pb.statsapi.config.Config;

/**
 * A rudimentary XLSX -> CSV processor modeled on the POI sample program
 * XLS2CSVmra from the package org.apache.poi.hssf.eventusermodel.examples. As
 * with the HSSF version, this tries to spot missing rows and cells, and output
 * empty entries for them.
 * <p/>
 * Data sheets are read using a SAX parser to keep the memory footprint
 * relatively small, so this should be able to read enormous workbooks. The
 * styles table and the shared-string table must be kept in memory. The standard
 * POI styles table class is used, but a custom (read-only) class is used for
 * the shared string table because the standard POI SharedStringsTable grows
 * very quickly with the number of unique strings.
 * <p/>
 * For a more advanced implementation of SAX event parsing of XLSX files, see
 * {@link XSSFEventBasedExcelExtractor} and {@link XSSFSheetXMLHandler}. Note
 * that for many cases, it may be possible to simply use those with a custom
 * {@link SheetContentsHandler} and no SAX code needed of your own!
 */
/**
 * @author mo009kh
 *
 */
public class Stats {
	/**
	 * Uses the XSSF Event SAX helpers to do most of the work of parsing the
	 * Sheet XML, and outputs the contents as a (basic) CSV.
	 */

	private static final boolean LOCAL_MODE = false;
	
	private static InputStream input = null;
	private static Properties prop = new Properties();
	private static String driver = "";
	public static String url = "";
	public static String username = "";
	public static String password = "";
	
	public static void main(String[] args) throws Exception {
		if (LOCAL_MODE) {
			args = new String[2];
			args[0] = "D:/apache-tomcat-8.0.33-windows-x64/apache-tomcat-8.0.33/webapps/stats-api/WEB-INF/classes/application.properties";
			args[1] = "D:/aawork/LIDataStats/PropertyAttributes/PropertAttributeParcelHistoric";
		}
		
		String propertiesFileName = args[0];		
		Config.setPropertiesFile(propertiesFileName);		

		driver = Config.getInstance().getDriver();
		if (driver != null) {
			Class.forName(driver);
		}

		url = Config.getInstance().getUrl();
		username = Config.getInstance().getUserName();
		password = Config.getInstance().getPassword();

		
		String inputDir = args[1];
		new Stats().runProcess(inputDir);
		//new StatsData().getPoiStats("poi", "world premium poi");
	}


	/**
	 * @param filepath
	 * @return
	 * @throws Exception
	 */
	public boolean runProcess(String filepath) throws Exception {
		String _prefixschema = null, _productline = null;
		String[] arrpath = null;

		final File folder = new File(filepath);
		String inputfilepath = filepath;

		arrpath = inputfilepath.split("/");

		_productline = arrpath[arrpath.length - 2];
		System.out.println("Product Line " + _productline);
		inputfilepath = inputfilepath.substring(inputfilepath.lastIndexOf("/") + 1, inputfilepath.length());
		String _tableprefix = inputfilepath;
		// System.out.println(_tableprefix);
		// _directory = _tableprefix;

		String _filename = null;

		for (final File fileEntry : folder.listFiles()) {

			 System.out.println(fileEntry.getName());
			_filename = fileEntry.getName();
			int dot = _filename.lastIndexOf('.');
			String basefilename = (dot == -1) ? _filename : _filename.substring(0, dot);
			String fileextension = (dot == -1) ? "" : _filename.substring(dot + 1);

			if (fileextension.equals("xlsx")) {
				File xlsxFile = new File(folder.getAbsolutePath() + "/" + _filename);
				if (!xlsxFile.exists()) {
					System.err.println("Not found or not a file: " + xlsxFile.getPath());
					return false;
				}

				int minColumns = -1;
				// The package open is instantaneous, as it should be.
				OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
				ProcessExcelData processexceldata = new ProcessExcelData(p, minColumns);
				_prefixschema = getTablePrefix(_tableprefix, basefilename);
				processexceldata.process(_prefixschema, fileextension, _tableprefix, _productline);
				p.close();
			} else if (fileextension.equals("csv")) {
				FileUtils.copyFile(new File(folder.getAbsolutePath() + "/" + _filename),
						new File("C:/output/" + _filename));
				File xlsxFile = new File(folder.getAbsolutePath() + "/" + _filename);
				if (!xlsxFile.exists()) {
					System.err.println("Not found or not a file: " + xlsxFile.getPath());
					return false;
				}

				_prefixschema = getTablePrefix(_tableprefix, basefilename);
				new CreateTableAndLoadData().createSchema(_prefixschema, basefilename, fileextension, _tableprefix,
						_productline);
			}
		}

		return true;

	}
	
	
	/**
	 * returns the table prefix as per the directory structure for creation of
	 * unique tables
	 * 
	 * @param _dirname
	 * @param _filename
	 * @return
	 * @throws Exception
	 */
	public static String getTablePrefix(String _dirname, String _filename) throws Exception {
		String _prefix = null;
		String _midname = null;

		if (_dirname.equals("Auto-Dealers")) {
			_prefix = "auto_dealers";
		} else if (_dirname.equals("Geofence")) {
			_prefix = "geofence";
		} else if (_dirname.equals("Neighborhood")) {
			_prefix = "nbrhood";
		} else if (_dirname.equals("CameoGlobalCatalouge")) {
			_prefix = "wd_camglocat";
		} else if (_dirname.equals("Schools")) {
			_prefix = _filename.substring(_filename.indexOf("_") + 1);
		} else if (_dirname.equals("Social Places")) {
			_midname = _filename.substring(15, _filename.lastIndexOf("_"));
			String filter1 = _midname;
			// _prefix = "social_places"+"_"+_midname;
			_prefix = _midname;
		} else if (_dirname.equals("Global Gazetter")) {
			_prefix = "gzt";
		} else if (_dirname.toLowerCase().equals("world premium points of interest")) {
			_prefix = "wppoi";
		} else if (_dirname.toLowerCase().equals("world premium plus points of interest")) {
			_prefix = "wpppoi";
		} else if (_dirname.toLowerCase().equals("world premium points of interest csmr")) {
			_prefix = "wppoic";
		} else if (_dirname.toLowerCase().equals("world premium plus points of interest csmr")) {
			_prefix = "wpppoic";
		} else if (_dirname.toLowerCase().equals("gemfilesizes")) {
			_prefix = "gem";
		} else if (_dirname.toLowerCase().equals("fabricfilesizes")) {
			_prefix = "fabric";
		} else if ((_dirname.toLowerCase().equals("propertyattributeparcelclassic"))
				|| (_dirname.toLowerCase().equals("propertyattributeparcelpremium"))
				|| (_dirname.toLowerCase().equals("propertyattributeparcelplus"))) {
			_prefix = "pap";
		} else if (_dirname.toLowerCase().equals("propertattributeparcelhistoric")) {
			_prefix = "paph";
		} else if (_dirname.toLowerCase().equals("gepropertyattributeshistoric")) {
			_prefix = "pah";
		} else if (_dirname.toLowerCase().equals("gepropertyattributesultimate")) {
			_prefix = "pag";
		} else if (_dirname.toLowerCase().equals("propertyattributefabric")) {
			_prefix = "paf";
		} else if (_dirname.toLowerCase()
				.equals("gfk postcode and administrative boundaries product line world boundaries")) {
			_prefix = "gpaabplwb";
		} else if (_dirname.toLowerCase().equals("propertyattributefabric")) {
			_prefix = "paf";
		} else if (_dirname.toLowerCase().equals("filesizes")) {
			_prefix = "";
		} else {
			_prefix = "";
		}
		return _prefix;
	}


}