package com.pb.ddd.delivery.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.pb.ddd.delivery.tool.model.DeliveryModel;

/**
 * @author mo009kh
 *
 */
public class AWSS3Util {

	private static final long DEFAULT_FILE_PART_SIZE = 5 * 1024 * 1024; // 5MB
	private static long FILE_PART_SIZE = DEFAULT_FILE_PART_SIZE;

	/**
	 * A utility method to upload delivery file to S3
	 * 
	 * @param uploadFileName
	 * @param productName
	 * @param deliveryId
	 * @param env
	 * @param count
	 * @throws Exception
	 */
	static void uploadDeliveryFile(String uploadFileName,String productName,String vintage,boolean isConfig,String secretkey, String accesskey) throws Exception {
		boolean isUploaded = false;
//		int retryCount = new Integer(
//				PropertyUtil.INSTANCE.getValue("retry.count")).intValue();
//		int retryInterval = new Integer(
//				PropertyUtil.INSTANCE.getValue("retry.interval.minutes"))
//				.intValue();
		int retryCount = 3;
		int retryInterval = 1;
		
		for (int count = 1; count <= retryCount; count++) {
			boolean isSucess = tryUploadDelivery(uploadFileName, productName, count, vintage, isConfig, secretkey, accesskey);
			if (isSucess) {
				isUploaded = true;
				break;
			} else {
				Thread.sleep(60000 * retryInterval);
			}
		}
		if (!isUploaded) {
			throw new Exception("Cannot upload delivery checkumm file");
		}
	}


	
	private static boolean tryUploadDelivery(String uploadFileName,String productName, int count,String vintage,boolean isConfig,String secretkey, String accesskey) {
		AmazonS3 s3Client = AWSS3Util.createAWSS3Client(secretkey, accesskey);
		//String bucketName = AWSS3Util.getBucketName();
		String bucketName = "pb-addressing-iad-312260495963-pbaddressing-qa";
		
		String keyName = null;
		boolean isSucess = true;
		
		if(isConfig)
			keyName = "config_files";
		else{
			keyName = "data/"+productName+"/"+vintage;
		}
			
		//System.out.println(keyName);

		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
				bucketName, keyName + "/" + FilenameUtils.getName(uploadFileName));
		InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

