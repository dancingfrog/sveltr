package com.pb.ddd.delivery.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pb.ddd.delivery.tool.model.DeliveryModel;
import com.pb.ddd.delivery.tool.model.FilePathInfo;
import com.pb.ddd.delivery.tool.model.ProcessRowResponse;
import com.pb.ddd.delivery.tool.model.ChangedSchemaInfo;


/**
 * The responsibility of this class it read the excel and create delivery and
 * also upload the delivery file and checksum file to S3
 * 
 * @author aslam
 *
 */
public class DeliveryProcess {

	private static List<ChangedSchemaInfo> lstChangedSchema = new ArrayList<>();	
	/**
	 * Read the excel and create delivery and also upload the delivery file and
	 * checksum file to S3
	 * 
	 * @param xlsFilepath
	 * @param auditLogFolderPath
	 * @param env
	 */
	static void execute(String xlsFilepath, String auditLogFolderPath,
			String env, String secretkey, String accesskey) {
		System.out.println("Execute.....");
		boolean isDeliverySheet = false,isHeaderSheet = false,isSchemaSheet = false;
		DeliveryModel deliveryModel = new DeliveryModel();
		ChangedSchemaInfo changedschemainfo = new ChangedSchemaInfo();
		FilePathInfo filepathinfo = new FilePathInfo();
		
		try {
			File xlsFile = new File(xlsFilepath);
			FileInputStream excelFile = new FileInputStream(xlsFile);
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet deliverySheet = workbook.getSheetAt(0);
			Sheet headerSheet = workbook.getSheetAt(1);
			Sheet schemaSheet = workbook.getSheetAt(2);
			boolean isErrors = validateRows(deliverySheet,headerSheet,schemaSheet, auditLogFolderPath);
			if (isErrors) {
				System.exit(0);
			}
			File auditFile = new File(auditLogFolderPath + "/auditLog.csv");
			if (auditFile.exists()) {
				auditFile.delete();
			}
//			java.io.FileWriter fw = new java.io.FileWriter(auditFile);
//			fw.write("productName|headerAvailable|schemaChange|month|year|inputFilePath");
//			fw.write(System.lineSeparator());
//			fw.flush();
			int i = 0;

			
			for (Row row : deliverySheet) {
				isDeliverySheet = true;
				isHeaderSheet = false;
				isSchemaSheet = false;
				if (i != 0) {
					ProcessRowResponse resp = processRow(deliverySheet,row, env,isDeliverySheet,isHeaderSheet,isSchemaSheet,deliveryModel,changedschemainfo,filepathinfo);
					if (!resp.isProcessedCompletely()) {
						System.out.println("Error: ***** The row number : "
								+ (row.getRowNum() + 1)
								+ " of config sheet did not get processed successfully *****");
//						fw.write(resp.toString());
//						fw.write(System.lineSeparator());
//						fw.flush();
					} else {
						System.out.println("Success: ***** The row number : "
								+ (row.getRowNum() + 1)
								+ " of config sheet got processed successfully *****");
//						fw.write(resp.toString());
//						fw.write(System.lineSeparator());
//						fw.flush();
					}
				}
				i++;
			}
			
			if(deliveryModel.getHeader_attached().equals("NO")){
				isDeliverySheet = false;
				isHeaderSheet = true;
				isSchemaSheet = false;
				for (Row row : headerSheet) {
					if (i != 0) {
						ProcessRowResponse resp = processRow(headerSheet,row, env,isDeliverySheet,isHeaderSheet,isSchemaSheet,deliveryModel,changedschemainfo,filepathinfo);
						if (!resp.isProcessedCompletely()) {
							System.out.println("Error: ***** The row number : "
									+ (row.getRowNum() + 1)
									+ " of header sheet did not get processed successfully *****");
//							fw.write(resp.toString());
//							fw.write(System.lineSeparator());
//							fw.flush();
						} else {
							System.out.println("Success: ***** The row number : "
									+ (row.getRowNum() + 1)
									+ "  of header sheet got processed successfully *****");
//							fw.write(resp.toString());
//							fw.write(System.lineSeparator());
//							fw.flush();
						}
					}
					
					i++;
				}
			}
//			else{
//				System.out.println("Error: ***** The value specefied in HeaderAvailable in Config sheet is incorrect *****");
//				System.exit(0);
//			}
			
			if(deliveryModel.getSchema_change().equals("YES")){
				isDeliverySheet = false;
				isHeaderSheet = false;
				isSchemaSheet = true;
				i = 0;
				for (Row row : schemaSheet) {
					if (i != 0) {
						ProcessRowResponse resp = processRow(schemaSheet, row, env,isDeliverySheet,isHeaderSheet,isSchemaSheet,deliveryModel,changedschemainfo,filepathinfo);
						if (!resp.isProcessedCompletely()) {
							System.out.println("Error: ***** The row number : "
									+ (row.getRowNum() + 1)
									+ " of Schema Sheet did not get processed successfully *****");
//							fw.write(resp.toString());
//							fw.write(System.lineSeparator());
//							fw.flush();
						} else {
							System.out.println("Success: ***** The row number : "
									+ (row.getRowNum() + 1)
									+ "  of Schema Sheet got processed successfully *****");
//							fw.write(resp.toString());
//							fw.write(System.lineSeparator());
//							fw.flush();
						}
					
					}
					i++;
				}
			}
//			else{
//				System.out.println("Error: ***** The value specefied in SchemaChange in Config sheet is incorrect *****");
//				System.exit(0);
//			}
			
			workbook.close();
			//fw.close();
			
			String filepath = null;
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(deliveryModel);
		
			// Convert object to JSON string and pretty print
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(deliveryModel);
			//System.out.println(jsonInString);
			File file = new File(filepathinfo.getInputFilePath());
			filepath = file.getParent();
			
			mapper.writeValue(new File(filepath +"\\"+deliveryModel.getName().toLowerCase() +"_config.json"), deliveryModel);
			
			uploadDelivery(deliveryModel,filepathinfo, secretkey, accesskey, auditLogFolderPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * check if all rows are proper and header is fine
	 * 
	 * @param deliverySheet
	 *            Sheet
	 * @param auditLogFolderPath
	 * @return boolean
	 */
	private static boolean validateRows(Sheet deliverySheet,Sheet headerSheet,Sheet schemaSheet,
			String auditLogFolderPath) {
		boolean isError = false;
		boolean isSchemaSheet = false;
		
		List<String> errorList = new ArrayList<String>();
		// validate all rows
		for (Row record : deliverySheet) {
			int totalCellCount = record.getPhysicalNumberOfCells();
			if (totalCellCount == 8 && record.getRowNum() == 0) {
				boolean isValid = validateHeaders(record,isSchemaSheet);
				if (!isValid) {
					errorList
							.add("Error - Please ensure that name and order of header fields in the excel confirms to the standard template");
				}
			} else if (totalCellCount != 6 && record.getRowNum() == 0) {
				errorList
						.add("Error - Please ensure that there are 6 header columns. The header now has : "
								+ totalCellCount + " columns");
			} /*
			 * else if (totalCellCount != 19 && record.getRowNum() > 0) {
			 * errorList .add(
			 * "Error - Please ensure that there are 19 columns in the row number : "
			 * + (record.getRowNum() + 1) + ". The row has " + totalCellCount +
			 * " columns"); }
			 */else {
				List<String> errList = validateRecords(record);
				errorList.addAll(errList);
			}
		}
		
		isSchemaSheet = true;
		for (Row record : schemaSheet) {
			int totalCellCount = record.getPhysicalNumberOfCells();
			if (totalCellCount == 2 && record.getRowNum() == 0) {
				boolean isValid = validateHeaders(record,isSchemaSheet);
				if (!isValid) {
					errorList
							.add("Error - Please ensure that name and order of header fields in the excel confirms to the standard template");
				}
			} else if (totalCellCount != 2 && record.getRowNum() == 0) {
				errorList
						.add("Error - Please ensure that there are 2 header columns. The header now has : "
								+ totalCellCount + " columns");
			} /*
			 * else if (totalCellCount != 19 && record.getRowNum() > 0) {
			 * errorList .add(
			 * "Error - Please ensure that there are 19 columns in the row number : "
			 * + (record.getRowNum() + 1) + ". The row has " + totalCellCount +
			 * " columns"); }
			 */else {
				//List<String> errList = validateRecords(record);
				//errorList.addAll(errList);
			}
		}
		
		if (errorList.size() > 0)
			isError = true;
		try {
			File preAuditFile = new File(auditLogFolderPath
					+ "/preAuditLog.txt");
			if (preAuditFile.exists()) {
				preAuditFile.delete();
			}

			java.io.FileWriter fw = new java.io.FileWriter(preAuditFile);
			fw.write("Errors");
			fw.write(System.lineSeparator());
			fw.flush();
			for (String error : errorList) {
				System.out.println(error);
				fw.write(error);
				fw.write(System.lineSeparator());
				fw.flush();
			}
			fw.close();
		} catch (Exception e) {
			System.out.println("Error writing preaudit file");
		}

		return isError;
	}

	/***
	 * check if header row matches the desired template
	 * 
	 * @param header
	 *            row
	 * @return boolean
	 */
	private static boolean validateHeaders(Row header, boolean isSchemaSheet) {
		boolean isValid = true;
		int currentCellCount = 0;
		
		if(isSchemaSheet){
			currentCellCount = 0;
		
			for (Cell cell : header) {
				switch (currentCellCount) {
				case 0:
					if (!"Original".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 1:
					if (!"Updated".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				}
				currentCellCount++;
			}
			
		}else{
			currentCellCount = 0;
			for (Cell cell : header) {
				switch (currentCellCount) {
				case 0:
					if (!"productName".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 1:
					if (!"headerAvailable".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 2:
					if (!"schemaChange".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 3:
					if (!"month".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 4:
					if (!"year".equals(cell.getStringCellValue().trim())) {
						isValid = false;
					}
					break;
				case 5:
					if (!"one_time_upload_data".equals(cell.getStringCellValue()
							.trim())) {
						isValid = false;
					}
					break;
				case 6:
					if (!"incremental_update".equals(cell.getStringCellValue()
							.trim())) {
						isValid = false;
					}
					break;	
				case 7:
					if (!"inputFilePath".equals(cell.getStringCellValue()
							.trim())) {
						isValid = false;
					}
					break;
	
				}
	
				currentCellCount++;
			}
		}
		return isValid;
	}

	/**
	 * process each row of the excel and upload delivery and checksum file
	 * 
	 * @param row
	 * @return
	 */
	private static ProcessRowResponse processRow(Sheet sheet, Row row, String env,boolean isDeliverySheet,boolean isHeaderSheet,boolean isSchemaSheet,
			DeliveryModel deliveryModel,ChangedSchemaInfo changedschemainfo,FilePathInfo filepathinfo) {
		String deliveryId = null;
		ProcessRowResponse processRowResponse = new ProcessRowResponse();
		if (row == null) {
			System.out.println("Warning: ***** "
					+ "The row is empty.Hence was not processed *****");
			return processRowResponse;
		} else {

			//DeliveryModel deliveryModel = null;
			try {
				deliveryModel = populateDeliveryModel(sheet, row,isDeliverySheet,isHeaderSheet,isSchemaSheet,deliveryModel,changedschemainfo,filepathinfo);
				processRowResponse.setProcessedCompletely(true);
				
			} catch (ParseException ex) {
				System.out
						.println("Error: ***** The row number : "
								+ (row.getRowNum() + 1)
								+ " has some issue in field .Hence was not processed.  Exception : "
								+ ex.getMessage() + " *****" + "\n");
			} catch (Exception ex) {
				System.out
						.println("Error parsing the value from input config file.  Exception : "
								+ ex.getMessage() + "\n");
			}

				File productFile = new File(filepathinfo.getInputFilePath());
				if (!productFile.isFile() || !productFile.exists()) {
					System.out.println("Error **** The product file path "
							+ productFile + " provided is not valid ****");
				} else if (!productFile.canRead()) {
					System.out.println("Error **** The product file path "
							+ productFile
							+ " provided cannot be read by application ****");
				} else {
					if (!("csv".equals(FilenameUtils.getExtension(filepathinfo
							.getInputFilePath()))
							|| "txt".equals(FilenameUtils
									.getExtension(filepathinfo
											.getInputFilePath()))
							|| "xlsx".equals(FilenameUtils
									.getExtension(filepathinfo
											.getInputFilePath())) || "xls"
								.equals(FilenameUtils
										.getExtension(filepathinfo
												.getInputFilePath())))) {
						System.out
								.println("Error **** The product file provided "
										+ productFile
										+ " is not an csv,txt,xlsx or xls file ****");
					} 
				}

		}
		return processRowResponse;
	}

	
	
	private static DeliveryModel populateDeliveryModel(Sheet sheet, Row row,boolean isDeliverySheet,boolean isHeaderSheet,boolean isSchemaSheet,DeliveryModel deliveryModel,
			ChangedSchemaInfo changedschemainfo,FilePathInfo filepathinfo)
			throws ParseException {
		int currentCellCount = 0;		
		
		String productName = null;
		String headerAvailable = null;
		String schemaChange = null;
		String onetimeuploaddata = null;
		String incrementalupdate = null;
		int month = 0;
		int year = 0;
		String inputFilePath = null;
		String headers = null;
		String deliminator = null;
		String originalFeild = null;
		String updatedFeild = null;

		String productFilePath = null;
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone serverTZ = TimeZone.getTimeZone("UTC");
		sm.setTimeZone(serverTZ);
		
	
		
		//Vintage vintage = new Vintage();
		if(isDeliverySheet){
			currentCellCount = 0;
			for (int i = 0; i < 8; i++) {
				Cell cell = row.getCell(i);
				switch (i) {
				case 0:
					if (cell != null) {
						productName = cell.getStringCellValue();
						deliveryModel.setName(productName.toLowerCase());
					}
					break;
				case 1:
					if (cell != null) {
						headerAvailable = cell.getStringCellValue();
						deliveryModel.setHeader_attached(headerAvailable);
					}
					break;
				case 2:
					if (cell != null) {
						schemaChange = cell.getStringCellValue();
						deliveryModel.setSchema_change(schemaChange);
					}
					break;
				case 3:
					if (cell != null) {
						month = (int) cell.getNumericCellValue();
					}
					break;
				case 4:
					if (cell != null) {
						year = (int) cell.getNumericCellValue();
						deliveryModel.setVintage(year+"_"+month);
					}
					break;
				case 5:
					if (cell != null) {
						onetimeuploaddata = cell.getStringCellValue();
						String incrementalupdatevalue = row.getCell(6).getStringCellValue();
						if((onetimeuploaddata.equals("YES")) && (incrementalupdatevalue.equals("YES"))){
							System.out.println("Error One Time Upload cannot have value 'YES' as this is an incremental update ");
							System.exit(0);
						}
						else{
							deliveryModel.setOne_time_upload(onetimeuploaddata);
						}
						
					}
					break;
				case 6:
					if (cell != null) {
						incrementalupdate = cell.getStringCellValue();
						if((incrementalupdate.equals("YES")) && (deliveryModel.getOne_time_upload().equals("YES"))){
							System.out.println("Error Incremental Update cannot have value 'YES' as this is One Time Upload ");
							System.exit(0);
						}
						else{
							deliveryModel.setIncremental_update(incrementalupdate);
						}
					}
					break;	
				case 7:
					if (cell != null) {
						inputFilePath = cell.getStringCellValue();
						if(inputFilePath.contains("\\"))
							filepathinfo.setInputFilePath(inputFilePath.replaceAll("\\\\", "/"));
						else	
							filepathinfo.setInputFilePath(inputFilePath);
					}
					break;
				}
				
				currentCellCount++;
			}
		}else if(isHeaderSheet){
			currentCellCount = 0;
			List<String> lst = new ArrayList<>();
			
			Iterator<Row> rowIterator = sheet.rowIterator();
			while(rowIterator.hasNext())
			{
				Row newrow = rowIterator.next();
				Iterator<Cell> cellIterator = newrow.cellIterator();

				while(cellIterator.hasNext())
				{	
					Cell cell = cellIterator.next();
					lst.add(cell.getStringCellValue());
				}
			}
			deliveryModel.setData_header(lst);
		}else if(isSchemaSheet){
			currentCellCount = 0;
			
			changedschemainfo = new ChangedSchemaInfo();
			for (int i = 0; i < 2; i++) {
				Cell cell = row.getCell(i);
				switch (i) {
				case 0:
					if (cell != null) {
						originalFeild = cell.getStringCellValue();
						changedschemainfo.setOriginalFeild(originalFeild);
					}
					break;
				case 1:
					if (cell != null) {
						updatedFeild = cell.getStringCellValue();
						changedschemainfo.setUpdatedFeild(updatedFeild);
					}
					break;	
				}						
				currentCellCount++;
			}
			lstChangedSchema.add(changedschemainfo);	
					
		}
		deliveryModel.setMapping_schema(lstChangedSchema);
		return deliveryModel;

	}

	/**
	 * validate each row for basic data correctness without making API call
	 * 
	 * @param row
	 * @return
	 * @throws ParseException
	 */
	private static List<String> validateRecords(Row row) {
		List<String> errList = new ArrayList<String>();
		int month = 0;
		int year = 0;

		for (int i = 0; i < 8; i++) {
			Cell cell = row.getCell(i);
			switch (i) {
			case 0:
				if (cell == null || cell.getStringCellValue() == null
						|| "".equals(cell.getStringCellValue().trim())) {
					errList.add("Error Product Name provided in row "
							+ (row.getRowNum() + 1) + " is null or blank");
				}else{
					Pattern p = Pattern.compile("[^A-Za-z0-9]");
				    Matcher m = p.matcher(cell.getStringCellValue());
				    boolean b = m.find();
				    if (b == true)
				    	 errList.add("Special Character not allowed in Product Name ");
				}
				break;
			case 1:
				if (cell == null || cell.getStringCellValue() == null
						|| "".equals(cell.getStringCellValue().trim())) {
					errList.add("Error Header Availability provided in row "
							+ (row.getRowNum() + 1) + " is null or blank");
				}else {
					if (!("YES".equals(cell.getStringCellValue())
							|| "NO".equals(cell.getStringCellValue()) )) {
						errList.add("Error Header Availability provided in row "
								+ (row.getRowNum() + 1)
								+ " is not one of YES or NO");
					}
				}
				break;
			case 2:
				if (cell == null || cell.getStringCellValue() == null
				|| "".equals(cell.getStringCellValue().trim())) {
					errList.add("Error Schema Change provided in row "
							+ (row.getRowNum() + 1) + " is null or blank");
				}else {
					if (!("YES".equals(cell.getStringCellValue())
							|| "NO".equals(cell.getStringCellValue()) )) {
						errList.add("Error Schema Change provided in row "
								+ (row.getRowNum() + 1)
								+ " is not one of YES or NO");
					}
				}
				break;
			case 3:
				try {
					if (cell != null) {
						int inputmonth = (int) cell.getNumericCellValue();
						if((inputmonth > 0) && (inputmonth < 13))
							month = (int) cell.getNumericCellValue();
						else{
							errList.add("Error Month provided in row "
									+ (row.getRowNum() + 1)
									+ " is not valid");
						}							
							
					} else {
						errList.add("Error Month provided in row "
								+ (row.getRowNum() + 1)
								+ " should not be null or blank");
					}
				} catch (Exception e) {
					errList.add("Error Month provided in row "
							+ (row.getRowNum() + 1) + " should be a number");
				}
				break;
			case 4:
				try {
					if (cell != null) {
						int inputyear = (int) cell.getNumericCellValue();
						if(inputyear > 0)
							year = (int) cell.getNumericCellValue();
						else{
							errList.add("Error Year provided in row "
									+ (row.getRowNum() + 1)
									+ " is not valid");
						}							
							
					} else {
						errList.add("Error Year provided in row "
								+ (row.getRowNum() + 1)
								+ " should not be null or blank");
					}
				} catch (Exception e) {
					errList.add("Error Year provided in row "
							+ (row.getRowNum() + 1) + " should be a number");
				}
				break;
			case 5:
				if (cell == null || cell.getStringCellValue() == null
				|| "".equals(cell.getStringCellValue().trim())) {
					errList.add("Error One Time Upload value provided in row "
							+ (row.getRowNum() + 1) + " is null or blank");
				}else {
					if (!("YES".equals(cell.getStringCellValue())
							|| "NO".equals(cell.getStringCellValue()) )) {
						errList.add("Error One Time Upload value provided in row "
								+ (row.getRowNum() + 1)
								+ " is not one of YES or NO");
					}
				}
				break;	
			case 6:
				if (cell == null || cell.getStringCellValue() == null
				|| "".equals(cell.getStringCellValue().trim())) {
					errList.add("Error Incremental Update Value provided in row "
							+ (row.getRowNum() + 1) + " is null or blank");
				}else {
					if (!("YES".equals(cell.getStringCellValue())
							|| "NO".equals(cell.getStringCellValue()) )) {
						errList.add("Error Incremental Update Value provided in row "
								+ (row.getRowNum() + 1)
								+ " is not one of YES or NO");
					}
				}
				break;	
			case 7:			
					if (cell == null || cell.getStringCellValue() == null
							|| "".equals(cell.getStringCellValue().trim())) {
						errList.add("Error Input Path provided in row "
								+ (row.getRowNum() + 1) + " is null or blank");
					}
					break;
			}
		}
		return errList;

	}

	/**
	 * Upload Delivery File to S3
	 * 
	 * @param deliveryModel
	 * @param deliveryId
	 * @param env
	 * @return
	 */
	private static boolean uploadDelivery(DeliveryModel deliveryModel,FilePathInfo filepathinfo,String secretkey, String accesskey, String auditLogFolderPath) {
		boolean isProcessedSuccessfully = true;
		String configFilePath = (filepathinfo.getInputFilePath().substring(0,filepathinfo.getInputFilePath().lastIndexOf("/")))+"/"+deliveryModel.getName()+"_config.json";
		System.out.println("**** Config file created **********************");
		//System.out.println(configFilePath);
		try {
			AWSS3Util.uploadDeliveryFile(configFilePath,deliveryModel.getName(),deliveryModel.getVintage(), true, secretkey, accesskey);//uploading config file 
			AWSS3Util.uploadDeliveryFile(filepathinfo.getInputFilePath(),deliveryModel.getName().toLowerCase(),deliveryModel.getVintage(), false, secretkey, accesskey);//uploading the input data file
		} catch (Exception ex) {
			isProcessedSuccessfully = false;
			System.out
					.println("Caught an Exception while making request to upload Delivery file"+ex);
			try {
				File uploadErrorFile = new File(auditLogFolderPath
						+ "/UploadErrorFile.txt");
				if (uploadErrorFile.exists()) {
					uploadErrorFile.delete();
				}

				java.io.FileWriter fw = new java.io.FileWriter(uploadErrorFile);
				fw.write("Error while making request to upload Delivery file");
				fw.write(System.lineSeparator());
				fw.write(ex.getMessage());
				fw.close();
			} catch (Exception e) {
				System.out.println("Error writing preaudit file");
			}
		}
		return isProcessedSuccessfully;
	}

	/**
	 * Upload Delivery File to S3
	 * 
	 * @param deliveryModel
	 * @param deliveryId
	 * @param env
	 * @return
	 */
	private static boolean checkFileOnS3(String productId, String deliveryId,
			String fileName, String env) throws Exception {
		boolean isExists = false;
		try {
			isExists = AWSS3Util.checkFileExists(productId, deliveryId, env,
					fileName);
		} catch (Exception ex) {
			System.out
					.println("Caught an Exception while making request to check if file: "
							+ fileName
							+ " already exists in Fusion Data Lake S3");
			throw ex;
		}
		return isExists;
	}




}
