package com.pb.ddd.delivery.tool.model;

/**
 * Model class to capture success and failure of each row processed
 * 
 * @author amit
 *
 */
public class ProcessRowResponse {

	private String productName;
	private String deliveryId;
	private String deliveryDesc;
	private String deliveryFile;
	private String deliveryChecksumFile;
	private boolean isDeliveryCreated = false;
	private boolean isDeliveryExists = false;
	private boolean isDeliveryFileUploaded = false;
	private boolean isDeliveryFileExists = false;
	private boolean isDeliveryCheckSumFileUploaded = false;
	private boolean isDeliveryCheckSumFileExists = false;
	private boolean isDeliveryAvailabilityUpdated = false;
	private boolean isProcessedCompletely = false;
	private int rowNum = 0;

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getDeliveryDesc() {
		return deliveryDesc;
	}

	public void setDeliveryDesc(String deliveryDesc) {
		this.deliveryDesc = deliveryDesc;
	}

	public String getDeliveryFile() {
		return deliveryFile;
	}

	public void setDeliveryFile(String deliveryFile) {
		this.deliveryFile = deliveryFile;
	}

	public String getDeliveryChecksumFile() {
		return deliveryChecksumFile;
	}

	public void setDeliveryChecksumFile(String deliveryChecksumFile) {
		this.deliveryChecksumFile = deliveryChecksumFile;
	}

	public boolean isDeliveryCreated() {
		return isDeliveryCreated;
	}

	public void setDeliveryCreated(boolean isDeliveryCreated) {
		this.isDeliveryCreated = isDeliveryCreated;
	}

	public boolean isProcessedCompletely() {
		return isProcessedCompletely;
	}

	public void setProcessedCompletely(boolean isProcessedCompletely) {
		this.isProcessedCompletely = isProcessedCompletely;
	}

	public boolean isDeliveryFileUploaded() {
		return isDeliveryFileUploaded;
	}

	public void setDeliveryFileUploaded(boolean isDeliveryFileUploaded) {
		this.isDeliveryFileUploaded = isDeliveryFileUploaded;
	}

	public boolean isDeliveryCheckSumFileUploaded() {
		return isDeliveryCheckSumFileUploaded;
	}
	
	
	public boolean isDeliveryAvailabilityUpdated() {
		return isDeliveryAvailabilityUpdated;
	}

	public void setDeliveryAvailabilityUpdated(boolean isDeliveryAvailabilityUpdated) {
		this.isDeliveryAvailabilityUpdated = isDeliveryAvailabilityUpdated;
	}

	public void setDeliveryCheckSumFileUploaded(
			boolean isDeliveryCheckSumFileUploaded) {
		this.isDeliveryCheckSumFileUploaded = isDeliveryCheckSumFileUploaded;
	}
	
	

	public boolean isDeliveryExists() {
		return isDeliveryExists;
	}

	public void setDeliveryExists(boolean isDeliveryExists) {
		this.isDeliveryExists = isDeliveryExists;
	}

	public boolean isDeliveryFileExists() {
		return isDeliveryFileExists;
	}

	public void setDeliveryFileExists(boolean isDeliveryFileExists) {
		this.isDeliveryFileExists = isDeliveryFileExists;
	}

	public boolean isDeliveryCheckSumFileExists() {
		return isDeliveryCheckSumFileExists;
	}

	public void setDeliveryCheckSumFileExists(boolean isDeliveryCheckSumFileExists) {
		this.isDeliveryCheckSumFileExists = isDeliveryCheckSumFileExists;
	}

	@Override
	public String toString() {
		String str = null;
		productName = (productName == null) ? "" : productName;
		deliveryId = (deliveryId == null) ? "" : deliveryId;
		deliveryDesc = (deliveryDesc == null) ? "" : deliveryDesc;
		str = rowNum + "|" + productName + "|" + deliveryDesc + "|" + deliveryId + "|"
				+ isDeliveryCreated + "|" +  isDeliveryCreated + "|" + isDeliveryFileExists + "|" +  isDeliveryFileUploaded + "|"
				+ isDeliveryCheckSumFileExists + "|" + isDeliveryCheckSumFileUploaded + "|" + isDeliveryAvailabilityUpdated + "|" + isProcessedCompletely;
		return str;
	}

}
