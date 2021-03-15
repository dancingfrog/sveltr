package com.ericsson.webService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.json.JSONArray;

import com.ericsson.Methods.GetProductMaster;
import com.ericsson.Methods.POIStats;
import com.ericsson.Methods.Users;
import com.google.gson.Gson;

@Path("/WebService")
public class WebService {

	public WebService() {
		// TODO Auto-generated constructor stub
	}
	
	
	@GET
	@Path("/GetCountryList")
	@Produces("application/html")
	public String getCountryList() {
		String _result = null;
		try {
			Gson gson = new Gson();
			_result = gson.toJson(new GetProductMaster().getCountryList());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result;
	}

	@GET
	@Path("/GetCountries/{_tablename}")
	@Produces("application/html")
	public String getCountries(@PathParam("_tablename") String _tablename) {
		String _result = null;
		try {
			Gson gson = new Gson();
			_result = gson.toJson(new POIStats().getCountries(_tablename).toArray());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result;
	}
	
	@GET
	@Path("/GetFips/{product}/{state}")
	@Produces("application/html")
	public String getCountries(@PathParam("product") String product,@PathParam("state") String state) {
		JSONArray _result = null;
		try {
			_result= new GetProductMaster().getFips(product, state); 
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
			_result= new POIStats().getAEDStats(product, state, fips); 
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
			_result = gson.toJson(new GetProductMaster().getProductTree());
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
			_result= new GetProductMaster().getProductData(); 
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
			_result= new GetProductMaster().getProductline(); 
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
			_result= new GetProductMaster().getProducts(productline); 
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
			_result= new GetProductMaster().getFirstFilter(product); 
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
			_result= new GetProductMaster().getSecondFilterData(product); 
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
			_result= new GetProductMaster().getSecondFilter(product, filter1); 
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	

	@GET
	@Path("/GetPoiStats/{_tablename}")
	@Produces("application/html")
	public String getPoiStats(@PathParam("_tablename") String _tablename) {
		JSONArray _result = null;
		try {
			_result= new POIStats().getPoiStats(_tablename); 
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
			_result= new POIStats().getCountryPoiStats(_tablename,_country); 
			//Gson gson = new Gson();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result.toString();
	}
	
	@GET
	@Path("/GetUsers/{_email}/{_password}")
	@Produces("application/html")
	public String getUsers(@PathParam("_email") String _email,@PathParam("_password") String _password) {
		String _result = null;
		try {
			_result= new Users().getUsers(_email, _password);
			//Gson gson = new Gson();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
		return _result;
	}
	


}
