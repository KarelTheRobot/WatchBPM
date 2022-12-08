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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * Arithmetical operations on LargeArrays.
 *
 * @author Piotr Wendykier (p.wendykier@uksw.edu.pl)
 */
public enum LargeArrayOperator
{
    /**
     * Absolute value
     */
    ABS()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, true);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.abs(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.abs(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return FastMath.abs(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return FastMath.abs(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.abs(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.abs(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return new float[]{LargeArrayArithmetics.complexAbs(x), 0};
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return new double[]{LargeArrayArithmetics.complexAbs(x), 0};
            };
            Function<float[], Float> complexFloatRealFun = (x) -> {
                return LargeArrayArithmetics.complexAbs(x);
            };
            Function<double[], Double> complexDoubleRealFun = (x) -> {
                return LargeArrayArithmetics.complexAbs(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, complexFloatRealFun, complexDoubleRealFun);
        }
        //</editor-fold>
    },
    /**
     * The inverse of the cosine
     */
    ACOS()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.acos(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.acos(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.acos(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.acos(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.acos(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.acos(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexAcos(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexAcos(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Addition
     */
    ADD()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length < 2) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                int ndata = in.length;
                byte res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                int ndata = in.length;
                short res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;

            };
            Function<int[], Integer> intFun = (in) -> {
                int ndata = in.length;
                int res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;

            };
            Function<long[], Long> longFun = (in) -> {
                int ndata = in.length;
                long res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;
            };
            Function<float[], Float> floatFun = (in) -> {
                int ndata = in.length;
                float res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;
            };
            Function<double[], Double> doubleFun = (in) -> {
                int ndata = in.length;
                double res = 0;
                for (int i = 0; i < ndata; i++) {
                    res += in[i];
                }
                return res;
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                int ndata = in.length;
                float[] res = {0, 0};
                for (int i = 0; i < ndata; i++) {
                    res = LargeArrayArithmetics.complexAdd(res, in[i]);
                }
                return res;
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                int ndata = in.length;
                double[] res = {0, 0};
                for (int i = 0; i < ndata; i++) {
                    res = LargeArrayArithmetics.complexAdd(res, in[i]);
                }
                return res;
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * The inverse of the sine
     */
    ASIN()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.asin(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.asin(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.asin(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.asin(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.asin(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.asin(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexAsin(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexAsin(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * The inverse of the tangent
     */
    ATAN()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.atan(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.atan(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.atan(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.atan(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.atan(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.atan(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexAtan(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexAtan(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * A*X+Y
     */
    AXPY()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 3) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                return (byte) (in[0] * in[1] + in[2]);
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                return (short) (in[0] * in[1] + in[2]);
            };
            Function<int[], Integer> intFun = (in) -> {
                return in[0] * in[1] + in[2];
            };
            Function<long[], Long> longFun = (in) -> {
                return in[0] * in[1] + in[2];
            };
            Function<float[], Float> floatFun = (in) -> {
                return in[0] * in[1] + in[2];
            };
            Function<double[], Double> doubleFun = (in) -> {
                return in[0] * in[1] + in[2];
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                return LargeArrayArithmetics.complexAxpy(in[0], in[1], in[2]);
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                return LargeArrayArithmetics.complexAxpy(in[0], in[1], in[2]);
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Cosine
     */
    COS()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.cos(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.cos(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.cos(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.cos(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.cos(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.cos(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexCos(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexCos(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Subtraction
     */
    DIFF()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 2) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                return (byte) (in[0] - in[1]);
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                return (short) (in[0] - in[1]);
            };
            Function<int[], Integer> intFun = (in) -> {
                return in[0] - in[1];
            };
            Function<long[], Long> longFun = (in) -> {
                return in[0] - in[1];
            };
            Function<float[], Float> floatFun = (in) -> {
                return in[0] - in[1];
            };
            Function<double[], Double> doubleFun = (in) -> {
                return in[0] - in[1];
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                return LargeArrayArithmetics.complexDiff(in[0], in[1]);
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                return LargeArrayArithmetics.complexDiff(in[0], in[1]);
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Division
     */
    DIV()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 2) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                return (byte) (in[0] / in[1]);
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                return (short) (in[0] / in[1]);
            };
            Function<int[], Integer> intFun = (in) -> {
                return in[0] / in[1];
            };
            Function<long[], Long> longFun = (in) -> {
                return in[0] / in[1];
            };
            Function<float[], Float> floatFun = (in) -> {
                return in[0] / in[1];
            };
            Function<double[], Double> doubleFun = (in) -> {
                return in[0] / in[1];
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                return LargeArrayArithmetics.complexDiv(in[0], in[1]);
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                return LargeArrayArithmetics.complexDiv(in[0], in[1]);
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Exponential
     */
    EXP()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.exp(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.exp(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.exp(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.exp(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.exp(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.exp(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexExp(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexExp(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Natural logarithm
     */
    LOG()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.log(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.log(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.log(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.log(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.log(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.log(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexLog(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexLog(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Logarithm with base 10
     */
    LOG10()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.log10(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.log10(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.log10(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.log10(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.log10(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.log10(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexLog10(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexLog10(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Multiplication
     */
    MULT()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length < 2) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                int ndata = in.length;
                byte res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                int ndata = in.length;
                short res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<int[], Integer> intFun = (in) -> {
                int ndata = in.length;
                int res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<long[], Long> longFun = (in) -> {
                int ndata = in.length;
                long res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<float[], Float> floatFun = (in) -> {
                int ndata = in.length;
                float res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<double[], Double> doubleFun = (in) -> {
                int ndata = in.length;
                double res = 1;
                for (int i = 0; i < ndata; i++) {
                    res *= in[i];
                }
                return res;
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                int ndata = in.length;
                float[] res = {1, 0};
                for (int i = 0; i < ndata; i++) {
                    res = LargeArrayArithmetics.complexMult(res, in[i]);
                }
                return res;
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                int ndata = in.length;
                double[] res = {1, 0};
                for (int i = 0; i < ndata; i++) {
                    res = LargeArrayArithmetics.complexMult(res, in[i]);
                }
                return res;
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Negation
     */
    NEG()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) -x;
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) -x;
            };
            Function<Integer, Integer> intFun = (x) -> {
                return -x;
            };
            Function<Long, Long> longFun = (x) -> {
                return -x;
            };
            Function<Float, Float> floatFun = (x) -> {
                return -x;
            };
            Function<Double, Double> doubleFun = (x) -> {
                return -x;
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return new float[]{-x[0], -x[1]};
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return new double[]{-x[0], -x[1]};
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Power
     */
    POW()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 2) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }
            Function<byte[], Byte> logicByteFun = (in) -> {
                return (byte) FastMath.pow(in[0], in[1]);
            };
            Function<short[], Short> unsignedByteShortFun = (in) -> {
                return (short) FastMath.pow(in[0], in[1]);
            };
            Function<int[], Integer> intFun = (in) -> {
                return (int) FastMath.pow(in[0], in[1]);
            };
            Function<long[], Long> longFun = (in) -> {
                return (long) FastMath.pow(in[0], in[1]);
            };
            Function<float[], Float> floatFun = (in) -> {
                return (float) FastMath.pow(in[0], in[1]);
            };
            Function<double[], Double> doubleFun = (in) -> {
                return FastMath.pow(in[0], in[1]);
            };
            Function<float[][], float[]> complexFloatFun = (in) -> {
                return LargeArrayArithmetics.complexPow(in[0], in[1]);
            };
            Function<double[][], double[]> complexDoubleFun = (in) -> {
                return LargeArrayArithmetics.complexPow(in[0], in[1]);
            };
            return evaluateVarArgFunction(args, outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Signum
     */
    SIGNUM()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (args[0].getType().isComplexNumericType()) {
                throw new IllegalArgumentException("Signum operation is not defined for complex arrays.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, false, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.signum(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.signum(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.signum(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.signum(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.signum(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.signum(x);
            };

            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, null, null, null, null);
        }
        //</editor-fold>
    },
    /**
     * Sine
     */
    SIN()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.sin(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.sin(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.sin(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.sin(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.sin(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.sin(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexSin(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexSin(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Square root
     */
    SQRT()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.sqrt(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.sqrt(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.sqrt(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.sqrt(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.sqrt(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.sqrt(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexSqrt(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexSqrt(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    },
    /**
     * Tangent
     */
    TAN()
    //<editor-fold defaultstate="collapsed"> 
    {
        @Override
        public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args)
        {
            if (outputType != null && !outputType.isNumericType()) {
                throw new IllegalArgumentException("Invalid output type.");
            }
            if (args == null || args.length != 1) {
                throw new IllegalArgumentException("Invalid arguments.");
            }
            if (outputType == null) {
                outputType = selectBestNumericOutputType(args, true, false);
            }

            Function<Byte, Byte> logicByteFun = (x) -> {
                return (byte) FastMath.tan(x);
            };
            Function<Short, Short> unsignedByteShortFun = (x) -> {
                return (short) FastMath.tan(x);
            };
            Function<Integer, Integer> intFun = (x) -> {
                return (int) FastMath.tan(x);
            };
            Function<Long, Long> longFun = (x) -> {
                return (long) FastMath.tan(x);
            };
            Function<Float, Float> floatFun = (x) -> {
                return (float) FastMath.tan(x);
            };
            Function<Double, Double> doubleFun = (x) -> {
                return FastMath.tan(x);
            };
            Function<float[], float[]> complexFloatFun = (x) -> {
                return LargeArrayArithmetics.complexTan(x);
            };
            Function<double[], double[]> complexDoubleFun = (x) -> {
                return LargeArrayArithmetics.complexTan(x);
            };
            return evaluateSingleArgFunction(args[0], outputType, logicByteFun, logicByteFun, unsignedByteShortFun, unsignedByteShortFun, intFun, longFun, floatFun, doubleFun, complexFloatFun, complexDoubleFun, null, null);
        }
        //</editor-fold>
    };

    /**
     * Abstract methods for defining arithmetical operations on large arrays.
     *
     * @param outputType output array type
     * @param args       arguments
     *
     * @return output array
     */
    abstract public LargeArray evaluate(LargeArrayType outputType, LargeArray[] args);

    private static LargeArrayType getLargestNumericType(LargeArray[] args)
    {
        LargeArrayType largestType;
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Invalid arguments.");
        }
        largestType = args[0].getType();
        if (!largestType.isNumericType()) {
            throw new IllegalArgumentException("Only numeric types are supported.");
        }
        for (int i = 1; i < args.length; i++) {
            if (!args[i].getType().isNumericType()) {
                throw new IllegalArgumentException("Only numeric types are supported.");
            }
            if (args[i].getType().ordinal() > largestType.ordinal()) {
                largestType = args[i].getType();
            }
        }
        return largestType;
    }

    private static LargeArrayType selectBestNumericOutputType(LargeArray[] args, boolean floatingPointOperation, boolean complexToReal)
    {
        LargeArrayType largestInType = getLargestNumericType(args);

        if (floatingPointOperation && largestInType.isIntegerNumericType()) {
            if (largestInType.equals(LargeArrayType.LONG)) {
                return LargeArrayType.DOUBLE;
            } else {
                return LargeArrayType.FLOAT;
            }
        }
        if (largestInType == LargeArrayType.COMPLEX_FLOAT && complexToReal) {
            return LargeArrayType.FLOAT;
        } else if (largestInType == LargeArrayType.COMPLEX_DOUBLE && complexToReal) {
            return LargeArrayType.DOUBLE;
        }
        return largestInType;
    }

    private static LargeArray evaluateSingleArgFunction(LargeArray in,
                                                        LargeArrayType outType,
                                                        Function<Byte, Byte> logicFun,
                                                        Function<Byte, Byte> byteFun,
                                                        Function<Short, Short> unsignedByteFun,
                                                        Function<Short, Short> shortFun,
                                                        Function<Integer, Integer> intFun,
                                                        Function<Long, Long> longFun,
                                                        Function<Float, Float> floatFun,
                                                        Function<Double, Double> doubleFun,
                                                        Function<float[], float[]> complexFloatFun,
                                                        Function<double[], double[]> complexDoubleFun,
                                                        Function<float[], Float> complexFloatRealFun,
                                                        Function<double[], Double> complexDoubleRealFun)
    {

        if (in == null) {
            throw new IllegalArgumentException("Input array is null.");
        }
        if (!in.isNumeric()) {
            throw new IllegalArgumentException("Input array is non-numeric.");
        }
        if (!outType.isNumericType()) {
            throw new IllegalArgumentException("Output type is not numeric.");
        }

        switch (outType) {
            case LOGIC:
                return evaluateSingleArgFunctionLogicAndByte(in, outType, logicFun);
            case BYTE:
                return evaluateSingleArgFunctionLogicAndByte(in, outType, byteFun);
            case UNSIGNED_BYTE:
                return evaluateSingleArgFunctionUnsignedByte(in, outType, unsignedByteFun);
            case SHORT:
                return evaluateSingleArgFunctionShort(in, outType, shortFun);
            case INT:
                return evaluateSingleArgFunctionInteger(in, outType, intFun);
            case LONG:
                return evaluateSingleArgFunctionLong(in, outType, longFun);
            case FLOAT:
                if (in.getType() == LargeArrayType.COMPLEX_FLOAT) {
                    return evaluateSingleArgFunctionComplexFloatReal(in, outType, complexFloatRealFun);
                } else {
                    return evaluateSingleArgFunctionFloat(in, outType, floatFun);
                }
            case DOUBLE:
                if (in.getType() == LargeArrayType.COMPLEX_DOUBLE) {
                    return evaluateSingleArgFunctionComplexDoubleReal(in, outType, complexDoubleRealFun);
                } else {
                    return evaluateSingleArgFunctionDouble(in, outType, doubleFun);
                }
            case COMPLEX_FLOAT:
                return evaluateSingleArgFunctionComplexFloat(in, outType, complexFloatFun);
            case COMPLEX_DOUBLE:
                return evaluateSingleArgFunctionComplexDouble(in, outType, complexDoubleFun);
            default:
                throw new IllegalArgumentException("Not supported output type.");
        }
    }

    private static void applyLogicAndByteFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Byte, Byte> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setByte(i, fun.apply(in.getByte(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionLogicAndByte(LargeArray in, LargeArrayType outType, Function<Byte, Byte> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getByte(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyLogicAndByteFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyLogicAndByteFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyLogicAndByteFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyUnsignedByteFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Short, Short> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setUnsignedByte(i, fun.apply(in.getUnsignedByte(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionUnsignedByte(LargeArray in, LargeArrayType outType, Function<Short, Short> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getUnsignedByte(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyUnsignedByteFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyUnsignedByteFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyUnsignedByteFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyShortFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Short, Short> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setShort(i, fun.apply(in.getShort(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionShort(LargeArray in, LargeArrayType outType, Function<Short, Short> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getShort(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyShortFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyShortFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyShortFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyIntegerFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Integer, Integer> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setInt(i, fun.apply(in.getInt(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionInteger(LargeArray in, LargeArrayType outType, Function<Integer, Integer> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getInt(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyIntegerFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyIntegerFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyIntegerFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyLongFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Long, Long> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setLong(i, fun.apply(in.getLong(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionLong(LargeArray in, LargeArrayType outType, Function<Long, Long> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getLong(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyLongFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyLongFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyLongFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyFloatFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Float, Float> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setFloat(i, fun.apply(in.getFloat(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionFloat(LargeArray in, LargeArrayType outType, Function<Float, Float> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getFloat(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyFloatFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyFloatFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyFloatFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyDoubleFun(LargeArray out, LargeArray in, long firstIdx, long lastIdx, Function<Double, Double> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setDouble(i, fun.apply(in.getDouble(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionDouble(LargeArray in, LargeArrayType outType, Function<Double, Double> fun)
    {
        long length = in.length();
        if (in.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(in.getDouble(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyDoubleFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyDoubleFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyDoubleFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexFloatFun(LargeArray out, ComplexFloatLargeArray in, long firstIdx, long lastIdx, Function<float[], float[]> fun)
    {
        ComplexFloatLargeArray outFloat;
        ComplexDoubleLargeArray outDouble;

        if (out.getType() == LargeArrayType.COMPLEX_FLOAT) {
            outFloat = (ComplexFloatLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                outFloat.setComplexFloat(i, fun.apply(in.getComplexFloat(i)));
            }
        } else {
            outDouble = (ComplexDoubleLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                outDouble.setComplexFloat(i, fun.apply(in.getComplexFloat(i)));
            }
        }
    }

    private static LargeArray evaluateSingleArgFunctionComplexFloat(LargeArray in, LargeArrayType outType, Function<float[], float[]> fun)
    {
        long length = in.length();
        LargeArrayType inType = LargeArrayType.COMPLEX_FLOAT;
        ComplexFloatLargeArray inComplex = (ComplexFloatLargeArray) LargeArrayUtils.convert(in, inType);
        if (inComplex.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(inComplex.getComplexFloat(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexFloatFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexFloatFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexFloatFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexDoubleFun(LargeArray out, ComplexDoubleLargeArray in, long firstIdx, long lastIdx, Function<double[], double[]> fun)
    {
        ComplexFloatLargeArray outFloat;
        ComplexDoubleLargeArray outDouble;

        if (out.getType() == LargeArrayType.COMPLEX_FLOAT) {
            outFloat = (ComplexFloatLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                outFloat.setComplexDouble(i, fun.apply(in.getComplexDouble(i)));
            }
        } else {
            outDouble = (ComplexDoubleLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                outDouble.setComplexDouble(i, fun.apply(in.getComplexDouble(i)));
            }
        }
    }

    private static LargeArray evaluateSingleArgFunctionComplexDouble(LargeArray in, LargeArrayType outType, Function<double[], double[]> fun)
    {
        long length = in.length();
        LargeArrayType inType = LargeArrayType.COMPLEX_DOUBLE;
        ComplexDoubleLargeArray inComplex = (ComplexDoubleLargeArray) LargeArrayUtils.convert(in, inType);
        if (inComplex.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(inComplex.getComplexDouble(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexDoubleFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexDoubleFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexDoubleFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexFloatRealFun(LargeArray out, ComplexFloatLargeArray in, long firstIdx, long lastIdx, Function<float[], Float> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setFloat(i, fun.apply(in.getComplexFloat(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionComplexFloatReal(LargeArray in, LargeArrayType outType, Function<float[], Float> fun)
    {
        long length = in.length();
        LargeArrayType inType = LargeArrayType.COMPLEX_FLOAT;
        ComplexFloatLargeArray inComplex = (ComplexFloatLargeArray) LargeArrayUtils.convert(in, inType);
        if (inComplex.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(inComplex.getComplexFloat(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexFloatRealFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexFloatRealFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexFloatRealFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexDoubleRealFun(LargeArray out, ComplexDoubleLargeArray in, long firstIdx, long lastIdx, Function<double[], Double> fun)
    {
        for (long i = firstIdx; i < lastIdx; i++) {
            out.setDouble(i, fun.apply(in.getComplexDouble(i)));
        }
    }

    private static LargeArray evaluateSingleArgFunctionComplexDoubleReal(LargeArray in, LargeArrayType outType, Function<double[], Double> fun)
    {
        long length = in.length();
        LargeArrayType inType = LargeArrayType.COMPLEX_DOUBLE;
        ComplexDoubleLargeArray inComplex = (ComplexDoubleLargeArray) LargeArrayUtils.convert(in, inType);
        if (inComplex.isConstant()) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(inComplex.getComplexDouble(0)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexDoubleRealFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexDoubleRealFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexDoubleRealFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static boolean areConstantArrays(LargeArray[] in)
    {
        boolean res = true;
        for (LargeArray largeArray : in) {
            res = res && largeArray.isConstant();
        }
        return res;
    }

    private static byte[] getConstantLogicAndByteArgs(LargeArray[] in)
    {
        int nData = in.length;
        byte[] args = new byte[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getByte(0);
        }
        return args;
    }

    private static void applyLogicAndByteFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<byte[], Byte> fun)
    {
        int nData = in.length;
        byte[] args = new byte[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getByte(i);
            }
            out.setByte(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionLogicAndByte(LargeArray[] in, LargeArrayType outType, Function<byte[], Byte> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantLogicAndByteArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyLogicAndByteFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyLogicAndByteFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyLogicAndByteFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static short[] getConstantUnsignedByteArgs(LargeArray[] in)
    {
        int nData = in.length;
        short[] args = new short[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getUnsignedByte(0);
        }
        return args;
    }

    private static void applyUnsignedByteFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<short[], Short> fun)
    {
        int nData = in.length;
        short[] args = new short[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getUnsignedByte(i);
            }
            out.setUnsignedByte(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionUnsignedByte(LargeArray[] in, LargeArrayType outType, Function<short[], Short> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantUnsignedByteArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyUnsignedByteFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyUnsignedByteFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyUnsignedByteFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static short[] getConstantShortArgs(LargeArray[] in)
    {
        int nData = in.length;
        short[] args = new short[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getShort(0);
        }
        return args;
    }

    private static void applyShortFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<short[], Short> fun)
    {
        int nData = in.length;
        short[] args = new short[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getShort(i);
            }
            out.setShort(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionShort(LargeArray[] in, LargeArrayType outType, Function<short[], Short> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantShortArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyShortFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyShortFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyShortFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static int[] getConstantIntegerArgs(LargeArray[] in)
    {
        int nData = in.length;
        int[] args = new int[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getInt(0);
        }
        return args;
    }

    private static void applyIntegerFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<int[], Integer> fun)
    {
        int nData = in.length;
        int[] args = new int[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getInt(i);
            }
            out.setInt(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionInteger(LargeArray[] in, LargeArrayType outType, Function<int[], Integer> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantIntegerArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyIntegerFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyIntegerFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyIntegerFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static long[] getConstantLongArgs(LargeArray[] in)
    {
        int nData = in.length;
        long[] args = new long[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getLong(0);
        }
        return args;
    }

    private static void applyLongFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<long[], Long> fun)
    {
        int nData = in.length;
        long[] args = new long[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getLong(i);
            }
            out.setLong(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionLong(LargeArray[] in, LargeArrayType outType, Function<long[], Long> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantLongArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyLongFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyLongFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyLongFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static float[] getConstantFloatArgs(LargeArray[] in)
    {
        int nData = in.length;
        float[] args = new float[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getFloat(0);
        }
        return args;
    }

    private static void applyFloatFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<float[], Float> fun)
    {
        int nData = in.length;
        float[] args = new float[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getFloat(i);
            }
            out.setFloat(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionFloat(LargeArray[] in, LargeArrayType outType, Function<float[], Float> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantFloatArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyFloatFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyFloatFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyFloatFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static double[] getConstantDoubleArgs(LargeArray[] in)
    {
        int nData = in.length;
        double[] args = new double[nData];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getDouble(0);
        }
        return args;
    }

    private static void applyDoubleFun(LargeArray out, LargeArray[] in, long firstIdx, long lastIdx, Function<double[], Double> fun)
    {
        int nData = in.length;
        double[] args = new double[nData];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getDouble(i);
            }
            out.setDouble(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionDouble(LargeArray[] in, LargeArrayType outType, Function<double[], Double> fun)
    {
        long length = in[0].length();
        if (areConstantArrays(in)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantDoubleArgs(in)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyDoubleFun(res, in, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyDoubleFun(res, in, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyDoubleFun(res, in, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static float[][] getConstantComplexFloatArgs(ComplexFloatLargeArray[] in)
    {
        int nData = in.length;
        float[][] args = new float[nData][];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getComplexFloat(0);
        }
        return args;
    }

    private static void applyComplexFloatFun(LargeArray out, ComplexFloatLargeArray[] in, long firstIdx, long lastIdx, Function<float[][], float[]> fun)
    {
        int nData = in.length;
        float[][] args = new float[nData][];
        ComplexFloatLargeArray outFloat;
        ComplexDoubleLargeArray outDouble;

        if (out.getType() == LargeArrayType.COMPLEX_FLOAT) {
            outFloat = (ComplexFloatLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                for (int v = 0; v < nData; v++) {
                    args[v] = in[v].getComplexFloat(i);
                }
                outFloat.setComplexFloat(i, fun.apply(args));
            }
        } else {
            outDouble = (ComplexDoubleLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                for (int v = 0; v < nData; v++) {
                    args[v] = in[v].getComplexFloat(i);
                }
                outDouble.setComplexFloat(i, fun.apply(args));
            }
        }

    }

    private static LargeArray evaluateVarArgFunctionComplexFloat(LargeArray[] in, LargeArrayType outType, Function<float[][], float[]> fun)
    {
        long length = in[0].length();
        ComplexFloatLargeArray[] inComplex = new ComplexFloatLargeArray[in.length];
        LargeArrayType inType = LargeArrayType.COMPLEX_FLOAT;
        for (int i = 0; i < in.length; i++) {
            inComplex[i] = (ComplexFloatLargeArray) LargeArrayUtils.convert(in[i], inType);
        }
        if (areConstantArrays(inComplex)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantComplexFloatArgs(inComplex)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexFloatFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexFloatFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexFloatFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static double[][] getConstantComplexDoubleArgs(ComplexDoubleLargeArray[] in)
    {
        int nData = in.length;
        double[][] args = new double[nData][];
        for (int v = 0; v < nData; v++) {
            args[v] = in[v].getComplexDouble(0);
        }
        return args;
    }

    private static void applyComplexDoubleFun(LargeArray out, ComplexDoubleLargeArray[] in, long firstIdx, long lastIdx, Function<double[][], double[]> fun)
    {
        int nData = in.length;
        double[][] args = new double[nData][];
        ComplexFloatLargeArray outFloat;
        ComplexDoubleLargeArray outDouble;

        if (out.getType() == LargeArrayType.COMPLEX_FLOAT) {
            outFloat = (ComplexFloatLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                for (int v = 0; v < nData; v++) {
                    args[v] = in[v].getComplexDouble(i);
                }
                outFloat.setComplexDouble(i, fun.apply(args));
            }
        } else {
            outDouble = (ComplexDoubleLargeArray) out;
            for (long i = firstIdx; i < lastIdx; i++) {
                for (int v = 0; v < nData; v++) {
                    args[v] = in[v].getComplexDouble(i);
                }
                outDouble.setComplexDouble(i, fun.apply(args));
            }
        }
    }

    private static LargeArray evaluateVarArgFunctionComplexDouble(LargeArray[] in, LargeArrayType outType, Function<double[][], double[]> fun)
    {
        long length = in[0].length();
        ComplexDoubleLargeArray[] inComplex = new ComplexDoubleLargeArray[in.length];
        LargeArrayType inType = LargeArrayType.COMPLEX_DOUBLE;
        for (int i = 0; i < in.length; i++) {
            in[i] = (ComplexDoubleLargeArray) LargeArrayUtils.convert(in[i], inType);
        }
        if (areConstantArrays(inComplex)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantComplexDoubleArgs(inComplex)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexDoubleFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexDoubleFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexDoubleFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexFloatRealFun(LargeArray out, ComplexFloatLargeArray[] in, long firstIdx, long lastIdx, Function<float[][], Float> fun)
    {
        int nData = in.length;
        float[][] args = new float[nData][];
        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getComplexFloat(i);
            }
            out.setFloat(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionComplexFloatReal(LargeArray[] in, LargeArrayType outType, Function<float[][], Float> fun)
    {
        long length = in[0].length();
        ComplexFloatLargeArray[] inComplex = new ComplexFloatLargeArray[in.length];
        LargeArrayType inType = LargeArrayType.COMPLEX_FLOAT;
        for (int i = 0; i < in.length; i++) {
            inComplex[i] = (ComplexFloatLargeArray) LargeArrayUtils.convert(in[i], inType);
        }
        if (areConstantArrays(inComplex)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantComplexFloatArgs(inComplex)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexFloatRealFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexFloatRealFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexFloatRealFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static void applyComplexDoubleRealFun(LargeArray out, ComplexDoubleLargeArray[] in, long firstIdx, long lastIdx, Function<double[][], Double> fun)
    {
        int nData = in.length;
        double[][] args = new double[nData][];

        for (long i = firstIdx; i < lastIdx; i++) {
            for (int v = 0; v < nData; v++) {
                args[v] = in[v].getComplexDouble(i);
            }
            out.setDouble(i, fun.apply(args));
        }
    }

    private static LargeArray evaluateVarArgFunctionComplexDoubleReal(LargeArray[] in, LargeArrayType outType, Function<double[][], Double> fun)
    {
        long length = in[0].length();
        ComplexDoubleLargeArray[] inComplex = new ComplexDoubleLargeArray[in.length];
        LargeArrayType inType = LargeArrayType.COMPLEX_DOUBLE;
        for (int i = 0; i < in.length; i++) {
            inComplex[i] = (ComplexDoubleLargeArray) LargeArrayUtils.convert(in[i], inType);
        }
        if (areConstantArrays(inComplex)) {
            return LargeArrayUtils.createConstant(outType, length, fun.apply(getConstantComplexDoubleArgs(inComplex)));
        } else {
            LargeArray res = LargeArrayUtils.create(outType, length, false);
            int nthreads = (int) FastMath.min(length, ConcurrencyUtils.getNumberOfThreads());
            if (nthreads < 2 || length < ConcurrencyUtils.getConcurrentThreshold()) {
                applyComplexDoubleRealFun(res, inComplex, 0, length, fun);
            } else {
                Future[] threads = new Future[nthreads];
                for (int j = 0; j < nthreads; j++) {
                    final long firstIdx = (j * length) / nthreads;
                    final long lastIdx = ((j + 1) * length) / nthreads;
                    threads[j] = ConcurrencyUtils.submit(() -> {
                        applyComplexDoubleRealFun(res, inComplex, firstIdx, lastIdx, fun);
                    });
                }
                try {
                    ConcurrencyUtils.waitForCompletion(threads);
                } catch (InterruptedException | ExecutionException ex) {
                    applyComplexDoubleRealFun(res, inComplex, 0, length, fun);
                }
            }
            return res;
        }
    }

    private static LargeArray evaluateVarArgFunction(LargeArray[] arrays,
                                                     LargeArrayType outType,
                                                     Function<byte[], Byte> logicFun,
                                                     Function<byte[], Byte> byteFun,
                                                     Function<short[], Short> unsignedByteFun,
                                                     Function<short[], Short> shortFun,
                                                     Function<int[], Integer> intFun,
                                                     Function<long[], Long> longFun,
                                                     Function<float[], Float> floatFun,
                                                     Function<double[], Double> doubleFun,
                                                     Function<float[][], float[]> complexFloatFun,
                                                     Function<double[][], double[]> complexDoubleFun,
                                                     Function<float[][], Float> complexFloatRealFun,
                                                     Function<double[][], Double> complexDoubleRealFun)
    {

        if (arrays == null || arrays.length == 0) {
            throw new IllegalArgumentException("Input arrays are null or empty.");
        }
        long length = arrays[0].length();
        for (LargeArray array : arrays) {
            if (array == null || !array.isNumeric() || array.length() != length) {
                throw new IllegalArgumentException("Some input arrays are null, non-numeric, or have wrong number of elements.");
            }
        }
        if (!outType.isNumericType()) {
            throw new IllegalArgumentException("Output type is not numeric.");
        }

        switch (outType) {
            case LOGIC:
                return evaluateVarArgFunctionLogicAndByte(arrays, outType, logicFun);
            case BYTE:
                return evaluateVarArgFunctionLogicAndByte(arrays, outType, byteFun);
            case UNSIGNED_BYTE:
                return evaluateVarArgFunctionUnsignedByte(arrays, outType, unsignedByteFun);
            case SHORT:
                return evaluateVarArgFunctionShort(arrays, outType, shortFun);
            case INT:
                return evaluateVarArgFunctionInteger(arrays, outType, intFun);
            case LONG:
                return evaluateVarArgFunctionLong(arrays, outType, longFun);
            case FLOAT:
                if (getLargestNumericType(arrays) == LargeArrayType.COMPLEX_FLOAT) {
                    return evaluateVarArgFunctionComplexFloatReal(arrays, outType, complexFloatRealFun);
                } else {
                    return evaluateVarArgFunctionFloat(arrays, outType, floatFun);
                }
            case DOUBLE:
                if (getLargestNumericType(arrays) == LargeArrayType.COMPLEX_DOUBLE) {
                    return evaluateVarArgFunctionComplexDoubleReal(arrays, outType, complexDoubleRealFun);
                } else {
                    return evaluateVarArgFunctionDouble(arrays, outType, doubleFun);
                }
            case COMPLEX_FLOAT:
                return evaluateVarArgFunctionComplexFloat(arrays, outType, complexFloatFun);
            case COMPLEX_DOUBLE:
                return evaluateVarArgFunctionComplexDouble(arrays, outType, complexDoubleFun);
            default:
                throw new IllegalArgumentException("Not supported output type.");
        }
    }

}
