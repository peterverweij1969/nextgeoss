package nl.wur.grids;

import nl.wur.math.Comparer;

import java.io.*;
import java.util.ArrayList;
import java.text.DecimalFormat;

/**
 * Class : AsciiGrid
 * Project : Eururalis
 * Group : Grid data
 * Author : Rob Knapen, Peter Verweij
 * Date : November 2005
 * <p/>
 * Purpose : Representation of ASCII grid (two dimensional) double data.
 * <p/>
 * TODO : - Add support for reading compressed (zip/binary) file
 * History:
 * Early 2006: support added for writing data to file
 * 28-Sep-2006: bug fixed in method writeToFile by Steven Hoek
 */
public class AsciiGrid extends DataGrid
{
    /**
     * Generated serialisation ID.
     */
    private static final long serialVersionUID = 3755652339459965694L;

    // keys in file header
    private final static String NRCOLS = "ncols";

    private final static String NRROWS = "nrows";

    private final static String XLLCORNER = "xllcorner";

    private final static String YLLCORNER = "yllcorner";

    private final static String CELLSIZE = "cellsize";

    private final static String NODATAVALUE = "NODATA_value";

    // file extensions
    // private final static String FILEEXTENSION_HEADER = ".hdr";
    // private final static String FILEEXTENSION_doubleDATA = ".flt";

    // exception messages
    private final static String TXT_READ_ERROR_HEADER = "Reading ASCII grid header failed!";
    private final static String TXT_READ_ERROR_DATA = "Reading ASCII grid data failed!";
    // private final static String ERR_MISSING_GRID_FILES = "Unable to read the grid file '%s'";

    // - FIELDS ----------------------------------------------------------------

    private double cornerX;

    private double cornerY;

    private double noDataValue;

    private double cellSize;

    private int colCount;

    private int rowCount;

    protected ArrayList<Double> data;

    // TODO Store data in a double[] to increase performance (probably)

    // -------------------------------------------------------------------------


    public AsciiGrid()
    {
        super();
        setCaption("New AsciiGrid");
        data = new ArrayList<Double>();
        setCellSize(1);
    }

    /*
      * Implement custom read / write methods to serialize the grid by only
      * storing its properties and filename, and not the actual data. This would
      * cause projects to become *really* large. Think of something to do with
      * temporary (calculated) grids that have no storage location. Probably just
      * remove then and they have to be recalculated the next time.
      */

    /*
      * private void writeObject(java.io.ObjectOutputStream out) throws
      * IOException { out.writeObject(getCaption());
      * out.writeObject(getFileName()); out.writedouble(cornerX);
      * out.writedouble(cornerY); out.writedouble(noDataValue);
      * out.writedouble(cellSize); out.writeInt(colCount);
      * out.writeInt(rowCount); }
      *
      *
      * private void readObject(java.io.ObjectInputStream in) throws IOException,
      * ClassNotFoundException { // start with reading fields into object
      * setCaption((String) in.readObject()); setFileName((String)
      * in.readObject());
      *
      * cornerX = in.readdouble(); cornerY = in.readdouble(); noDataValue =
      * in.readdouble(); cellSize = in.readdouble(); colCount = in.readInt();
      * rowCount = in.readInt();
      *  // check filename, if it exists try to read the data from it File
      * gridFile = new File(getFileName()); if (gridFile.exists() &&
      * (gridFile.canRead())) this.readFromFile(gridFile.getCanonicalPath());
      * else throw new IOException( String.format(ERR_MISSING_GRID_FILES,
      * gridFile.getCanonicalPath()) ); }
      */


    public Object clone() throws CloneNotSupportedException
    {
        AsciiGrid grid = new AsciiGrid();

        grid.setCaption("Copy of " + getCaption());
        grid.setCornerX(getCornerX());
        grid.setCornerY(getCornerY());
        grid.setNoDataValue(getNoDataValue());
        grid.setCellSize(getCellSize());

        // copy the grid data (in memory)
        grid.colCount = this.getColCount();
        grid.rowCount = this.getRowCount();
        grid.data.clear();
        grid.data.ensureCapacity(this.data.size());

        for (double f : this.data)
            grid.data.add(Double.valueOf(f));

        return grid;
    }


    public void readFromFile(String fileName) throws IOException
    {
        clear();

        setFileName(fileName);
        setCaption(fileName);

        FileReader r = new FileReader(new File(fileName));
        StreamTokenizer inputTokenizer = new StreamTokenizer(r);

        inputTokenizer.wordChars(33, 128);

        if (!readHeader(inputTokenizer))
            throw new IOException(TXT_READ_ERROR_HEADER);

        if (!readData(inputTokenizer))
            throw new IOException(TXT_READ_ERROR_DATA);
    }


