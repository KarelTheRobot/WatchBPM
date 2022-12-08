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
 * Arithmetical operations on LargeArrays.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public class LargeArrayArithmetics
{

    private LargeArrayArithmetics()
    {
    }

    /**
     * Complex sine.
     * 
     * @param a complex number
     * 
     * @return sin(a)
     */
    public static float[] complexSin(float[] a)
    {
        float[] res = new float[2];
        res[0] = (float) (FastMath.sin(a[0]) * FastMath.cosh(a[1]));
        res[1] = (float) (FastMath.cos(a[0]) * FastMath.sinh(a[1]));
        return res;
    }

    /**
     * Complex sine.
     * 
     * @param a complex number
     * 
     * @return sin(a)
     */
    public static double[] complexSin(double[] a)
    {
        double[] res = new double[2];
        res[0] = FastMath.sin(a[0]) * FastMath.cosh(a[1]);
        res[1] = FastMath.cos(a[0]) * FastMath.sinh(a[1]);
        return res;
    }

    /**
     * Complex cosine.
     * 
     * @param a complex number
     * 
     * @return cos(a)
     */
    public static float[] complexCos(float[] a)
    {
        float[] res = new float[2];
        res[0] = (float) (FastMath.cos(a[0]) * FastMath.cosh(a[1]));
        res[1] = (float) (-FastMath.sin(a[0]) * FastMath.sinh(a[1]));
        return res;
    }

    /**
     * Complex cosine.
     * 
     * @param a complex number
     * 
     * @return cos(a)
     */
    public static double[] complexCos(double[] a)
    {
        double[] res = new double[2];
        res[0] = FastMath.cos(a[0]) * FastMath.cosh(a[1]);
        res[1] = -FastMath.sin(a[0]) * FastMath.sinh(a[1]);
        return res;
    }

    /**
     * Complex tangent.
     * 
     * @param a complex number
     * 
     * @return tan(a)
     */
    public static float[] complexTan(float[] a)
    {
        float[] s = complexSin(a);
        float[] c = complexCos(a);
        return complexDiv(s, c);
    }

    /**
     * Complex tangent.
     * 
     * @param a complex number
     * 
     * @return tan(a)
     */
    public static double[] complexTan(double[] a)
    {
        double[] s = complexSin(a);
        double[] c = complexCos(a);
        return complexDiv(s, c);
    }

    /**
     * Complex addition.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a+b
     */
    public static float[] complexAdd(float[] a, float[] b)
    {
        float[] res = new float[2];
        res[0] = a[0] + b[0];
        res[1] = a[1] + b[1];
        return res;
    }

    /**
     * Complex addition.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a+b
     */
    public static double[] complexAdd(double[] a, double[] b)
    {
        double[] res = new double[2];
        res[0] = a[0] + b[0];
        res[1] = a[1] + b[1];
        return res;
    }

    /**
     * Complex subtraction.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a-b
     */
    public static float[] complexDiff(float[] a, float[] b)
    {
        float[] res = new float[2];
        res[0] = a[0] - b[0];
        res[1] = a[1] - b[1];
        return res;
    }

    /**
     * Complex subtraction.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a-b
     */
    public static double[] complexDiff(double[] a, double[] b)
    {
        double[] res = new double[2];
        res[0] = a[0] - b[0];
        res[1] = a[1] - b[1];
        return res;
    }

    /**
     * Complex multiplication.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a*b
     */
    public static float[] complexMult(float[] a, float[] b)
    {
        float[] res = new float[2];
        res[0] = a[0] * b[0] - a[1] * b[1];
        res[1] = a[1] * b[0] + a[0] * b[1];
        return res;
    }

    /**
     * Complex multiplication.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a*b
     */
    public static double[] complexMult(double[] a, double[] b)
    {
        double[] res = new double[2];
        res[0] = a[0] * b[0] - a[1] * b[1];
        res[1] = a[1] * b[0] + a[0] * b[1];
        return res;
    }

    /**
     * Complex division.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a/b
     */
    public static float[] complexDiv(float[] a, float[] b)
    {
        float r = b[0] * b[0] + b[1] * b[1];
        float[] res = new float[2];
        res[0] = (a[0] * b[0] + a[1] * b[1]) / r;
        res[1] = (a[1] * b[0] - a[0] * b[1]) / r;
        return res;
    }

    /**
     * Complex division.
     * 
     * @param a complex number
     * @param b complex number
     * 
     * @return a/b
     */
    public static double[] complexDiv(double[] a, double[] b)
    {
        double r = b[0] * b[0] + b[1] * b[1];
        double[] res = new double[2];
        res[0] = (a[0] * b[0] + a[1] * b[1]) / r;
        res[1] = (a[1] * b[0] - a[0] * b[1]) / r;
        return res;
    }

    /**
     * Complex power.
     * 
     * @param a complex number
     * @param n exponent
     * 
     * @return a^n
     */
    public static float[] complexPow(float[] a, double n)
    {
        float[] res = new float[2];
        double mod = FastMath.pow(complexAbs(a), n);
        double arg = FastMath.atan2(a[1], a[0]);
        res[0] = (float) (mod * FastMath.cos(n * arg));
        res[1] = (float) (mod * FastMath.sin(n * arg));
        return res;
    }

    /**
     * Complex power.
     * 
     * @param a complex number
     * @param n exponent
     * 
     * @return a^n
     */
    public static double[] complexPow(double[] a, double n)
    {
        double[] res = new double[2];
        double mod = FastMath.pow(complexAbs(a), n);
        double arg = FastMath.atan2(a[1], a[0]);
        res[0] = mod * FastMath.cos(n * arg);
        res[1] = mod * FastMath.sin(n * arg);
        return res;
    }

    /**
     * Complex power.
     * 
     * @param a complex number
     * @param n exponent
     * 
     * @return a^n
     */
    public static float[] complexPow(float[] a, float[] n)
    {
        return complexExp(complexMult(n, complexLog(a)));
    }

    /**
     * Complex power.
     * 
     * @param a complex number
     * @param n exponent
     * 
     * @return a^n
     */
    public static double[] complexPow(double[] a, double[] n)
    {
        return complexExp(complexMult(n, complexLog(a)));
    }

    /**
     * Complex square root.
     * 
     * @param a complex number
     * 
     * @return sqrt(a)
     */
    public static float[] complexSqrt(float[] a)
    {
        float[] res = new float[2];
        double mod = complexAbs(a);
        res[0] = (float) FastMath.sqrt(FastMath.max(0, (a[0] + mod) / 2.0));
        res[1] = (float) (FastMath.signum(a[1]) * FastMath.sqrt(FastMath.max(0, (-a[0] + mod) / 2.0)));
        return res;
    }

    /**
     * Complex square root.
     * 
     * @param a complex number
     * 
     * @return sqrt(a)
     */
    public static double[] complexSqrt(double[] a)
    {
        double[] res = new double[2];
        double mod = complexAbs(a);
        res[0] = FastMath.sqrt(FastMath.max(0, (a[0] + mod) / 2.0));
        res[1] = FastMath.signum(a[1]) * FastMath.sqrt(FastMath.max(0, (-a[0] + mod) / 2.0));
        return res;
    }

    /**
     * Complex absolute value.
     * 
     * @param a complex number
     * 
     * @return abs(a)
     */
    public static float complexAbs(float[] a)
    {
        return (float) ((a[1] == 0.0f) ? a[0] : FastMath.sqrt(a[0] * a[0] + a[1] * a[1]));
    }

    /**
     * Complex absolute value.
     * 
     * @param a complex number
     * 
     * @return abs(a)
     */
    public static double complexAbs(double[] a)
    {
        return (a[1] == 0.0f) ? a[0] : FastMath.sqrt(a[0] * a[0] + a[1] * a[1]);
    }

    /**
     * Complex natural logarithm.
     * 
     * @param a complex number
     * 
     * @return log(a)
     */
    public static float[] complexLog(float[] a)
    {
        float[] res = new float[2];
        double mod = complexAbs(a);
        double arg = FastMath.atan2(a[1], a[0]);
        res[0] = (float) FastMath.log(mod);
        res[1] = (float) arg;
        return res;
    }

    /**
     * Complex natural logarithm.
     * 
     * @param a complex number
     * 
     * @return log(a)
     */
    public static double[] complexLog(double[] a)
    {
        double[] res = new double[2];
        double mod = complexAbs(a);
        double arg = FastMath.atan2(a[1], a[0]);
        res[0] = FastMath.log(mod);
        res[1] = arg;
        return res;
    }

    /**
     * Complex base-10 logarithm.
     * 
     * @param a complex number
     * 
     * @return log10(a)
     */
    public static float[] complexLog10(float[] a)
    {
        float[] res = new float[2];
        final double scale = FastMath.log(10.0);
        double mod = complexAbs(a);
        double arg = FastMath.atan2(a[1], a[0]) / scale;
        res[0] = (float) ((FastMath.log(mod) / scale));
        res[1] = (float) arg;
        return res;
    }

    /**
     * Complex base-10 logarithm.
     * 
     * @param a complex number
     * 
     * @return log10(a)
     */
    public static double[] complexLog10(double[] a)
    {
        double[] res = new double[2];
        final double scale = FastMath.log(10.0);
        double mod = complexAbs(a);
        double arg = FastMath.atan2(a[1], a[0]) / scale;
        res[0] = (FastMath.log(mod) / scale);
        res[1] = arg;
        return res;
    }

    /**
     * Complex exponent.
     * 
     * @param a complex number
     * 
     * @return exp(a)
     */
    public static float[] complexExp(float[] a)
    {
        float[] res = new float[2];
        res[0] = (float) (FastMath.exp(a[0]) * FastMath.cos(a[1]));
        res[1] = (float) (FastMath.exp(a[0]) * FastMath.sin(a[1]));
        return res;
    }

    /**
     * Complex exponent.
     * 
     * @param a complex number
     * 
     * @return exp(a)
     */
    public static double[] complexExp(double[] a)
    {
        double[] res = new double[2];
        res[0] = FastMath.exp(a[0]) * FastMath.cos(a[1]);
        res[1] = FastMath.exp(a[0]) * FastMath.sin(a[1]);
        return res;
    }

    /**
     * Complex inverse sine.
     * 
     * @param a complex number
     * 
     * @return asin(a)
     */
    public static float[] complexAsin(float[] a)
    {
        float[] res;
        float[] i = new float[]{0, 1};
        float[] mi = new float[]{0, -1};
        res = complexMult(a, a);
        res[0] = 1 - res[0];
        res[1] = 1 - res[1];
        res = complexLog(res);
        i = complexMult(i, a);
        res[0] += i[0];
        res[1] += i[1];
        return complexMult(mi, res);
    }

    /**
     * Complex inverse sine.
     * 
     * @param a complex number
     * 
     * @return asin(a)
     */
    public static double[] complexAsin(double[] a)
    {
        double[] res;
        double[] i = new double[]{0, 1};
        double[] mi = new double[]{0, -1};
        res = complexMult(a, a);
        res[0] = 1 - res[0];
        res[1] = 1 - res[1];
        res = complexLog(res);
        i = complexMult(i, a);
        res[0] += i[0];
        res[1] += i[1];
        return complexMult(mi, res);
    }

    /**
     * Complex inverse cosine.
     * 
     * @param a complex number
     * 
     * @return acos(a)
     */
    public static float[] complexAcos(float[] a)
    {
        float[] res;
        float[] i = new float[]{0, 1};
        float[] mi = new float[]{0, -1};
        res = complexMult(a, a);
        res[0] = 1 - res[0];
        res[1] = 1 - res[1];
        res = complexMult(i, res);
        res[0] += a[0];
        res[1] += a[1];
        res = complexLog(res);
        return complexMult(mi, res);
    }

    /**
     * Complex inverse cosine.
     * 
     * @param a complex number
     * 
     * @return acos(a)
     */
    public static double[] complexAcos(double[] a)
    {
        double[] res;
        double[] i = new double[]{0, 1};
        double[] mi = new double[]{0, -1};
        res = complexMult(a, a);
        res[0] = 1 - res[0];
        res[1] = 1 - res[1];
        res = complexMult(i, res);
        res[0] += a[0];
        res[1] += a[1];
        res = complexLog(res);
        return complexMult(mi, res);
    }

    /**
     * Complex inverse tangent.
     * 
     * @param a complex number
     * 
     * @return atan(a)
     */
    public static float[] complexAtan(float[] a)
    {
        float[] res = new float[2];
        float[] tmp = new float[2];
        float[] i = new float[]{0, 1};
        res[0] = i[0] + a[0];
        res[1] = i[1] + a[1];
        tmp[0] = i[0] - a[0];
        tmp[1] = i[1] - a[1];
        res = complexLog(complexDiv(res, tmp));
        i[1] /= 2.0;
        return complexMult(i, res);
    }

    /**
     * Complex inverse tangent.
     * 
     * @param a complex number
     * 
     * @return atan(a)
     */
    public static double[] complexAtan(double[] a)
    {
        double[] res = new double[2];
        double[] tmp = new double[2];
        double[] i = new double[]{0, 1};
        res[0] = i[0] + a[0];
        res[1] = i[1] + a[1];
        tmp[0] = i[0] - a[0];
        tmp[1] = i[1] - a[1];
        res = complexLog(complexDiv(res, tmp));
        i[1] /= 2.0;
        return complexMult(i, res);
    }

    /**
     * Complex axpy operation.
     * 
     * @param a complex number
     * @param x complex number
     * @param b complex number
     * 
     * @return atan(a)
     */
    public static float[] complexAxpy(float[] a, float[] x, float[] b)
    {
        return complexAdd(complexMult(a, x), b);
    }

    /**
     * Complex axpy operation.
     * 
     * @param a complex number
     * @param x complex number
     * @param b complex number
     * 
     * @return atan(a)
     */
    public static double[] complexAxpy(double[] a, double[] x, double[] b)
    {
        return complexAdd(complexMult(a, x), b);
    }

    /**
     * Addition of two LargeArrays.
     * 
     * @param a input array
     * @param b input array
     * 
     * @return a+b
     */
    public static LargeArray add(final LargeArray a, final LargeArray b)
    {
        return add(a, b, null);
    }

    /**
     * Addition of two LargeArrays.
     * 
     * @param a       input array
     * @param b       input array
     * @param outType output type
     * 
     * @return a+b
     */
    public static LargeArray add(final LargeArray a, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.ADD.evaluate(outType, new LargeArray[]{a, b});
    }

    /**
     * Computes ax+b.
     * 
     * @param a input array (constant)
     * @param x input array
     * @param b input array (constant)
     * 
     * @return ax+b
     */
    public static LargeArray axpy(final LargeArray a, final LargeArray x, final LargeArray b)
    {
        return axpy(a, x, b, null);
    }

    /**
     * Computes ax+b.
     * 
     * @param a       input array (constant)
     * @param x       input array
     * @param b       input array (constant)
     * @param outType output type
     * 
     * @return a+b
     */
    public static LargeArray axpy(final LargeArray a, final LargeArray x, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.AXPY.evaluate(outType, new LargeArray[]{a, x, b});
    }

    /**
     * Subtraction of two LargeArrays.
     * 
     * @param a input array
     * @param b input array
     * 
     * @return a-b
     */
    public static LargeArray diff(final LargeArray a, final LargeArray b)
    {
        return diff(a, b, null);
    }

    /**
     * Subtraction of two LargeArrays.
     * 
     * @param a       input array
     * @param b       input array
     * @param outType output type
     * 
     * @return a-b
     */
    public static LargeArray diff(final LargeArray a, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.DIFF.evaluate(outType, new LargeArray[]{a, b});
    }

    /**
     * Multiplication of two LargeArrays.
     * 
     * @param a input array
     * @param b input array
     * 
     * @return a*b
     */
    public static LargeArray mult(final LargeArray a, final LargeArray b)
    {
        return mult(a, b, null);
    }

    /**
     * Multiplication of two LargeArrays.
     * 
     * @param a       input array
     * @param b       input array
     * @param outType output type
     * 
     * @return a*b
     */
    public static LargeArray mult(final LargeArray a, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.MULT.evaluate(outType, new LargeArray[]{a, b});
    }

    /**
     * Division of two LargeArrays.
     * 
     * @param a input array
     * @param b input array
     * 
     * @return a/b
     */
    public static LargeArray div(final LargeArray a, final LargeArray b)
    {
        return div(a, b, null);
    }

    /**
     * Division of two LargeArrays.
     * 
     * @param a       input array
     * @param b       input array
     * @param outType output type
     * 
     * @return a/b
     */
    public static LargeArray div(final LargeArray a, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.DIV.evaluate(outType, new LargeArray[]{a, b});
    }

    /**
     * Power of LargeArray.
     * 
     * @param a input array
     * @param n exponent
     * 
     * @return a^n
     */
    public static LargeArray pow(final LargeArray a, final double n)
    {
        return pow(a, n, null);
    }

    /**
     * Power of LargeArray.
     * 
     * @param a       input array
     * @param n       exponent
     * @param outType output type
     * 
     * @return a^n
     */
    public static LargeArray pow(final LargeArray a, final double n, final LargeArrayType outType)
    {
        LargeArrayType ntype = (a.getType().isIntegerNumericType() || a.getType() == LargeArrayType.FLOAT) ? LargeArrayType.FLOAT : LargeArrayType.DOUBLE;
        LargeArray narray = LargeArrayUtils.createConstant(ntype, a.length(), n);
        return LargeArrayOperator.POW.evaluate(outType, new LargeArray[]{a, narray});
    }

    /**
     * Power of two LargeArrays.
     * 
     * @param a input array
     * @param b input array
     * 
     * @return a^b
     */
    public static LargeArray pow(final LargeArray a, final LargeArray b)
    {
        return pow(a, b, null);
    }

    /**
     * Power of two LargeArrays.
     * 
     * @param a       input array
     * @param b       input array
     * @param outType output type
     *
     * @return a^b
     */
    public static LargeArray pow(final LargeArray a, final LargeArray b, final LargeArrayType outType)
    {
        return LargeArrayOperator.POW.evaluate(outType, new LargeArray[]{a, b});
    }

    /**
     * Negation of LargeArray.
     * 
     * @param a input array
     * 
     * @return -a
     */
    public static LargeArray neg(final LargeArray a)
    {
        return neg(a, null);
    }

    /**
     * Negation of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return -a
     */
    public static LargeArray neg(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.NEG.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Square root of LargeArray. For complex arrays the principal square root is returned.
     * 
     * @param a input array
     * 
     * @return sqrt(a)
     */
    public static LargeArray sqrt(final LargeArray a)
    {
        return sqrt(a, null);
    }

    /**
     * Square root of LargeArray. For complex arrays the principal square root is returned.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return sqrt(a)
     */
    public static LargeArray sqrt(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.SQRT.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Natural logarithm of LargeArray. For complex arrays the principal value logarithm is returned.
     * 
     * @param a input array
     * 
     * @return log(a)
     */
    public static LargeArray log(final LargeArray a)
    {
        return log(a, null);
    }

    /**
     * Natural logarithm of LargeArray. For complex arrays the principal value logarithm is returned.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return log(a)
     */
    public static LargeArray log(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.LOG.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Base-10 logarithm of LargeArray. For complex arrays the principal value logarithm is returned.
     * 
     * @param a input array
     * 
     * @return log10(a)
     */
    public static LargeArray log10(final LargeArray a)
    {
        return log10(a, null);
    }

    /**
     * Base-10 logarithm of LargeArray. For complex arrays the principal value logarithm is returned.
     * 
     * @param a       input array
     * 
     * @param outType output type
     * 
     * @return log10(a)
     */
    public static LargeArray log10(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.LOG10.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Exponent of LargeArray.
     * 
     * @param a input array
     * 
     * @return exp(a)
     */
    public static LargeArray exp(final LargeArray a)
    {
        return exp(a, null);
    }

    /**
     * Exponent of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return exp(a)
     */
    public static LargeArray exp(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.EXP.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Absolute value of LargeArray.
     * 
     * @param a input array
     * 
     * @return |a|
     */
    public static LargeArray abs(final LargeArray a)
    {
        return abs(a, null);
    }

    /**
     * Absolute value of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return |a|
     */
    public static LargeArray abs(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.ABS.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Sine of LargeArray.
     * 
     * @param a input array
     * 
     * @return sin(a)
     */
    public static LargeArray sin(final LargeArray a)
    {
        return sin(a, null);
    }

    /**
     * Sine of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return sin(a)
     */
    public static LargeArray sin(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.SIN.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Cosine of LargeArray.
     * 
     * @param a input array
     * 
     * @return cos(a)
     */
    public static LargeArray cos(final LargeArray a)
    {
        return cos(a, null);
    }

    /**
     * Cosine of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return cos(a)
     */
    public static LargeArray cos(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.COS.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Tangent of LargeArray.
     * 
     * @param a input array
     * 
     * @return tan(a)
     */
    public static LargeArray tan(final LargeArray a)
    {
        return tan(a, null);
    }

    /**
     * Tangent of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return tan(a)
     */
    public static LargeArray tan(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.TAN.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Inverse sine of LargeArray.
     * 
     * @param a input array
     * 
     * @return asin(a)
     */
    public static LargeArray asin(final LargeArray a)
    {
        return asin(a, null);
    }

    /**
     * Inverse sine of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return asin(a)
     */
    public static LargeArray asin(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.ASIN.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Inverse cosine of LargeArray.
     * 
     * @param a input array
     * 
     * @return acos(a)
     */
    public static LargeArray acos(final LargeArray a)
    {
        return acos(a, null);
    }

    /**
     * Inverse cosine of LargeArray.
     * 
     * @param a       input array
     * 
     * @param outType output type
     * 
     * @return acos(a)
     */
    public static LargeArray acos(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.ACOS.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Inverse tangent of LargeArray.
     * 
     * @param a input array
     * 
     * @return atan(a)
     */
    public static LargeArray atan(final LargeArray a)
    {
        return atan(a, null);
    }

    /**
     * Inverse tangent of LargeArray.
     * 
     * @param a       input array
     * @param outType output type
     * 
     * @return atan(a)
     */
    public static LargeArray atan(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.ATAN.evaluate(outType, new LargeArray[]{a});
    }

    /**
     * Signum of LargeArray.
     * 
     * @param a input array
     * 
     * @return signum(a)
     */
    public static LargeArray signum(final LargeArray a)
    {
        LargeArrayType outType = LargeArrayType.BYTE;
        return signum(a, outType);
    }

    /**
     * Signum of LargeArray.
     * 
     * @param a       input array
     * 
     * @param outType output type
     * 
     * @return signum(a)
     */
    public static LargeArray signum(final LargeArray a, final LargeArrayType outType)
    {
        return LargeArrayOperator.SIGNUM.evaluate(outType, new LargeArray[]{a});
    }

}
