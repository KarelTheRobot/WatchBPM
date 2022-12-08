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

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.math3.util.FastMath;
import java.lang.ref.Cleaner;

/**
 *
 * LargeArray utilities.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class LargeArrayUtils
{

    /**
     * An object for performing low-level, unsafe operations.
     */
    public static final sun.misc.Unsafe UNSAFE;

    static {
        Object theUnsafe = null;
        Exception exception = null;
        try {
            Class<?> uc = Class.forName("sun.misc.Unsafe");
            Field f = uc.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = f.get(uc);
        } catch (ClassNotFoundException e) {
            exception = e;
        } catch (IllegalAccessException e) {
            exception = e;
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (NoSuchFieldException e) {
            exception = e;
        } catch (SecurityException e) {
            exception = e;
        }
        UNSAFE = (sun.misc.Unsafe) theUnsafe;
        if (UNSAFE == null) {
            throw new Error("Could not obtain access to sun.misc.Unsafe", exception);
        }
    }
    
    /**
     * An instance of a Cleaner object.
     */
    public static final Cleaner CLEANER = Cleaner.create();


    private LargeArrayUtils()
    {
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Both arrays need to be of the same type. Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    public static void arraycopy(final Object src, final long srcPos, final Object dest, final long destPos, final long length)
    {
        if ((src instanceof LargeArray) && (dest instanceof LargeArray)) {
            arraycopy((LargeArray) src, srcPos, (LargeArray) dest, destPos, length);
        } else if ((src instanceof LargeArray)) {
            arraycopy((LargeArray) src, srcPos, dest, (int) destPos, (int) length);
        } else if ((dest instanceof LargeArray)) {
            arraycopy(src, (int) srcPos, (LargeArray) dest, destPos, (int) length);
        } else {
            System.arraycopy(src, (int) srcPos, dest, (int) destPos, (int) length);
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Both arrays need to be of the same type. Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LargeArray src, final long srcPos, final LargeArray dest, final long destPos, final long length)
    {
        if (src.getType() != dest.getType()) {
            throw new IllegalArgumentException("Types of source and destination arrays do not match.");
        }
        switch (src.getType()) {
            case LOGIC:
                arraycopy((LogicLargeArray) src, srcPos, (LogicLargeArray) dest, destPos, length);
                break;
            case BYTE:
                arraycopy((ByteLargeArray) src, srcPos, (ByteLargeArray) dest, destPos, length);
                break;
            case UNSIGNED_BYTE:
                arraycopy((UnsignedByteLargeArray) src, srcPos, (UnsignedByteLargeArray) dest, destPos, length);
                break;
            case SHORT:
                arraycopy((ShortLargeArray) src, srcPos, (ShortLargeArray) dest, destPos, length);
                break;
            case INT:
                arraycopy((IntLargeArray) src, srcPos, (IntLargeArray) dest, destPos, length);
                break;
            case LONG:
                arraycopy((LongLargeArray) src, srcPos, (LongLargeArray) dest, destPos, length);
                break;
            case FLOAT:
                arraycopy((FloatLargeArray) src, srcPos, (FloatLargeArray) dest, destPos, length);
                break;
            case DOUBLE:
                arraycopy((DoubleLargeArray) src, srcPos, (DoubleLargeArray) dest, destPos, length);
                break;
            case COMPLEX_FLOAT:
                arraycopy((ComplexFloatLargeArray) src, srcPos, (ComplexFloatLargeArray) dest, destPos, length);
                break;
            case COMPLEX_DOUBLE:
                arraycopy((ComplexDoubleLargeArray) src, srcPos, (ComplexDoubleLargeArray) dest, destPos, length);
                break;
            case STRING:
                arraycopy((StringLargeArray) src, srcPos, (StringLargeArray) dest, destPos, length);
                break;
            case OBJECT:
                arraycopy((ObjectLargeArray) src, srcPos, (ObjectLargeArray) dest, destPos, length);
                break;
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Both arrays need to be of the same type. Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final Object src, final int srcPos, final LargeArray dest, final long destPos, final int length)
    {
        Class dataClass = src.getClass();
        Class componentClass = dataClass.getComponentType();
        switch (dest.getType()) {
            case LOGIC:
                if (componentClass == Boolean.TYPE) {
                    arraycopy((boolean[]) src, srcPos, (LogicLargeArray) dest, destPos, length);
                } else if (componentClass == Byte.TYPE) {
                    arraycopy((byte[]) src, srcPos, (LogicLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case BYTE:
                if (componentClass == Byte.TYPE) {
                    arraycopy((byte[]) src, srcPos, (ByteLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case UNSIGNED_BYTE: {
                if (componentClass == Byte.TYPE) {
                    arraycopy((byte[]) src, srcPos, (UnsignedByteLargeArray) dest, destPos, length);
                } else if (componentClass == Short.TYPE) {
                    arraycopy((short[]) src, srcPos, (UnsignedByteLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            }
            case SHORT:
                if (componentClass == Short.TYPE) {
                    arraycopy((short[]) src, srcPos, (ShortLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case INT:
                if (componentClass == Integer.TYPE) {
                    arraycopy((int[]) src, srcPos, (IntLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case LONG:
                if (componentClass == Long.TYPE) {
                    arraycopy((long[]) src, srcPos, (LongLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case FLOAT:
                if (componentClass == Float.TYPE) {
                    arraycopy((float[]) src, srcPos, (FloatLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case DOUBLE:
                if (componentClass == Double.TYPE) {
                    arraycopy((double[]) src, srcPos, (DoubleLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case COMPLEX_FLOAT:
                if (componentClass == Float.TYPE) {
                    arraycopy((float[]) src, srcPos, (ComplexFloatLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case COMPLEX_DOUBLE:
                if (componentClass == Double.TYPE) {
                    arraycopy((double[]) src, srcPos, (ComplexDoubleLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case STRING:
                if (src instanceof String[]) {
                    arraycopy((String[]) src, srcPos, (StringLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case OBJECT:
                if (src instanceof Object[]) {
                    arraycopy((Object[]) src, srcPos, (ObjectLargeArray) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Both arrays need to be of the same type. Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LargeArray src, final long srcPos, final Object dest, final int destPos, final int length)
    {
        Class dataClass = dest.getClass();
        Class componentClass = dataClass.getComponentType();
        switch (src.getType()) {
            case LOGIC:
                if (componentClass == Boolean.TYPE) {
                    arraycopy((LogicLargeArray) src, srcPos, (boolean[]) dest, destPos, length);
                } else if (componentClass == Byte.TYPE) {
                    arraycopy((LogicLargeArray) src, srcPos, (byte[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case BYTE:
                if (componentClass == Byte.TYPE) {
                    arraycopy((ByteLargeArray) src, srcPos, (byte[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case UNSIGNED_BYTE: {
                if (componentClass == Byte.TYPE) {
                    arraycopy((UnsignedByteLargeArray) src, srcPos, (byte[]) dest, destPos, length);
                } else if (componentClass == Short.TYPE) {
                    arraycopy((UnsignedByteLargeArray) src, srcPos, (short[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            }
            case SHORT:
                if (componentClass == Short.TYPE) {
                    arraycopy((ShortLargeArray) src, srcPos, (short[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case INT:
                if (componentClass == Integer.TYPE) {
                    arraycopy((IntLargeArray) src, srcPos, (int[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case LONG:
                if (componentClass == Long.TYPE) {
                    arraycopy((LongLargeArray) src, srcPos, (long[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case FLOAT:
                if (componentClass == Float.TYPE) {
                    arraycopy((FloatLargeArray) src, srcPos, (float[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case DOUBLE:
                if (componentClass == Double.TYPE) {
                    arraycopy((DoubleLargeArray) src, srcPos, (double[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case COMPLEX_FLOAT:
                if (componentClass == Float.TYPE) {
                    arraycopy((ComplexFloatLargeArray) src, srcPos, (float[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case COMPLEX_DOUBLE:
                if (componentClass == Double.TYPE) {
                    arraycopy((ComplexDoubleLargeArray) src, srcPos, (double[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case STRING:
                if (dest instanceof String[]) {
                    arraycopy((StringLargeArray) src, srcPos, (String[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            case OBJECT:
                if (dest instanceof Object[]) {
                    arraycopy((ObjectLargeArray) src, srcPos, (Object[]) dest, destPos, length);
                } else {
                    throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LogicLargeArray src, final long srcPos, final LogicLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setByte(j, src.getByte(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src.getByte(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setByte(j, src.getByte(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final boolean[] src, final int srcPos, final LogicLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setBoolean(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setBoolean(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setBoolean(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final byte[] src, final int srcPos, final LogicLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setByte(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setByte(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LogicLargeArray src, final long srcPos, final boolean[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getBoolean(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getBoolean(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getBoolean(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LogicLargeArray src, final long srcPos, final byte[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getByte(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getByte(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getByte(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ByteLargeArray src, final long srcPos, final ByteLargeArray dest, final long destPos, final long length)
    {

        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setByte(j, src.getByte(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src.getByte(srcPos + k));
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setByte(j, src.getByte(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final byte[] src, final int srcPos, final ByteLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setByte(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setByte(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ByteLargeArray src, final long srcPos, final byte[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getByte(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getByte(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getByte(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final UnsignedByteLargeArray src, final long srcPos, final UnsignedByteLargeArray dest, final long destPos, final long length)
    {

        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setByte(j, src.getByte(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src.getByte(srcPos + k));
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setByte(j, src.getByte(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final byte[] src, final int srcPos, final UnsignedByteLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setByte(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setByte(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setByte(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final short[] src, final int srcPos, final UnsignedByteLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setUnsignedByte(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setUnsignedByte(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setUnsignedByte(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final UnsignedByteLargeArray src, final long srcPos, final byte[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getByte(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getByte(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getByte(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final UnsignedByteLargeArray src, final long srcPos, final short[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getUnsignedByte(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getUnsignedByte(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getUnsignedByte(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ShortLargeArray src, final long srcPos, final ShortLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setShort(j, src.getShort(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setShort(destPos + k, src.getShort(srcPos + k));
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setShort(j, src.getShort(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final short[] src, final int srcPos, final ShortLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setShort(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setShort(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setShort(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ShortLargeArray src, final long srcPos, final short[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getShort(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getShort(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getShort(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final IntLargeArray src, final long srcPos, final IntLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setInt(j, src.getInt(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setInt(destPos + k, src.getInt(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setInt(j, src.getInt(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final int[] src, final int srcPos, final IntLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setInt(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setInt(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setInt(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final IntLargeArray src, final long srcPos, final int[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getInt(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getInt(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getInt(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LongLargeArray src, final long srcPos, final LongLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setLong(j, src.getLong(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setLong(destPos + k, src.getLong(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setLong(j, src.getLong(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final long[] src, final int srcPos, final LongLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }

        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setLong(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setLong(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setLong(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final LongLargeArray src, final long srcPos, final long[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getLong(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getLong(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getLong(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final FloatLargeArray src, final long srcPos, final FloatLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setFloat(j, src.getFloat(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setFloat(destPos + k, src.getFloat(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setFloat(j, src.getFloat(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final float[] src, final int srcPos, final FloatLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setFloat(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setFloat(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setFloat(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final FloatLargeArray src, final long srcPos, final float[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getFloat(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getFloat(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getFloat(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final DoubleLargeArray src, final long srcPos, final DoubleLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setDouble(j, src.getDouble(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setDouble(destPos + k, src.getDouble(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setDouble(j, src.getDouble(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final double[] src, final int srcPos, final DoubleLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.setDouble(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setDouble(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.setDouble(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final DoubleLargeArray src, final long srcPos, final double[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.getDouble(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.getDouble(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.getDouble(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ComplexFloatLargeArray src, final long srcPos, final ComplexFloatLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setComplexFloat(j, src.getComplexFloat(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setComplexFloat(destPos + k, src.getComplexFloat(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setComplexFloat(j, src.getComplexFloat(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final float[] src, final int srcPos, final ComplexFloatLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + 2 * length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + 2 * length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            float[] elem = new float[2];
            for (long j = destPos; j < destPos + length; j++) {
                elem[0] = src[i];
                elem[1] = src[i + 1];
                dest.setComplexFloat(j, elem);
                i += 2;
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        float[] elem = new float[2];
                        int l = srcPos + (int) firstIdx;
                        for (long k = firstIdx; k < lastIdx; k++) {
                            elem[0] = src[l];
                            elem[1] = src[l + 1];
                            dest.setComplexFloat(destPos + k, elem);
                            l += 2;
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                float[] elem = new float[2];
                for (long j = destPos; j < destPos + length; j++) {
                    elem[0] = src[i];
                    elem[1] = src[i + 1];
                    dest.setComplexFloat(j, elem);
                    i += 2;
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ComplexFloatLargeArray src, final long srcPos, final float[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + 2 * length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + 2 * length > dest.length()");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                float[] elem = src.getComplexFloat(j);
                dest[i] = elem[0];
                dest[i + 1] = elem[1];
                i += 2;
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int l = destPos + (int) firstIdx;
                        for (long k = firstIdx; k < lastIdx; k++) {
                            float[] elem = src.getComplexFloat(firstIdx + k);
                            dest[l] = elem[0];
                            dest[l + 1] = elem[1];
                            l += 2;
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    float[] elem = src.getComplexFloat(j);
                    dest[i] = elem[0];
                    dest[i + 1] = elem[1];
                    i += 2;
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ComplexDoubleLargeArray src, final long srcPos, final ComplexDoubleLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.setComplexDouble(j, src.getComplexDouble(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.setComplexDouble(destPos + k, src.getComplexDouble(srcPos + k));
                        }
                    }
                });

            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.setComplexDouble(j, src.getComplexDouble(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final double[] src, final int srcPos, final ComplexDoubleLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            double[] elem = new double[2];
            for (long j = destPos; j < destPos + length; j++) {
                elem[0] = src[i];
                elem[1] = src[i + 1];
                dest.setComplexDouble(j, elem);
                i += 2;
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        double[] elem = new double[2];
                        int l = srcPos + (int) firstIdx;
                        for (long k = firstIdx; k < lastIdx; k++) {
                            elem[0] = src[l];
                            elem[1] = src[l + 1];
                            dest.setComplexDouble(destPos + k, elem);
                            l += 2;
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                double[] elem = new double[2];
                for (long j = destPos; j < destPos + length; j++) {
                    elem[0] = src[i];
                    elem[1] = src[i + 1];
                    dest.setComplexDouble(j, elem);
                    i += 2;
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ComplexDoubleLargeArray src, final long srcPos, final double[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + 2 * length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + 2 * length > dest.length()");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                double[] elem = src.getComplexDouble(j);
                dest[i] = elem[0];
                dest[i + 1] = elem[1];
                i += 2;
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int l = destPos + (int) firstIdx;
                        for (long k = firstIdx; k < lastIdx; k++) {
                            double[] elem = src.getComplexDouble(firstIdx + k);
                            dest[l] = elem[0];
                            dest[l + 1] = elem[1];
                            l += 2;
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    double[] elem = src.getComplexDouble(j);
                    dest[i] = elem[0];
                    dest[i + 1] = elem[1];
                    i += 2;
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final StringLargeArray src, final long srcPos, final StringLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.set(j, src.get(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.set(destPos + k, src.get(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.set(j, src.get(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final String[] src, final int srcPos, final StringLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.set(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.set(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.set(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final StringLargeArray src, final long srcPos, final String[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.get(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.get(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.get(j);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ObjectLargeArray src, final long srcPos, final ObjectLargeArray dest, final long destPos, final long length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                dest.set(j, src.get(i));
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.set(destPos + k, src.get(srcPos + k));
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long i = srcPos, j = destPos; i < srcPos + length; i++, j++) {
                    dest.set(j, src.get(i));
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final Object[] src, final int srcPos, final ObjectLargeArray dest, final long destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length");
        }
        if (destPos < 0 || destPos + length > dest.length()) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length()");
        }
        if (dest.isConstant()) {
            throw new IllegalArgumentException("Constant arrays cannot be modified.");
        }
        int i = srcPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = destPos; j < destPos + length; j++) {
                dest.set(j, src[i++]);
            }
        } else {
            long k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final long firstIdx = j * k;
                final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (long k = firstIdx; k < lastIdx; k++) {
                            dest.set(destPos + k, src[srcPos + (int) k]);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = destPos; j < destPos + length; j++) {
                    dest.set(j, src[i++]);
                }
            }
        }
    }

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * Array bounds are checked.
     *
     * @param src     the source array.
     * @param srcPos  starting position in the source array.
     * @param dest    the destination array.
     * @param destPos starting position in the destination data.
     * @param length  the number of array elements to be copied.
     */
    private static void arraycopy(final ObjectLargeArray src, final long srcPos, final Object[] dest, final int destPos, final int length)
    {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (srcPos < 0 || srcPos + length > src.length()) {
            throw new ArrayIndexOutOfBoundsException("srcPos < 0 || srcPos + length > src.length()");
        }
        if (destPos < 0 || destPos + length > dest.length) {
            throw new ArrayIndexOutOfBoundsException("destPos < 0 || destPos + length > dest.length");
        }

        int i = destPos;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
            for (long j = srcPos; j < srcPos + length; j++) {
                dest[i++] = src.get(j);
            }
        } else {
            int k = length / nthreads;
            Future[] threads = new Future[nthreads];
            for (int j = 0; j < nthreads; j++) {
                final int firstIdx = j * k;
                final int lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
                threads[j] = ConcurrencyUtils.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int k = firstIdx; k < lastIdx; k++) {
                            dest[destPos + k] = src.get(srcPos + k);
                        }
                    }
                });
            }
            try {
                ConcurrencyUtils.waitForCompletion(threads);
            } catch (InterruptedException | ExecutionException ex) {
                for (long j = srcPos; j < srcPos + length; j++) {
                    dest[i++] = src.get(j);
                }
            }
        }
    }

    private static boolean recomputeCopyLimits(long[] srcDim, long[] destDim, long[] srcPos, long[] destPos, long[] size)
    {
        for (int i = 0; i < srcDim.length; i++) {
            if (srcPos[i] < 0) {
                size[i] += srcPos[i];
                destPos[i] -= srcPos[i];
                if (size[i] < 1)
                    return false;
                srcPos[i] = 0;
            }
            if (srcPos[i] + size[i] > srcDim[i]) {
                size[i] = srcDim[i] - srcPos[i];
                if (size[i] < 1)
                    return false;
            }
            if (destPos[i] < 0) {
                size[i] += destPos[i];
                srcPos[i] -= destPos[i];
                if (size[i] < 1)
                    return false;
                destPos[i] = 0;
            }
            if (destPos[i] + size[i] > destDim[i]) {
                size[i] = destDim[i] - destPos[i];
                if (size[i] < 1)
                    return false;
            }
        }
        return true;
    }

    private static long product(long[] a)
    {
        long p = 1;
        for (int i = 0; i < a.length; i++) {
            p *= a[i];
        }
        return p;
    }

    private static void checkArrayTypeAndSize(final Object src, final long[] srcDim, final Object dest, final long[] destDim)
    {
        Class srcDataClass = src.getClass();
        Class srcComponentClass = srcDataClass.getComponentType();
        Class destDataClass = dest.getClass();
        Class destComponentClass = destDataClass.getComponentType();

        if (src instanceof LargeArray && dest instanceof LargeArray) {
            if (((LargeArray) src).getType() != ((LargeArray) dest).getType()) {
                throw new IllegalArgumentException("Types of source and destination arrays do not match.");
            }
            if (product(srcDim) != ((LargeArray) src).length()) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((LargeArray) dest).length()) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (src instanceof LargeArray) {
            if (product(srcDim) != ((LargeArray) src).length()) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            switch (((LargeArray) src).getType()) {
                case LOGIC:
                    if (destComponentClass != Boolean.TYPE && destComponentClass != Byte.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (destComponentClass == Boolean.TYPE && product(destDim) != ((boolean[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    if (destComponentClass == Byte.TYPE && product(destDim) != ((byte[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case BYTE:
                    if (destComponentClass != Byte.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((byte[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case UNSIGNED_BYTE:
                    if (destComponentClass != Byte.TYPE && destComponentClass != Short.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (destComponentClass == Byte.TYPE && product(destDim) != ((byte[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    if (destComponentClass == Short.TYPE && product(destDim) != ((short[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case SHORT:
                    if (destComponentClass != Short.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((short[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case INT:
                    if (destComponentClass != Integer.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((int[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case LONG:
                    if (destComponentClass != Long.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((long[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case FLOAT:
                    if (destComponentClass != Float.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((float[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case DOUBLE:
                    if (destComponentClass != Double.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((double[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case COMPLEX_FLOAT:
                    if (destComponentClass != Float.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((float[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case COMPLEX_DOUBLE:
                    if (destComponentClass != Double.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((double[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case STRING:
                    if (!(dest instanceof String[])) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((String[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                case OBJECT:
                    if (!(dest instanceof Object[])) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(destDim) != ((Object[]) dest).length) {
                        throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid array type.");
            }
        } else if (dest instanceof LargeArray) {
            if (product(destDim) != ((LargeArray) dest).length()) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
            switch (((LargeArray) dest).getType()) {
                case LOGIC:
                    if (srcComponentClass != Boolean.TYPE && srcComponentClass != Byte.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (srcComponentClass == Boolean.TYPE && product(srcDim) != ((boolean[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    if (srcComponentClass == Byte.TYPE && product(srcDim) != ((byte[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case BYTE:
                    if (srcComponentClass != Byte.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((byte[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case UNSIGNED_BYTE:
                    if (srcComponentClass != Byte.TYPE && srcComponentClass != Short.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (srcComponentClass == Byte.TYPE && product(srcDim) != ((byte[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    if (srcComponentClass == Short.TYPE && product(srcDim) != ((short[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case SHORT:
                    if (srcComponentClass != Short.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((short[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case INT:
                    if (srcComponentClass != Integer.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((int[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case LONG:
                    if (srcComponentClass != Long.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((long[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case FLOAT:
                    if (srcComponentClass != Float.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((float[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case DOUBLE:
                    if (srcComponentClass != Double.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((double[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case COMPLEX_FLOAT:
                    if (srcComponentClass != Float.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((float[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case COMPLEX_DOUBLE:
                    if (srcComponentClass != Double.TYPE) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((double[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case STRING:
                    if (!(src instanceof String[])) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((String[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                case OBJECT:
                    if (!(src instanceof Object[])) {
                        throw new IllegalArgumentException("Types of source and destination arrays do not match.");
                    }
                    if (product(srcDim) != ((Object[]) src).length) {
                        throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid array type.");
            }
        } else if (srcComponentClass == Boolean.TYPE && destComponentClass == Boolean.TYPE) {
            if (product(srcDim) != ((boolean[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((boolean[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Byte.TYPE && destComponentClass == Byte.TYPE) {
            if (product(srcDim) != ((byte[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((byte[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Short.TYPE && destComponentClass == Short.TYPE) {
            if (product(srcDim) != ((short[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((short[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Integer.TYPE && destComponentClass == Integer.TYPE) {
            if (product(srcDim) != ((int[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((int[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Long.TYPE && destComponentClass == Long.TYPE) {
            if (product(srcDim) != ((long[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((long[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Float.TYPE && destComponentClass == Float.TYPE) {
            if (product(srcDim) != ((float[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((float[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (srcComponentClass == Double.TYPE && destComponentClass == Double.TYPE) {
            if (product(srcDim) != ((double[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((double[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (src instanceof String[] && dest instanceof String[]) {
            if (product(srcDim) != ((String[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((String[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else if (src instanceof Object[] && src instanceof Object[]) {
            if (product(srcDim) != ((Object[]) src).length) {
                throw new IllegalArgumentException("The length of source array does not match the source dimensions parameter.");
            }
            if (product(destDim) != ((Object[]) dest).length) {
                throw new IllegalArgumentException("The length of destimation array does not match the destimation dimensions parameter.");
            }
        } else {
            throw new IllegalArgumentException("Types of source and destination arrays do not match.");
        }
    }

    /**
     * Copies a rectangular area of rank 1 to 4 from source to target.
     *
     * @param src     source array of the length srcDim[0] * srcDim[1] * ... * srcDim[srcDim.length-1]
     * @param srcDim  dimensions of the source array (of length 1, 2, 3 or 4)
     * @param srcPos  lower left corner of the copied area (of length 1, 2, 3 or 4)
     * @param dest    destination array of the length destDim[0] * destDim[1] * ... * destDim[destDim.length-1]; source and destination arrays must be of the
     *                same type
     * @param destDim dimensions of the destination array
     * @param destPos lower left corner of the destination area
     * @param size    dimensions of the copied area
     */
    public static void subarraycopy(final Object src, final long[] srcDim, final long[] srcPos, final Object dest, final long[] destDim, final long[] destPos, final long[] size)
    {
        if (src == null || srcDim == null || srcPos == null || dest == null || destDim == null || destPos == null || size == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        if (srcDim.length != srcPos.length || srcDim.length != destDim.length || srcDim.length != destPos.length || srcDim.length != size.length) {
            throw new IllegalArgumentException("srcDim.length != srcPos.length || srcDim.length != destDim.length || srcDim.length != destPos || srcDim.length != size.length");
        }

        checkArrayTypeAndSize(src, srcDim, dest, destDim);

        long[] sp = srcPos.clone();
        long[] dp = destPos.clone();
        long[] s = size.clone();

        if (!recomputeCopyLimits(srcDim, destDim, sp, dp, s)) {
            throw new IllegalArgumentException("Invalid value in one of the parameters defining dimensions, positions and size.");
        }

        switch (srcDim.length) {
            case 4:
                for (int r3 = 0; r3 < s[3]; r3++) {
                    for (int r2 = 0; r2 < s[2]; r2++) {
                        for (int r1 = 0; r1 < s[1]; r1++) {
                            arraycopy(src, (((r3 + sp[3]) * srcDim[2] + (r2 + sp[2])) * srcDim[1] + r1 + sp[1]) * srcDim[0] + sp[0], dest, (((r3 + dp[3]) * destDim[2] + (r2 + dp[2])) * destDim[1] + r1 + dp[1]) * destDim[0] + dp[0], s[0]);
                        }
                    }
                }
                break;
            case 3:
                for (int r2 = 0; r2 < s[2]; r2++) {
                    for (int r1 = 0; r1 < s[1]; r1++) {
                        arraycopy(src, ((r2 + sp[2]) * srcDim[1] + r1 + sp[1]) * srcDim[0] + sp[0], dest, ((r2 + dp[2]) * destDim[1] + r1 + dp[1]) * destDim[0] + dp[0], s[0]);
                    }
                }
                break;
            case 2:
                for (int r1 = 0; r1 < s[1]; r1++) {
                    arraycopy(src, (r1 + sp[1]) * srcDim[0] + sp[0], dest, (r1 + dp[1]) * destDim[0] + dp[0], s[0]);
                }
                break;
            case 1:
                arraycopy(src, sp[0], dest, dp[0], s[0]);
                break;

            default:
                throw new IllegalArgumentException("Arrays of rank greater than 4 are not supported.");
        }
    }

    /**
     * Copies a rectangular area of rank 1 to 4 from source to target.
     *
     * @param src     source array of the length srcDim[0] * srcDim[1] * ... * srcDim[srcDim.length-1]
     * @param srcDim  dimensions of the source array (of length 1, 2, 3 or 4)
     * @param srcPos  lower left corner of the copied area (of length 1, 2, 3 or 4)
     * @param dest    destination array of the length destDim[0] * destDim[1] * ... * destDim[destDim.length-1]; source and destination arrays must be of the
     *                same type
     * @param destDim dimensions of the destination array
     * @param destPos lower left corner of the destination area
     * @param size    dimensions of the copied area
     */
    public static final void subarraycopy(Object src, int[] srcDim, int[] srcPos, Object dest, int[] destDim, int[] destPos, int[] size)
    {

        if (src == null || srcDim == null || srcPos == null || dest == null || destDim == null || destPos == null || size == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }
        if (srcDim.length != srcPos.length || srcDim.length != destDim.length || srcDim.length != destPos.length || srcDim.length != size.length) {
            throw new IllegalArgumentException("srcDim.length != srcPos.length || srcDim.length != destDim.length || srcDim.length != destPos || srcDim.length != size.length");
        }

        long[] srcDimLong = new long[srcDim.length];
        long[] srcPosLong = new long[srcDim.length];
        long[] destDimLong = new long[srcDim.length];
        long[] destPosLong = new long[srcDim.length];
        long[] sizeLong = new long[srcDim.length];

        for (int i = 0; i < srcDim.length; i++) {
            srcDimLong[i] = srcDim[i];
            srcPosLong[i] = srcPos[i];
            destDimLong[i] = destDim[i];
            destPosLong[i] = destPos[i];
            sizeLong[i] = size[i];

        }
        subarraycopy(src, srcDimLong, srcPosLong, dest, destDimLong, destPosLong, sizeLong);
    }

    /**
     * Creates a new constant LargeArray.
     *
     * @param type   the type of constant LargeArray
     * @param length the length of constant LargeArray
     * @param value  the value of constant LargeArray
     *
     * @return constant LargeArray of a specified type
     */
    public static LargeArray createConstant(final LargeArrayType type, final long length, Object value)
    {
        switch (type) {
            case LOGIC: {
                byte v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? (byte) 1 : (byte) 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).byteValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).byteValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).byteValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).byteValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).byteValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).byteValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new LogicLargeArray(length, v, true);
            }
            case BYTE: {
                byte v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? (byte) 1 : (byte) 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).byteValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).byteValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).byteValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).byteValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).byteValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).byteValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new ByteLargeArray(length, v, true);
            }
            case UNSIGNED_BYTE: {
                short v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? (short) 1 : (short) 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).shortValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).shortValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).shortValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).shortValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).shortValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).shortValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new UnsignedByteLargeArray(length, v, true);
            }
            case SHORT: {
                short v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? (short) 1 : (short) 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).shortValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).shortValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).shortValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).shortValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).shortValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).shortValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new ShortLargeArray(length, v, true);
            }
            case INT: {
                int v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? 1 : 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).intValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).intValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).intValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).intValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).intValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).intValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new IntLargeArray(length, v, true);
            }
            case LONG: {
                long v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? 1 : 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).longValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).longValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).longValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).longValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).longValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).longValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new LongLargeArray(length, v, true);
            }
            case FLOAT: {
                float v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value) == true ? 1 : 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).floatValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).floatValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).floatValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).floatValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).floatValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).floatValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new FloatLargeArray(length, v, true);
            }
            case DOUBLE: {
                double v;
                if (value instanceof Boolean) {
                    v = ((Boolean) value).booleanValue() == true ? 1 : 0;
                } else if (value instanceof Byte) {
                    v = ((Byte) value).doubleValue();
                } else if (value instanceof Short) {
                    v = ((Short) value).doubleValue();
                } else if (value instanceof Integer) {
                    v = ((Integer) value).doubleValue();
                } else if (value instanceof Long) {
                    v = ((Long) value).doubleValue();
                } else if (value instanceof Float) {
                    v = ((Float) value).doubleValue();
                } else if (value instanceof Double) {
                    v = ((Double) value).doubleValue();
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new DoubleLargeArray(length, v, true);
            }
            case COMPLEX_FLOAT: {
                Class dataClass = value.getClass();
                Class componentClass = dataClass.getComponentType();
                float[] v;
                if (componentClass == Float.TYPE) {
                    v = (float[]) value;
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new ComplexFloatLargeArray(length, v, true);
            }
            case COMPLEX_DOUBLE: {
                Class dataClass = value.getClass();
                Class componentClass = dataClass.getComponentType();
                double[] v;
                if (componentClass == Double.TYPE) {
                    v = (double[]) value;
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new ComplexDoubleLargeArray(length, v, true);
            }
            case STRING:
                String v;
                if (value instanceof String) {
                    v = (String) value;
                } else {
                    throw new IllegalArgumentException("Invalid value type.");
                }
                return new StringLargeArray(length, LargeArray.DEFAULT_MAX_STRING_LENGTH, v, true);
            case OBJECT:
                return new ObjectLargeArray(length, LargeArray.DEFAULT_MAX_OBJECT_SIZE, value, true);
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
    }

    /**
     * Creates a new instance of LargeArray. The native memory is zeroed.
     *
     * @param type   the type of LargeArray
     * @param length number of elements
     *
     * @return new instance of LargeArray
     */
    public static LargeArray create(LargeArrayType type, long length)
    {
        return create(type, length, true);
    }

    /**
     * Creates a new instance of LargeArray
     *
     * @param type             the type of LargeArray
     * @param length           number of elements
     * @param zeroNativeMemory if true, then the native memory is zeroed
     *
     * @return new instance of LargeArray
     */
    public static LargeArray create(LargeArrayType type, long length, boolean zeroNativeMemory)
    {
        switch (type) {
            case LOGIC:
                return new LogicLargeArray(length, zeroNativeMemory);
            case BYTE:
                return new ByteLargeArray(length, zeroNativeMemory);
            case UNSIGNED_BYTE:
                return new UnsignedByteLargeArray(length, zeroNativeMemory);
            case SHORT:
                return new ShortLargeArray(length, zeroNativeMemory);
            case INT:
                return new IntLargeArray(length, zeroNativeMemory);
            case LONG:
                return new LongLargeArray(length, zeroNativeMemory);
            case FLOAT:
                return new FloatLargeArray(length, zeroNativeMemory);
            case DOUBLE:
                return new DoubleLargeArray(length, zeroNativeMemory);
            case COMPLEX_FLOAT:
                return new ComplexFloatLargeArray(length, zeroNativeMemory);
            case COMPLEX_DOUBLE:
                return new ComplexDoubleLargeArray(length, zeroNativeMemory);
            case STRING:
                return new StringLargeArray(length, 100, zeroNativeMemory);
            case OBJECT:
                return new ObjectLargeArray(length, 100, zeroNativeMemory);
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
    }

    /**
     * Generates a random LargeArray
     *
     * @param type   the type of LargeArray
     * @param length number of elements
     *
     * @return random LargeArray
     */
    public static LargeArray generateRandom(LargeArrayType type, long length)
    {
        LargeArray res = create(type, length, false);
        Random rand = new Random();
        switch (type) {
            case LOGIC: {
                for (long i = 0; i < length; i++) {
                    res.setBoolean(i, rand.nextBoolean());
                }
                break;
            }
            case BYTE:
            case UNSIGNED_BYTE: {
                long i;
                int r;
                for (i = 0; i < length - 4; i += 4) {
                    r = rand.nextInt();
                    res.setByte(i, (byte) (r));
                    res.setByte(i + 1, (byte) (r >> Byte.SIZE));
                    res.setByte(i + 2, (byte) (r >> (2 * Byte.SIZE)));
                    res.setByte(i + 3, (byte) (r >> (3 * Byte.SIZE)));
                }
                r = rand.nextInt();
                for (; i < length; i++) {
                    res.setByte(i, (byte) (r >> ((length - 1 - i) * Byte.SIZE)));
                }
                break;
            }
            case SHORT: {
                long i;
                int r;
                for (i = 0; i < length - 2; i += 2) {
                    r = rand.nextInt();
                    res.setShort(i, (short) (r));
                    res.setShort(i + 1, (short) (r >> Short.SIZE));
                }
                r = rand.nextInt();
                for (; i < length; i++) {
                    res.setShort(i, (short) (r >> ((length - 1 - i) * Short.SIZE)));
                }
                break;
            }
            case INT: {
                for (long i = 0; i < length; i++) {
                    res.setInt(i, rand.nextInt());
                }
                break;
            }
            case LONG: {
                for (long i = 0; i < length; i++) {
                    res.setLong(i, rand.nextLong());
                }
                break;
            }
            case FLOAT: {
                for (long i = 0; i < length; i++) {
                    res.setFloat(i, rand.nextFloat());
                }
                break;
            }
            case DOUBLE: {
                for (long i = 0; i < length; i++) {
                    res.setDouble(i, rand.nextDouble());
                }
                break;
            }
            case COMPLEX_FLOAT: {
                ComplexFloatLargeArray res_c = (ComplexFloatLargeArray) res;
                float[] elem_res = new float[2];
                for (long i = 0; i < length; i++) {
                    elem_res[0] = rand.nextFloat();
                    elem_res[1] = rand.nextFloat();
                    res_c.setComplexFloat(i, elem_res);
                }
                break;
            }
            case COMPLEX_DOUBLE: {
                ComplexDoubleLargeArray res_c = (ComplexDoubleLargeArray) res;
                double[] elem_res = new double[2];
                for (long i = 0; i < length; i++) {
                    elem_res[0] = rand.nextDouble();
                    elem_res[1] = rand.nextDouble();
                    res_c.setComplexDouble(i, elem_res);
                }
                break;
            }
            case STRING: {
                for (long i = 0; i < length; i++) {
                    res.setFloat(i, rand.nextFloat());
                }
                break;
            }
            case OBJECT: {
                for (long i = 0; i < length; i++) {
                    res.set(i, rand.nextFloat());
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid array type.");
        }
        return res;
    }

    /**
     * Converts LargeArray to a given type.
     *
     * @param src  the source array
     * @param type the type of LargeArray
     *
     * @return LargeArray of a specified type
     */
    public static LargeArray convert(final LargeArray src, final LargeArrayType type)
    {
        if (src.getType() == type) {
            return src;
        }
        if (src.isConstant()) {
            switch (type) {
                case LOGIC:
                case BYTE:
                case UNSIGNED_BYTE:
                case SHORT:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                case STRING:
                case OBJECT:
                    return createConstant(type, src.length(), src.get(0));
                case COMPLEX_FLOAT: {
                    if (src.getType() == LargeArrayType.COMPLEX_DOUBLE) {
                        return new ComplexFloatLargeArray(src.length(), ((ComplexDoubleLargeArray) src).getComplexFloat(0), true);
                    } else {
                        return new ComplexFloatLargeArray(src.length(), new float[]{src.getFloat(0), 0f}, true);
                    }
                }
                case COMPLEX_DOUBLE:
                    if (src.getType() == LargeArrayType.COMPLEX_FLOAT) {
                        return new ComplexDoubleLargeArray(src.length(), ((ComplexFloatLargeArray) src).getComplexDouble(0), true);
                    } else {
                        return new ComplexDoubleLargeArray(src.length(), new double[]{src.getDouble(0), 0}, true);
                    }
                default:
                    throw new IllegalArgumentException("Invalid array type.");
            }
        } else {
            long length = src.length;
            final LargeArray out = create(type, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                switch (type) {
                    case LOGIC:
                        for (long i = 0; i < length; i++) {
                            out.setByte(i, src.getByte(i) != 0 ? (byte) 1 : (byte) 0);
                        }
                        break;
                    case BYTE:
                        for (long i = 0; i < length; i++) {
                            out.setByte(i, src.getByte(i));
                        }
                        break;
                    case UNSIGNED_BYTE:
                        for (long i = 0; i < length; i++) {
                            out.setUnsignedByte(i, src.getUnsignedByte(i));
                        }
                        break;
                    case SHORT:
                        for (long i = 0; i < length; i++) {
                            out.setShort(i, src.getShort(i));
                        }
                        break;
                    case INT:
                        for (long i = 0; i < length; i++) {
                            out.setInt(i, src.getInt(i));
                        }
                        break;
                    case LONG:
                        for (long i = 0; i < length; i++) {
                            out.setLong(i, src.getLong(i));
                        }
                        break;
                    case FLOAT:
                        for (long i = 0; i < length; i++) {
                            out.setFloat(i, src.getFloat(i));
                        }
                        break;
                    case DOUBLE:
                        for (long i = 0; i < length; i++) {
                            out.setDouble(i, src.getDouble(i));
                        }
                        break;
                    case COMPLEX_FLOAT:
                        if (src.getType() == LargeArrayType.COMPLEX_DOUBLE) {
                            for (long i = 0; i < length; i++) {
                                ((ComplexFloatLargeArray) out).setComplexFloat(i, ((ComplexDoubleLargeArray) src).getComplexFloat(i));
                            }
                        } else {
                            for (long i = 0; i < length; i++) {
                                out.setFloat(i, src.getFloat(i));
                            }
                        }
                        break;
                    case COMPLEX_DOUBLE:
                        if (src.getType() == LargeArrayType.COMPLEX_FLOAT) {
                            for (long i = 0; i < length; i++) {
                                ((ComplexDoubleLargeArray) out).setComplexDouble(i, ((ComplexFloatLargeArray) src).getComplexDouble(i));
                            }
                        } else {
                            for (long i = 0; i < length; i++) {
                                out.setDouble(i, src.getDouble(i));
                            }
                        }
                        break;
                    case STRING:
                        for (long i = 0; i < length; i++) {
                            out.set(i, src.get(i).toString());
                        }
                        break;
                    case OBJECT:
                        for (long i = 0; i < length; i++) {
                            out.set(i, src.get(i));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid array type.");
                }
            } else {
                long k = length / nthreads;
                Future[] threads = new Future[nthreads];
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
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setByte(i, src.getByte(i) != 0 ? (byte) 1 : (byte) 0);
                                    }
                                    break;
                                case BYTE:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setByte(i, src.getByte(i));
                                    }
                                    break;
                                case UNSIGNED_BYTE:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setUnsignedByte(i, src.getUnsignedByte(i));
                                    }
                                    break;
                                case SHORT:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setShort(i, src.getShort(i));
                                    }
                                    break;
                                case INT:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setInt(i, src.getInt(i));
                                    }
                                    break;
                                case LONG:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setLong(i, src.getLong(i));
                                    }
                                    break;
                                case FLOAT:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setFloat(i, src.getFloat(i));
                                    }
                                    break;
                                case DOUBLE:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.setDouble(i, src.getDouble(i));
                                    }
                                    break;
                                case COMPLEX_FLOAT:
                                    if (src.getType() == LargeArrayType.COMPLEX_DOUBLE) {
                                        for (long i = firstIdx; i < lastIdx; i++) {
                                            ((ComplexFloatLargeArray) out).setComplexFloat(i, ((ComplexDoubleLargeArray) src).getComplexFloat(i));
                                        }
                                    } else {
                                        for (long i = firstIdx; i < lastIdx; i++) {
                                            out.setFloat(i, src.getFloat(i));
                                        }
                                    }
                                    break;
                                case COMPLEX_DOUBLE:
                                    if (src.getType() == LargeArrayType.COMPLEX_FLOAT) {
                                        for (long i = firstIdx; i < lastIdx; i++) {
                                            ((ComplexDoubleLargeArray) out).setComplexDouble(i, ((ComplexFloatLargeArray) src).getComplexDouble(i));
                                        }
                                    } else {
                                        for (long i = firstIdx; i < lastIdx; i++) {
                                            out.setDouble(i, src.getDouble(i));
                                        }
                                    }
                                    break;
                                case STRING:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.set(i, src.get(i).toString());
                                    }
                                    break;
                                case OBJECT:
                                    for (long i = firstIdx; i < lastIdx; i++) {
                                        out.set(i, src.get(i));
                                    }
                                    break;
                                default:
                                    throw new IllegalArgumentException("Invalid array type.");
                            }
                        }
                    });

                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    switch (type) {
                        case LOGIC:
                            for (long i = 0; i < length; i++) {
                                out.setByte(i, src.getByte(i) != 0 ? (byte) 1 : (byte) 0);
                            }
                            break;
                        case BYTE:
                            for (long i = 0; i < length; i++) {
                                out.setByte(i, src.getByte(i));
                            }
                            break;
                        case UNSIGNED_BYTE:
                            for (long i = 0; i < length; i++) {
                                out.setUnsignedByte(i, src.getUnsignedByte(i));
                            }
                            break;
                        case SHORT:
                            for (long i = 0; i < length; i++) {
                                out.setShort(i, src.getShort(i));
                            }
                            break;
                        case INT:
                            for (long i = 0; i < length; i++) {
                                out.setInt(i, src.getInt(i));
                            }
                            break;
                        case LONG:
                            for (long i = 0; i < length; i++) {
                                out.setLong(i, src.getLong(i));
                            }
                            break;
                        case FLOAT:
                            for (long i = 0; i < length; i++) {
                                out.setFloat(i, src.getFloat(i));
                            }
                            break;
                        case DOUBLE:
                            for (long i = 0; i < length; i++) {
                                out.setDouble(i, src.getDouble(i));
                            }
                            break;
                        case COMPLEX_FLOAT:
                            if (src.getType() == LargeArrayType.COMPLEX_DOUBLE) {
                                for (long i = 0; i < length; i++) {
                                    ((ComplexFloatLargeArray) out).setComplexFloat(i, ((ComplexDoubleLargeArray) src).getComplexFloat(i));
                                }
                            } else {
                                for (long i = 0; i < length; i++) {
                                    out.setFloat(i, src.getFloat(i));
                                }
                            }
                            break;
                        case COMPLEX_DOUBLE:
                            if (src.getType() == LargeArrayType.COMPLEX_FLOAT) {
                                for (long i = 0; i < length; i++) {
                                    ((ComplexDoubleLargeArray) out).setComplexDouble(i, ((ComplexFloatLargeArray) src).getComplexDouble(i));
                                }
                            } else {
                                for (long i = 0; i < length; i++) {
                                    out.setDouble(i, src.getDouble(i));
                                }
                            }
                            break;
                        case STRING:
                            for (long i = 0; i < length; i++) {
                                out.set(i, src.get(i).toString());
                            }
                            break;
                        case OBJECT:
                            for (long i = 0; i < length; i++) {
                                out.set(i, src.get(i));
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid array type.");
                    }
                }
            }
            return out;
        }
    }

    /**
     * Returns all elements of the specified source array for which the corresponding mask element is equal to 1.
     *
     * @param src  the source array
     * @param mask the mask array
     * 
     * @return selection of elements from the source array
     */
    public static LargeArray select(final LargeArray src, final LogicLargeArray mask)
    {
        if (src.length != mask.length) {
            throw new IllegalArgumentException("src.length != mask.length");
        }
        long length = src.length;
        long count = 0;
        int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
        long k = length / nthreads;
        Future[] futures = new Future[nthreads];
        for (int j = 0; j < nthreads; j++) {
            final long firstIdx = j * k;
            final long lastIdx = (j == nthreads - 1) ? length : firstIdx + k;
            futures[j] = ConcurrencyUtils.submit(new Callable()
            {
                @Override
                public Long call()
                {
                    long count = 0;
                    for (long k = firstIdx; k < lastIdx; k++) {
                        if (mask.getByte(k) == 1) count++;
                    }
                    return count;
                }
            });
        }
        try {
            for (int j = 0; j < nthreads; j++) {
                count += (Long) (futures[j].get());
            }
        } catch (InterruptedException | ExecutionException ex) {
            for (long j = 0; j < length; j++) {
                if (mask.getByte(j) == 1) count++;
            }
        }

        if (count <= 0) return null;

        LargeArray res = create(src.getType(), count, false);
        k = 0;
        for (long j = 0; j < length; j++) {
            if (mask.getByte(j) == 1) {
                res.set(k++, src.get(j));
            }
        }

        return res;
    }
}
