/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package somecloudservice;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.wur.grids.AsciiGrid;
import nl.wur.grids.DataGrid;

/**
 *
 * @author verwe008
 */
public class SomeCloudService {
    static String ROOT= "D:/UserData/NextGEOSS/github/nextgeoss/someCloudService/";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("running with:");
            System.out.println(args[0]);
            System.out.println(args[1]);
            System.out.println(args[2]);
            
            DataGrid inputGrid1 = createInMemoryGrid(ROOT+args[0]);
            DataGrid inputGrid2 = createInMemoryGrid(ROOT+args[1]);
            AsciiGrid outputGrid = doSomeCalculation(inputGrid1, inputGrid2);
            outputGrid.writeToFile(ROOT+args[2]);
        } catch (Exception e) {
            System.out.println("Cloud service failed :"+e.getMessage());
            System.out.println("Usage: <inputFile1.asc> <inputFile2.asc> <outputFile.asc>");
            throw new RuntimeException(e);
        }
    }
    
    private static DataGrid createInMemoryGrid(String filename) throws IOException {
        DataGrid result = new AsciiGrid();
        result.readFromFile(filename);
        return result;
    }

    private static AsciiGrid doSomeCalculation(DataGrid inputGrid1, DataGrid inputGrid2) throws CloneNotSupportedException {
        AsciiGrid result = (AsciiGrid)inputGrid1.clone(); // assume input grids are AsciiGrids
        
        // assume input grids have same number of columns and rows, origin, cellszize, etc.
        for (int col=0; col<inputGrid1.getColCount(); col++)
            for (int row=0; row<inputGrid1.getRowCount(); row++) {
                double cell1 = inputGrid1.getValue(col, row);
                double cell2 = inputGrid2.getValue(col, row);
                result.setValue(col, row, cell1 + cell2);
            }
        
        return result;
    }
    
}
