package com.pb.statsapi.response;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.ext.Provider;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pb.statsapi.config.Config;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Provider
public class StatsData implements ContainerResponseFilter{
	String _message = "";
	java.sql.Date _date;
	boolean _flag = false;
	private static String parcelpremiumcolumns = "";
	private static String parcelpluscolumns = "";
	private static String parcelclassiccolumns = "";
	private static String parcelpremiumhistoriccolumns = "";
	private static String stateparcelpremiumcolumns = "";
	private static String stateparcelpluscolumns = "";
	private static String stateparcelclassiccolumns = "";
	private static String fipsparcelpremiumcolumns = "";
	private static String fipsparcelpluscolumns = "";
	private static String fipsparcelclassiccolumns = "";
	private static String completepropertyattributesultimatecolumns = "";
	private static String statepropertyattributesultimatecolumns = "";
	private static String fipspropertyattributesultimatecolumns = "";
	
	private static String driver = "";
	private static String url = "";
	private static String username = "";
	private static String password = "";
	

    public ContainerResponse filter(ContainerRequest request, 
      ContainerResponse response)  {
        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        System.out.println(response.getHttpHeaders());
        return response;
    }
    
	public StatsData() throws ClassNotFoundException {
		driver = "com.mysql.jdbc.Driver";
		if (driver != null) {
			Class.forName(driver);
		}

		InputStream input = null;
		Properties prop = new Properties();
		try {

	        input = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
	        prop.load(input);
	        
	        url = Config.getInstance().getUrl();
			username = Config.getInstance().getUserName();
			password = Config.getInstance().getPassword();
		
			parcelpremiumcolumns = Config.getInstance().getParcelPremiumColumns();
			parcelpluscolumns = Config.getInstance().getParcelPlusColumns();
			parcelclassiccolumns = Config.getInstance().getParcelClassicColumns();
			parcelpremiumhistoriccolumns = Config.getInstance().getParcelPremiumHistoricColumns();
			stateparcelpremiumcolumns = Config.getInstance().getParcelPremiumStateColumns();
			stateparcelpluscolumns = Config.getInstance().getParcelPlusStateColumns();
			stateparcelclassiccolumns = Config.getInstance().getParcelClassicStateColumns();
			fipsparcelpremiumcolumns = Config.getInstance().getParcelPremiumFipsColumns();
			fipsparcelpluscolumns = Config.getInstance().getParcelPlusFipsColumns();
			fipsparcelclassiccolumns = Config.getInstance().getParcelClassicFipsColumns();
			completepropertyattributesultimatecolumns = Config.getInstance().getPropertyAttributesUltimateColumns();
			statepropertyattributesultimatecolumns = Config.getInstance().getPropertyAttributesUltimateStateColumns();
			fipspropertyattributesultimatecolumns = Config.getInstance().getPropertyAttributesUltimateFipsColumns();
			
	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

	
	public static void main(String[] args) throws Exception {
		//new StatsData().getPoiStats("GlobalDataScorecard", "StreetPro Classic");
		new StatsData().getStatsWithPagination("Routing", "routing",3,50);
		//new StatsData().getStats("Routing", "routing");
		//new StatsData().getAEDStats("GEPropertyAttributesUltimate", "AZ", "04001");
	}

	public JSONArray getAEDStats(String product, String state, String fips) {
		String tablename = "";
		String sql = null;
		String _columns = "";
		String convertedstring = "";

		if ((product.toLowerCase().equals("propertyattributeparcelplus"))
				|| (product.toLowerCase().equals("propertyattributeparcelpremium"))
				|| (product.toLowerCase().equals("propertyattributeparcelclassic"))
				|| (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))) {
			if (fips.equals("All")) {
				if (product.toLowerCase().equals("propertyattributeparcelpremium"))
					_columns = parcelpremiumcolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelplus"))
					_columns = parcelpluscolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelclassic"))
					_columns = parcelclassiccolumns;
				else if (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
					_columns = parcelpremiumhistoriccolumns;
			} else if (state.toLowerCase().equals("usa")) {
				if (product.toLowerCase().equals("propertyattributeparcelpremium"))
					_columns = stateparcelpremiumcolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelplus"))
					_columns = stateparcelpluscolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelclassic"))
					_columns = stateparcelclassiccolumns;
				else if (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
					_columns = parcelpremiumhistoriccolumns;
			} else {
				if (product.toLowerCase().equals("propertyattributeparcelpremium"))
					_columns = fipsparcelpremiumcolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelplus"))
					_columns = fipsparcelpluscolumns;
				else if (product.toLowerCase().equals("propertyattributeparcelclassic"))
					_columns = fipsparcelclassiccolumns;
				else if (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
					_columns = parcelpremiumhistoriccolumns;

			}
		} else if ((product.toLowerCase().equals("propertyattributegem"))
				|| (product.toLowerCase().equals("gepropertyattributesultimate"))) {
			if (fips.toLowerCase().equals("all"))
				_columns = completepropertyattributesultimatecolumns;
			else if (state.toLowerCase().equals("usa"))
				_columns = statepropertyattributesultimatecolumns;
			else
				_columns = fipspropertyattributesultimatecolumns;
		}

		if (state.toLowerCase().equals("usa")) {
			if ((product.toLowerCase().equals("propertyattributeparcelplus"))
					|| (product.toLowerCase().equals("propertyattributeparcelpremium"))
					|| (product.toLowerCase().equals("propertyattributeparcelclassic")))
				tablename = "pap_usa";
			else if (product.toLowerCase().equals("gepropertyattributesultimate"))
				tablename = "pag_usa";
			else if (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
				tablename = "paph_usa";

			if (product.toLowerCase().equals("propertattributeparcelpremiumhistoric"))
				sql = "select " + _columns + " from " + tablename + "";
			else
				sql = "select " + _columns + " from " + tablename + "  order by ind_code desc";

		} else {
			if ((product.toLowerCase().equals("propertyattributeparcelplus"))
					|| (product.toLowerCase().equals("propertyattributeparcelpremium"))
					|| (product.toLowerCase().equals("propertyattributeparcelclassic")))
				tablename = "pap_usa_state";
			else if (product.toLowerCase().equals("gepropertyattributesultimate"))
				tablename = "pag_usa_state";

			if (fips.toLowerCase().equals("all"))
				sql = "select " + _columns + " from " + tablename + " where stabb ='" + state
						+ "' order by ind_code desc";
			else {
				if ((product.toLowerCase().equals("propertyattributeparcelplus"))
						|| (product.toLowerCase().equals("propertyattributeparcelpremium"))
						|| (product.toLowerCase().equals("propertyattributeparcelclassic")))
					tablename = "pap_metrics";
				else if (product.toLowerCase().equals("gepropertyattributesultimate"))
					tablename = "pag_metrics";

				sql = "select " + _columns + " from " + tablename + " where stabb ='" + state + "' and fips = '" + fips
						+ "'  order by ind_code desc";
			}
		}

		JSONArray json = new JSONArray();

		try {
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println(sql);
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				json = new JSONArray();
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					int numColumns = rsmd.getColumnCount();
					Map<String, Object> obj = new LinkedHashMap<String, Object>();

					for (int i = 1; i < numColumns + 1; i++) {
						String column_name = rsmd.getColumnName(i);
						// System.out.println(column_name);

						if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
							obj.put(column_name, rs.getArray(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
							obj.put(column_name, rs.getBoolean(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
							obj.put(column_name, rs.getBlob(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
							if (rs.getString(column_name) != null) {
								if (rs.getString(column_name).indexOf(".") > 0) {
									double dblvalue = Double.parseDouble(rs.getString(column_name));
									convertedstring = String.format("%.3f", dblvalue);
									obj.put(column_name, convertedstring);
								} else {
									obj.put(column_name, rs.getString(column_name));
								}
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
							obj.put(column_name, rs.getFloat(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
							obj.put(column_name, rs.getNString(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
							if (rs.getString(column_name) != null) {
								if (column_name.toLowerCase().equals("ind_code")) {
									if (rs.getString(column_name).equals("A"))
										obj.put(column_name, "All");
									else if (rs.getString(column_name).equals("R"))
										obj.put(column_name, "Residential");
									else if (rs.getString(column_name).equals("C"))
										obj.put(column_name, "Commercial");
									else if (rs.getString(column_name).equals("V"))
										obj.put(column_name, "Vacant");
									else if (rs.getString(column_name).equals("N"))
										obj.put(column_name, "Unknown");
								} else if (column_name.toLowerCase().equals("stabb")) {
									obj.put(column_name, rs.getString(column_name));
								} else {
									if (rs.getString(column_name).indexOf(".") > 0) {
										double dblvalue = Double.parseDouble(rs.getString(column_name));
										convertedstring = String.format("%.3f", dblvalue);
										obj.put(column_name, convertedstring);
									} else {
										obj.put(column_name, rs.getString(column_name));
									}
								}
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
							obj.put(column_name, rs.getDate(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
							obj.put(column_name, rs.getTimestamp(column_name));
						} else {
							obj.put(column_name, rs.getObject(column_name));
						}
					}

					Iterator<Entry<String, Object>> itr = obj.entrySet().iterator();
					JsonObject jsonobj = new JsonObject();
					while (itr.hasNext()) {
						Entry<String, Object> en = itr.next();
						jsonobj.addProperty(en.getKey(), en.getValue().toString());

					}

					json.put(jsonobj);
				}

			} finally {
				conn.close();
			}
		} catch (Exception ex) {
			_flag = false;
			ex.printStackTrace();
		}

		//System.out.println(json);
		return json;
	}

	// ******************************************************************************************************************************
	public JSONObject getStats(String product, String tablename) {
		JSONArray json = new JSONArray();
		JSONObject result = new JSONObject();
		String sql = null;
		String convertedstring = "";
		
		try {
			Connection conn = DriverManager.getConnection(url, username, password);

			if (tablename.toLowerCase().equals("world premium poi"))
				sql = "select Region, Country, WPPOI_Cycle, WPPOI_Data_Source, WPPOI_Data_Available, WPPOI_Version, WPPOI_Released_Month,WPPOI_Includes,WPPOI_DATA_PATH  from productmaster "
						+ "where (WPPOI_Cycle <>'' or WPPOI_Data_Source <>'' or  WPPOI_Data_Available <>'' or  WPPOI_Version <>'' or WPPOI_Released_Month <>'')";
			else if (tablename.toLowerCase().equals("world poi"))
				sql = "select Region, Country, WPOI_Cycle, WPOI_Data_Source, WPOI_Data_Available, WPOI_Vintage, WPOI_Released_Month,WPOI_Includes  from productmaster "
						+ " where (WPOI_Cycle <> '' or WPOI_Data_Source <> '' or WPOI_Data_Available <> '' or WPOI_Vintage <> '' or WPOI_Released_Month <> '')";
			else if (tablename.toLowerCase().equals("world boundaries premium"))
				sql = "select Region, Country, WBO_Cycle, WBO_Data_Source, WBO_Data_Available, WBO_Vintage, WBO_Released_Month,WBO_Includes,WBO_Data_Path  from productmaster "
						+ " where (WBO_Cycle <> '' or WBO_Data_Source <> '' or WBO_Data_Available <> '' or WBO_Vintage <> '' or WBO_Released_Month <> '')";
			else if (tablename.toLowerCase().equals("postal and admin boundaries"))
				sql = "select Region, Country, Admin_Cycle, Admin_Source, Admin_Available, Admin_Vintage, Admin_Data_Released  from productmaster "
						+ " where (Admin_Cycle <> '' or Admin_Source <> '' or Admin_Available <> '' or Admin_Vintage <> '' or Admin_Data_Released <> '')";
			else if (tablename.toLowerCase().equals("streetpro classic"))
				sql = "select Region, Country, StreetPro_Cycle, StreetPro_Data_Source, StreetPro_Available, StreetPro_Vintage, StreetPro_Released,StreetPro_Includes,StreetPro_Data_Path,StreetPro_Disp_Classic_Codes  from productmaster "
						+ " where (StreetPro_Cycle <> '' or StreetPro_Data_Source <> '' or StreetPro_Available <> '' or StreetPro_Vintage <> '' or StreetPro_Released <> '' or StreetPro_Disp_Classic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro display"))
				sql = "select Region, Country, StreetPro_Cycle, StreetPro_Data_Source, StreetPro_Available, StreetPro_Vintage, StreetPro_Released,StreetPro_Includes,StreetPro_Data_Path,StreetPro_Disp_Classic_Codes  from productmaster "
						+ " where (StreetPro_Cycle <> '' or StreetPro_Data_Source <> '' or StreetPro_Available <> '' or StreetPro_Vintage <> '' or StreetPro_Released <> '' or StreetPro_Disp_Classic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro navigation"))
				sql = "select Region, Country, StreetProNav_Cycle, StreetProNav_Data_Source, StreetProNav_Available, StreetProNav_Vintage, StreetProNav_Released,StreetProNav_Includes,StreetProNav_Data_Path,StreetProNav_Prem_Codes from productmaster "
						+ " where (StreetProNav_Cycle <> '' or StreetProNav_Data_Source <> '' or StreetProNav_Available <> '' or StreetProNav_Vintage <> '' or StreetProNav_Released <> '' or StreetProNav_Prem_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro traffic"))
				sql = "select Region, Country, StreetProTraffic_Cycle, StreetProTraffic_Data_Source, StreetProTraffic_Available, StreetProTraffic_Vintage, StreetProTraffic_Released,StreetProTraffic_Data_Path,StreetProTraffic_Codes  from productmaster "
						+ " where (StreetProTraffic_Cycle <> '' or StreetProTraffic_Data_Source <> '' or StreetProTraffic_Available <> '' or StreetProTraffic_Vintage <> '' or StreetProTraffic_Released <> '' or StreetProTraffic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro wrld"))
				sql = "select Region, Country, StreetProWrld_Cycle, StreetProWrld_Data_Source, StreetProWrld_Available, StreetProWrld_Vintage, StreetProWrld_Released,StreetProWrld_Codes  from productmaster "
						+ " where (StreetProWrld_Cycle <> '' or StreetProWrld_Data_Source <> '' or StreetProWrld_Available <> '' or StreetProWrld_Vintage <> '' or StreetProWrld_Released <> '' or StreetProWrld_Codes <> '')";
			else if (tablename.toLowerCase().equals("enterprise routing module"))
				sql = "select Region, Country, ERM_Cycle,ERM_Cycle_Month,ERM_Source,ERM_Available,ERM_Vintage,ERM_Released,ERM_Historic_Data,ERM_CVR,ERM_Includes,ERM_Data_Path  from productmaster "
						+ " where (ERM_Cycle <>'' or ERM_Source <>'' or ERM_Available <>'' or ERM_Vintage <>'' or ERM_Released<>'')";
			else if (tablename.toLowerCase().equals("route finder data"))
				sql = "select Region, Country, RF_Cycle,RF_Cycle_Month, RF_Source, RF_Available, RF_Version, RF_Data_Vintage, RF_Released,RF_Includes,RF_Data_Path  from productmaster "
						+ " where (RF_Cycle <> '' or RF_Cycle_Month <> '' or RF_Source <> '' or RF_Available <> '' or RF_Version <> '' or RF_Data_Vintage <> '' or RF_Released <>'')";
			else if (tablename.toLowerCase().equals("address doctor"))
				sql = "select Region, Country, AD_Cycle, AD_Source, AD_Available, AD_Vintage, AD_Released, AD_Updated,AD_Data_Path  from productmaster "
						+ " where (AD_Cycle <> '' or AD_Source <> '' or AD_Available <> '' or AD_Vintage <> '' or AD_Released <> '' or AD_Updated	<>'')";
			else if (tablename.toLowerCase().equals("loqate"))
				sql = "select Region, Country, Loqate_Cycle, Loqate_Source, Loqate_Available, Loqate_Released_Month, Loqate_Verification_Level, Loqate_Geocoding_Level, Loqate_Power_Search,Loqate_Data_Path from productmaster "
						+ " where (Loqate_Cycle <> '' or Loqate_Source <> '' or Loqate_Available <> '' or Loqate_Released_Month <> '' or Loqate_Verification_Level <> '' or Loqate_Geocoding_Level <> '' or Loqate_Power_Search <> '')";
			else if (tablename.toLowerCase().equals("address now"))
				sql = "select Region, Country, ADN_Cycle, ADN_Source, ADN_Available, ADN_Version, ADN_Released_Month, ADN_Knowledgebase_Updated, ADN_Validation_Level, ADN_RefData_Updated,ADN_Geocode_Updated,ADN_Data_Path  from productmaster "
						+ " where (ADN_Cycle <> '' or ADN_Source <> '' or ADN_Available <> '' or ADN_Version <> '' or ADN_Released_Month <> '' or ADN_Knowledgebase_Updated <> '' or ADN_Validation_Level <> '' or ADN_RefData_Updated <> '' or ADN_Geocode_Updated <> '')";
			else if (tablename.toLowerCase().equals("icp"))
				sql = "select Region, Country, ICP_Cycle, ICP_Source, ICP_Available, ICP_Released, ICP_Updated, ICP_Level,ICP_Data_Path  from productmaster "
						+ " where (ICP_Cycle <> '' or ICP_Source <> '' or ICP_Available <> '' or ICP_Released <> '' or ICP_Updated <> '' or ICP_Level	<>'')";
			else if (tablename.toLowerCase().equals("cameo"))
				sql = "select Region, Country, Cameo_Data, Cameo_Level  from productmaster where (Cameo_Data<>'' or Cameo_Level <> '')";
			else if (tablename.toLowerCase().equals("detail demographics"))
				sql = "select Region, Country,DetDem_Data, DetDem_Level  from productmaster where (DetDem_Data <> '' or DetDem_Level <> '')";
			else if (tablename.toLowerCase().equals("base demographics"))
				sql = "select Region, Country, BaseDem_Data, BaseDem_Level  from productmaster where (BaseDem_Data <> '' or BaseDem_Level <> '')";
			else if (tablename.toLowerCase().equals("property attribute data"))
				sql = "select Region, Country, `PAD Release Cycle`,`PAD Source`,`PAD Version`,`PAD Availibilty`,`PAD Part Code`,`PAD Format`,`PAD Geography Level`,`PAD Group`  from productmaster "
						+ " where (`PAD Release Cycle` <> '' or `PAD Source` <> '' or `PAD Version` <> '' or `PAD Availibilty` <> '' or `PAD Part Code` <> '' or `PAD Format` <> '' or `PAD Geography Level` <> '' or `PAD Group` <> '')";
			else if (tablename.toLowerCase().equals("addressing enrichment data"))
				sql = "select Region, Country, `AED Release Cycle`,`AED Source`,`AED Version`,`AED Availibilty`,`AED Part Code`,`AED Format`,`AED Geography Level`,`AED Group`  from productmaster "
						+ " where (`AED Release Cycle` <> '' or `AED Source` <> '' or `AED Version` <> '' or `AED Availibilty` <> '' or `AED Part Code` <> '' or `AED Format` <> '' or `AED Geography Level` <> '' or `AED Group` <> '')";
			else {
				tablename = getTablename(product, tablename);
				sql = "Select * from " + tablename;
			}

			System.out.println(sql);
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				json = new JSONArray();
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					int numColumns = rsmd.getColumnCount();

					Map<String, Object> obj = new LinkedHashMap<String, Object>();

					for (int i = 1; i < numColumns + 1; i++) {
						String column_name = rsmd.getColumnName(i);
						// System.out.println(column_name);

						if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
							obj.put(column_name, rs.getArray(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
							obj.put(column_name, rs.getBoolean(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
							obj.put(column_name, rs.getBlob(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
							if (tablename.toLowerCase().equals("paph_usa")) {
								if (rs.getString(column_name) != null) {
									if (rs.getString(column_name).indexOf(".") > 0) {
										double dblvalue = Double.parseDouble(rs.getString(column_name));
										convertedstring = String.format("%.3f", dblvalue);
										obj.put(column_name, convertedstring);
									} else {
										obj.put(column_name, rs.getString(column_name));
									}
								}
							} else {
								obj.put(column_name, rs.getString(column_name));
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
							obj.put(column_name, rs.getFloat(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
							obj.put(column_name, rs.getNString(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
							if (tablename.toLowerCase().equals("paph_usa")) {
								if (rs.getString(column_name).indexOf(".") > 0) {
									double dblvalue = Double.parseDouble(rs.getString(column_name));
									convertedstring = String.format("%.3f", dblvalue);
									obj.put(column_name, convertedstring);
								} else {
									obj.put(column_name, rs.getString(column_name));
								}
							} else {
								obj.put(column_name, rs.getString(column_name));
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
							obj.put(column_name, rs.getDate(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
							obj.put(column_name, rs.getTimestamp(column_name));
						} else {
							obj.put(column_name, rs.getObject(column_name));
						}
					}

					Iterator<Entry<String, Object>> itr = obj.entrySet().iterator();
					JsonObject jsonobj = new JsonObject();
					while (itr.hasNext()) {
						Entry<String, Object> en = itr.next();
						jsonobj.addProperty(en.getKey(), en.getValue().toString());
					}

					json.put(jsonobj);
				}

			} finally {
				conn.close();
			}
		} catch (Exception ex) {
			_flag = false;
			ex.printStackTrace();
		}
		//System.out.println(json);
		result.put("docs", json);
		return result;

	}
	// ******************************************************************************************************************************
	public JSONObject getStatsWithPagination(String product, String tablename,int page,int pagesize) {
		JSONArray json = new JSONArray();
		JSONArray masterjson = new JSONArray();
		JSONObject result = new JSONObject();
		String sql = null;
		String convertedstring = "";
		try {
			Connection conn = DriverManager.getConnection(url, username, password);

			if (tablename.toLowerCase().equals("world premium poi"))
				sql = "select Region, Country, WPPOI_Cycle, WPPOI_Data_Source, WPPOI_Data_Available, WPPOI_Version, WPPOI_Released_Month,WPPOI_Includes,WPPOI_DATA_PATH  from productmaster "
						+ "where (WPPOI_Cycle <>'' or WPPOI_Data_Source <>'' or  WPPOI_Data_Available <>'' or  WPPOI_Version <>'' or WPPOI_Released_Month <>'')";
			else if (tablename.toLowerCase().equals("world poi"))
				sql = "select Region, Country, WPOI_Cycle, WPOI_Data_Source, WPOI_Data_Available, WPOI_Vintage, WPOI_Released_Month,WPOI_Includes  from productmaster "
						+ " where (WPOI_Cycle <> '' or WPOI_Data_Source <> '' or WPOI_Data_Available <> '' or WPOI_Vintage <> '' or WPOI_Released_Month <> '')";
			else if (tablename.toLowerCase().equals("world boundaries premium"))
				sql = "select Region, Country, WBO_Cycle, WBO_Data_Source, WBO_Data_Available, WBO_Vintage, WBO_Released_Month,WBO_Includes,WBO_Data_Path  from productmaster "
						+ " where (WBO_Cycle <> '' or WBO_Data_Source <> '' or WBO_Data_Available <> '' or WBO_Vintage <> '' or WBO_Released_Month <> '')";
			else if (tablename.toLowerCase().equals("postal and admin boundaries"))
				sql = "select Region, Country, Admin_Cycle, Admin_Source, Admin_Available, Admin_Vintage, Admin_Data_Released  from productmaster "
						+ " where (Admin_Cycle <> '' or Admin_Source <> '' or Admin_Available <> '' or Admin_Vintage <> '' or Admin_Data_Released <> '')";
			else if (tablename.toLowerCase().equals("streetpro classic"))
				sql = "select Region, Country, StreetPro_Cycle, StreetPro_Data_Source, StreetPro_Available, StreetPro_Vintage, StreetPro_Released,StreetPro_Includes,StreetPro_Data_Path,StreetPro_Disp_Classic_Codes  from productmaster "
						+ " where (StreetPro_Cycle <> '' or StreetPro_Data_Source <> '' or StreetPro_Available <> '' or StreetPro_Vintage <> '' or StreetPro_Released <> '' or StreetPro_Disp_Classic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro display"))
				sql = "select Region, Country, StreetPro_Cycle, StreetPro_Data_Source, StreetPro_Available, StreetPro_Vintage, StreetPro_Released,StreetPro_Includes,StreetPro_Data_Path,StreetPro_Disp_Classic_Codes  from productmaster "
						+ " where (StreetPro_Cycle <> '' or StreetPro_Data_Source <> '' or StreetPro_Available <> '' or StreetPro_Vintage <> '' or StreetPro_Released <> '' or StreetPro_Disp_Classic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro navigation"))
				sql = "select Region, Country, StreetProNav_Cycle, StreetProNav_Data_Source, StreetProNav_Available, StreetProNav_Vintage, StreetProNav_Released,StreetProNav_Includes,StreetProNav_Data_Path,StreetProNav_Prem_Codes from productmaster "
						+ " where (StreetProNav_Cycle <> '' or StreetProNav_Data_Source <> '' or StreetProNav_Available <> '' or StreetProNav_Vintage <> '' or StreetProNav_Released <> '' or StreetProNav_Prem_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro traffic"))
				sql = "select Region, Country, StreetProTraffic_Cycle, StreetProTraffic_Data_Source, StreetProTraffic_Available, StreetProTraffic_Vintage, StreetProTraffic_Released,StreetProTraffic_Data_Path,StreetProTraffic_Codes  from productmaster "
						+ " where (StreetProTraffic_Cycle <> '' or StreetProTraffic_Data_Source <> '' or StreetProTraffic_Available <> '' or StreetProTraffic_Vintage <> '' or StreetProTraffic_Released <> '' or StreetProTraffic_Codes <> '')";
			else if (tablename.toLowerCase().equals("streetpro wrld"))
				sql = "select Region, Country, StreetProWrld_Cycle, StreetProWrld_Data_Source, StreetProWrld_Available, StreetProWrld_Vintage, StreetProWrld_Released,StreetProWrld_Codes  from productmaster "
						+ " where (StreetProWrld_Cycle <> '' or StreetProWrld_Data_Source <> '' or StreetProWrld_Available <> '' or StreetProWrld_Vintage <> '' or StreetProWrld_Released <> '' or StreetProWrld_Codes <> '')";
			else if (tablename.toLowerCase().equals("enterprise routing module"))
				sql = "select Region, Country, ERM_Cycle,ERM_Cycle_Month,ERM_Source,ERM_Available,ERM_Vintage,ERM_Released,ERM_Historic_Data,ERM_CVR,ERM_Includes,ERM_Data_Path  from productmaster "
						+ " where (ERM_Cycle <>'' or ERM_Source <>'' or ERM_Available <>'' or ERM_Vintage <>'' or ERM_Released<>'')";
			else if (tablename.toLowerCase().equals("route finder data"))
				sql = "select Region, Country, RF_Cycle,RF_Cycle_Month, RF_Source, RF_Available, RF_Version, RF_Data_Vintage, RF_Released,RF_Includes,RF_Data_Path  from productmaster "
						+ " where (RF_Cycle <> '' or RF_Cycle_Month <> '' or RF_Source <> '' or RF_Available <> '' or RF_Version <> '' or RF_Data_Vintage <> '' or RF_Released <>'')";
			else if (tablename.toLowerCase().equals("address doctor"))
				sql = "select Region, Country, AD_Cycle, AD_Source, AD_Available, AD_Vintage, AD_Released, AD_Updated,AD_Data_Path  from productmaster "
						+ " where (AD_Cycle <> '' or AD_Source <> '' or AD_Available <> '' or AD_Vintage <> '' or AD_Released <> '' or AD_Updated	<>'')";
			else if (tablename.toLowerCase().equals("loqate"))
				sql = "select Region, Country, Loqate_Cycle, Loqate_Source, Loqate_Available, Loqate_Released_Month, Loqate_Verification_Level, Loqate_Geocoding_Level, Loqate_Power_Search,Loqate_Data_Path from productmaster "
						+ " where (Loqate_Cycle <> '' or Loqate_Source <> '' or Loqate_Available <> '' or Loqate_Released_Month <> '' or Loqate_Verification_Level <> '' or Loqate_Geocoding_Level <> '' or Loqate_Power_Search <> '')";
			else if (tablename.toLowerCase().equals("address now"))
				sql = "select Region, Country, ADN_Cycle, ADN_Source, ADN_Available, ADN_Version, ADN_Released_Month, ADN_Knowledgebase_Updated, ADN_Validation_Level, ADN_RefData_Updated,ADN_Geocode_Updated,ADN_Data_Path  from productmaster "
						+ " where (ADN_Cycle <> '' or ADN_Source <> '' or ADN_Available <> '' or ADN_Version <> '' or ADN_Released_Month <> '' or ADN_Knowledgebase_Updated <> '' or ADN_Validation_Level <> '' or ADN_RefData_Updated <> '' or ADN_Geocode_Updated <> '')";
			else if (tablename.toLowerCase().equals("icp"))
				sql = "select Region, Country, ICP_Cycle, ICP_Source, ICP_Available, ICP_Released, ICP_Updated, ICP_Level,ICP_Data_Path  from productmaster "
						+ " where (ICP_Cycle <> '' or ICP_Source <> '' or ICP_Available <> '' or ICP_Released <> '' or ICP_Updated <> '' or ICP_Level	<>'')";
			else if (tablename.toLowerCase().equals("cameo"))
				sql = "select Region, Country, Cameo_Data, Cameo_Level  from productmaster where (Cameo_Data<>'' or Cameo_Level <> '')";
			else if (tablename.toLowerCase().equals("detail demographics"))
				sql = "select Region, Country,DetDem_Data, DetDem_Level  from productmaster where (DetDem_Data <> '' or DetDem_Level <> '')";
			else if (tablename.toLowerCase().equals("base demographics"))
				sql = "select Region, Country, BaseDem_Data, BaseDem_Level  from productmaster where (BaseDem_Data <> '' or BaseDem_Level <> '')";
			else if (tablename.toLowerCase().equals("property attribute data"))
				sql = "select Region, Country, `PAD Release Cycle`,`PAD Source`,`PAD Version`,`PAD Availibilty`,`PAD Part Code`,`PAD Format`,`PAD Geography Level`,`PAD Group`  from productmaster "
						+ " where (`PAD Release Cycle` <> '' or `PAD Source` <> '' or `PAD Version` <> '' or `PAD Availibilty` <> '' or `PAD Part Code` <> '' or `PAD Format` <> '' or `PAD Geography Level` <> '' or `PAD Group` <> '')";
			else if (tablename.toLowerCase().equals("addressing enrichment data"))
				sql = "select Region, Country, `AED Release Cycle`,`AED Source`,`AED Version`,`AED Availibilty`,`AED Part Code`,`AED Format`,`AED Geography Level`,`AED Group`  from productmaster "
						+ " where (`AED Release Cycle` <> '' or `AED Source` <> '' or `AED Version` <> '' or `AED Availibilty` <> '' or `AED Part Code` <> '' or `AED Format` <> '' or `AED Geography Level` <> '' or `AED Group` <> '')";
			else {
				tablename = getTablename(product, tablename);
				sql = "Select * from " + tablename;
			}

			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				json = new JSONArray();
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					int numColumns = rsmd.getColumnCount();

					Map<String, Object> obj = new LinkedHashMap<String, Object>();

					for (int i = 1; i < numColumns + 1; i++) {
						String column_name = rsmd.getColumnName(i);
						// System.out.println(column_name);

						if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
							obj.put(column_name, rs.getArray(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
							obj.put(column_name, rs.getBoolean(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
							obj.put(column_name, rs.getBlob(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
							if (tablename.toLowerCase().equals("paph_usa")) {
								if (rs.getString(column_name) != null) {
									if (rs.getString(column_name).indexOf(".") > 0) {
										double dblvalue = Double.parseDouble(rs.getString(column_name));
										convertedstring = String.format("%.3f", dblvalue);
										obj.put(column_name, convertedstring);
									} else {
										obj.put(column_name, rs.getString(column_name));
									}
								}
							} else {
								obj.put(column_name, rs.getString(column_name));
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
							obj.put(column_name, rs.getFloat(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
							obj.put(column_name, rs.getNString(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
							if (tablename.toLowerCase().equals("paph_usa")) {
								if (rs.getString(column_name).indexOf(".") > 0) {
									double dblvalue = Double.parseDouble(rs.getString(column_name));
									convertedstring = String.format("%.3f", dblvalue);
									obj.put(column_name, convertedstring);
								} else {
									obj.put(column_name, rs.getString(column_name));
								}
							} else {
								obj.put(column_name, rs.getString(column_name));
							}
						} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
							obj.put(column_name, rs.getDate(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
							obj.put(column_name, rs.getTimestamp(column_name));
						} else {
							obj.put(column_name, rs.getObject(column_name));
						}
					}

					Iterator<Entry<String, Object>> itr = obj.entrySet().iterator();
					JsonObject jsonobj = new JsonObject();
					while (itr.hasNext()) {
						Entry<String, Object> en = itr.next();
						jsonobj.addProperty(en.getKey(), en.getValue().toString());
					}

					masterjson.put(jsonobj);
				}

			} finally {
				conn.close();
			}
		} catch (Exception ex) {
			_flag = false;
			ex.printStackTrace();
		}
		//System.out.println("start idx: "+startindex+" page size: "+pagesize+" data length :"+masterjson.length());
		
		int startindex = 0;
		int adjustedpage = 0;
		if(page < 2){
			startindex = 1;
		}else{
			adjustedpage = page - 1;
			startindex = (adjustedpage*pagesize)+1;
		}		
		//System.out.println("Start Index : "+startindex);
		
		int maxdatalength = 0;
		if(startindex+pagesize < masterjson.length() ){
			maxdatalength = startindex+pagesize;
		} else {
			maxdatalength = masterjson.length();
		}
		
		if(pagesize < masterjson.length()){			
			for(int j=startindex;j<maxdatalength;j++)
			{
				//JSONObject jsonobj = masterjson.get(j);
				json.put(masterjson.get(j));
			}			
			System.out.println("Paginated Data Length :: "+json.length());
			result.put("docs",json);
			result.put("limit", pagesize);
			result.put("pages", Math.ceil(masterjson.length()/pagesize)+1);
			result.put("total", masterjson.length());
			result.put("page", page);
			
			return result;
		}else {
			System.out.println("Data Length :: "+masterjson.length());
			result.put("docs",masterjson);
			result.put("limit", pagesize);
			result.put("pages", Math.ceil(masterjson.length()/pagesize));
			result.put("total", masterjson.length());
			result.put("page", page);
			
			return result;
		}

	}

	// ************************************************************************************************************************************
	public String getTablename(String _productname, String _tablename) {
		// ArrayList<voProduct> country = new ArrayList<voProduct>();
		String sql = null;
		String tablereturn = null;
		boolean _flag = false;
		try {
			Connection conn = DriverManager.getConnection(url, username, password);

			sql = "select filter2 from filters where filter2display = '" + _tablename + "' and products = '"
					+ _productname + "'";

			System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					tablereturn = rs.getString("filter2");
				}
				// System.out.println("collected Country Data :: " +
				// country.size());

			} finally {
				conn.close();
			}
		} catch (Exception ex) {
			_flag = false;
			ex.printStackTrace();
		}
		return tablereturn;
	}

	// ************************************************************************************************************************
	public JSONArray getCountryPoiStats(String tablename, String country) {
		// ArrayList<voProduct> poiproduct = new ArrayList<voProduct>();
		JSONArray json = new JSONArray();
		String sql = null;
		try {
			Connection conn = DriverManager.getConnection(url, username, password);

			if ((tablename.equals("wppoi_country_category")) || (tablename.equals("wppoic_country_category")))
				sql = "Select * from " + tablename + " where countries ='" + country + "'";
			else if ((tablename.equals("wppoi_percentageattribute_fill_rates"))
					|| (tablename.equals("wppoic_percentageattribute_fill_rates")))
				sql = "Select * from " + tablename + " where `country name` ='" + country + "'";

			// System.out.println(sql);
			Statement stmt = null;
			try {

				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				json = new JSONArray();
				ResultSetMetaData rsmd = rs.getMetaData();

				while (rs.next()) {
					int numColumns = rsmd.getColumnCount();

					Map<String, Object> obj = new LinkedHashMap<String, Object>();

					for (int i = 1; i < numColumns + 1; i++) {
						String column_name = rsmd.getColumnName(i);
						// System.out.println(column_name);

						if (rsmd.getColumnType(i) == java.sql.Types.ARRAY) {
							obj.put(column_name, rs.getArray(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN) {
							obj.put(column_name, rs.getBoolean(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.BLOB) {
							obj.put(column_name, rs.getBlob(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE) {
							obj.put(column_name, rs.getDouble(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT) {
							obj.put(column_name, rs.getFloat(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR) {
							obj.put(column_name, rs.getNString(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR) {
							obj.put(column_name, rs.getString(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT) {
							obj.put(column_name, rs.getInt(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.DATE) {
							obj.put(column_name, rs.getDate(column_name));
						} else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP) {
							obj.put(column_name, rs.getTimestamp(column_name));
						} else {
							obj.put(column_name, rs.getObject(column_name));
						}
					}

					Iterator<Entry<String, Object>> itr = obj.entrySet().iterator();
					JsonObject jsonobj = new JsonObject();
					while (itr.hasNext()) {
						Entry<String, Object> en = itr.next();
						jsonobj.addProperty(en.getKey(), en.getValue().toString());
					}

					json.put(jsonobj);
				}

			} finally {
				conn.close();
			}
		} catch (Exception ex) {
			_flag = false;
			ex.printStackTrace();
		}

		return json;
	}


}