		try {
			File file = new File(uploadFileName);
			System.out.println("try count " + count
					+ " Uploading delivery file " + uploadFileName
					+ " to S3 " + "\n");

			List<PartETag> partETags = new ArrayList<PartETag>();

			// Step 1: Initialize.
			long contentLength = file.length();
			long partSize = FILE_PART_SIZE;
			// Step 2: Upload parts.
			long filePosition = 0;
			long partSize1 = FILE_PART_SIZE;
			long filePos =0;
			
			//get the info on number of multiparts upload request that will be created before
			//creating the request for percentage calculation
			int noOfMultipartUploads =0;
			for (int i = 1; filePos < contentLength; i++) {
				// Last part can be less than part size. Adjust part size.
				partSize1 = Math.min(partSize1, (contentLength - filePos));
				filePos += partSize1;
				noOfMultipartUploads =i;
			}
		
			
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than part size. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest()
						.withBucketName(bucketName)
						.withKey(
								keyName + "/" + FilenameUtils.getName(uploadFileName))
						.withUploadId(initResponse.getUploadId())
						.withPartNumber(i).withFileOffset(filePosition)
						.withFile(file).withPartSize(partSize);


				partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());
				System.out.println("Upload completed is : " + Math.round(i*100/noOfMultipartUploads) + "%");
				filePosition += partSize;
			}
			// Step 3: Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
					bucketName, keyName + "/" + FilenameUtils.getName(uploadFileName),
					initResponse.getUploadId(), partETags);

			s3Client.completeMultipartUpload(compRequest);
			System.out.println("All Complete -comitted the file upload");

			
		} catch (Exception e) {
			s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
					bucketName, keyName + "/" + FilenameUtils.getName(uploadFileName),
					initResponse.getUploadId()));
			isSucess = false;
		}
		return isSucess;
	}

	/**
	 * A utility method to upload delivery checksum file to S3
	 * 
	 * @param uploadFileName
	 * @param productName
	 * @param deliveryId
	 * @param env
	 * @throws Exception
	 */
	static void uploadDeliverychecksumFile(String uploadFileName,
			String productName, String deliveryId, String env) throws Exception {
		boolean isUploaded = false;
		int retryCount = new Integer(
				PropertyUtil.INSTANCE.getValue("retry.count")).intValue();
		int retryInterval = new Integer(
				PropertyUtil.INSTANCE.getValue("retry.interval.minutes"))
				.intValue();
		for (int count = 1; count <= retryCount; count++) {
			boolean isSucess = tryUploadDeliverychecksumFile(uploadFileName,count);
			if (isSucess) {
				isUploaded = true;
				break;
			} else {
				Thread.sleep(60000 * retryInterval);
			}
		}
		if (!isUploaded) {
			throw new Exception("Cannot upload delivery checkumm file");
		}
	}

	private static boolean tryUploadDeliverychecksumFile(String uploadFileName,int count) {
		AmazonS3 s3Client = AWSS3Util.createAWSS3Client();
		String bucketName = AWSS3Util.getBucketName();
		String keyName = AWSS3Util.getBucketKeyName();
		boolean isSuccess = true;

		return isSuccess;
	}
	
	public static boolean checkFileExists(String productId, String deliveryId,String env,String fileName) {
		boolean isExists = false;
		AmazonS3 s3Client = AWSS3Util.createAWSS3Client();
		String bucketName = AWSS3Util.getBucketName();
		String keyName = AWSS3Util.getBucketKeyName();
		ObjectListing list = s3Client.listObjects(bucketName, keyName + "/" + productId + "/" + deliveryId  + "/" + fileName);
		if(list != null && list.getObjectSummaries().size() > 0)
			isExists = true;
		return isExists;
	}

	/**
	 * A utility method to create AWSS3 client
	 * 
	 * @param env
	 * @return AmazonS3
	 */
	private static AmazonS3 createAWSS3Client(String secretkey, String accesskey) {

		AmazonS3 s3Client = null;

		try{
			BasicAWSCredentials creds = new BasicAWSCredentials(accesskey,
					secretkey);
			ClientConfiguration cc = new ClientConfiguration();
			cc.setRetryPolicy(PredefinedRetryPolicies.DEFAULT);
			s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(Regions.US_EAST_1)
					.withCredentials(new AWSStaticCredentialsProvider(creds))
					.withClientConfiguration(cc).build();

			
		}
		catch(Exception ex){
			System.out.println("Error **** The aceesskey "
					+ accesskey +" or the secret key "+ secretkey
					+ " provided is not valid ****");
		}
		return s3Client;
	}
	
	
	/**
	 * A utility method to create AWSS3 client
	 * 
	 * @param env
	 * @return AmazonS3
	 */
	private static AmazonS3 createAWSS3Client() {
		String accessKey = null;
		String secretKey = null;

		accessKey = PropertyUtil.INSTANCE
				.getValue("prd.addressdb.s3.accessKey");
		secretKey = PropertyUtil.INSTANCE
				.getValue("prd.addressdb.s3.secretKey");

			BasicAWSCredentials creds = new BasicAWSCredentials(accessKey,
					secretKey);
			ClientConfiguration cc = new ClientConfiguration();
			cc.setRetryPolicy(PredefinedRetryPolicies.DEFAULT);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
					.withRegion(Regions.US_EAST_1)
					.withCredentials(new AWSStaticCredentialsProvider(creds))
					.withClientConfiguration(cc).build();
			return s3Client;
	
	}

	/**
	 * A utility method which returns the configured S3 Bucket name
	 * 
	 * @param env
	 * @return String
	 */
	private static String getBucketName() {
		String bucketName = null;

		bucketName = PropertyUtil.INSTANCE.getValue("prd.addressdb.s3.bucket");

		return bucketName;
	}

	/**
	 * A utility method which returns the configured S3 BucketKey name
	 * 
	 * @param env
	 * @return String
	 */
	private static String getBucketKeyName() {
		String keyName = null;

		keyName = PropertyUtil.INSTANCE.getValue("prd.addressdb.s3.bucket.key");

		return keyName;

	}

}
