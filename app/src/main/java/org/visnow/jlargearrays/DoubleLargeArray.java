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
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import static org.visnow.jlargearrays.LargeArray.getMaxSizeOf32bitArray;

/**
 *
 * An array of doubles that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class DoubleLargeArray extends LargeArray
{

    private static final long serialVersionUID = 7436383149749497101L;

    /**
     * Stores elements of this array when LargeArray.isLarge() == false.
     */
    private double[] data;

    /**
     * Creates new instance of this class.
     *
     * @param length number of elements
     */
    public DoubleLargeArray(long length)
    {
        this(length, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public DoubleLargeArray(long length, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.DOUBLE;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
        allocateMemory(zeroNativeMemory, 0.0, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length    number of elements
     * @param initValue initialization value
     */
    public DoubleLargeArray(long length, double initValue)
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
    public DoubleLargeArray(long length, double initValue, boolean createConstant)
    {
        this.type = LargeArrayType.DOUBLE;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
        allocateMemory(true, initValue, createConstant);
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is used internally.
     */
    public DoubleLargeArray(double[] data)
    {
        this.type = LargeArrayType.DOUBLE;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public DoubleLargeArray clone()
    {
        return (DoubleLargeArray) super.clone();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof DoubleLargeArray))
            return false;
        DoubleLargeArray la = (DoubleLargeArray) o;
        boolean equal = this.type == la.type && this.length == la.length;
        if (equal == false) return false;
        if (this.parent != null && la.parent != null) {
            if (!this.parent.equals(la.parent)) return false;
        } else if (this.parent != la.parent) {
            return false;
        }
        for (long i = 0; i < this.length; i++) {
            if (Double.compare(this.getDouble(i), la.getDouble(i)) != 0) return false;
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
                long bits = Double.doubleToLongBits(getDouble(i));
                fprint = 31 * fprint + (int) (bits ^ (bits >>> 32));
            }
        }
        return fprint;
    }

    @Override
    public final Double get(long i)
    {
        return getDouble(i);
    }

    @Override
    public final Double getFromNative(long i)
    {
        return LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
    }

    @Override
    public final boolean getBoolean(long i)
    {
        if (ptr != 0) {
            return (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i)) != 0;
        } else if (isConstant) {
            return data[0] != 0;
        } else {
            return data[(int) i] != 0;
        }
    }

    @Override
    public final byte getByte(long i)
    {
        if (ptr != 0) {
            return (byte) (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (byte) data[0];
        } else {
            return (byte) data[(int) i];
        }
    }

    @Override
    public final short getUnsignedByte(long i)
    {
        if (ptr != 0) {
            return (short) (0xFF & (int) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (short) (0xFF & (int) data[0]);
        } else {
            return (short) (0xFF & (int) data[(int) i]);
        }
    }

    @Override
    public final short getShort(long i)
    {
        if (ptr != 0) {
            return (short) (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (short) data[0];
        } else {
            return (short) data[(int) i];
        }
    }

    @Override
    public final int getInt(long i)
    {
        if (ptr != 0) {
            return (int) (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (int) data[0];
        } else {
            return (int) data[(int) i];
        }
    }

    @Override
    public final long getLong(long i)
    {
        if (ptr != 0) {
            return (long) (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (long) data[0];
        } else {
            return (long) data[(int) i];
        }
    }

    @Override
    public final float getFloat(long i)
    {
        if (ptr != 0) {
            return (float) (LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i));
        } else if (isConstant) {
            return (float) data[0];
        } else {
            return (float) data[(int) i];
        }
    }

    @Override
    public final double getDouble(long i)
    {
        if (ptr != 0) {
            return LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
        } else if (isConstant) {
            return data[0];
        } else {
            return data[(int) i];
        }
    }

    @Override
    public final double[] getData()
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
                double v = LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                out[i] = v != 0;
            }
        } else if (isConstant) {
            boolean elem = data[0] != 0;
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = data[i] != 0;

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
                    double v = LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                    out[idx++] = v != 0;
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = data[0] != 0;
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    double v = data[(int) i];
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
                out[i] = (byte) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = (byte) elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (byte) data[i];

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
                    out[idx++] = (byte) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (byte) data[(int) i];
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
                out[i] = (short) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = (short) elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (short) data[i];

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
                    out[idx++] = (short) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (short) data[(int) i];
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
                out[i] = (int) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = (int) elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (int) data[i];

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
                    out[idx++] = (int) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (int) data[(int) i];
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
                out[i] = (long) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = (long) elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (long) data[i];

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
                    out[idx++] = (long) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (long) data[(int) i];
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
                out[i] = (float) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = (float) elem;
            }
        } else {
            for (int i = 0; i < length; i++) {
                out[i] = (float) data[i];

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
                    out[idx++] = (float) LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (float) data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = (float) data[(int) i];
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
                out[i] = LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
            }
        } else if (isConstant) {
            double elem = data[0];
            for (int i = 0; i < length; i++) {
                out[i] = elem;
            }
        } else {
            System.arraycopy(data, 0, out, 0, (int) length);
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
                    out[idx++] = LargeArrayUtils.UNSAFE.getDouble(ptr + this.type.sizeOf() * i);
                }
            } else if (isConstant) {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = data[0];
                }
            } else {
                for (long i = startPos; i < endPos; i += step) {
                    out[idx++] = data[(int) i];
                }
            }
            return out;
        }
    }

    @Override
    public final void setToNative(long i, Object value)
    {
        LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (Double) value);
    }

    @Override
    public final void setBoolean(long i, boolean value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, value == true ? 1.0 : 0.0);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setBoolean(i, value);
            } else {
                data[(int) i] = value == true ? 1.0 : 0.0;
            }
        }
    }

    @Override
    public final void setByte(long i, byte value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (double) value);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setByte(i, value);
            } else {
                data[(int) i] = (double) value;
            }
        }
    }

    @Override
    public final void setUnsignedByte(long i, short value)
    {
        setShort(i, value);
    }

    @Override
    public void setShort(long i, short value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (double) value);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setShort(i, value);
            } else {
                data[(int) i] = (double) value;
            }
        }
    }

    @Override
    public final void setInt(long i, int value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (double) value);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setInt(i, value);
            } else {
                data[(int) i] = (double) value;
            }
        }
    }

    @Override
    public final void setLong(long i, long value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (double) value);
        } else {
            if (isConstant) {
                if (isConstant) {
                    allocateMemory(true, getDouble(0), false);
                    isConstant = false;
                    setLong(i, value);
                } else {
                    data[(int) i] = (double) value;
                }
            }
        }
    }

    @Override
    public final void setFloat(long i, float value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, (double) value);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setFloat(i, value);
            } else {
                data[(int) i] = (double) value;
            }
        }
    }

    @Override
    public final void setDouble(long i, double value)
    {
        if (ptr != 0) {
            LargeArrayUtils.UNSAFE.putDouble(ptr + this.type.sizeOf() * i, value);
        } else {
            if (isConstant) {
                allocateMemory(true, getDouble(0), false);
                isConstant = false;
                setDouble(i, value);
            } else {
                data[(int) i] = (double) value;
            }
        }
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
                out.writeDouble(getDouble(i));
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
        if (type != LargeArrayType.DOUBLE) {
            throw new IllegalArgumentException("Invalid array type");
        }
        if (data == null) {
            ptr = LargeArrayUtils.UNSAFE.allocateMemory(length * type.sizeOf());
            LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, length, type.sizeOf()));
            MemoryCounter.increaseCounter(length * type.sizeOf());
            for (long i = 0; i < length; i++) {
                setDouble(i, in.readDouble());
            }
        }
    }

    private void allocateMemory(boolean initializeMemory, double initValue, boolean createConstant)
    {
        if (createConstant) {
            isConstant = true;
            data = new double[]{initValue};
        } else {
            if (length > getMaxSizeOf32bitArray()) {
                ptr = LargeArrayUtils.UNSAFE.allocateMemory(length * type.sizeOf());
                if (initializeMemory) {
                    initializeNativeMemory(length, initValue);
                }
                LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, length, type.sizeOf()));
                MemoryCounter.increaseCounter(length * type.sizeOf());
            } else {
                data = new double[(int) length];
                if (initializeMemory && initValue != 0.0) {
                    Arrays.fill(data, initValue);
                }
            }
        }
    }
}
