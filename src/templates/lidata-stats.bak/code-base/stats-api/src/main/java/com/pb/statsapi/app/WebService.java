package com.pb.statsapi.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.pb.statsapi.util.Stats;

@Path("/WebService")
public class WebService {

	public WebService() {
		// TODO Auto-generated constructor stub
	}

	
	
	@POST
	@Path("/ProcessInputData")
	@Consumes(MediaType.APPLICATION_JSON)
	public String processInputData(InputStream incomingData) {
		StringBuilder sb = new StringBuilder();		
		String path = "",finalpath = "";
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);	
			}
			String[] jsonpath = sb.toString().split(" : ");
			if(jsonpath.length > 0){
				path = jsonpath[1];
			}
			finalpath = path.substring(1, path.length() - 2);
			
			new Stats().runProcess(finalpath);
			return "true";
			//return Response.status(200).entity(sb.toString()).build();
		} catch (Exception exception) {
			// return HTTP response 404 in case of success
			//return Response.status(404).entity(sb.toString()).build();
			System.out.println(exception.getMessage());
			return "false";
		}
	}
	
	
	
	@GET
	@Path("/GetFips/{product}/{state}")
	@Produces("application/html")
	public String getFips(@PathParam("product") String product,@PathParam("state") String state) {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getFips(product, state); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	

	@GET
	@Path("/GetProductTree")
	@Produces("application/html")
	public String getProductTree() {
		String _result = null;
		try {
			Gson gson = new Gson();
			_result = gson.toJson(new UIFilters().getProductTree());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result;
	}
	
	
	@GET
	@Path("/GetProductMaster")
	@Produces("application/html")
	public String getProductMaster() {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getProductData(); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	
	@GET
	@Path("/GetProductline")
	@Produces("application/html")
	public String getProductline() {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getProductline(); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	
	@GET
	@Path("/GetProducts/{productline}")
	@Produces("application/html")
	public String getProducts(@PathParam("productline") String productline) {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getProducts(productline); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	
	@GET
	@Path("/GetFirstFilter/{product}")
	@Produces("application/html")
	public String getFirstFilter(@PathParam("product") String product) {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getFirstFilter(product); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	
	@GET
	@Path("/GetSecondFilterData/{product}")
	@Produces("application/html")
	public String getSecondFilterData(@PathParam("product") String product) {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getSecondFilterData(product); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}	
	
	
	@GET
	@Path("/GetSecondFilter/{product}/{filter1}")
	@Produces("application/html")
	public String getSecondFilter(@PathParam("product") String product,@PathParam("filter1") String filter1) {
		JSONArray _result = null;
		try {
			_result= new UIFilters().getSecondFilter(product, filter1); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
//***********************************************************************************************************************************
	@GET
	@Path("/GetPoiStats/{_product}/{_tablename}")
	@Produces("application/html")
	public String getPoiStats(@PathParam("_product") String _product,@PathParam("_tablename") String _tablename) {
		JSONArray _result = null;
		try {
			_result= new StatsData().getPoiStats(_product,_tablename); 
			//Gson gson = new Gson();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	@GET
	@Path("/GetCountryPoiStats/{_tablename}/{_country}")
	@Produces("application/html")
	public String getCountryPoiStats(@PathParam("_tablename") String _tablename,@PathParam("_country") String _country) {
		JSONArray _result = null;
		try {
			_result= new StatsData().getCountryPoiStats(_tablename,_country); 
			//Gson gson = new Gson();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	@GET
	@Path("/GetAEDStats/{product}/{state}/{fips}")
	@Produces("application/html")
	public String getCountries(@PathParam("product") String product,@PathParam("state") String state,@PathParam("fips") String fips) {
		JSONArray _result = null;
		try {
			_result= new StatsData().getAEDStats(product, state, fips); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}


}
