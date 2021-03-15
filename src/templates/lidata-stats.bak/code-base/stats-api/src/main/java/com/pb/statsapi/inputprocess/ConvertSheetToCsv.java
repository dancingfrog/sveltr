package com.pb.statsapi.inputprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

public class ConvertSheetToCsv implements SheetContentsHandler{
	
    private boolean firstCellOfRow = false;
    private int currentRow = -1;
    private int currentCol = -1;
    FileOutputStream output;
    String sheetName = null;
    
    int minColumns = -1;
    // The package open is instantaneous, as it should be.
    OPCPackage p = null ;
    ProcessExcelData xlsx2csv = new ProcessExcelData(p,  minColumns);
	
    public ConvertSheetToCsv(String sheetName) throws FileNotFoundException {
    	this.output = new FileOutputStream(new File("C:\\output\\"+sheetName +".csv"));
    	this.sheetName = sheetName;
	}
    

    
    private void outputMissingRows(int number) throws Exception{
        for (int i=0; i<number; i++) {
            for (int j=0; j<minColumns; j++) {
                output.write(',');
            }
            output.write('\n');
        }
    }


    //@Override
    public void startRow(int rowNum) {
        // If there were gaps, output the missing rows
        try {
			outputMissingRows(rowNum-currentRow-1);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
        // Prepare for this row
        firstCellOfRow = true;
      
        currentRow = rowNum;
        currentCol = -1;
    }

    //@Override
    public void endRow(int rowNum){
        // Ensure the minimum number of columns
        for (int i=currentCol; i<minColumns; i++) {
            try {
				output.write(',');
			} catch (IOException e) {
				
				e.printStackTrace();
			}
        }
        try {
			output.write('\n');
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
    }

    //@Override
    public void cell(String cellReference, String formattedValue,
            XSSFComment comment) {
        if (firstCellOfRow) {
            firstCellOfRow = false;
        } else {
            try {
				output.write(',');
			} catch (IOException e) {
				
				e.printStackTrace();
				System.exit(1);
			}
        }

        // gracefully handle missing CellRef here in a similar way as XSSFCell does
        if(cellReference == null) {
            cellReference = new CellAddress(currentRow, currentCol).formatAsString();
        }

        // Did we miss any cells?
        int thisCol = (new CellReference(cellReference)).getCol();
        int missedCols = thisCol - currentCol - 1;
        for (int i=0; i<missedCols; i++) {
            try {
				output.write(',');
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        currentCol = thisCol;
        
        // Number or string?
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(formattedValue);
            try {
				output.write(formattedValue.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
        } catch (NumberFormatException e) {
            try {
				output.write('"');
                output.write(formattedValue.getBytes());
                output.write('"');
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}

        }
    }

    //@Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        // Skip, no headers or footers in CSV
    }

}
