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

/**
 * Supported types of large arrays.
 *
 * @author Piotr Wendykier (p.wendykier@icm.edu.pl)
 */
public enum LargeArrayType
{

    /**
     * Logical type (0 and 1)
     */
    LOGIC
    {

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Signed integer of size 1 byte: [-128, 127]
     */
    BYTE
    {

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Unsigned integer of size 1 byte: [0, 255]
     */
    UNSIGNED_BYTE
    {

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Signed integer of size 2 bytes: [-2^15, 2^15-1]
     */
    SHORT
    {
        @Override
        public long sizeOf()
        {
            return 2;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Signed integer of size 4 bytes: [-2^31, 2^31-1]
     */
    INT
    {
        @Override
        public long sizeOf()
        {
            return 4;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Signed integer of size 8 bytes: [-2^63, 2^63-1]
     */
    LONG
    {
        @Override
        public long sizeOf()
        {
            return 8;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isIntegerNumericType()
        {
            return true;
        }

    },
    /**
     * Single precision real type
     */
    FLOAT
    {
        @Override
        public long sizeOf()
        {
            return 4;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isRealNumericType()
        {
            return true;
        }

    },
    /**
     * Double precision real type
     */
    DOUBLE
    {
        @Override
        public long sizeOf()
        {
            return 8;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isRealNumericType()
        {
            return true;
        }

    },
    /**
     * Single precision complex type
     */
    COMPLEX_FLOAT
    {
        @Override
        public long sizeOf()
        {
            return 4;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isComplexNumericType()
        {
            return true;
        }

    },
    /**
     * Double precision complex type
     */
    COMPLEX_DOUBLE
    {
        @Override
        public long sizeOf()
        {
            return 8;
        }

        @Override
        public boolean isNumericType()
        {
            return true;
        }

        @Override
        public boolean isComplexNumericType()
        {
            return true;
        }

    },
    /**
     * String type
     */
    STRING,
    /**
     * Object type
     */
    OBJECT;

    /**
     * Returns the size (in bytes) of a given LargeArray type.
     *
     * @return size (in bytes) of a given LargeArray type
     */
    public long sizeOf()
    {
        return 1;

    }

    /**
     * Returns true if a given LargeArray type is numeric, false otherwise
     *
     * @return true if a given LargeArray type is numeric, false otherwise
     */
    public boolean isNumericType()
    {
        return false;
    }

    /**
     * Returns true if a given LargeArray type is integer numeric, false otherwise
     *
     * @return true if a given LargeArray type is integer numeric, false otherwise
     */
    public boolean isIntegerNumericType()
    {
        return false;
    }

    /**
     * Returns true if a given LargeArray type is real numeric, false otherwise
     *
     * @return true if a given LargeArray type is real numeric, false otherwise
     */
    public boolean isRealNumericType()
    {
        return false;
    }

    /**
     * Returns true if a given LargeArray type is complex numeric, false otherwise
     *
     * @return true if a given LargeArray type is complex numeric, false otherwise
     */
    public boolean isComplexNumericType()
    {
        return false;
    }

}
