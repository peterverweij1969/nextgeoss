package nl.wur.math;

/*
 * Comparer.java
 *
 * Methods to compare data type values. Code partially borrowed from Delphi 7
 * Math unit (math.pas) and used to translate other Delphi code into Java.
 *
 * Created on April 19, 2005, 4:33 PM
 */

public class Comparer
{
    public final static double FUZZ_FACTOR = 1000;
    public final static double DOUBLE_RESOLUTION = 1e-15 * FUZZ_FACTOR;
    public final static float FLOAT_RESOLUTION = (float) (1e-7 * FUZZ_FACTOR);


    /**
     * Check if value is between mimimum and maximum (both inclusive), returns
     * true if so and false otherwise.
     */
    public static boolean isInRange(int value, int minimum, int maximum)
    {
        return !((value < minimum) || (value > maximum));
    }


    /**
     * Check if value is between mimimum and maximum (both inclusive), returns
     * true if so and false otherwise.
     */
    public static boolean isInRange(float value, float minimum, float maximum)
    {
        return !((value < minimum) || (value > maximum));
    }


    public static boolean sameValue(float a, float b, float epsilon)
    {
        if (epsilon == 0)
            epsilon = Math.max(Math.min(Math.abs(a), Math.abs(b)) * FLOAT_RESOLUTION, FLOAT_RESOLUTION);

        if (a > b)
            return ((a - b) <= epsilon);
        else
            return ((b - a) <= epsilon);

    }


    public static boolean sameValue(double a, double b, double epsilon)
    {
        if (epsilon == 0)
            epsilon = Math.max(Math.min(Math.abs(a), Math.abs(b)) * DOUBLE_RESOLUTION, DOUBLE_RESOLUTION);

        if (a > b)
            return ((a - b) <= epsilon);
        else
            return ((b - a) <= epsilon);

    }


    public static boolean isZero(float a, float epsilon)
    {
        if (epsilon == 0)
            epsilon = FLOAT_RESOLUTION;

        return (Math.abs(a) <= epsilon);
    }


    public static boolean isZero(double a, double epsilon)
    {
        if (epsilon == 0)
            epsilon = DOUBLE_RESOLUTION;

        return (Math.abs(a) <= epsilon);
    }


    public static boolean areEqual(boolean a, boolean b)
    {
        return a == b;
    }


    public static boolean areEqual(char a, char b)
    {
        return a == b;
    }


    public static boolean areEqual(long a, long b)
    {
        return a == b;
    }


    public static boolean areEqual(float a, float b)
    {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b);
    }


    public static boolean areEqual(double a, double b)
    {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }


    public static boolean areEqual(Object a, Object b)
    {
        return a == null ? b == null : a.equals(b);
    }
}