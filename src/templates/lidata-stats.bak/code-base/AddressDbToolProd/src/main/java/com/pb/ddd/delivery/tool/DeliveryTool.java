package com.pb.ddd.delivery.tool;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

/**
 * CommandLine tool to add delivery in batch into DDD DB and to push associated
 * delivery file into Fusion DataLake based on configuration file passed
 * 
 * @author aslam
 *
 */
public class DeliveryTool {

	/**
	 * Main executable method of the DDD Delivery Tool
	 * 
	 * @param commandLineArguments
	 *            Commmand-line arguments.
	 */
	public static void main(final String[] commandLineArguments) {
		try {
			final CommandLineParser parser = new DefaultParser();
			final Options options = ApacheCLIUtility.constructOptions();
			CommandLine commandLine = null;
			String xlsFilepath = null;
			String auditLogFolderPath = null;
			String env = null;
			String secretkey = null;
			String accesskey = null;
			
			commandLine = parser.parse(options, commandLineArguments);
			if (commandLine.hasOption("v")) {
				System.out
						.println("The version of the AddressDB Upload Tool is 1.0");
				System.exit(0);
			}
			if (commandLine.hasOption("h")) {
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			}
			if (commandLine.hasOption('c')) {
				xlsFilepath = commandLine.getOptionValue('c');
				File xlsFile = new File(xlsFilepath);
				
				if (!xlsFile.isFile() || !xlsFile.exists()) {
					System.out.println("Error **** The config excel file path "
							+ xlsFilepath + " provided is not valid ****");
					ApacheCLIUtility.displayInformation();
					System.exit(0);
				} else if (!xlsFile.canRead()) {
					System.out.println("Error **** The config excel file path "
							+ xlsFilepath
							+ " provided cannot be read by application ****");
					ApacheCLIUtility.displayInformation();
					System.exit(0);
				} else {
					if (!("xls".equals(FilenameUtils.getExtension(xlsFilepath)) || "xlsx"
							.equals(FilenameUtils.getExtension(xlsFilepath)))) {
						System.out
								.println("Error **** The config excel file provided "
										+ xlsFilepath
										+ " is not an excel file ****");
						ApacheCLIUtility.displayInformation();
						System.exit(0);
					}
				}
			}
			if (!commandLine.hasOption("c")) {
				ApacheCLIUtility.displayBlankLines(1, System.out);
				System.out
						.println("Option -c is required to be  provided at commandline to do actual processing");
				ApacheCLIUtility.displayBlankLines(1, System.out);
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			}
			if (!commandLine.hasOption("e")) {
				ApacheCLIUtility.displayBlankLines(1, System.out);
				System.out
						.println("Option -e is required to be  provided at commandline to do actual processing");
				ApacheCLIUtility.displayBlankLines(1, System.out);
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			} else {
				env = commandLine.getOptionValue('e');
				if (!("prd".equals(env))) {
					System.out.println("Error **** The env provided " + env
							+ " is not prd ****");
					ApacheCLIUtility.displayInformation();
					System.exit(0);
				}
			}
			if (!commandLine.hasOption("sk")) {
				ApacheCLIUtility.displayBlankLines(1, System.out);
				System.out
						.println("Option -sk is required to be provided at commandline to input user secret key");
				ApacheCLIUtility.displayBlankLines(1, System.out);
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			} else {
				secretkey = commandLine.getOptionValue("sk");
			}
			if (!commandLine.hasOption("ak")) {
				ApacheCLIUtility.displayBlankLines(1, System.out);
				System.out
						.println("Option -ak is required to be provided at commandline to input user access key");
				ApacheCLIUtility.displayBlankLines(1, System.out);
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			} else {
				accesskey = commandLine.getOptionValue("ak");
			}
			if (commandLine.hasOption('a')) {
				auditLogFolderPath = commandLine.getOptionValue('a');
				File auditLogFolder = new File(auditLogFolderPath);
				if (!auditLogFolder.isDirectory() || !auditLogFolder.exists()) {
					System.out.println("Error **** The folder for auditLog "
							+ auditLogFolder
							+ " provided is not valid or does not exists ****");
					ApacheCLIUtility.displayInformation();
					System.exit(0);
				} else if (!auditLogFolder.canWrite()) {
					System.out.println("Error **** The folder for auditLog "
							+ xlsFilepath
							+ " provided, application cannot write to it ****");
					ApacheCLIUtility.displayInformation();
					System.exit(0);
				}
			} else {
				ApacheCLIUtility.displayBlankLines(1, System.out);
				System.out
						.println("Option -a is required to be  provided at commandline for auditLog.csv to be created into that folder path");
				ApacheCLIUtility.displayBlankLines(1, System.out);
				ApacheCLIUtility.displayInformation();
				System.exit(0);
			}
			DeliveryProcess.execute(xlsFilepath,auditLogFolderPath, env, secretkey, accesskey);
		} catch (ParseException ex) {
			ApacheCLIUtility.displayInformation();
			ApacheCLIUtility.displayProvidedCommandLineArguments(
					commandLineArguments, System.out);
		}
	}
}
