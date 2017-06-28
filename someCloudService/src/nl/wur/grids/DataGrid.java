package nl.wur.grids;

import java.io.IOException;
import java.io.Serializable;

/**
 * Class    : DataGrid
 * Project  : Eururalis
 * Group    : Grid data
 * Author   : Rob Knapen, Peter Verweij
 * Date     : November 2005
 * <p/>
 * Purpose  :
 * Representation of abstract grid (two dimensional) float data.
 * 28-Sep-2006: bug fixed in method getMinMaxValues by Steven Hoek
 */
public abstract class DataGrid implements Serializable, Cloneable
{
    private String caption;
    private String filename;

    private double min=0;
    private double max=0;

    public abstract double getCornerX();


    public abstract void setCornerX(double value);


    public abstract double getCornerY();


    public abstract void setCornerY(double value);


    public abstract void setCellSize(double value);


    public abstract double getCellSize();


    public abstract int getColCount();


    public abstract int getRowCount();


    public abstract int getSize();


    public abstract double getNoDataValue();


    public abstract void setNoDataValue(double value);


    public abstract void setValue(int column, int row, double value);


    public abstract double getValue(int column, int row);


    public abstract void setGridSize(int numberOfColumns, int numberOfRows);


    public abstract void readFromFile(String fileName) throws IOException;


    public abstract void writeToFile(String fileName) throws IOException;


    public abstract Object clone() throws CloneNotSupportedException;

    /*  TO BE ADDED IN THE FUTURE:
    function    GetCumulativeValues(ClassCount: Integer; MinVal, MaxVal: Single): TAreaValues;
    function    GetNumberOfSamples: integer; virtual;
    function    GetSampleDensity: double; virtual;
    */


    public String getCaption()
    {
        return caption;
    }


    public void setCaption(String caption)
    {
        this.caption = caption;
    }


    public String getFileName()
    {
        return filename;
    }


    public void setFileName(String filename)
    {
        this.filename = filename;
    }


    public void clear()
    {
        setCellSize(1);
        setCornerX(0);
        setCornerY(0);
        setGridSize(0, 0);
    }

    /**
     * Traverse the array with data and determine the minimum and maximum
     */
    private void getMinMaxValues()
    {
        double noDataValue = getNoDataValue();

        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;

        for (int col = 0; col < getColCount() - 1; col++)
            for (int row = 0; row < getRowCount() - 1; row++)
            {
                double value = getValue(col, row);
                if (value != noDataValue)
                {
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                }
            }
    }


    public double getMinValue()
    {
        getMinMaxValues();
        return min;
    }


    public double getMaxValue()
    {
        getMinMaxValues();
        return max;
    }

}