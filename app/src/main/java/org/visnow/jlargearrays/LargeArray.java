/*
 * JLargeArrays
 * Copyright (C) 2006-2019 University of Warsaw, ICM
 * Copyright (C) 2020 onward visnow.org
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 */
package org.visnow.jlargearrays;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.math3.util.FastMath;

/**
 * The base class for all large arrays. All implementations of this abstract
 * class can store up to 2<SUP>63</SUP> elements of primitive data types.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public abstract class LargeArray implements java.io.Serializable
{

    private static final long serialVersionUID = 7921589398878016801L;

    /**
     * Data type.
     */
    protected LargeArrayType type;

    /**
     * Number of elements.
     */
    protected long length;

    /**
     * True when array is constant.
     */
    protected boolean isConstant = false;

    /**
     * Class instance responsible for handling the native pointer's life cycle.
     */
    protected Object parent = null;

    /**
     * Pointer to native memory.
     */
    protected transient long ptr = 0;

    /**
     * The largest array size for which a regular 1D Java array is used to store data.
     */
    private static int maxSizeOf32bitArray = 1073741824; // 2^30;

    /**
     * If the number of elements in an array is greater than the value of LARGEST_SUBARRAY, then getXXXData() methods return null.
     */
    public static final int LARGEST_SUBARRAY = 1073741824; // 2^30;

    /**
     * Default value of maximal string length.
     */
    public static final int DEFAULT_MAX_STRING_LENGTH = 100;

    /**
     * Default value of maximal object size.
     */
    public static final int DEFAULT_MAX_OBJECT_SIZE = 1024; //1KB

    /**
     * Creates new instance of this class.
     */
    protected LargeArray()
    {
        super();
    }

    /**
     * Creates new instance of this class by wrapping a native pointer.
     * Providing an invalid pointer, parent or length will result in
     * unpredictable behavior and likely JVM crash. The assumption is that the
     * pointer is valid as long as the parent is not garbage collected.
     *
     * @param parent         class instance responsible for handling the pointer's life
     *                       cycle, the created instance of LargeArray will prevent the GC from
     *                       reclaiming the parent.
     * @param nativePointer  native pointer to wrap.
     * @param largeArrayType type of array
     * @param length         array length
     */
    public LargeArray(final Object parent,
                      final long nativePointer,
                      final LargeArrayType largeArrayType,
                      final long length)
    {
        super();
        this.parent = parent;
        this.ptr = nativePointer;
        this.type = largeArrayType;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
    }

    /**
     * Returns the internal pointer address (if isLarge)
     *
     * @return native pointer address or 0 if not large array.
     */
    public long nativePointer()
    {
        return ptr;
    }

    /**
     * Returns the length of an array.
     *
     * @return the length of an array
     */
    public long length()
    {
        return length;
    }

    /**
     * Returns the type of an array.
     *
     * @return the type of an array
     */
    public LargeArrayType getType()
    {
        return type;
    }

    /**
     * Returns a value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract Object get(long i);

    /**
     * Returns a value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public Object get_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return get(i);
    }

    /**
     * Returns a value at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i index
     *
     * @return a value at index i. The type of returned value is the same as the
     *         type of this array.
     */
    public abstract Object getFromNative(long i);

    /**
     * Returns a boolean value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a boolean value at index i.
     */
    public abstract boolean getBoolean(long i);

    /**
     * Returns a boolean value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a boolean value at index i.
     */
    public boolean getBoolean_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getBoolean(i);
    }

    /**
     * Returns a signed byte value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract byte getByte(long i);

    /**
     * Returns a signed byte value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public byte getByte_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getByte(i);
    }

    /**
     * Returns an unsigned byte value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract short getUnsignedByte(long i);

    /**
     * Returns an unsigned byte value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public short getUnsignedByte_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getUnsignedByte(i);
    }

    /**
     * Returns a short value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract short getShort(long i);

    /**
     * Returns a short value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public short getShort_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getShort(i);
    }

    /**
     * Returns an int value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract int getInt(long i);

    /**
     * Returns an int value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public int getInt_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getInt(i);
    }

    /**
     * Returns a long value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract long getLong(long i);

    /**
     * Returns a long value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public long getLong_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getLong(i);
    }

    /**
     * Returns a float value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract float getFloat(long i);

    /**
     * Returns a float value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public float getFloat_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getFloat(i);
    }

    /**
     * Returns a double value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public abstract double getDouble(long i);

    /**
     * Returns a double value at index i. Array bounds are checked.
     *
     * @param i an index
     *
     * @return a value at index i.
     */
    public double getDouble_safe(final long i)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        return getDouble(i);
    }

    /**
     * Returns a reference to the internal data array. For constant arrays the length of the returned array is always equal to 1.
     * If isLarge() returns true, then this method returns null.
     *
     * @return reference to the internal data array or null
     */
    public abstract Object getData();

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns boolean data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract boolean[] getBooleanData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract boolean[] getBooleanData(boolean[] a,
                                             long startPos,
                                             long endPos,
                                             long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns byte data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract byte[] getByteData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract byte[] getByteData(byte[] a,
                                       long startPos,
                                       long endPos,
                                       long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns short data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract short[] getShortData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract short[] getShortData(short[] a,
                                         long startPos,
                                         long endPos,
                                         long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns int data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract int[] getIntData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract int[] getIntData(int[] a,
                                     long startPos,
                                     long endPos,
                                     long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns long data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract long[] getLongData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract long[] getLongData(long[] a,
                                       long startPos,
                                       long endPos,
                                       long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns float data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract float[] getFloatData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract float[] getFloatData(float[] a,
                                         long startPos,
                                         long endPos,
                                         long step);

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns double data. Otherwise, it returns null.
     *
     * @return an array containing the elements of this object or null
     */
    public abstract double[] getDoubleData();

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of this object. Otherwise, it returns
     * null. If (endPos - startPos) / step is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of this object or null
     */
    public abstract double[] getDoubleData(double[] a,
                                           long startPos,
                                           long endPos,
                                           long step);

    /**
     * Sets a value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public void set(final long i, final Object value)
    {
        if (value instanceof Boolean) {
            setBoolean(i, (Boolean) value);
        } else if (value instanceof Byte) {
            setByte(i, (Byte) value);
        } else if (value instanceof Short) {
            setShort(i, (Short) value);
        } else if (value instanceof Integer) {
            setInt(i, (Integer) value);
        } else if (value instanceof Long) {
            setLong(i, (Long) value);
        } else if (value instanceof Float) {
            setFloat(i, (Float) value);
        } else if (value instanceof Double) {
            setDouble(i, (Double) value);
        } else {
            throw new IllegalArgumentException("Unsupported type.");
        }
    }

    /**
     * Sets a value at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     *
     * @throws ClassCastException if the type of value argument is different
     *                            than the type of the array
     */
    public abstract void setToNative(long i, Object value);

    /**
     * Sets a value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void set_safe(final long i, final Object value)
    {
        if (value instanceof Boolean) {
            setBoolean_safe(i, (Boolean) value);
        } else if (value instanceof Byte) {
            setByte_safe(i, (Byte) value);
        } else if (value instanceof Short) {
            setShort_safe(i, (Short) value);
        } else if (value instanceof Integer) {
            setInt_safe(i, (Integer) value);
        } else if (value instanceof Long) {
            setLong_safe(i, (Long) value);
        } else if (value instanceof Float) {
            setFloat_safe(i, (Float) value);
        } else if (value instanceof Double) {
            setDouble_safe(i, (Double) value);
        } else {
            throw new IllegalArgumentException("Unsupported type.");
        }
    }

    /**
     * Sets a boolean value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setBoolean(long i, boolean value);

    /**
     * Sets a boolean value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setBoolean_safe(final long i, final boolean value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setBoolean(i, value);
    }

    /**
     * Sets a byte value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setByte(long i, byte value);

    /**
     * Sets a byte value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setByte_safe(final long i, final byte value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setByte(i, value);
    }

    /**
     * Sets an unsigned byte value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setUnsignedByte(long i, short value);

    /**
     * Sets an unsigned value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setUnsignedByte_safe(final long i, final byte value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setUnsignedByte(i, value);
    }

    /**
     * Sets a short value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setShort(long i, short value);

    /**
     * Sets a short value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setShort_safe(final long i, final short value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setShort(i, value);
    }

    /**
     * Sets an int value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setInt(long i, int value);

    /**
     * Sets an int value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setInt_safe(final long i, final int value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setInt(i, value);
    }

    /**
     * Sets a long value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setLong(long i, long value);

    /**
     * Sets a long value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setLong_safe(final long i, final long value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setLong(i, value);
    }

    /**
     * Sets a float value at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setFloat(long i, float value);

    /**
     * Sets a float value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setFloat_safe(final long i, final float value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setFloat(i, value);
    }

    /**
     * Sets a double value at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public abstract void setDouble(long i, double value);

    /**
     * Sets a double value at index i. Array bounds are checked.
     *
     * @param i     index
     * @param value value to set
     */
    public void setDouble_safe(final long i, final double value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        setDouble(i, value);
    }

    /**
     * Returns true if the off-heap memory is used to store array elements.
     *
     * @return true if the off-heap memory is used to store array elements,
     *         false otherwise.
     */
    public boolean isLarge()
    {
        return ptr != 0;
    }

    /**
     * Returns true if the type of the array is numeric, false otherwise.
     *
     * @return true if the type of the array is numeric, false otherwise.
     */
    public boolean isNumeric()
    {
        return type.isNumericType();
    }

    /**
     * Return true if the array is constant. Constant arrays cannot be modified,
     * i.e. all setters throw an IllegalAccessError exception.
     *
     * @return true if the arrays is constant, false otherwise
     */
    public boolean isConstant()
    {
        return isConstant;
    }

    /**
     * Sets the maximal size of a 32-bit array. For arrays of the size larger
     * than index, the data is stored in the memory allocated by
     * sun.misc.Unsafe.allocateMemory().
     *
     * @param index the maximal size of a 32-bit array.
     */
    public static void setMaxSizeOf32bitArray(final int index)
    {
        if (index < 0) {
            throw new IllegalArgumentException("index cannot be negative");
        }
        maxSizeOf32bitArray = index;
    }

    /**
     * Returns the maximal size of a 32-bit array.
     *
     * @return the maximal size of a 32-bit array.
     */
    public static int getMaxSizeOf32bitArray()
    {
        return maxSizeOf32bitArray;
    }

    public LargeArray clone()
    {
        if (isConstant) {
            return LargeArrayUtils.createConstant(type, length, get(0));
        } else {
            LargeArray v = LargeArrayUtils.create(type, length, false);
            LargeArrayUtils.arraycopy(this, 0, v, 0, length);
            return v;
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The {@code equals} method implements "deep comparison": all properties are compared and all corresponding elements in both arrays are compared.
     *
     * @param o the reference object with which to compare.
     *
     * @return {@code true} if this object is the same as the o
     *         argument; {@code false} otherwise.
     *
     * @see #hashCode()
     * @see java.util.HashMap
     */
    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode()
    {
        return hashCode(1f);
    }

    /**
     * Generates an approximate hash code using a quality argument. For quality equal to 1 this method is equivalent to hashCode().
     *
     * @param quality a number between 0 and 1.
     *
     * @return an approximate hash code
     */
    public int hashCode(float quality)
    {
        if (quality < 0 || quality > 1) {
            throw new IllegalArgumentException("The quality argument should be between 0 and 1");
        }
        int fprint = 7;
        fprint = 29 * fprint + (this.type != null ? this.type.hashCode() : 0);
        fprint = 29 * fprint + (int) (this.length ^ (this.length >>> 32));
        fprint = 29 * fprint + (this.isConstant ? 1 : 0);
        fprint = 29 * fprint + (this.parent != null ? this.parent.hashCode() : 0);
        return fprint;
    }

    /**
     * Memory deallocator.
     */
    protected static class Deallocator implements Runnable
    {

        private long ptr;
        private final long length;
        private final long sizeof;

        /**
         * Creates new instance of Decallocator.
         *
         * @param ptr    native pointer
         * @param length number of array elements
         * @param sizeof size of each element
         */
        public Deallocator(final long ptr,
                           final long length,
                           final long sizeof)
        {
            this.ptr = ptr;
            this.length = length;
            this.sizeof = sizeof;
        }

        @Override
        public void run()
        {
            if (ptr != 0) {
                LargeArrayUtils.UNSAFE.freeMemory(ptr);
                ptr = 0;
                MemoryCounter.decreaseCounter(length * sizeof);
            }
        }
    }

    /**
     * Initializes allocated native memory.
     *
     * @param length    the length of native memory block
     * @param initValue initialization value
     */
    protected final void initializeNativeMemory(final long length, final Number initValue)
    {
        final long sizeof = type.sizeOf();
        if (ptr != 0) {
            final int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads <= 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                switch (type) {
                    case LOGIC:
                    case BYTE:
                    case UNSIGNED_BYTE:
                    case STRING:
                    case OBJECT: {
                        byte value = initValue.byteValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putByte(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    case SHORT: {
                        short value = initValue.shortValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putShort(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    case INT: {
                        int value = initValue.intValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putInt(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    case LONG: {
                        long value = initValue.longValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putLong(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    case FLOAT: {
                        float value = initValue.floatValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putFloat(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    case DOUBLE: {
                        double value = initValue.doubleValue();
                        for (long k = 0; k < length; k++) {
                            LargeArrayUtils.UNSAFE.putDouble(ptr + sizeof * k, value);
                        }
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Invalid array type.");
                }
            } else {
                final long k = length / nthreads;
                final Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = j * k;
                    final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                    threads[j] = ConcurrencyUtils.submit(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            switch (type) {
                                case LOGIC:
                                case BYTE:
                                case UNSIGNED_BYTE:
                                case STRING:
                                case OBJECT: {
                                    byte value = initValue.byteValue();
                                    for (long k = firstIdx; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putByte(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                case SHORT: {
                                    short value = initValue.shortValue();
                                    for (long k = firstIdx; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putShort(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                case INT: {
                                    int value = initValue.intValue();
                                    for (long k = firstIdx; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putInt(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                case LONG: {
                                    long value = initValue.longValue();
                                    for (long k = 0; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putLong(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                case FLOAT: {
                                    float value = initValue.floatValue();
                                    for (long k = firstIdx; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putFloat(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                case DOUBLE: {
                                    double value = initValue.doubleValue();
                                    for (long k = firstIdx; k < lastIdx; k++) {
                                        LargeArrayUtils.UNSAFE.putDouble(ptr + sizeof * k, value);
                                    }
                                    break;
                                }
                                default:
                                    throw new IllegalArgumentException("Invalid array type.");
                            }
                        }
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    /**
     * Initializes the state of the object for its particular class in the event that the serialization stream does not list the given class as a superclass of
     * the object being deserialized. Here it always throws InvalidObjectException.
     *
     * @throws ObjectStreamException this method always throws InvalidObjectException
     */
    private void readObjectNoData() throws ObjectStreamException
    {
        throw new InvalidObjectException("Stream data required");
    }
}