    private String readHeaderKeyValue(StreamTokenizer tokenizer)
    {
        try
        {
            tokenizer.nextToken();
            String key = tokenizer.sval;
            tokenizer.nextToken();
            double value = tokenizer.nval;

            if (NRCOLS.equalsIgnoreCase(key))
                colCount = (int) value;
            else if (NRROWS.equalsIgnoreCase(key))
                rowCount = (int) value;
            else if (XLLCORNER.equalsIgnoreCase(key))
                setCornerX((double) value);
            else if (YLLCORNER.equalsIgnoreCase(key))
                setCornerY((double) value);
            else if (CELLSIZE.equalsIgnoreCase(key))
                setCellSize((double) value);
            else if (NODATAVALUE.equalsIgnoreCase(key))
                setNoDataValue((double) value);

            return key;
        }
        catch (IOException e)
        {
            return "";
        }
    }


    private boolean readHeader(StreamTokenizer tokenizer)
    {
        // TODO Decimal seperator must be set to '.'

        boolean ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(NRCOLS);
        if (ok)
            ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(NRROWS);
        if (ok)
            ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(XLLCORNER);
        if (ok)
            ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(YLLCORNER);
        if (ok)
            ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(CELLSIZE);
        if (ok)
            ok = readHeaderKeyValue(tokenizer).equalsIgnoreCase(NODATAVALUE);

        // TODO Reset decimal seperator to previous setting

        return ok;
    }


    private boolean readData(StreamTokenizer tokenizer)
    {
        setGridSize(getColCount(), getRowCount());

        for (int row = 0; row <= getRowCount() - 1; row++)
            for (int col = 0; col <= getColCount() - 1; col++)
                try
                {
                    if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER)
                        setValue(col, row, (double) tokenizer.nval);
                    else
                        return false;
                }
                catch (IOException e)
                {
                    return false;
                }
        return true;
    }


    public void writeToFile(String fileName) throws IOException
    {
        int rowcount = getRowCount();
        int colcount = getColCount();

        // create file

        FileOutputStream fos = new FileOutputStream(fileName);
        PrintStream ps = new PrintStream(fos);

        ps.print(NRCOLS);
        ps.print(" ");
        ps.println(colCount);
        ps.print(NRROWS);
        ps.print(" ");
        ps.println(rowCount);
        ps.print(XLLCORNER);
        ps.print(" ");
        ps.println(cornerX);
        ps.print(YLLCORNER);
        ps.print(" ");
        ps.println(cornerY);
        ps.print(CELLSIZE);
        ps.print(" ");
        ps.println(cellSize);
        ps.print(NODATAVALUE);
        ps.print(" ");
        ps.println(noDataValue);

        // write data
        int ix = 0;
        final double epsilon = 0.0000000001;
        double value = 0;
        DecimalFormat df = new DecimalFormat(("##########.##############"));
        for (int row = 0; row < rowcount; row++)
        {
            ix = row * colcount;
            for (int col = 0; col < colcount; col++)
            {
                value = data.get(ix + col);
                if (Math.abs(value) < epsilon) value = 0;
                ps.print(df.format(value));
                ps.print(" ");
            }
            ps.println();
        }
        ps.close();
    }


    public void setValue(int column, int row, double value)
    {
        if (Comparer.isInRange(column, 0, getColCount() - 1)
                && Comparer.isInRange(row, 0, getRowCount() - 1))
            data.set(row * getColCount() + column, value);
        else
            throw new IndexOutOfBoundsException();
    }


    public void setCellSize(double value)
    {
        cellSize = value;
    }


    public void setCornerX(double value)
    {
        cornerX = value;
    }


    public void setCornerY(double value)
    {
        cornerY = value;
    }


    public void setNoDataValue(double value)
    {
        // replace previous no data values in grid
        for (int i = 0; i < data.size() - 1; i++)
            if (data.get(i) == noDataValue)
                data.set(i, noDataValue);

        noDataValue = value;
    }


    public double getCellSize()
    {
        return cellSize;
    }


    public int getColCount()
    {
        return colCount;
    }


    public double getCornerX()
    {
        return cornerX;
    }


    public double getCornerY()
    {
        return cornerY;
    }


    public double getNoDataValue()
    {
        return noDataValue;
    }


    public int getRowCount()
    {
        return rowCount;
    }


    public int getSize()
    {
        return data.size();
    }


    public double getValue(int column, int row)
    {
        if (Comparer.isInRange(column, 0, getColCount() - 1)
                && Comparer.isInRange(row, 0, getRowCount() - 1))
            return data.get(row * getColCount() + column);
        else
            throw new IndexOutOfBoundsException();
    }


    public void setGridSize(int numberOfColumns, int numberOfRows)
    {
        colCount = numberOfColumns;
        rowCount = numberOfRows;

        data.clear();
        data.ensureCapacity(numberOfRows * numberOfColumns);
        for (int i = 0; i < numberOfRows * numberOfColumns; i++)
            data.add(getNoDataValue());
    }
}