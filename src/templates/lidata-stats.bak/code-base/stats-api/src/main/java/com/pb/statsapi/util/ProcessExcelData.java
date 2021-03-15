package com.pb.statsapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class ProcessExcelData {

	private final OPCPackage xlsxPackage;
	private final int minColumns;
	
    /**
     * Creates a new XLSX -> CSV converter
     *
     * @param pkg        The XLSX package to process
     * @param output     The PrintStream to output the CSV to
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     */
    public ProcessExcelData(OPCPackage pkg,int minColumns) {
        this.xlsxPackage = pkg;
        this.minColumns = minColumns;
    }
    
    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws IOException If reading the data from the package fails.
     * @throws SAXException if parsing the XML data fails.
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public void process(String _tableprefix,String extention,String _directory,String _productline) throws IOException, OpenXML4JException, SAXException, ClassNotFoundException, SQLException {
        CreateTableAndLoadData createtableandloaddata = new CreateTableAndLoadData();
    	ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        while (iter.hasNext()) {
        	
            InputStream stream = iter.next();
            String sheetName = iter.getSheetName();
            processSheet(styles, strings, new ConvertSheetToCsv(sheetName), stream);
            createtableandloaddata.createSchema(_tableprefix,sheetName,extention,_directory,_productline);
            stream.close();
            ++index;
            

           //break;
        }
    }
    
    
    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *
     * @param styles The table of styles that may be referenced by cells in the sheet
     * @param strings The table of strings that may be referenced by cells in the sheet
     * @param sheetInputStream The stream to read the sheet-data from.

     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @throws SAXException if parsing the XML data fails.
     */
    public void processSheet(StylesTable styles,
            				ReadOnlySharedStringsTable strings,
            				SheetContentsHandler sheetHandler, 
            InputStream sheetInputStream) throws IOException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(
                  styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
           
         } catch(ParserConfigurationException e) {
        	 System.exit(1);
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
            
         }
    }
    
    
}
