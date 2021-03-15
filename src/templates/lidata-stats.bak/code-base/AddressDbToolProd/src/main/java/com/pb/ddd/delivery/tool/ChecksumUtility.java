package com.pb.ddd.delivery.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * A utility to generate checksum of a given file
 * 
 * @author aslam
 *
 */
public class ChecksumUtility {

	/**
	 * Generate checksum of a given file
	 * 
	 * @param prodFile
	 * @return
	 * @throws IOException
	 */
	static String generateChecksum(File prodFile) throws IOException {
		String checksum = null;
		FileInputStream fis = new FileInputStream(prodFile);
		checksum = DigestUtils.md5Hex(fis);
		fis.close();
		return checksum;

	}

	/**
	 * A method to create a checksum file
	 * 
	 * @param checksum
	 * @param deliveryId
	 * @throws IOException
	 */
	static void createChecksumFile(String checksum, String productFilePath)
			throws IOException {
		File tempFile = new File(FilenameUtils.getBaseName(productFilePath)
				+ ".json");
		if (tempFile.exists()) {
			tempFile.delete();
		}
		File newTempFile = new File(FilenameUtils.getBaseName(productFilePath)
				+ ".json");
		FileWriter fw = new FileWriter(newTempFile);
		fw.write("{\"checksum\" : " + "\"" + checksum + "\"}");
		fw.flush();
		fw.close();
	}

	/***
	 * A method which deleted the temporary checksum file
	 * 
	 * @param deliveryId
	 */
	static void deleteChecksumFile(String productFilePath) {
		File tempFile = new File(FilenameUtils.getBaseName(productFilePath)
				+ ".json");
		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

}
