package com.pb.ddd.delivery.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A utility class which exposes command line parsing functionality
 * 
 * @author aslam
 *
 */
public class ApacheCLIUtility {

	/**
	 * Write "help" to the provided OutputStream.
	 */
	static void printHelp(final Options options, final int printedRowWidth,
			final String header, final String footer,
			final int spacesBeforeOption,
			final int spacesBeforeOptionDescription, final boolean displayUsage) {
		final String commandLineSyntax = "java -cp AddressDBDataUploadTool-1.0.0.jar com.pb.ddd.delivery.tool.DeliveryTool -e <env> -c <config_file_name> -a <log_file_path> -sk <Secret key> -ak <Access key>";
		PrintWriter writer = new PrintWriter(System.out);
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax,
				header, options, spacesBeforeOption,
				spacesBeforeOptionDescription, footer, displayUsage);
		writer.flush();
	}

	/**
	 * Construct and provide Options.
	 * 
	 * @return Options expected from command-line.
	 */
	static Options constructOptions() {
		final Options options = new Options();
		Option help = new Option("h", "help", false,
				"Prints the detail help and usage information of the tool");
		Option version = new Option("v", "version", false,
				"prints the version information of the tool");
		Option configFile = Option.builder("c").longOpt("configFile")
				.desc("uses the given config excel file").hasArg()
				.build();
		Option accessKey = Option.builder("ak").longOpt("configFile")
				.desc("access key for accessing s3 bucket").hasArg()
				.build();
		Option secretKey = Option.builder("sk").longOpt("configFile")
				.desc("secret key for accessing s3 bucket").hasArg()
				.build();
		Option env = Option.builder("e").longOpt("env")
				.desc("env on which to run the tool is prd").hasArg()
				.build();
		Option auditLog = Option.builder("a").longOpt("auditLog")
				.desc("folder path inside which the auditLog.csv is created that captures the success or failure of each row in the excel").hasArg()
				.build();
		Option property = Option.builder("D").hasArgs()
				.argName("property=value").valueSeparator('=')
				.desc("usage as property=value").build();

		options.addOption(help);
		options.addOption(version);
		options.addOption(configFile);
		options.addOption(accessKey);
		options.addOption(secretKey);
		options.addOption(auditLog);
		options.addOption(env);
		options.addOption(property);
		return options;
	}

	/**
	 * Write the provided number of blank lines to the provided OutputStream.
	 * 
	 * @param numberBlankLines
	 *            Number of blank lines to write.
	 * @param out
	 *            OutputStream to which to write the blank lines.
	 */
	static void displayBlankLines(final int numberBlankLines,
			final OutputStream out) {
		try {
			for (int i = 0; i < numberBlankLines; ++i) {
				out.write("\n".getBytes());
			}
		} catch (IOException ioEx) {
			for (int i = 0; i < numberBlankLines; ++i) {
				System.out.println();
			}
		}
	}

	/**
	 * Display application header.
	 * 
	 * @out OutputStream to which header should be written.
	 */
	static void displayHeader(final OutputStream out) {
		final String header = "[Application to help Users" + 
				 " to upload data to the Addressing product DB ]\n ";
		try {
			out.write(header.getBytes());
		} catch (IOException ioEx) {
			System.out.println(header);
		}
	}

	/**
	 * Display command-line arguments without processing them in any further
	 * way.
	 * 
	 * @param commandLineArguments
	 *            Command-line arguments to be displayed.
	 */
	static void displayProvidedCommandLineArguments(
			final String[] commandLineArguments, final OutputStream out) {
		final StringBuffer buffer = new StringBuffer();
		try {
			out.write(("CommandLine Argument passed by you were : ").getBytes());
			for (final String argument : commandLineArguments) {
				buffer.append(argument).append(" ");
			}
			out.write((buffer.toString() + "\n").getBytes());
		} catch (IOException ioEx) {
			System.err
					.println("WARNING: Exception encountered trying to write to OutputStream:\n"
							+ ioEx.getMessage());
			System.out.println(buffer.toString());
		}
	}

	static void displayInformation() {
		displayBlankLines(1, System.out);
		displayHeader(System.out);
		displayBlankLines(2, System.out);
		System.out.println("-- HELP --");
		displayBlankLines(1, System.out);
		printHelp(constructOptions(), 80, "HELP", " -- End OF HELP -- ", 3, 5,
				true);

	}

}
