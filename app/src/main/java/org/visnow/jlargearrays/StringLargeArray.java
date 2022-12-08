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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import static org.visnow.jlargearrays.LargeArray.getMaxSizeOf32bitArray;

/**
 *
 * An array of strings that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class StringLargeArray extends LargeArray
{

    private static final long serialVersionUID = -4425856472553793027L;

    /**
     * Stores elements of this array when LargeArray.isLarge() == false.
     */
    private String[] data;

    /**
     * Stores the length of each string element when LargeArray.isLarge() == true.
     */
    private ShortLargeArray stringLengths;
    
    /**
     * Maximal length of an element. Used only when LargeArray.isLarge() == true.
     */
    private int maxStringLength;
    
    /**
     * Amount of allocated native memory in bytes.
     */
    private long size;
    
    private static final String CHARSET = "UTF-8";
    private static final int CHARSET_SIZE = 4; //UTF-8 uses between 1 and 4 bytes to encode a single character 

    /**
     * Creates new instance of this class. The maximal string length is set to 100.
     *
     * @param length number of elements
     */
    public StringLargeArray(long length)
    {
        this(length, LargeArray.DEFAULT_MAX_STRING_LENGTH);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length          number of elements
     * @param maxStringLength maximal length of the string, it is ignored when number of elements is smaller than LargeArray.getMaxSizeOf32bitArray().
     */
    public StringLargeArray(long length, int maxStringLength)
    {
        this(length, maxStringLength, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param maxStringLength  maximal length of the string, it is ignored when number of elements is smaller than LargeArray.getMaxSizeOf32bitArray().
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public StringLargeArray(long length, int maxStringLength, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.STRING;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value.");
        }
        if (maxStringLength <= 0) {
            throw new IllegalArgumentException(maxStringLength + " is not a positive int value.");
        }
        this.length = length;
        this.size = length * (long) maxStringLength * (long) CHARSET_SIZE;
        this.maxStringLength = maxStringLength;
        allocateMemory(zeroNativeMemory, null, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length          number of elements
     * @param maxStringLength maximal length of the string, it is ignored when number of elements is smaller than LargeArray.getMaxSizeOf32bitArray().
     * @param initValue       initialization value
     */
    public StringLargeArray(long length, int maxStringLength, String initValue)
    {
        this(length, maxStringLength, initValue, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length          number of elements
     * @param maxStringLength maximal length of the string, it is ignored when number of elements is smaller than LargeArray.getMaxSizeOf32bitArray().
     * @param initValue       initialization value
     * @param createConstant  if true, then a constant array is created
     */
    public StringLargeArray(long length, int maxStringLength, String initValue, boolean createConstant)
    {
        this.type = LargeArrayType.STRING;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
        this.size = length * (long) maxStringLength * (long) CHARSET_SIZE;
        this.maxStringLength = maxStringLength;
        allocateMemory(true, initValue, createConstant);
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is used internally.
     */
    public StringLargeArray(String[] data)
    {
        this.type = LargeArrayType.STRING;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public StringLargeArray clone()
    {
        if (isConstant) {
            return new StringLargeArray(length, maxStringLength, get(0), true);
        } else {
            StringLargeArray v = new StringLargeArray(length, FastMath.max(1, maxStringLength), false);
            LargeArrayUtils.arraycopy(this, 0, v, 0, length);
            return v;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof StringLargeArray))
            return false;
        StringLargeArray la = (StringLargeArray) o;
        boolean equal = this.type == la.type && this.length == la.length && this.maxStringLength == la.maxStringLength;
        if (equal == false) return false;
        if (this.parent != null && la.parent != null) {
            if (!this.parent.equals(la.parent)) return false;
        } else if (this.parent != la.parent) {
            return false;
        }
        for (long i = 0; i < this.length; i++) {
            String e1 = this.get(i);
            String e2 = la.get(i);
            if (!((e1 == null && e2 == null) || (e1 != null && e1.equals(e2)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(float quality)
    {
        int fprint = 29 * super.hashCode(quality);
        fprint = 29 * fprint + (int) (this.maxStringLength ^ (this.maxStringLength >>> 16));
        if (quality > 0) {
            fprint = 29 * fprint + (this.stringLengths != null ? this.stringLengths.hashCode(quality) : 0);
            long step = (long) FastMath.ceil((1 - length) * quality + length);
            for (long i = 0; i < length; i += step) {
                String element = get(i);
                fprint = 31 * fprint + (element == null ? 0 : (element.isEmpty() ? 1 : element.hashCode()));
            }
        }
        return fprint;
    }

    @Override
    public final String get(long i)
    {
        if (ptr != 0) {
            short strLen = stringLengths.getShort(i);
            if (strLen < 0) {
                return null;
            } else if (strLen == 0) {
                return "";
            }
            long offset = this.type.sizeOf() * i * maxStringLength * CHARSET_SIZE;
            byte[] byteArray = new byte[maxStringLength * CHARSET_SIZE];
            for (int j = 0; j < strLen; j++) {
                byteArray[j] = LargeArrayUtils.UNSAFE.getByte(ptr + offset + this.type.sizeOf() * j);
            }
            try {
                return new String(byteArray, 0, strLen, CHARSET);
            } catch (UnsupportedEncodingException ex) {
                return null;
            }
        } else if (isConstant) {
            return data[0];
        } else {
            return data[(int) i];
        }
    }

    @Override
    public final String getFromNative(long i)
    {
        short strLen = stringLengths.getShort(i);
        if (strLen < 0) return null;
        long offset = this.type.sizeOf() * i * maxStringLength * CHARSET_SIZE;
        byte[] byteArray = new byte[maxStringLength * CHARSET_SIZE];
        for (int j = 0; j < strLen; j++) {
            byteArray[j] = LargeArrayUtils.UNSAFE.getByte(ptr + offset + this.type.sizeOf() * j);
        }
        try {
            return new String(byteArray, 0, strLen, CHARSET);
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    @Override
    public final boolean getBoolean(long i)
    {
        String s = get(i);
        return s != null ? s.length() != 0 : false;
    }

    @Override
    public final byte getByte(long i)
    {
        String s = get(i);
        return (byte) (s != null ? s.length() : 0);
    }

    @Override
    public final short getUnsignedByte(long i)
    {
        String s = get(i);
        return (short) (s != null ? 0xFF & s.length() : 0);
    }

    @Override
    public final short getShort(long i)
    {
        String s = get(i);
        return (short) (s != null ? s.length() : 0);
    }

    @Override
    public final int getInt(long i)
    {
        String s = get(i);
        return (int) (s != null ? s.length() : 0);
    }

    @Override
    public final long getLong(long i)
    {
        String s = get(i);
        return (long) (s != null ? s.length() : 0);
    }

    @Override
    public final float getFloat(long i)
    {
        String s = get(i);
        return (float) (s != null ? s.length() : 0);

    }

    @Override
    public final double getDouble(long i)
    {
        String s = get(i);
        return (double) (s != null ? s.length() : 0);
    }

    @Override
    public final String[] getData()
    {
        return data;
    }

    @Override
    public final boolean[] getBooleanData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        boolean[] out = new boolean[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                short strLen = stringLengths.getShort(i);
                out[i] = strLen != 0;
            }
        } else if (isConstant) {
            boolean elem = data[0] != null ? data[0].length() != 0 : false;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != null ? data[i].length() != 0 : false;

            }
        }
        return out;
    }

    @Override
    public final boolean[] getBooleanData(boolean[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            boolean[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new boolean[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    short strLen = stringLengths.getShort(i);
                    out[idx++] = strLen > 0;
                }
            } else if (isConstant) {
                boolean elem = data[0] != null ? data[0].length() != 0 : false;
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = elem;
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    int v = data[(int) i] != null ? data[(int) i].length() : 0;
                    out[idx++] = v != 0;
                }
            }
            return out;
        }
    }

    @Override
    public final byte[] getByteData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        byte[] out = new byte[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = (byte) stringLengths.getShort(i);
            }
        } else if (isConstant) {
            byte elem = (byte) (data[0] != null ? data[0].length() : 0);
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (byte) (data[i] != null ? data[i].length() : 0);

            }
        }
        return out;
    }

    @Override
    public final byte[] getByteData(byte[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            byte[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new byte[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) (data[0] != null ? data[0].length() : 0);
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final short[] getShortData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        short[] out = new short[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = stringLengths.getShort(i);
            }
        } else if (isConstant) {
            short elem = (short) (data[0] != null ? data[0].length() : 0);
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (short) (data[i] != null ? data[i].length() : 0);

            }
        }
        return out;
    }

    @Override
    public final short[] getShortData(short[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            short[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new short[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) (data[0] != null ? data[0].length() : 0);
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final int[] getIntData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        int[] out = new int[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = stringLengths.getShort(i);
            }
        } else if (isConstant) {
            int elem = data[0] != null ? data[0].length() : 0;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != null ? data[i].length() : 0;

            }
        }
        return out;
    }

    @Override
    public final int[] getIntData(int[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            int[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new int[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) (data[0] != null ? data[0].length() : 0);
                }
            } else {

                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final long[] getLongData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        long[] out = new long[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = stringLengths.getShort(i);
            }
        } else if (isConstant) {
            int elem = data[0] != null ? data[0].length() : 0;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != null ? data[i].length() : 0;

            }
        }
        return out;
    }

    @Override
    public final long[] getLongData(long[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            long[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new long[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) (data[0] != null ? data[0].length() : 0);
                }
            } else {

                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final float[] getFloatData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        float[] out = new float[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = stringLengths.getShort(i);
            }
        } else if (isConstant) {
            int elem = data[0] != null ? data[0].length() : 0;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != null ? data[i].length() : 0;

            }
        }
        return out;
    }

    @Override
    public final float[] getFloatData(float[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
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
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (float) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = data[0] != null ? data[0].length() : 0;
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (float) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final double[] getDoubleData()
    {
        if (length > LargeArray.LARGEST_SUBARRAY) return null;
        double[] out = new double[(int) length];
        if (ptr != 0) {
            for (int i = 0; i < length; i++) {
                out[i] = stringLengths.getShort(i);
            }
        } else if (isConstant) {
            int elem = data[0] != null ? data[0].length() : 0;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != null ? data[i].length() : 0;

            }
        }
        return out;
    }

    @Override
    public final double[] getDoubleData(double[] a, long startPos, long endPos, long step)
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

        long len = (long) FastMath.ceil((endPos - startPos) / (double) step);
        if (len > LargeArray.LARGEST_SUBARRAY) {
            return null;
        } else {
            double[] out;
            if (a != null && a.length >= len) {
                out = a;
            } else {
                out = new double[(int) len];
            }
            int idx = 0;
            if (ptr != 0) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (double) stringLengths.getShort(i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = data[0] != null ? data[0].length() : 0;
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (double) (data[(int) i] != null ? data[(int) i].length() : 0);
                }
            }
            return out;
        }
    }

    @Override
    public final void setToNative(long i, Object o)
    {
        if (o == null) {
            stringLengths.setShort(i, (short) -1);
        } else {
            if (!(o instanceof String)) {
                throw new IllegalArgumentException(o + " is not a string.");
            }
            String s = (String) o;
            if (s.length() > maxStringLength) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            byte[] tmp;
            try {
                tmp = s.getBytes(CHARSET);
            } catch (UnsupportedEncodingException ex) {
                return;
            }
            int strLen = tmp.length;
            if (strLen > Short.MAX_VALUE) {
                throw new IllegalArgumentException("String  " + s + " is too long.");
            }
            stringLengths.setShort(i, (short) strLen);
            long offset = this.type.sizeOf() * i * maxStringLength * CHARSET_SIZE;
            for (int j = 0; j < strLen; j++) {
                LargeArrayUtils.UNSAFE.putByte(ptr + offset + this.type.sizeOf() * j, tmp[j]);
            }
        }
    }

    @Override
    public final void set(long i, Object o)
    {
        if (o == null) {
            if (ptr != 0) {
                stringLengths.setShort(i, (short) -1);
            } else {
                if (isConstant) {
                    throw new IllegalAccessError("Constant arrays cannot be modified.");
                }
                data[(int) i] = null;
            }
        } else {
            if (!(o instanceof String)) {
                throw new IllegalArgumentException(o + " is not a string.");
            }
            String s = (String) o;
            if (ptr != 0) {
                if (s.length() > maxStringLength) {
                    throw new IllegalArgumentException("String  " + s + " is too long.");
                }
                byte[] tmp;
                try {
                    tmp = s.getBytes(CHARSET);
                } catch (UnsupportedEncodingException ex) {
                    return;
                }
                int strLen = tmp.length;
                if (strLen > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("String  " + s + " is too long.");
                }
                stringLengths.setShort(i, (short) strLen);
                long offset = this.type.sizeOf() * i * maxStringLength * CHARSET_SIZE;
                for (int j = 0; j < strLen; j++) {
                    LargeArrayUtils.UNSAFE.putByte(ptr + offset + this.type.sizeOf() * j, tmp[j]);
                }
            } else {
                if (isConstant) {
                    allocateMemory(true, get(0), false);
                    isConstant = false;
                    set(i, s);
                } else {
                    data[(int) i] = s;
                }
            }
        }
    }

    @Override
    public final void set_safe(long i, Object o)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        set(i, o);
    }

    @Override
    public final void setBoolean(long i, boolean value)
    {
        set(i, Boolean.toString(value));
    }

    @Override
    public final void setByte(long i, byte value)
    {
        set(i, Byte.toString(value));
    }

    @Override
    public final void setUnsignedByte(long i, short value)
    {
        setShort(i, value);
    }

    @Override
    public final void setShort(long i, short value)
    {
        set(i, Short.toString(value));
    }

    @Override
    public final void setInt(long i, int value)
    {
        set(i, Integer.toString(value));
    }

    @Override
    public final void setLong(long i, long value)
    {
        set(i, Long.toString(value));
    }

    @Override
    public final void setFloat(long i, float value)
    {
        set(i, Float.toString(value));
    }

    @Override
    public final void setDouble(long i, double value)
    {
        set(i, Double.toString(value));
    }

    /**
     * Returns maximal length of each element.
     *
     * @return maximal length of each element
     *
     */
    public int getMaxStringLength()
    {
        return maxStringLength;
    }

    /**
     * Method used in serialization.
     *
     * @param out output stream
     *
     * @throws IOException any exception thrown by the output stream
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
        if (data == null) {
            for (long i = 0; i < length; i++) {
                String elem = get(i);
                if (elem == null) {
                    out.writeObject(null);
                } else {
                    out.writeUTF(get(i));
                }
            }
        }
    }

    /**
     * Method used in deserialization.
     *
     * @param in input stream
     *
     * @throws IOException            any exception thrown by the input stream
     * @throws ClassNotFoundException class of a serialized object cannot be found
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if (type != LargeArrayType.STRING) {
            throw new IllegalArgumentException("Invalid array type");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Non-positive array length");
        }
        if (CHARSET_SIZE != 4) {
            throw new IllegalArgumentException("CHARSET_SIZE != 4");
        }
        if (CHARSET == null || !CHARSET.equals("UTF-8")) {
            throw new IllegalArgumentException("CHARSET == null || !CHARSET.equals(UTF-8)");
        }

        if (data == null) {
            if (size <= 0) {
                throw new IllegalArgumentException("Non-positive size");
            }
            if (maxStringLength <= 0) {
                throw new IllegalArgumentException(maxStringLength + " is not a positive int value.");
            }
            if (stringLengths == null || stringLengths.length() != length) {
                throw new IllegalArgumentException("stringLengths == null || stringLengths.length() != length");
            }
            ptr = LargeArrayUtils.UNSAFE.allocateMemory(size * type.sizeOf());
            LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, size, type.sizeOf()));
            MemoryCounter.increaseCounter(size * type.sizeOf());
            for (long i = 0; i < length; i++) {
                if (stringLengths.getShort(i) > 0) {
                    set(i, in.readUTF());
                } else {
                    in.readObject();
                }
            }
        }
    }

    private void allocateMemory(boolean initializeMemory, String initValue, boolean createConstant)
    {
        if (createConstant) {
            isConstant = true;
            data = new String[]{initValue};
        } else {
            if (length > getMaxSizeOf32bitArray()) {
                ptr = LargeArrayUtils.UNSAFE.allocateMemory(size * type.sizeOf());
                LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, size, type.sizeOf()));
                MemoryCounter.increaseCounter(size * type.sizeOf());
                stringLengths = new ShortLargeArray(length);
                for (long i = 0; i < length; i++) {
                    stringLengths.setShort(i, (short) -1);
                }
                if (initializeMemory) {
                    if (initValue != null) {
                        for (long i = 0; i < length; i++) {
                            set(i, initValue);
                        }
                    } else {
                        initializeNativeMemory(size, 0);
                    }
                }
            } else {
                data = new String[(int) length];
                Arrays.fill(data, initValue);
            }
        }
    }

}
