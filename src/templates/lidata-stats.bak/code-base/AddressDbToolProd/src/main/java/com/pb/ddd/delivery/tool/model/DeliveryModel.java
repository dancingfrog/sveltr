package com.pb.ddd.delivery.tool.model;

import java.util.List;

/**
 * Delivery Model Pojo
 * 
 * @author aslam
 *
 */
public class DeliveryModel {
	private String name = null;
	private String header_attached = null;
	private String schema_change = null;
	private String vintage = null;
	//private String inputFilePath = null;
	private String one_time_upload = null;
	private String incremental_update = null;
	
	private List<String> data_header;
	private List<ChangedSchemaInfo> mapping_schema;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}





	public String getOne_time_upload() {
		return one_time_upload;
	}

	public void setOne_time_upload(String one_time_upload) {
		this.one_time_upload = one_time_upload;
	}

	public String getIncremental_update() {
		return incremental_update;
	}

	public void setIncremental_update(String incremental_update) {
		this.incremental_update = incremental_update;
	}

	public String getVintage() {
		return vintage;
	}

	public void setVintage(String vintage) {
		this.vintage = vintage;
	}

//	public String getInputFilePath() {
//		return inputFilePath;
//	}
//
//	public void setInputFilePath(String inputFilePath) {
//		this.inputFilePath = inputFilePath;
//	}


	public List<ChangedSchemaInfo> getMapping_schema() {
		return mapping_schema;
	}



	public String getHeader_attached() {
		return header_attached;
	}

	public void setHeader_attached(String header_attached) {
		this.header_attached = header_attached;
	}

	public String getSchema_change() {
		return schema_change;
	}

	public void setSchema_change(String schema_change) {
		this.schema_change = schema_change;
	}

	public List<String> getData_header() {
		return data_header;
	}

	public void setData_header(List<String> data_header) {
		this.data_header = data_header;
	}

	public void setMapping_schema(List<ChangedSchemaInfo> mapping_schema) {
		this.mapping_schema = mapping_schema;
	}



}
