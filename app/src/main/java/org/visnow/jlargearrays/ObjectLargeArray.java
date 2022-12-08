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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.util.FastMath;
import static org.visnow.jlargearrays.LargeArray.getMaxSizeOf32bitArray;

/**
 *
 * An array of objects that can store up to 2<SUP>63</SUP> elements.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class ObjectLargeArray extends LargeArray
{

    private static final long serialVersionUID = -4096759496772248522L;

    /**
     * Stores elements of this array when LargeArray.isLarge() == false.
     */
    private Object[] data;

    /**
     * Stores the size of each element when LargeArray.isLarge() == true.
     */
    private ShortLargeArray objectSizes;

    /**
     * Maximal size of an element. Used only when LargeArray.isLarge() == true.
     */
    private int maxObjectSize;

    /**
     * Amount of allocated native memory in bytes.
     */
    private long size;

    /**
     * Class of each element.
     */
    private Class clazz = null;

    /**
     * Creates new instance of this class. The maximal string length is set to 100.
     *
     * @param length number of elements
     */
    public ObjectLargeArray(long length)
    {
        this(length, LargeArray.DEFAULT_MAX_OBJECT_SIZE);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length        number of elements
     * @param maxObjectSize maximal size of the object serialized to an array of bytes, it is ignored when number of elements is smaller than
     *                      LargeArray.getMaxSizeOf32bitArray()
     */
    public ObjectLargeArray(long length, int maxObjectSize)
    {
        this(length, maxObjectSize, true);
    }

    /**
     * Creates new instance of this class.
     *
     * @param length           number of elements
     * @param maxObjectSize    maximal size of the object serialized to an array of bytes, it is ignored when number of elements is smaller than
     *                         LargeArray.getMaxSizeOf32bitArray()
     * @param zeroNativeMemory if true, then the native memory is zeroed.
     */
    public ObjectLargeArray(long length, int maxObjectSize, boolean zeroNativeMemory)
    {
        this.type = LargeArrayType.OBJECT;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value.");
        }
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException(maxObjectSize + " is not a positive int value.");
        }
        this.length = length;
        this.size = length * (long) maxObjectSize;
        this.maxObjectSize = maxObjectSize;
        allocateMemory(zeroNativeMemory, null, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length        number of elements
     * @param maxObjectSize maximal size of the object serialized to an array of bytes, it is ignored when number of elements is smaller than
     *                      LargeArray.getMaxSizeOf32bitArray()
     * @param initValue     initialization value
     */
    public ObjectLargeArray(long length, int maxObjectSize, Object initValue)
    {
        this(length, maxObjectSize, initValue, false);
    }

    /**
     * Creates new instance of this class
     *
     * @param length         number of elements
     * @param maxObjectSize  maximal size of the object serialized to an array of bytes, it is ignored when number of elements is smaller than
     *                       LargeArray.getMaxSizeOf32bitArray()
     * @param initValue      initialization value
     * @param createConstant if true, then a constant array is created
     */
    public ObjectLargeArray(long length, int maxObjectSize, Object initValue, boolean createConstant)
    {
        this.type = LargeArrayType.OBJECT;
        if (length < 0) {
            throw new IllegalArgumentException(length + " is not a nonnegative long value");
        }
        this.length = length;
        this.size = length * (long) maxObjectSize;
        this.maxObjectSize = maxObjectSize;
        if (initValue != null) {
            if (!(initValue instanceof java.io.Serializable)) {
                throw new IllegalArgumentException("Initialization value " + initValue + " must implement java.io.Serializable");
            }
            this.clazz = initValue.getClass();
        }
        allocateMemory(true, initValue, createConstant);
    }

    /**
     * Creates new instance of this class.
     *
     * @param data data array, this reference is used internally.
     */
    public ObjectLargeArray(Object[] data)
    {
        if (data == null) {
            throw new IllegalArgumentException("Data array cannot be null");
        }
        if (data.length > 0 && data[0] != null) {
            if (!(data[0] instanceof java.io.Serializable)) {
                throw new IllegalArgumentException("All elements of data array must implement java.io.Serializable");
            }
            this.clazz = data[0].getClass();
        }
        for (Object data1 : data) {
            if (data1 == null) {
                throw new IllegalArgumentException("Elements of data array cannot be null");
            } else if (data1.getClass() != this.clazz) {
                throw new IllegalArgumentException("All elements of data array must be of the same type");
            }
        }
        this.type = LargeArrayType.OBJECT;
        this.length = data.length;
        this.data = data;
    }

    /**
     * Returns a deep copy of this instance. (The elements themselves are copied.)
     *
     * @return a clone of this instance
     */
    @Override
    public ObjectLargeArray clone()
    {
        if (isConstant) {
            return new ObjectLargeArray(length, maxObjectSize, get(0), true);
        } else {
            ObjectLargeArray v = new ObjectLargeArray(length, FastMath.max(1, maxObjectSize), false);
            LargeArrayUtils.arraycopy(this, 0, v, 0, length);
            v.clazz = this.clazz;
            return v;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof ObjectLargeArray))
            return false;
        ObjectLargeArray la = (ObjectLargeArray) o;
        boolean equal = this.type == la.type && this.length == la.length && this.maxObjectSize == la.maxObjectSize && this.clazz == la.clazz;
        if (equal == false) return false;
        if (this.parent != null && la.parent != null) {
            if (!this.parent.equals(la.parent)) return false;
        } else if (this.parent != la.parent) {
            return false;
        }
        for (long i = 0; i < this.length; i++) {
            Object e1 = this.get(i);
            Object e2 = la.get(i);
            if (e1 == e2)
                continue;
            if (e1 == null)
                return false;
            if (!e1.equals(e2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(float quality)
    {
        int fprint = 29 * super.hashCode(quality);
        fprint = 29 * fprint + (int) (this.maxObjectSize ^ (this.maxObjectSize >>> 16));
        fprint = 29 * fprint + (this.clazz != null ? this.clazz.hashCode() : 0);
        if (quality > 0) {
            fprint = 29 * fprint + (this.objectSizes != null ? this.objectSizes.hashCode(quality) : 0);
            long step = (long) FastMath.ceil((1 - length) * quality + length);
            for (long i = 0; i < length; i += step) {
                Object element = get(i);
                fprint = 31 * fprint + (element == null ? 0 : element.hashCode());
            }
        }
        return fprint;
    }

    @Override
    public final Object get(long i)
    {
        if (ptr != 0) {
            short objLen = objectSizes.getShort(i);
            if (objLen <= 0) return null;
            long offset = this.type.sizeOf() * i * maxObjectSize;
            byte[] byteArray = new byte[maxObjectSize];
            for (int j = 0; j < objLen; j++) {
                byteArray[j] = LargeArrayUtils.UNSAFE.getByte(ptr + offset + this.type.sizeOf() * j);
            }
            try {
                return fromByteArray(byteArray);
            } catch (IOException ex) {
                return null;
            }
        } else if (isConstant) {
            return data[0];
        } else {
            return data[(int) i];
        }
    }

    @Override
    public final Object getFromNative(long i)
    {
        short objLen = objectSizes.getShort(i);
        if (objLen < 0) return null;
        long offset = this.type.sizeOf() * i * maxObjectSize;
        byte[] byteArray = new byte[maxObjectSize];
        for (int j = 0; j < objLen; j++) {
            byteArray[j] = LargeArrayUtils.UNSAFE.getByte(ptr + offset + this.type.sizeOf() * j);
        }
        try {
            return fromByteArray(byteArray);
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public final boolean getBoolean(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final byte getByte(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final short getUnsignedByte(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final short getShort(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final int getInt(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final long getLong(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final float getFloat(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final double getDouble(long i)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final Object[] getData()
    {
        return data;
    }

    @Override
    public final boolean[] getBooleanData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final boolean[] getBooleanData(boolean[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final byte[] getByteData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final byte[] getByteData(byte[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final short[] getShortData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final short[] getShortData(short[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final int[] getIntData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final int[] getIntData(int[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final long[] getLongData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final long[] getLongData(long[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final float[] getFloatData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final float[] getFloatData(float[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final double[] getDoubleData()
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final double[] getDoubleData(double[] a, long startPos, long endPos, long step)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setToNative(long i, Object value)
    {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        } else {
            if (this.clazz == null) {
                if (!(value instanceof java.io.Serializable)) {
                    throw new IllegalArgumentException("Object " + value + " must implement java.io.Serializable");
                }
                this.clazz = value.getClass();
            } else if (value.getClass() != this.clazz) {
                throw new IllegalArgumentException("The type of value does not match the type of this ObjectLargeArray");
            }
            byte[] ba = null;
            try {
                ba = toByteArray(value);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Object " + value + " cannot be serialized");
            }
            if (ba.length > maxObjectSize) {
                throw new IllegalArgumentException("Object  " + value + " is too large.");
            }
            int objLen = ba.length;
            if (objLen > Short.MAX_VALUE) {
                throw new IllegalArgumentException("Object  " + value + " is too large.");
            }
            objectSizes.setShort(i, (short) objLen);
            long offset = this.type.sizeOf() * i * maxObjectSize;
            for (int j = 0; j < objLen; j++) {
                LargeArrayUtils.UNSAFE.putByte(ptr + offset + this.type.sizeOf() * j, ba[j]);
            }
        }
    }

    @Override
    public final void set(long i, Object o)
    {

        if (o == null) {
            throw new IllegalArgumentException("Value cannot be null");
        } else {
            if (this.clazz == null) {
                if (!(o instanceof java.io.Serializable)) {
                    throw new IllegalArgumentException("Object " + o + " must implement java.io.Serializable");
                }
                this.clazz = o.getClass();
            } else if (o.getClass() != this.clazz) {
                throw new IllegalArgumentException("The type of value does not match the type of this ObjectLargeArray");
            }
            if (ptr != 0) {
                byte[] ba = null;
                try {
                    ba = toByteArray(o);
                } catch (IOException ex) {
                    throw new IllegalArgumentException("Object " + o + " cannot be serialized");
                }
                if (ba.length > maxObjectSize) {
                    throw new IllegalArgumentException("Object  " + o + " is too large.");
                }
                int objLen = ba.length;
                if (objLen > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Object  " + o + " is too large.");
                }
                objectSizes.setShort(i, (short) objLen);
                long offset = this.type.sizeOf() * i * maxObjectSize;
                for (int j = 0; j < objLen; j++) {
                    LargeArrayUtils.UNSAFE.putByte(ptr + offset + this.type.sizeOf() * j, ba[j]);
                }
            } else {
                if (isConstant) {
                    allocateMemory(true, get(0), false);
                    isConstant = false;
                    set(i, o);
                } else {
                    data[(int) i] = o;
                }
            }
        }
    }

    @Override
    public final void set_safe(long i, Object value)
    {
        if (i < 0 || i >= length) {
            throw new ArrayIndexOutOfBoundsException(Long.toString(i));
        }
        set(i, value);
    }

    @Override
    public final void setBoolean(long i, boolean value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setByte(long i, byte value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setUnsignedByte(long i, short value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setShort(long i, short value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setInt(long i, int value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setLong(long i, long value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setFloat(long i, float value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public final void setDouble(long i, double value)
    {
        throw new UnsupportedOperationException("Not supported yet");
    }

    private static byte[] toByteArray(final Object obj) throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return baos.toByteArray();
    }

    private static Object fromByteArray(final byte[] objectData) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            final Object obj = ois.readObject();
            return obj;

        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Returns maximal size of each object serialized to an array of bytes.
     *
     * @return maximal size of each object serialized to an array of bytes
     *
     */
    public int getMaxObjectSize()
    {
        return maxObjectSize;
    }

    /**
     * Returns the runtime class of each element of this ObjectLargeArray.
     *
     * @return The object that represents the runtime class of each element of this ObjectLargeArray.
     */
    public Class getElementClass()
    {
        return clazz;
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
                out.writeObject(get(i));
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
        if (type != LargeArrayType.OBJECT) {
            throw new IllegalArgumentException("Invalid array type");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Non-positive array length");
        }
        if (data == null) {
            if (size <= 0) {
                throw new IllegalArgumentException("Non-positive size");
            }
            if (maxObjectSize <= 0) {
                throw new IllegalArgumentException(maxObjectSize + " is not a positive int value.");
            }
            if (objectSizes == null || objectSizes.length() != length) {
                throw new IllegalArgumentException("objectSizes == null || objectSizes.length() != length");
            }
            ptr = LargeArrayUtils.UNSAFE.allocateMemory(size * type.sizeOf());
            LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, size, type.sizeOf()));
            MemoryCounter.increaseCounter(size * type.sizeOf());
            for (long i = 0; i < length; i++) {
                if (objectSizes.getShort(i) > 0) {
                    set(i, in.readObject());
                } else {
                    in.readObject();
                }
            }
        }
    }

    private void allocateMemory(boolean initializeMemory, Object initValue, boolean createConstant)
    {
        if (createConstant) {
            isConstant = true;
            data = new Object[]{initValue};
        } else {
            if (length > getMaxSizeOf32bitArray()) {
                ptr = LargeArrayUtils.UNSAFE.allocateMemory(size * type.sizeOf());
                LargeArrayUtils.CLEANER.register(this, new Deallocator(ptr, size, type.sizeOf()));
                MemoryCounter.increaseCounter(size * type.sizeOf());
                objectSizes = new ShortLargeArray(length);
                for (long i = 0; i < length; i++) {
                    objectSizes.setShort(i, (short) -1);
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
                data = new Object[(int) length];
            }
        }
    }

}
