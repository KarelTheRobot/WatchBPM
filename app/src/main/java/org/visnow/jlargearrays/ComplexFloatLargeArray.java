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

import org.apache.commons.math3.util.FastMath;

/**
 *
 * An array of complex numbers (double precision) that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class ComplexFloatLargeArray extends LargeArray
{

    private static final long serialVersionUID = 7475425228321409910L;

    /**
     * Stores elements of real part.
     */
    private FloatLargeArray dataRe;

    /**
     * Stores elements of imaginary part.
     */
    private FloatLargeArray dataIm;

    /**
     * Creates new instance of this class.
     *
     * @param length number of elements
     */
    public ComplexFloatLargeArray(long length)
    {
        this(length, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public ComplexFloatLargeArray(long length, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.COMPLEX_FLOAT;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
        dataRe = new FloatLargeArray(length, zeroNativeMemory);
        dataIm = new FloatLargeArray(length, zeroNativeMemory);
    }

    /**
     * Creates new instance of this class
     *
     * @param length    number of elements
     * @param initValue initialization value
     */
    public ComplexFloatLargeArray(long length, float[] initValue)
    {
        this(length, initValue, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length         number of elements
     * @param initValue      initialization value
     * @param createConstant if true, then a constant array is created
     */
    public ComplexFloatLargeArray(long length, float[] initValue, boolean createConstant)
    {
        this.type = LargeArrayType.COMPLEX_FLOAT;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        if (initValue == null || initValue.length != 2) {
            throw new IllegalArgumentException("initValue == null || initValue.length != 2");
        }
        this.length = length;
        this.isConstant = createConstant;
        this.dataRe = new FloatLargeArray(length, initValue[0], createConstant);
        this.dataIm = new FloatLargeArray(length, initValue[1], createConstant);
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is not used internally.
     */
    public ComplexFloatLargeArray(float[] data)
    {
        this(new FloatLargeArray(data));
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is not used internally.
     */
    public ComplexFloatLargeArray(FloatLargeArray data)
    {
        if (data.length() % 2 != 0) {
            throw new IllegalArgumentException("The length of the data array must be even.");
        }
        if (data.length() < 0) {
            throw new IllegalArgumentException(data.length() + " is not a nonnegative long value");
        }
        this.type = LargeArrayType.COMPLEX_FLOAT;
        this.length = data.length / 2;
        this.isConstant = data.isConstant;
        if (this.isConstant) {
            this.dataRe = new FloatLargeArray(length, data.getFloat(0), true);
            this.dataIm = new FloatLargeArray(length, data.getFloat(1), true);
        } else {
            dataRe = new FloatLargeArray(length, false);
            dataIm = new FloatLargeArray(length, false);
            for (long i = 0; i < this.length; i++) {
                dataRe.setFloat(i, data.getFloat(2 * i));
                dataIm.setFloat(i, data.getFloat(2 * i + 1));
            }
        }
    }

    /**
     * Creates new instance of this class.
     *
     * @param dataRe real part, this reference is used internally.
     * @param dataIm imaginary part, this reference not used internally.
     */
    public ComplexFloatLargeArray(float[] dataRe, float[] dataIm)
    {
        this(new FloatLargeArray(dataRe), new FloatLargeArray(dataIm));
    }

    /**
     * Creates new instance of this class.
     *
     * @param dataRe real part, this reference is used internally.
     * @param dataIm imaginary part, this reference is used internally.
     */
    public ComplexFloatLargeArray(FloatLargeArray dataRe, FloatLargeArray dataIm)
    {
        if (dataRe.length() != dataIm.length()) {
            throw new IllegalArgumentException("The length of the dataRe must be equal to the length of dataIm.");
        }
        if (dataRe.length() < 0) {
            throw new IllegalArgumentException(dataRe.length() + " is not a nonnegative long value");
        }
        this.type = LargeArrayType.COMPLEX_FLOAT;
        this.length = dataRe.length();
        this.dataRe = dataRe;
        this.dataIm = dataIm;
    }

    @Override
    public ComplexFloatLargeArray clone()
    {
        return (ComplexFloatLargeArray) super.clone();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof ComplexFloatLargeArray))
            return false;
        ComplexFloatLargeArray la = (ComplexFloatLargeArray) o;
        boolean equal = this.type == la.type && this.length == la.length;
        if (equal == false) return false;
        if (this.parent != null && la.parent != null) {
            if (!this.parent.equals(la.parent)) return false;
        } else if (this.parent != la.parent) {
            return false;
        }
        for (long i = 0; i < this.length; i++) {
            float[] elem1 = this.get(i);
            float[] elem2 = la.get(i);
            if (Float.compare(elem1[0], elem2[0]) != 0) return false;
            if (Float.compare(elem1[1], elem2[1]) != 0) return false;
        }
        return true;
    }

    @Override
    public int hashCode(float quality)
    {
        int fprint = 29 * super.hashCode(quality);
        if (quality > 0) {
            long step = (long) FastMath.ceil((1 - length) * quality + length);
            for (long i = 0; i < length; i += step) {
                float[] element = get(i);
                fprint = 31 * fprint + Float.floatToIntBits(element[0]) + Float.floatToIntBits(element[1]);
            }
        }
        return fprint;
    }

    @Override
    public boolean isLarge()
    {
        return dataRe.isLarge() || dataIm.isLarge();
    }

    @Override
    public boolean isConstant()
    {
        return dataRe.isConstant() && dataIm.isConstant();
    }

    /**
     * Returns the real part of this array.
     *
     * @return reference to the real part.
     */
    public final FloatLargeArray getRealArray()
    {
        return dataRe;
    }

    /**
     * Returns the imaginary part of this array.
     *
     * @return reference to the imaginary part.
     */
    public final FloatLargeArray getImaginaryArray()
    {
        return dataIm;
    }

    /**
     * Returns the absolute value of this array.
     *
     * @return the absolute value.
     */
    public final FloatLargeArray getAbsArray()
    {
        FloatLargeArray out = new FloatLargeArray(length, false);
        for (long i = 0; i < length; i++) {
            double re = dataRe.getFloat(i);
            double im = dataIm.getFloat(i);
            out.setFloat(i, (float) FastMath.sqrt(re * re + im * im));
        }
        return out;
    }

    /**
     * Returns the argument of this array.
     *
     * @return the argument
     */
    public final FloatLargeArray getArgArray()
    {
        FloatLargeArray out = new FloatLargeArray(length, false);
        for (long i = 0; i < length; i++) {
            double re = dataRe.getFloat(i);
            double im = dataIm.getFloat(i);
            out.setFloat(i, (float) FastMath.atan2(im, re));
        }
        return out;
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    @Override
    public final float[] get(long i)
    {
        return getComplexFloat(i);
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    @Override
    public final float[] getFromNative(long i)
    {
        return new float[]{dataRe.getFromNative(i), dataIm.getFromNative(i)};
    }

    /**
     * Returns a boolean value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final boolean getBoolean(long i)
    {
        return dataRe.getBoolean(i);
    }

    /**
     * Returns a signed byte value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final byte getByte(long i)
    {
        return dataRe.getByte(i);
    }

    /**
     * Returns an unsigned byte value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final short getUnsignedByte(long i)
    {
        return dataRe.getUnsignedByte(i);
    }

    /**
     * Returns a short value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final short getShort(long i)
    {
        return dataRe.getShort(i);
    }

    /**
     * Returns an int value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final int getInt(long i)
    {
        return dataRe.getInt(i);
    }

    /**
     * Returns a long value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final long getLong(long i)
    {
        return dataRe.getLong(i);
    }

    /**
     * Returns a float value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final float getFloat(long i)
    {
        return dataRe.getFloat(i);
    }

    /**
     * Returns a double value that corresponds to the real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value that corresponds to the real part at index i.
     */
    @Override
    public final double getDouble(long i)
    {
        return dataRe.getDouble(i);
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    public final float[] getComplexFloat(long i)
    {
        return new float[]{dataRe.getFloat(i), dataIm.getFloat(i)};
    }

    /**
     * Returns a complex value ({re, im}) at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i an index
     *
     * @return a value at index i ({re, im}).
     */
    public final double[] getComplexDouble(long i)
    {
        return new double[]{dataRe.getDouble(i), dataIm.getDouble(i)};
    }

    /**
     * Returns a reference to the internal data arrays in the form of result[2][length],
     * where result[0] holds the reference to the real part and result[1] holds the reference to the imaginary part.
     * If isLarge() returns true, then this method returns null.
     *
     * @return reference to the internal data arrays or null
     */
    @Override
    public final float[][] getData()
    {
        if (isLarge()) {
            return null;
        } else {
            return new float[][]{dataRe.getData(), dataIm.getData()};
        }
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns boolean data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final boolean[] getBooleanData()
    {
        return dataRe.getBooleanData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final boolean[] getBooleanData(boolean[] a, long startPos, long endPos, long step)
    {
        return dataRe.getBooleanData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns byte data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final byte[] getByteData()
    {
        return dataRe.getByteData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final byte[] getByteData(byte[] a, long startPos, long endPos, long step)
    {
        return dataRe.getByteData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns short data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final short[] getShortData()
    {
        return dataRe.getShortData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final short[] getShortData(short[] a, long startPos, long endPos, long step)
    {
        return dataRe.getShortData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns int data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final int[] getIntData()
    {
        return dataRe.getIntData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final int[] getIntData(int[] a, long startPos, long endPos, long step)
    {
        return dataRe.getIntData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns long data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final long[] getLongData()
    {
        return dataRe.getLongData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final long[] getLongData(long[] a, long startPos, long endPos, long step)
    {
        return dataRe.getLongData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns float data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final float[] getFloatData()
    {
        return dataRe.getFloatData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final float[] getFloatData(float[] a, long startPos, long endPos, long step)
    {
        return dataRe.getFloatData(a, startPos, endPos, step);
    }

    /**
     * If the size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns double data that correspond to the real part of this object. Otherwise, it returns null.
     *
     * @return an array containing the elements of the real part of this object or null
     */
    @Override
    public final double[] getDoubleData()
    {
        return dataRe.getDoubleData();
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of the real part of this object. Otherwise, it returns
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
     * @return an array containing the elements of the real part this object or null
     */
    @Override
    public final double[] getDoubleData(double[] a, long startPos, long endPos, long step)
    {
        return dataRe.getDoubleData(a, startPos, endPos, step);
    }

    /**
     * If 2 * size of the array is smaller than LargeArray.LARGEST_SUBARRAY, then this
     * method returns complex data in the interleaved layout. Otherwise, it returns null.
     *
     * @return an array containing the elements of the list or null
     */
    public final float[] getComplexData()
    {
        if (2 * length > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            float[] out = new float[(int) (2 * length)];
            for (int i = 0; i < length; i++) {
                out[2 * i] = dataRe.getFloat(i);
                out[2 * i + 1] = dataIm.getFloat(i);
            }
            return out;
        }
    }

    /**
     * If (endPos - startPos) / step is smaller than LargeArray.LARGEST_SUBARRAY, then
     * this method returns selected elements of an array. Otherwise, it returns
     * null. If 2 * ((endPos - startPos) / step) is smaller or equal to a.length, it
     * is returned therein. Otherwise, a new array is allocated and returned.
     * Array bounds are checked.
     *
     * @param a        the array into which the elements are to be stored, if it is big
     *                 enough; otherwise, a new array of is allocated for this purpose.
     * @param startPos starting position (included)
     * @param endPos   ending position (excluded)
     * @param step     step size
     *
     * @return an array containing the elements of the list or null
     */
    public final float[] getComplexData(float[] a, long startPos, long endPos, long step)
    {
        if (startPos < 0 || startPos >= length) {
            throw new ArrayIndexOutOfBoundsException("startPos < 0 || startPos >= length");
        }
        if (endPos < 0 || endPos > length || endPos < startPos) {
            throw new ArrayIndexOutOfBoundsException("endPos < 0 || endPos > length || endPos < startPos");
        }
        if (step < 1) {
            throw new IllegalArgumentException("step < 1");
        }

        long len = 2 * (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            float[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new float[(int) len];
            }
            int idx = 0;
            for (long i = startPos; i < endPos; i += step) {
                out[idx++] = dataRe.getFloat(i);
                out[idx++] = dataIm.getFloat(i);
            }

            return out;
        }
    }

    /**
     * Sets a complex value at index i. Array bounds are not checked. If isLarge()
     * returns false for a given array or the index argument is invalid, then
     * calling this method will cause JVM crash.
     *
     * @param i     index
     * @param value value to set, must be float[] of length 2
     *
     * @throws ClassCastException if the type of value argument is different
     *                            than the type of the array
     */
    @Override
    public final void setToNative(long i, Object value)
    {
        if (!(value instanceof float[])) {
            throw new IllegalArgumentException(value + " is not an array of floats.");
        }
        dataRe.setToNative(i, ((float[]) value)[0]);
        dataIm.setToNative(i, ((float[]) value)[1]);
    }

    /**
     * Sets a boolean value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setBoolean(long i, boolean value)
    {
        dataRe.setBoolean(i, value);
        dataIm.setBoolean(i, false);
    }

    /**
     * Sets a signed byte value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setByte(long i, byte value)
    {
        dataRe.setByte(i, value);
        dataIm.setByte(i, (byte) 0);
    }

    /**
     * Sets an unsigned byte value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setUnsignedByte(long i, short value)
    {
        dataRe.setUnsignedByte(i, value);
        dataIm.setUnsignedByte(i, (byte) 0);
    }

    /**
     * Sets a short value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setShort(long i, short value)
    {
        dataRe.setShort(i, value);
        dataIm.setShort(i, (short) 0);
    }

    /**
     * Sets an int value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setInt(long i, int value)
    {
        dataRe.setInt(i, value);
        dataIm.setInt(i, 0);
    }

    /**
     * Sets a long value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setLong(long i, long value)
    {
        dataRe.setLong(i, value);
        dataIm.setLong(i, 0);
    }

    /**
     * Sets a float value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setFloat(long i, float value)
    {
        dataRe.setFloat(i, value);
        dataIm.setFloat(i, 0);
    }

    /**
     * Sets a double value as a real part at index i. Array bounds are not checked. Calling
     * this method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set as a real part
     */
    @Override
    public final void setDouble(long i, double value)
    {
        dataRe.setDouble(i, value);
        dataIm.setDouble(i, 0);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set, must be float[] of length 2
     */
    @Override
    public final void set(long i, Object value)
    {
        if (!(value instanceof float[])) {
            throw new IllegalArgumentException(value + " is not an array of floats.");
        }
        setComplexFloat(i, (float[]) value);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public final void setComplexFloat(long i, float[] value)
    {
        dataRe.setFloat(i, value[0]);
        dataIm.setFloat(i, value[1]);
    }

    /**
     * Sets a complex value ({re, im}) at index i. Array bounds are not checked. Calling this
     * method with invalid index argument will cause JVM crash.
     *
     * @param i     index
     * @param value value to set
     */
    public final void setComplexDouble(long i, double[] value)
    {
        dataRe.setDouble(i, value[0]);
        dataIm.setDouble(i, value[1]);
    }

}
